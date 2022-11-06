package com.nolis.productsearch.controller;

import com.nolis.productsearch.DTO.BestBuyProductDetailDTO;
import com.nolis.productsearch.exception.BadRequestException;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AuthService;
import com.nolis.productsearch.service.consumer.BestBuyScrapper;
import com.nolis.productsearch.service.producer.SearchService;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequestMapping("api/v1/product-search/best-buy")
public record BestBuyController(
        SearchService searchService,
        AuthService authService,
        BestBuyScrapper bestBuyScrapper
) {

    @GetMapping
    public String hi(HttpServletRequest request, HttpServletResponse response) {
        if(hasAuthority(request, "ROLE_BESTBUY_USER")) {
            return "Hello World";
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }
    @PostMapping()
    public void registerSearch(@RequestBody SearchRequest searchRequest) {
        log.info("New Search Request {}", searchRequest);
        searchService.registerSearch(searchRequest);
    }

    @GetMapping("/search")
    public ArrayList<BestBuyProductDetailDTO.Product> SearchBestBuy(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "2") String pageSize,
            @RequestParam(required = false, defaultValue = "") String category,
            HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .searchLocation(location)
                .query(searchRequest.query())
                .category(category)
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .build();

        if(hasAuthority(request, "ROLE_BESTBUY_USER")) {
            log.info("Best Buy Search Request {}", search);
            return bestBuyScrapper.getProductsBySearchQuery(search);
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    private boolean hasAuthority(HttpServletRequest request, String scope) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authService.hasAuthority(authorizationHeader, scope);
    }

}
