package com.nolis.productsearch.controller;

import com.nolis.productsearch.DTO.amazon.AmazonProductDetailDTO;
import com.nolis.productsearch.DTO.bestbuy.CustomHttpResponseDTO;
import com.nolis.productsearch.exception.BadRequestException;
import com.nolis.productsearch.exception.TokenUnauthorizedToScopeException;
import com.nolis.productsearch.helper.ControllerHelper;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.model.Search;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AmazonScrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

@RestController @Slf4j
@RequestMapping("api/v1/product-search/amazon")
public record AmazonController(
        ControllerHelper controllerHelper,
        AmazonScrapper amazonScrapper,
        ResponseHandler responseHandler
) {

    @GetMapping("/search")
    public ResponseEntity<CustomHttpResponseDTO> SearchAmazon(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "2") String pageSize, HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .query(searchRequest.query())
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .build();

        if(controllerHelper.hasAuthority(request, "ROLE_AMAZON_USER")) {
            log.info("Best Buy Search Request {}", search);
            ArrayList<AmazonProductDetailDTO> products = amazonScrapper
                    .getProductsBySearchQuery(search);
            Map<String, Object> data = Map.of(
                    "amazon", products);
            return products.size() > 0 ? responseHandler.httpResponse(
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
