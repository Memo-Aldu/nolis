package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.bestbuy.BestBuyAvailabilityDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyLocationDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyProductDetailDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyProductsDTO;
import com.nolis.productsearch.model.Search;

public interface BestBuyScrapper {
    BestBuyProductsDTO getProductsInfoBySearchQuery(Search search);
    BestBuyLocationDTO getLocation(String location);
    BestBuyProductDetailDTO getProductsDetailsWithQuery(Search search);
    BestBuyAvailabilityDTO getAvailability(String sku, String locationCode);

}
