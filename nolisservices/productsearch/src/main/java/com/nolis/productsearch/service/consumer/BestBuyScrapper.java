package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.bestbuy.BestBuyProductAvailabilityDTO;
import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyStoreLocationDTO;
import com.nolis.commondata.model.Search;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface BestBuyScrapper {
    BestBuySearchResultsDTO getProductsBySearchQuery(Search search);
    @Async
    CompletableFuture<BestBuySearchResultsDTO> getProductsBySearchQueryAsync(Search search);

    BestBuySearchResultsDTO getProductsDetailsWithQuery(Search search);

    BestBuyStoreLocationDTO getLocation(String location);
    BestBuyProductAvailabilityDTO getAvailability(String sku, String locationCode);

}
