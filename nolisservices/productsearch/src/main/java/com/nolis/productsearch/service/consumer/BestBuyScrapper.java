package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.BestBuyProductDTO;
import com.nolis.productsearch.model.Search;

import java.util.ArrayList;

public interface BestBuyScrapper {
    ArrayList<BestBuyProductDTO.Product> getProductsBySearchQuery(Search search);

}
