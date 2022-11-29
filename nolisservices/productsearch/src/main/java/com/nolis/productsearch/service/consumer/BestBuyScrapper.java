package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.bestbuy.BestBuyAvailabilityDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyLocationDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyProductResponseDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyProductsDTO;
import com.nolis.productsearch.model.Search;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface BestBuyScrapper {
    BestBuyProductsDTO getProductsBySearchQuery(Search search);
    @Async
    CompletableFuture<BestBuyProductsDTO> getProductsBySearchQueryAsync(Search search);

    BestBuyProductsDTO getProductsDetailsWithQuery(Search search);

    BestBuyLocationDTO getLocation(String location);
    BestBuyAvailabilityDTO getAvailability(String sku, String locationCode);

}
