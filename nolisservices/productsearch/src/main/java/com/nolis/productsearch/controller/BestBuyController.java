package com.nolis.productsearch.controller;

import com.nolis.commondata.dto.bestbuy.BestBuySearchResultsDTO;
import com.nolis.commondata.dto.http.CustomHttpResponseDTO;
import com.nolis.commondata.enums.ProductType;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.TokenUnauthorizedToScopeException;
import com.nolis.commondata.model.Search;
import com.nolis.productsearch.helper.ControllerHelper;
import com.nolis.productsearch.helper.ResponseHandler;
import com.nolis.productsearch.request.SearchRequest;
import com.nolis.productsearch.service.consumer.BestBuyScrapper;
import com.nolis.productsearch.service.producer.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/product-search/best-buy")
public record BestBuyController(
        ControllerHelper controllerHelper,
        SearchService searchService,
        BestBuyScrapper bestBuyScrapper,
        ResponseHandler responseHandler) {

    @PostMapping()
    public void registerSearch(@RequestBody SearchRequest searchRequest) {
        log.info("New Search Request {}", searchRequest);
        searchService.registerSearch(searchRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomHttpResponseDTO> SearchBestBuy(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String category, HttpServletRequest request) {
        if(!searchRequest.isValidate()) {
            throw new BadRequestException("Invalid Search Request");
        }
        Search search = Search.builder()
                .query(searchRequest.query())
                .category(category)
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId()) // not required id or email
                .productType(ProductType.BestBuy)
                .build();

        if(controllerHelper.hasAuthority(request, "ROLE_BESTBUY_USER")) {
            log.info("Best Buy Search Request {}", search);
            BestBuySearchResultsDTO products = bestBuyScrapper.searchBestBuy(search);
            return getCustomHttpResponseDTOResponseEntity(request, search, products);
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    @GetMapping("/search/stock")
    public ResponseEntity<CustomHttpResponseDTO> SearchBestBuyStock(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize,
            @RequestParam(required = false, defaultValue = "false") Boolean inStockOnly,
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
                .productType(ProductType.BestBuy)
                .userId(searchRequest.userId())
                .build();
        // TODO: Add search to database
        // TODO: add a role for inStockOnly
        if(controllerHelper.hasAuthority(request, "ROLE_BESTBUY_USER")) {
            log.info("Best Buy Search Request {}", search);
            BestBuySearchResultsDTO products = bestBuyScrapper.searchBestBuyWithStock(search);
            return getCustomHttpResponseDTOResponseEntity(request, search, products);
        } else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    /**
     * returns an appropriate response entity based on the data
     * @param request HttpServletRequest
     * @param search Search
     * @param products BestBuyProductsDTO
     * @return ResponseEntity<CustomHttpResponseDTO>
     */
    private ResponseEntity<CustomHttpResponseDTO> getCustomHttpResponseDTOResponseEntity(
            HttpServletRequest request, Search search, BestBuySearchResultsDTO products) {
        Map<String, Object> data = Map.of(
                "best-buy", products);
        return products.getProducts() != null && products.getProducts().size() > 0 ?
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
    }
}
