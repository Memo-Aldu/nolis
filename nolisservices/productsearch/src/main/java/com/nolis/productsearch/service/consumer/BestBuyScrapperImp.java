package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.BestBuyDTO;
import com.nolis.productsearch.DTO.BestBuyResponseDTO;
import com.nolis.productsearch.helper.RandomUserAgent;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.apache.http.client.params.ClientPNames.COOKIE_POLICY;
import static org.apache.http.client.params.CookiePolicy.BROWSER_COMPATIBILITY;

@Service @Slf4j
public record BestBuyScrapperImp(
        @Qualifier("withoutEureka") RestTemplate restTemplate
) implements BestBuyScrapper {
    private static final String SEARCH_URL = "https://www.bestbuy.ca/api/v2/json/search?categoryid=%s&ang=en-CA&query=%s";
    //use a builder to create a new header with params in it

    @Override
    public BestBuyDTO[] getProducts(Search search) {
        HttpEntity<Void> request = new HttpEntity<>(getBestBuyHeaders());
        HttpEntity<BestBuyResponseDTO> response = restTemplate.exchange(
                String.format(SEARCH_URL, "", search.getQuery()), HttpMethod.GET, request,
                BestBuyResponseDTO.class);
        log.info("BestBuy response: {}", Objects.requireNonNull(response.getBody()).getProducts()[0].getSeller());
        return response.getBody().getProducts();
    }


    private HttpHeaders getBestBuyHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("authority", "sdk.split.io");
        headers.add("pragma", "no-cache");
        headers.add("cache-control", "no-cache");
        headers.add("accept", "*/*");
        headers.add("accept-language", "en-US,en;q=0.9");
        headers.add("sec-fetch-dest", "empty");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-site", "cross-site");
        headers.add(COOKIE_POLICY, BROWSER_COMPATIBILITY);
        headers.add("user-agent", RandomUserAgent.getRandomUserAgent());
        headers.add("Cookie", "SERVERID=c52");
        return headers;
    }
}
