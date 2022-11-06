package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.BestBuyProductDetailDTO;
import com.nolis.productsearch.model.Search;

import java.util.ArrayList;

public interface BestBuyScrapper {
    ArrayList<BestBuyProductDetailDTO.Product> getProductsBySearchQuery(Search search);

}
