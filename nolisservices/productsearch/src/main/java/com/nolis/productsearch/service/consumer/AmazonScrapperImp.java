package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.amazon.AmazonProductDetailDTO;
import com.nolis.productsearch.DTO.amazon.AmazonProductResponseDTO;
import com.nolis.productsearch.configuration.ExternalApiConfig;
import com.nolis.productsearch.helper.RandomUserAgent;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;


@Service @Slf4j
public class AmazonScrapperImp implements AmazonScrapper    {

    @Qualifier("withoutEureka")
    private final RestTemplate restTemplate;
    private final ExternalApiConfig externalApiConfig;
    private final String HOST_NAME = "www.amazon.ca";

    public AmazonScrapperImp(@Qualifier("withoutEureka") RestTemplate restTemplate,
                              ExternalApiConfig externalApiConfig) {
        this.restTemplate = restTemplate;
        this.externalApiConfig = externalApiConfig;
    }

    @Override
    public ArrayList<AmazonProductDetailDTO> getProductsBySearchQuery(Search search) {
        HttpEntity<Void> request = new HttpEntity<>(getAmazonHeader());
        String url = String.format(externalApiConfig.amazonProductUrl(),
                search.getQuery().replace(" ", "+"),
                search.getPage());
        log.info("Product details url: {}", url);
        HttpEntity<String> productsResponse = restTemplate.exchange(
                url, HttpMethod.POST, request,
                String.class);
        ArrayList<AmazonProductResponseDTO> responseArray;
        try {
            responseArray = convertStringResponseToDTO(Objects.requireNonNull(productsResponse.getBody()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return getProductDetailsFromHtmlString(responseArray);
    }

    private ArrayList<AmazonProductDetailDTO> getProductDetailsFromHtmlString(ArrayList<AmazonProductResponseDTO> productsResponse) {
        return productsResponse.stream()
                .map(
                        productResponse -> {
                            Document doc = Jsoup.parse(productResponse.getHtml());
                            String stock = doc.select("span.a-color-price").text();
                            String reviewCount= doc.select("span.s-link-centralized-style").text();
                            Boolean isPrime = doc.select("i.a-icon-prime").size() > 0;
                            log.info("reviewCount: {}", reviewCount);
                            return AmazonProductDetailDTO.builder()
                                    .isPrime(isPrime)
                                    .name(doc.select("h2").text())
                                    .price(doc.select("span.a-offscreen").text())
                                    .image(doc.select("img").attr("src"))
                                    .productUrl("https://" + HOST_NAME +
                                            doc.select("a").attr("href"))
                                    .asin(productResponse.getAsin())
                                    .customerReviewAverage(doc.select("span.a-icon-alt").text())
                                    .customerReviewCount(Objects.equals(reviewCount, "") ? "0" : reviewCount)
                                    .availability(Objects.equals(stock, "") ? "In Stock" : stock)

                                    .build();
                        }
                ).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<AmazonProductResponseDTO> convertStringResponseToDTO(String response) throws JSONException {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(response.split("&&&")));
        list.remove(list.size() - 1);
        return list.stream().map(
                s -> {
                    s = s.replace("[", "{")
                            .replace("]", "}")
                            .replace("} }", "}")
                            .replace("\"dispatch\", ", "");
                    s = "{" +  s.split("\", \\{")[1];
                    try {
                        return new JSONObject(s);
                    } catch (JSONException e) {
                        log.error("Error while parsing json: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
        ).map(
                jsonObject -> {
                        try {
                            if(jsonObject.has("asin") && jsonObject.getString("asin").length() > 0) {
                                return new AmazonProductResponseDTO(
                                        jsonObject.getString("asin"),
                                        jsonObject.getString("html"),
                                        jsonObject.getInt("index")
                                );
                            }
                        } catch (JSONException e) {
                            log.info("Error while parsing json: {}", e.getMessage());
                            return null;
                        }
                    return null;
                }
        ).filter(
                Objects::nonNull
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private HttpHeaders getAmazonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", HOST_NAME);
        headers.set("authority", HOST_NAME);
        headers.set("Accept", "application/json");
        headers.set("Accept-Language", "en-US,en;q=0.5");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");
        headers.add("user-agent", RandomUserAgent.getRandomSafariUserAgent());
        return headers;
    }
}
