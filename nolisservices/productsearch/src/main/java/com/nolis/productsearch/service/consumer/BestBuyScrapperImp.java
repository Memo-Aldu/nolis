package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.Configuration.ExternalApiConfig;
import com.nolis.productsearch.DTO.BestBuyAvailabilityDTO;
import com.nolis.productsearch.DTO.BestBuyLocationDTO;
import com.nolis.productsearch.DTO.BestBuyProductDetailDTO;
import com.nolis.productsearch.DTO.BestBuyProductsDTO;
import com.nolis.productsearch.exception.HttpClientErrorException;
import com.nolis.productsearch.exception.ServerErrorException;
import com.nolis.productsearch.helper.RandomUserAgent;
import com.nolis.productsearch.model.Search;
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
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.client.params.ClientPNames.COOKIE_POLICY;
import static org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service @Slf4j
public class BestBuyScrapperImp implements BestBuyScrapper {
    @Qualifier("withoutEureka")
    private final RestTemplate restTemplate;
    private final ExternalApiConfig externalApiConfig;

    private final String HOST_NAME = "www.bestbuy.ca";

    public BestBuyScrapperImp(@Qualifier("withoutEureka") RestTemplate restTemplate,
                              ExternalApiConfig externalApiConfig) {
        this.restTemplate = restTemplate;
        this.externalApiConfig = externalApiConfig;
    }

