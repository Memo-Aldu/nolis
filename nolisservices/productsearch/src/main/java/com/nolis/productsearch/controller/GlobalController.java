package com.nolis.productsearch.controller;

import com.nolis.commondata.dto.amazon.AmazonSearchResultsDTO;
import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.http.CustomHttpResponseDTO;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import com.nolis.productsearch.helper.ControllerHelper;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AmazonScrapper;
import com.nolis.productsearch.service.consumer.BestBuyScrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j @RestController
@RequestMapping("api/v1/product-search")
public record GlobalController(
        ControllerHelper controllerHelper,
        AmazonScrapper amazonScrapper,
        BestBuyScrapper bestBuyScrapper,
        ResponseHandler responseHandler
) {

    @GetMapping("/search")
    public ResponseEntity<CustomHttpResponseDTO> searchAll(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "false") boolean inStockOnly,
            @RequestParam(required = false, defaultValue = "") String category, HttpServletRequest request) {

        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .query(searchRequest.query())
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .searchLocation(location)
                .inStockOnly(inStockOnly)
                .category(category)
                .build();
        if(controllerHelper.hasAuthority(request, "ROLE_SEARCH_ALL")) {
            log.info("Best Buy Search Request {}", search);
            CompletableFuture<BestBuySearchResultsDTO> bestBuyProductsFuture = bestBuyScrapper
                    .getProductsBySearchQueryAsync(search);
            CompletableFuture<AmazonSearchResultsDTO> amazonProductsFuture = amazonScrapper
                    .getProductsBySearchQueryAsync(search);
            CompletableFuture.allOf(bestBuyProductsFuture, amazonProductsFuture).join();
            try {
                BestBuySearchResultsDTO bestBuyProducts = bestBuyProductsFuture.get();
                AmazonSearchResultsDTO amazonProducts = amazonProductsFuture.get();
                Map<String, Object> data = Map.of(
                        "bestBuyProducts", bestBuyProducts,
                        "amazonProducts", amazonProducts
                );
                if(bestBuyProducts.getProducts() != null && bestBuyProducts.getProducts().size() > 0 ||
                        amazonProducts.getProducts() != null && amazonProducts.getProducts().size() > 0) {
                    return responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("Search Request Successful")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request));
                } else {
                    return responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("No products found with the given search criteria "
                                            + search.getQuery())
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request));
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred while searching for products {}", e.getMessage());
                return responseHandler.InternalServerErrorResponse(e);
            }
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }
}
