package com.nolis.productsearch.controller;

import com.nolis.productsearch.DTO.bestbuy.BestBuyProductDetailDTO;
import com.nolis.productsearch.DTO.bestbuy.BestBuyProductsDTO;
import com.nolis.productsearch.DTO.bestbuy.CustomHttpResponseDTO;
import com.nolis.productsearch.exception.BadRequestException;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import com.nolis.productsearch.helper.ControllerHelper;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.BestBuyScrapper;
import com.nolis.productsearch.service.producer.SearchService;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/product-search/best-buy")
public record BestBuyController(
        ControllerHelper controllerHelper,
        SearchService searchService,
        BestBuyScrapper bestBuyScrapper,
        ResponseHandler responseHandler) {

    @GetMapping
    public String hi(HttpServletRequest request, HttpServletResponse response) {
        if(controllerHelper.hasAuthority(request, "ROLE_BESTBUY_USER")) {
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
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "2") String pageSize,
            @RequestParam(required = false, defaultValue = "") String category, HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .query(searchRequest.query())
                .category(category)
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .build();

        if(controllerHelper.hasAuthority(request, "ROLE_BESTBUY_USER")) {
            log.info("Best Buy Search Request {}", search);
            BestBuyProductDetailDTO products = bestBuyScrapper.getProductsDetailsWithQuery(search);
            Map<String, Object> data = Map.of(
                    "best-buy", products);
            return products.getProductDetails() != null && products.getProductDetails().size() > 0 ?
                    responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("Search Request Successful")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request)) :
                    responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("No products found with the given search criteria "
                                            + search.getQuery())
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request));
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    @GetMapping("/search/stock")
    public ResponseEntity<CustomHttpResponseDTO> SearchBestBuyStock(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "2") String pageSize,
            @RequestParam(required = false, defaultValue = "false") String inStockOnly,
            @RequestParam(required = false, defaultValue = "") String category, HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .searchLocation(location)
                .query(searchRequest.query())
                .inStockOnly(inStockOnly)
                .category(category)
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .build();
        // TODO: Add search to database
        // TODO: add a role for inStockOnly
        if(controllerHelper.hasAuthority(request, "ROLE_BESTBUY_USER")) {
            log.info("Best Buy Search Request {}", search);
            BestBuyProductsDTO products = bestBuyScrapper.getProductsInfoBySearchQuery(search);
            Map<String, Object> data = Map.of(
                    "best-buy", products);
            return  products.getProducts() != null && products.getProducts().size() > 0 ?
                    responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("Search Request Successful")
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request)) :
                    responseHandler.httpResponse(
                            CustomHttpResponseDTO.builder()
                                    .message("No products found with the given search criteria "
                                            + search.getQuery())
                                    .data(data)
                                    .success(true)
                                    .timestamp(System.currentTimeMillis())
                                    .status(HttpStatus.OK)
                                    .build(),
                            controllerHelper.setupResponseHeaders(request));
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

}
