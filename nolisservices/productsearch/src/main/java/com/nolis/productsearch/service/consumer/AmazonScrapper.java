package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.amazon.AmazonSearchResultsDTO;
import com.nolis.commondata.model.Search;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface AmazonScrapper {
    AmazonSearchResultsDTO getProductsBySearchQuery(Search search);
    @Async
    CompletableFuture<AmazonSearchResultsDTO> getProductsBySearchQueryAsync(Search search);
}
