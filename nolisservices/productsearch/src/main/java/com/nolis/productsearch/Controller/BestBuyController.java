package com.nolis.productsearch.Controller;

import com.nolis.productsearch.Request.SearchRequest;
import com.nolis.productsearch.Service.SearchService;
import com.nolis.productsearch.model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/products")
public record BestBuyController(SearchService searchService) {

    @PostMapping
    public void registerSearch(@RequestBody SearchRequest searchRequest) {
        log.info("New Search Request {}", searchRequest);
        searchService.registerSearch(searchRequest);
    }

    @GetMapping("/bestbuy")
    public String SearchBestBuy(@RequestBody SearchRequest searchRequest,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer pageSize) {
        Search search = Search.builder()
                .searchLocation(location)
                .query(searchRequest.query())
                .pageSize(pageSize)
                .page(page)
                .userId(searchRequest.userId())
                .build();
        log.info("Best Buy Search Request {}", search);

        return "Best Buy Search Request " + search.toString();
    }

}
