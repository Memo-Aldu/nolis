package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.amazon.AmazonProductDetailDTO;
import com.nolis.productsearch.model.Search;

import java.util.ArrayList;

public interface AmazonScrapper {
    ArrayList<AmazonProductDetailDTO> getProductsBySearchQuery(Search search);
}