    /**
     * This method is used to get product details and stock information
     * from the BestBuy api .
     * @param search Search
     * @return BestBuyProductsDTO
     */
    @Override
    public BestBuyProductsDTO getProductsInfoBySearchQuery(Search search) {
        try {
            // time before request
            long startTime = System.currentTimeMillis();
            CompletableFuture<BestBuyProductDetailDTO> products = getProductsDetailsWithQueryAsync(search);
            String locationCodes = "";

            if(!search.getSearchLocation().isEmpty()) {
                log.info("Location is not empty, calling external api to get location codes");
                CompletableFuture<BestBuyLocationDTO> location = getLocationAsync(search.getSearchLocation());
                CompletableFuture.allOf(location, products).join(); // wait for all to complete
                locationCodes = getLocationCodeFromLocation(location.get().getLocation());
            }
            // time after request
            long endTime = System.currentTimeMillis();
            log.info("Async calls took: {} ms", endTime - startTime);
            String skus = getSkusFromProductsDetails(products.get().getProductDetails());
            BestBuyAvailabilityDTO availability = getAvailability(skus, locationCodes);

            return Objects.equals(search.getInStockOnly(), "true") ?
                    getProducts(getInventoryWithStock(availability), products.get())
                    : getProducts(availability.getProductsAvailable(), products.get());

        }catch (HttpClientErrorException e ) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new HttpClientErrorException(e.getMessage(), e.getHttpResponse());
        } catch ( InterruptedException | ExecutionException e) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new ServerErrorException(e.getMessage(), e);
        }
    }

    /**
     * Get products details from BestBuy api
     * @param search Search
     * @return BestBuyProductDetailDTO
     */
    @Override
    public BestBuyProductDetailDTO getProductsDetailsWithQuery(Search search) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                search.getPage(), search.getPageSize(), search.getQuery());
        log.info("Product details url: {}", url);
        HttpEntity<BestBuyProductDetailDTO> productsResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyProductDetailDTO.class);
        return productsResponse.getBody();
    }

    /**
     * Get location codes from location name ex: "Mississauga, ON"
     * @param location string
     * @return BestBuyLocationDTO
     */
    @Override
    public BestBuyLocationDTO getLocation(String location) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyLocationUrl(), location);
        log.info("Location url: {}", url);
        HttpEntity<BestBuyLocationDTO> locationResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyLocationDTO.class);
        return locationResponse.getBody();
    }

    /**
     * Get availabilities(stock info) of a string of skus
     * @param skus string
     * @param locationCodes string
     * @return BestBuyAvailabilityDTO
     */
    @Override
    public BestBuyAvailabilityDTO getAvailability(String skus, String locationCodes) {
        HttpEntity<Object> request = new HttpEntity<>(getInventoryHeaders());
        String url = MessageFormat.format(externalApiConfig.bestBuyInventoryUrl(), locationCodes, skus);
        try {
            URI uri = new URI(url);
            log.info("Availability uri: {}", uri);
            HttpEntity<BestBuyAvailabilityDTO> availabilityResponse = restTemplate.exchange(
                    uri, HttpMethod.GET, request,
                    BestBuyAvailabilityDTO.class);
            log.info("availability response: {}", availabilityResponse.getBody());
            return availabilityResponse.getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Async call to get the location codes from a location name ex: "Mississauga, ON"
     * @param location string
     * @return CompletableFuture<BestBuyLocationDTO>
     */
    @Async
    protected CompletableFuture<BestBuyLocationDTO> getLocationAsync(String location) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyLocationUrl(), location);
        log.info("Async location url: {}", url);
        HttpEntity<BestBuyLocationDTO> locationResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyLocationDTO.class);
        return CompletableFuture.completedFuture(locationResponse.getBody());
    }

    /**
     * Async call to the product details api
     * @param search Search
     * @return CompletableFuture<BestBuyProductDetailDTO>
     */
    @Async
    protected CompletableFuture<BestBuyProductDetailDTO> getProductsDetailsWithQueryAsync(Search search) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                search.getPage(), search.getPageSize(), search.getQuery());
        log.info("Async product details url: {}", url);
        HttpEntity<BestBuyProductDetailDTO> productsResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyProductDetailDTO.class);
        return CompletableFuture.completedFuture(productsResponse.getBody());
    }

    private String getSkusFromProductsDetails(ArrayList<BestBuyProductDetailDTO.ProductDetail> products) {
        return products.stream()
                .map(BestBuyProductDetailDTO.ProductDetail::getSku)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private String getLocationCodeFromLocation(ArrayList<BestBuyLocationDTO.LocationRoot> location) {
        return location.stream()
                .map(BestBuyLocationDTO.LocationRoot::getLocation)
                .map(BestBuyLocationDTO.LocationInfo::getLocationCode)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private BestBuyProductsDTO getProducts( ArrayList<BestBuyAvailabilityDTO.ProductAvailability> availability,
                                            BestBuyProductDetailDTO productDetails) {
        ArrayList<BestBuyProductsDTO.Product> products  = productDetails.getProductDetails().stream()
                .map(product -> {
                    BestBuyAvailabilityDTO.ProductAvailability availabilityItem = availability
                            .stream()
                            .filter(x -> x.getSku().equals(product.getSku()))
                            .findFirst()
                            .orElse(null);
                    return new BestBuyProductsDTO.Product(product, availabilityItem);
                })
                // remove if no availability
                .filter(x -> x.getProductAvailability() != null)
                // add the host name to the product url
                .peek(product -> product.getProductDetail()
                        .setProductUrl("https://" + HOST_NAME + product.getProductDetail().getProductUrl()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return BestBuyProductsDTO.builder()
                .currentPage(productDetails.getCurrentPage())
                .pageSize(products.size())
                .totalPages(productDetails.getTotalPages())
                .total(productDetails.getTotal())
                .productStatusCode(productDetails.getProductStatusCode())
                .brand(productDetails.getBrand())
                .hasBrandStore(productDetails.getHasBrandStore())
                .products(products)
                .build();
    }

    private ArrayList<BestBuyAvailabilityDTO.ProductAvailability> getInventoryWithStock(
            BestBuyAvailabilityDTO availability) {

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
        headers.add("user-agent", RandomUserAgent.getRandomSafariUserAgent());
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
        headers.add("user-agent", RandomUserAgent.getRandomSafariUserAgent());
        headers.add("Cookie", "SERVERID=c52");
        return headers;
    }
}
