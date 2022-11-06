package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.Configuration.ExternalApiConfig;
import com.nolis.productsearch.DTO.BestBuyAvailabilityDTO;
import com.nolis.productsearch.DTO.BestBuyLocationDTO;
import com.nolis.productsearch.DTO.BestBuyProductDetailDTO;
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

    public BestBuyScrapperImp(@Qualifier("withoutEureka") RestTemplate restTemplate,
                              ExternalApiConfig externalApiConfig) {
        this.restTemplate = restTemplate;
        this.externalApiConfig = externalApiConfig;
    }
    @Override
    public ArrayList<BestBuyProductDetailDTO.Product> getProductsBySearchQuery(Search search) {
        try {
            // time before request
            long startTime = System.currentTimeMillis();
            CompletableFuture<BestBuyProductDetailDTO> products = getProductsWithQuery(search);
            String locationCodes = "";
            if(!search.getSearchLocation().isEmpty()) {
                log.info("Location is not empty, calling external api to get location codes");
                CompletableFuture<BestBuyLocationDTO> location = getLocation(search.getSearchLocation());
                CompletableFuture.allOf(location, products).join(); // wait for all to complete
                locationCodes = getLocationCodeFromLocation(location.get().getLocation());
            }
            // time after request
            long endTime = System.currentTimeMillis();
            log.info("Async calls took: {} ms", endTime - startTime);
            String skus = getSkusFromProducts(products.get().getProducts());
            BestBuyAvailabilityDTO availability = getAvailability(skus, locationCodes);

            log.info("Availability: {}", availability);
            log.info("locationCode: {}\nSKU's {}", locationCodes, skus);
            return products.get().getProducts().stream()
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }catch (HttpClientErrorException e ) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new HttpClientErrorException(e.getMessage(), e.getHttpResponse());
        } catch ( InterruptedException | ExecutionException e) {
            log.error("Error while fetching products from BestBuy {}", e.getMessage());
            throw new ServerErrorException(e.getMessage(), e);
        }
    }

    @Async
    protected CompletableFuture<BestBuyLocationDTO> getLocation(String location) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        log.info("location url: {}", externalApiConfig.bestBuyLocationUrl());
        HttpEntity<BestBuyLocationDTO> locationResponse = restTemplate.exchange(
                String.format(externalApiConfig.bestBuyLocationUrl(), location),
                HttpMethod.GET, request,
                BestBuyLocationDTO.class);
        return CompletableFuture.completedFuture(locationResponse.getBody());
    }

    @Async
    protected CompletableFuture<BestBuyProductDetailDTO> getProductsWithQuery(Search search) {
        // Todo: custom headers for each request
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        HttpEntity<BestBuyProductDetailDTO> productsResponse = restTemplate.exchange(
                String.format(externalApiConfig.bestBuyProductUrl(), search.getCategory(),
                        search.getPage(), search.getPageSize(), search.getQuery()),
                HttpMethod.GET, request,
                BestBuyProductDetailDTO.class);
        return CompletableFuture.completedFuture(productsResponse.getBody());
    }

    protected BestBuyAvailabilityDTO getAvailability(String sku, String locationCode) {
        HttpEntity<Object> request = new HttpEntity<>(getInventoryHeaders());
        String url = MessageFormat.format(externalApiConfig.bestBuyInventoryUrl(), locationCode, sku);
        try {
            URI uri = new URI(url);
            log.info("availability url: {}", uri);
            HttpEntity<BestBuyAvailabilityDTO> availabilityResponse = restTemplate.exchange(
                    uri,
                    HttpMethod.GET, request,
                    BestBuyAvailabilityDTO.class);
            log.info("availability response: {}", availabilityResponse.getBody());
            return availabilityResponse.getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSkusFromProducts(ArrayList<BestBuyProductDetailDTO.Product> products) {
        return products.stream()
                .map(BestBuyProductDetailDTO.Product::getSku)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private String getLocationCodeFromLocation(ArrayList<BestBuyLocationDTO.LocationRoot> location) {
        return location.stream()
                .map(BestBuyLocationDTO.LocationRoot::getLocation)
                .map(BestBuyLocationDTO.Location::getLocationCode)
                .reduce((s, s2) -> s + "%7C" + s2)
                .orElse("");
    }

    private HttpHeaders getInventoryHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("authority", "www.bestbuy.ca");
        headers.add("Connection", "keep-alive");
        headers.add("accept", APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
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
