package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.bestbuy.BestBuyProductAvailabilityDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyProductResponseDTO;
import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyStoreLocationDTO;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.ServerErrorException;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.configuration.ExternalApiConfig;
import com.nolis.commondata.exception.HttpClientErrorException;
import com.nolis.productsearch.helper.RandomUserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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


import static com.nolis.commondata.constants.Caches.*;
import static com.nolis.commondata.constants.Servers.BESTBUY_HOST_NAME;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.client.params.ClientPNames.COOKIE_POLICY;
import static org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Service @Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BestBuyScrapperImp implements BestBuyScrapper {
    @Qualifier("withoutLoadBalanced")
    private final RestTemplate restTemplate;
    // instance of this, wrapper in stub proxy for redis
    private final BestBuyScrapperImp _bestBuyScrapperImp;
    private final ExternalApiConfig externalApiConfig;
    private final RandomUserAgent randomUserAgent;

    public BestBuyScrapperImp(@Qualifier("withoutLoadBalanced") RestTemplate restTemplate,
                              BestBuyScrapperImp bestBuyScrapperImp, ExternalApiConfig externalApiConfig,
                              RandomUserAgent randomUserAgent) {
        this.restTemplate = restTemplate;
        this._bestBuyScrapperImp = bestBuyScrapperImp;
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
    @Cacheable(value = SEARCH, key = "'best-buy_'.concat(#search.toString())")
    public BestBuySearchResultsDTO searchBestBuyWithStock(Search search) {
        try {
            log.info("Getting products from BestBuy for search: {}", search);
            // time before request
            long startTime = System.currentTimeMillis();
            CompletableFuture<BestBuySearchResultsDTO> searchResultsFuture = _bestBuyScrapperImp
                    .searchBestBuyAsync(search);
            String locationCodes = "";

            if(search.getSearchLocation() == null || !search.getSearchLocation().isEmpty()) {
                log.info("Location is not empty, calling external api to get location codes");
                CompletableFuture<BestBuyStoreLocationDTO> location = _bestBuyScrapperImp
                        .getLocationAsync(search.getSearchLocation());
                CompletableFuture.allOf(location, searchResultsFuture).join(); // wait for all to complete
                locationCodes = getLocationCodeFromLocation(location.get().getLocation());
            }
            // time after request
            long endTime = System.currentTimeMillis();
            log.info("Async calls took: {} ms", endTime - startTime);
            BestBuySearchResultsDTO searchResults = searchResultsFuture.get();
            String skus = getSkusFromProductsDetails(
                    searchResults.getProducts()
            );
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
            BestBuyProductAvailabilityDTO availability = _bestBuyScrapperImp
                    .getAvailability(skus, locationCodes);

            return search.getInStockOnly() ?
                    getInStockProducts(getInventoryWithStock(availability), searchResults)
                    : getInStockProducts(availability.getProductsAvailable(), searchResults);

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
    public CompletableFuture<BestBuySearchResultsDTO> searchBestBuyWithStockAsync(Search search) {
        log.info("Async -Getting products from BestBuy for search: {}", search);
        return CompletableFuture.completedFuture(
                _bestBuyScrapperImp.searchBestBuyWithStock(search));
    }

    /**
     * Get products details from BestBuy api
     * @param search Search
     * @return BestBuyProductDetailDTO
     */
    @Override
    @Cacheable(value = SEARCH, key = "'best-buy_'.concat(#search.toString())")
    public BestBuySearchResultsDTO searchBestBuy(Search search) {
        log.info("Calling BestBuy products API");
        // Todo: custom headers for each request
        log.info("Getting products from BestBuy for search: {}", search);
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                search.getPage(), search.getPageSize(), search.getQuery());
        log.info("Product details url: {}", url);
        HttpEntity<BestBuyProductResponseDTO> productsResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyProductResponseDTO.class);
        if(!productsResponse.hasBody()) {
            return new BestBuySearchResultsDTO();
        }
        BestBuyProductResponseDTO body = productsResponse.getBody();
        return BestBuySearchResultsDTO.builder()
                .totalItems(body.getTotalItems())
                .currentPage(body.getCurrentPage())
                .pageSize(body.getPageSize())
                .totalPages(body.getTotalPages())
                .products(enrichProducts(body.getProductDetails()))
                .build();
    }

    /**
     * Async call to the product details api
     * @param search Search
     * @return CompletableFuture<BestBuyProductDetailDTO>
     */
    @Async
    @Override
    public CompletableFuture<BestBuySearchResultsDTO> searchBestBuyAsync(Search search) {
        // Todo: custom headers for each request
        log.info("Async -Calling BestBuy products API");
        return CompletableFuture.completedFuture(
                _bestBuyScrapperImp.searchBestBuy(search));
    }

    /**
     * Get location codes from location name ex: "Mississauga, ON"
     * @param location string
     * @return BestBuyLocationDTO
     */
    @Override
    @Cacheable(value = BEST_BUY_LOCATIONS, key = "#location")
    public BestBuyStoreLocationDTO getLocation(String location) {
        // Todo: custom headers for each request
        log.info("Calling BestBuy Location API");
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        String url = String.format(externalApiConfig.bestBuyLocationUrl(), location);
        log.info("Location url: {}", url);
        HttpEntity<BestBuyStoreLocationDTO> locationResponse = restTemplate.exchange(
                url, HttpMethod.GET, request,
                BestBuyStoreLocationDTO.class);
        return locationResponse.getBody();
    }

    /**
     * Async call to get the location codes from a location name ex: "Mississauga, ON"
     * @param location string
     * @return CompletableFuture<BestBuyLocationDTO>
     */
    @Async
    @Override
    public CompletableFuture<BestBuyStoreLocationDTO> getLocationAsync(String location) {
        // Todo: custom headers for each request
        log.info("Async -Calling BestBuy Location API");
        return CompletableFuture.completedFuture(
                _bestBuyScrapperImp.getLocation(location));
    }

    /**
     * Get availabilities(stock info) of a string of skus
     * @param skus string
     * @param locationCodes string
     * @return BestBuyAvailabilityDTO
     */
    @Override
    public BestBuyProductAvailabilityDTO getAvailability(String skus, String locationCodes) {
        log.info("Calling BestBuy Availability API");
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

    /**----------------------------------------private methods-----------------------------------------**/

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

    private BestBuySearchResultsDTO getInStockProducts(ArrayList<BestBuyProductAvailabilityDTO.ProductAvailability> availability,
                                                BestBuySearchResultsDTO searchResults) {
        // check if this works
        searchResults.setProducts(
                searchResults.getProducts().stream()
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
                    .peek(product -> {
                            if(!product.getProductUrl().contains("https://")) {
                                product.setProductUrl("https://" + BESTBUY_HOST_NAME + product.getProductUrl());
                            }
                    })
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
        return searchResults;
    }

    private ArrayList<BestBuyProductResponseDTO.BestBuyProduct> enrichProducts(
            ArrayList<BestBuyProductResponseDTO.BestBuyProduct> products) {
        return products.stream()
                .peek(product -> {
                    if(!product.getProductUrl().contains("https://")) {
                        product.setProductUrl("https://" + BESTBUY_HOST_NAME + product.getProductUrl());
                    }
                }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
