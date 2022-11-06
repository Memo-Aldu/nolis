package com.nolis.productsearch.controller;

import com.nolis.productsearch.DTO.CustomHttpResponseDTO;
import com.nolis.productsearch.exception.BadRequestException;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AuthService;
import com.nolis.productsearch.service.consumer.BestBuyScrapper;
import com.nolis.productsearch.service.producer.SearchService;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
@RestController
@RequestMapping("api/v1/product-search/best-buy")
public record BestBuyController(
        SearchService searchService,
        AuthService authService,
        BestBuyScrapper bestBuyScrapper,
        ResponseHandler responseHandler) {

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
    public ResponseEntity<CustomHttpResponseDTO> SearchBestBuy(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "2") String pageSize,
            @RequestParam(required = false, defaultValue = "") String category, HttpServletRequest request) {
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
            Map<String, Object> data = Map.of(
                    "best-buy", bestBuyScrapper.getFullProductsInfoBySearchQuery(search));
            return responseHandler.httpResponse(
                    CustomHttpResponseDTO.builder()
                            .message("Search Request Successful")
                            .data(data)
                            .success(true)
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.OK)
                            .build(),
                    setupResponseHeaders(request));
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    private boolean hasAuthority(HttpServletRequest request, String scope) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authService.hasAuthority(authorizationHeader, scope);
    }

    private HttpHeaders setupResponseHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, "application/json");
        headers.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
        headers.add("X-Prev-Path", request.getRequestURI());
        headers.add("X-Request-Path", request.getHeader("X-Request-Path"));
        headers.add("X-Request-Id", request.getHeader("X-Request-Id"));
        return headers;
    }

}
