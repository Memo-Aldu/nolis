package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.amazon.AmazonProductDTO;
import com.nolis.productsearch.model.Search;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface AmazonScrapper {
    AmazonProductDTO getProductsBySearchQuery(Search search);
    @Async
    CompletableFuture<AmazonProductDTO> getProductsBySearchQueryAsync(Search search);
}
