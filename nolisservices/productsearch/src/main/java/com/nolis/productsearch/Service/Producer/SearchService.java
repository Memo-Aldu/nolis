package com.nolis.productsearch.Service.Producer;


import com.nolis.productsearch.Request.SearchRequest;
import com.nolis.productsearch.Service.Consumer.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public record SearchService(
        AuthService authService
) {
    public void registerSearch(SearchRequest request) {
        log.info("Registering the search request");
    }
}
