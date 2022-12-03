package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.bestbuy.BestBuyProductAvailabilityDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyProductResponseDTO;
import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyStoreLocationDTO;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.ServerErrorException;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.configuration.ExternalApiConfig;
import com.nolis.productsearch.exception.HttpClientErrorException;
import com.nolis.productsearch.helper.RandomUserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.client.params.ClientPNames.COOKIE_POLICY;
import static org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service @Slf4j
public class BestBuyScrapperImp implements BestBuyScrapper {
    @Qualifier("withoutEureka")
    private final RestTemplate restTemplate;
    private final ExternalApiConfig externalApiConfig;
    private final RandomUserAgent randomUserAgent;
    private final String HOST_NAME = "www.bestbuy.ca";

    public BestBuyScrapperImp(@Qualifier("withoutEureka") RestTemplate restTemplate,
                              ExternalApiConfig externalApiConfig, RandomUserAgent randomUserAgent) {
        this.restTemplate = restTemplate;
        this.externalApiConfig = externalApiConfig;
        this.randomUserAgent = randomUserAgent;
    }

    /**
     * This method is used to get product details and stock information
     * from the BestBuy api .
     * @param search Search
     * @return BestBuyProductsDTO
     */
    @Override
    public BestBuySearchResultsDTO getProductsBySearchQuery(Search search) {
        try {
            // time before request
            long startTime = System.currentTimeMillis();
            CompletableFuture<BestBuyProductResponseDTO> products = getProductsDetailsWithQueryAsync(search);
            String locationCodes = "";

            if(!search.getSearchLocation().isEmpty()) {
                log.info("Location is not empty, calling external api to get location codes");
                CompletableFuture<BestBuyStoreLocationDTO> location = getLocationAsync(search.getSearchLocation());
                CompletableFuture.allOf(location, products).join(); // wait for all to complete
                locationCodes = getLocationCodeFromLocation(location.get().getLocation());
            }
            // time after request
            long endTime = System.currentTimeMillis();
            log.info("Async calls took: {} ms", endTime - startTime);
            String skus = getSkusFromProductsDetails(products.get().getProductDetails());
            if(skus.isEmpty()) {
                return  BestBuySearchResultsDTO.builder()
                        .totalItems(0)
                        .currentPage(search.getPage())
                        .totalPages(0)
                        .pageSize(0)
                        .products(new ArrayList<>())
                        .totalItems(0)
                        .build();
            }
            BestBuyProductAvailabilityDTO availability = getAvailability(skus, locationCodes);

            return search.getInStockOnly() ?
                    getProducts(getInventoryWithStock(availability), products.get())
                    : getProducts(availability.getProductsAvailable(), products.get());

        }catch (HttpClientErrorException e ) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new HttpClientErrorException(e.getMessage(), e.getHttpResponse());
        } catch (Exception e) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new ServerErrorException(e.getMessage(), e);
        }
    }

    /**
     * Async call to get the Best Buy product details and stock information
     * @param search Search
     * @return CompletableFuture<BestBuyLocationDTO>
     */
    @Async
    @Override
    public CompletableFuture<BestBuySearchResultsDTO> getProductsBySearchQueryAsync(Search search) {
        return CompletableFuture.completedFuture(getProductsBySearchQuery(search));
    }

    /**
     * Get products details from BestBuy api
     * @param search Search
     * @return BestBuyProductDetailDTO
     */
    @Override
    public BestBuySearchResultsDTO getProductsDetailsWithQuery(Search search) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                search.getPage(), search.getPageSize(), search.getQuery());
        log.info("Product details url: {}", url);
        HttpEntity<BestBuyProductResponseDTO> productsResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyProductResponseDTO.class);
        BestBuyProductResponseDTO body = productsResponse.getBody();
        if(Objects.isNull(body)) {
            return new BestBuySearchResultsDTO();
        }
        return BestBuySearchResultsDTO.builder()
                .totalItems(body.getTotalItems())
                .currentPage(body.getCurrentPage())
                .pageSize(body.getPageSize())
                .totalPages(body.getTotalPages())
                .products(body.getProductDetails())
                .build();
    }

    /**
     * Get location codes from location name ex: "Mississauga, ON"
     * @param location string
     * @return BestBuyLocationDTO
     */
    @Override
    public BestBuyStoreLocationDTO getLocation(String location) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyLocationUrl(), location);
        log.info("Location url: {}", url);
        HttpEntity<BestBuyStoreLocationDTO> locationResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyStoreLocationDTO.class);
        return locationResponse.getBody();
    }

    /**
     * Get availabilities(stock info) of a string of skus
     * @param skus string
     * @param locationCodes string
     * @return BestBuyAvailabilityDTO
     */
    @Override
    public BestBuyProductAvailabilityDTO getAvailability(String skus, String locationCodes) {
        HttpEntity<Object> request = new HttpEntity<>(getInventoryHeaders());
        String url = MessageFormat.format(externalApiConfig.bestBuyInventoryUrl(), locationCodes, skus);
        try {
            URI uri = new URI(url);
            log.info("Availability uri: {}", uri);
            HttpEntity<BestBuyProductAvailabilityDTO> availabilityResponse = restTemplate.exchange(
                    uri, HttpMethod.GET, request,
                    BestBuyProductAvailabilityDTO.class);
            log.info("availability response: {}", availabilityResponse.getBody());
            return availabilityResponse.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("Invalid search query");
        }
    }

    /**
     * Async call to get the location codes from a location name ex: "Mississauga, ON"
     * @param location string
     * @return CompletableFuture<BestBuyLocationDTO>
     */
    @Async
    protected CompletableFuture<BestBuyStoreLocationDTO> getLocationAsync(String location) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyLocationUrl(), location);
        log.info("Async location url: {}", url);
        HttpEntity<BestBuyStoreLocationDTO> locationResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyStoreLocationDTO.class);
        return CompletableFuture.completedFuture(locationResponse.getBody());
    }

    /**
     * Async call to the product details api
     * @param search Search
     * @return CompletableFuture<BestBuyProductDetailDTO>
     */
    @Async
    protected CompletableFuture<BestBuyProductResponseDTO> getProductsDetailsWithQueryAsync(Search search) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                search.getPage(), search.getPageSize(), search.getQuery());
        log.info("Async product details url: {}", url);
        HttpEntity<BestBuyProductResponseDTO> productsResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyProductResponseDTO.class);
        return CompletableFuture.completedFuture(productsResponse.getBody());
    }

    private String getSkusFromProductsDetails(ArrayList<BestBuyProductResponseDTO.BestBuyProduct> products) {
        return products.stream()
                .map(BestBuyProductResponseDTO.BestBuyProduct::getSku)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private String getLocationCodeFromLocation(ArrayList<BestBuyStoreLocationDTO.LocationRoot> location) {
        return location.stream()
                .map(BestBuyStoreLocationDTO.LocationRoot::getLocation)
                .map(BestBuyStoreLocationDTO.LocationInfo::getLocationCode)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private BestBuySearchResultsDTO getProducts(ArrayList<BestBuyProductAvailabilityDTO.ProductAvailability> availability,
                                           BestBuyProductResponseDTO productDetails) {
        ArrayList<BestBuyProductResponseDTO.BestBuyProduct> products  = productDetails.getProductDetails().stream()
                .peek(product -> {
                    BestBuyProductAvailabilityDTO.ProductAvailability availabilityItem = availability
                            .stream()
                            .filter(x -> x.getSku().equals(product.getSku()))
                            .findFirst()
                            .orElse(null);
                    product.setAvailability(availabilityItem);
                })
                // remove if no availability
                .filter(x -> x.getAvailability() != null)
                // add the host name to the product url
                .peek(product -> product
                        .setProductUrl("https://" + HOST_NAME + product.getProductUrl()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return BestBuySearchResultsDTO.builder()
                .currentPage(productDetails.getCurrentPage())
                .pageSize(products.size())
                .totalPages(productDetails.getTotalPages())
                .totalItems(productDetails.getTotalItems())
                .products(products)
                .build();
    }

    private ArrayList<BestBuyProductAvailabilityDTO.ProductAvailability> getInventoryWithStock(
            BestBuyProductAvailabilityDTO availability) {

        availability.getProductsAvailable().forEach(av -> {
            if(av.getPickup().getLocations() != null) {
                av.getPickup().getLocations().removeIf(location ->
                        location.getQuantityOnHand() <= 0);
            }
        });
        return availability.getProductsAvailable().stream()
                // doesn't count for backorder; if backorder-able will still return true
                .filter(availability2 -> (availability2.getShipping().getQuantityRemaining() > 0 &&
                        availability2.getShipping().getPurchasable()) || availability2.getPickup().getPurchasable())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private HttpHeaders getInventoryHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("authority", "www.bestbuy.ca");
        headers.add("Connection", "keep-alive");
        headers.add("accept", APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", randomUserAgent.getRandomUserAgent());
        headers.set("Host", "www.bestbuy.ca");
        return headers;
    }


    private HttpHeaders getBestBuyHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("authority", "sdk.split.io");
        headers.add("Connection", "keep-alive");
        headers.add(COOKIE_POLICY, BROWSER_COMPATIBILITY);
        headers.set(CONTENT_TYPE, APPLICATION_JSON);
        headers.set("accept", "application/json");
        // safari useragent because it's faster with bestbuy
        headers.add("user-agent", randomUserAgent.getRandomUserAgent());
        headers.add("Cookie", "SERVERID=c52");
        return headers;
    }
}
