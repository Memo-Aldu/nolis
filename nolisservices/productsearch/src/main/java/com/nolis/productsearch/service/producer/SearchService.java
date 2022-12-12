package com.nolis.productsearch.service.producer;


import com.nolis.commonconfig.security.service.AuthService;
import com.nolis.productsearch.request.SearchRequest;
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
