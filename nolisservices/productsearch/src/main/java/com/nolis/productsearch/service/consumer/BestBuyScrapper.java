package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.BestBuyAvailabilityDTO;
import com.nolis.productsearch.DTO.BestBuyLocationDTO;
import com.nolis.productsearch.DTO.BestBuyProductDetailDTO;
import com.nolis.productsearch.DTO.BestBuyProductsDTO;
import com.nolis.productsearch.model.Search;

public interface BestBuyScrapper {
    BestBuyProductsDTO getFullProductsInfoBySearchQuery(Search search);
    BestBuyLocationDTO getLocation(String location);
    BestBuyProductDetailDTO getProductsDetailsWithQuery(Search search);
    BestBuyAvailabilityDTO getAvailability(String sku, String locationCode);

}
