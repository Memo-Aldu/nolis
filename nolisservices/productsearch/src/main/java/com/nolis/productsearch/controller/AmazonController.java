package com.nolis.productsearch.controller;

import com.nolis.commondata.dto.amazon.AmazonSearchResultsDTO;
import com.nolis.commondata.dto.http.CustomHttpResponseDTO;
import com.nolis.commondata.enums.ProductType;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.TokenUnauthorizedToScopeException;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.helper.ControllerHelper;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.AmazonScrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize, HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .query(searchRequest.query())
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .productType(ProductType.Amazon)
                .build();

        if(controllerHelper.hasAuthority(request, "ROLE_AMAZON_USER")) {
            log.info("Best Buy Search Request {}", search);
            AmazonSearchResultsDTO products = amazonScrapper
                    .getProductsBySearchQuery(search);
            Map<String, Object> data = Map.of(
                    "amazon", products);
            return products.getProducts().size() > 0 ? responseHandler.httpResponse(
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
