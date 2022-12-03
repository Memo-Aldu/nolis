package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.amazon.AmazonProductDTO;
import com.nolis.commondata.dto.amazon.AmazonProductResponseDTO;
import com.nolis.commondata.dto.amazon.AmazonSearchResultsDTO;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.configuration.ExternalApiConfig;
import com.nolis.productsearch.helper.RandomUserAgent;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service @Slf4j
public class AmazonScrapperImp implements AmazonScrapper    {

    @Qualifier("withoutEureka")
    private final RestTemplate restTemplate;
    private final ExternalApiConfig externalApiConfig;
    private final String HOST_NAME = "www.amazon.ca";
    private final RandomUserAgent randomUserAgent;

    public AmazonScrapperImp(@Qualifier("withoutEureka") RestTemplate restTemplate,
                             ExternalApiConfig externalApiConfig, RandomUserAgent randomUserAgent) {
        this.restTemplate = restTemplate;
        this.externalApiConfig = externalApiConfig;
        this.randomUserAgent = randomUserAgent;
    }

    // TODO: 2022-11-28  Add Pagination
    @Override
    public AmazonSearchResultsDTO getProductsBySearchQuery(Search search) {
        HttpEntity<Void> request = new HttpEntity<>(getAmazonHeader());
        String url = String.format(externalApiConfig.amazonProductUrl(),
                search.getQuery().replace(" ", "+"),
                search.getPage());
        log.info("Product details url: {}", url);
        HttpEntity<String> productsResponse = restTemplate.exchange(
                url, HttpMethod.POST, request,
                String.class);
        AmazonSearchResultsDTO response;
        ArrayList<JSONObject> responseJsonArray;
        try {
            responseJsonArray = convertStringResponseToJsonArray(Objects.requireNonNull(productsResponse.getBody()));
            response = convertJsonToAmazonProductDTO(responseJsonArray, search.getPageSize());
            setMetaData(responseJsonArray, response);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * Async call to get the best buy product details and stock information
     * @param search Search
     * @return CompletableFuture<BestBuyLocationDTO>
     */
    @Async
    @Override
    public CompletableFuture<AmazonSearchResultsDTO> getProductsBySearchQueryAsync(Search search) {
        return CompletableFuture.completedFuture(getProductsBySearchQuery(search));
    }

    private ArrayList<AmazonProductDTO> getProductFromHtmlString(ArrayList<AmazonProductResponseDTO> productsResponse) {
        return productsResponse.stream()
                .map(
                        productResponse -> {
                            log.info("Product html: {}", productResponse.getHtml());
                            // fix reviews not showing sometimes, and sometimes I get different html
                            Document doc = Jsoup.parse(productResponse.getHtml());
                            String stock = doc.select("span.a-color-price").text();
                            String reviewCount= doc.select("" +
                                    "div.a-spacing-top-micro a.s-underline-link-text span.a-size-base").text();
                            String reviewAverage = doc.select("span.a-icon-alt").text();
                            Boolean isPrime = doc.select("i.a-icon-prime").size() > 0;
                            Element priceEle = doc.select("span.a-offscreen").first();
                            String price = priceEle != null ? priceEle.text() : "";

                            return AmazonProductDTO.builder()
                                    .isPrime(isPrime)
                                    .name(doc.select("h2").text())
                                    .price(price)
                                    .image(doc.select("img").attr("src"))
                                    .productUrl("https://" + HOST_NAME + "/dp/" + productResponse.getAsin())
                                    .asin(productResponse.getAsin())
                                    .customerReviewAverage(Objects.equals(reviewAverage, "") ? 0 :
                                            Double.parseDouble(reviewAverage.split(" ")[0]))
                                    .customerReviewCount(Objects.equals(reviewCount, "") ? "0" : reviewCount)
                                    .availability(Objects.equals(stock, "") ? "In Stock" : stock)
                                    .build();
                        }
                ).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<JSONObject> convertStringResponseToJsonArray(String response) throws JSONException {
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
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    private void setMetaData(ArrayList<JSONObject> responseJsonArray, AmazonSearchResultsDTO results) throws JSONException {
        JSONObject metadata = responseJsonArray.get(1).getJSONObject("metadata");
        int totalItems = metadata.getInt("totalResultCount");
        int pageSize = results.getProducts().size();
        int totalPages = (int) Math.ceil((double)totalItems / pageSize);
        results.setTotalItems(totalItems);
        results.setPageSize(pageSize);
        results.setTotalPages(totalPages);
        results.setCurrentPage(metadata.getInt("page"));
    }

    private AmazonSearchResultsDTO convertJsonToAmazonProductDTO(
            ArrayList<JSONObject> responseJsonArray, Integer pageSize) {
        AmazonSearchResultsDTO results = new AmazonSearchResultsDTO();
        results.setProducts(
                getProductFromHtmlString(
                        responseJsonArray.stream()
                                .filter(
                                        // filter the products
                                        jsonObject -> {
                                            try {
                                                return jsonObject.has("asin") && jsonObject.getString("asin").length() > 0;
                                            } catch (JSONException e) {
                                                log.info("Error while parsing json: {}", e.getMessage());
                                                return false;
                                            }
                                        }
                                ).map(
                                        // map the products
                                        jsonObject -> {
                                            try {
                                                return new AmazonProductResponseDTO(
                                                        jsonObject.getString("asin"),
                                                        jsonObject.getString("html"),
                                                        jsonObject.getInt("index")
                                                );
                                            } catch (JSONException e) {
                                                log.info("Error while parsing json: {}", e.getMessage());
                                                return null;
                                            }
                                        }
                                ).filter(Objects::nonNull).limit(pageSize).collect(Collectors.toCollection(ArrayList::new))
                ));
        return results;
    }
    private HttpHeaders getAmazonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", HOST_NAME);
        headers.set("authority", HOST_NAME);
        headers.set("accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.set("accept-language", "en-US,en;q=0.5");
        headers.set("pragma", "no-cache");
        headers.set("viewport-width", String.valueOf(Math.floor(Math.random() * 2100) + 1200));
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("downlink", String.valueOf(Math.floor(Math.random() * 30) + 10));
        headers.set("device-memory", String.valueOf(Math.floor(Math.random() * 16) + 8));
        headers.set("rtt", String.valueOf(Math.floor(Math.random() * 100) + 50));
        headers.set("ect", "4g");
        headers.set("cookie", "");
        headers.set("Connection", "keep-alive");
        headers.add("user-agent", randomUserAgent.getRandomUserAgent());
        return headers;
    }
}
