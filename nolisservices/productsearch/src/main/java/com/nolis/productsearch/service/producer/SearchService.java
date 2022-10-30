package com.nolis.productsearch.service.producer;


import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AuthService;
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
