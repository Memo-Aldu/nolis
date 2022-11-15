package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.amazon.AmazonProductDTO;
import com.nolis.productsearch.model.Search;

public interface AmazonScrapper {
    AmazonProductDTO getProductsBySearchQuery(Search search);
}
