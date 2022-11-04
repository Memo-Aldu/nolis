package com.nolis.productsearch.service.consumer;

import com.nolis.productsearch.DTO.BestBuyDTO;
import com.nolis.productsearch.model.Search;

public interface BestBuyScrapper {
    BestBuyDTO[] getProducts(Search search);
}
