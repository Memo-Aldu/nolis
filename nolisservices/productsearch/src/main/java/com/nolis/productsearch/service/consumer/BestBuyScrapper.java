package com.nolis.productsearch.service.consumer;

import com.nolis.commondata.dto.bestbuy.BestBuyProductAvailabilityDTO;
import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.bestbuy.BestBuyStoreLocationDTO;
import com.nolis.commondata.model.Search;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface BestBuyScrapper {
    BestBuySearchResultsDTO searchBestBuyWithStock(Search search);
    @Async
    CompletableFuture<BestBuySearchResultsDTO> searchBestBuyWithStockAsync(Search search);

    BestBuySearchResultsDTO searchBestBuy(Search search);
    @Async
    CompletableFuture<BestBuySearchResultsDTO> searchBestBuyAsync(Search search);

    BestBuyStoreLocationDTO getLocation(String location);
    @Async
    CompletableFuture<BestBuyStoreLocationDTO> getLocationAsync(String location);
    BestBuyProductAvailabilityDTO getAvailability(String sku, String locationCode);

}
