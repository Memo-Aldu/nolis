package com.nolis.productsearch.Service;


import com.nolis.productsearch.Request.SearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public record SearchService() {
    public void registerSearch(SearchRequest request) {
        log.info("Registering the search request");
    }
}
