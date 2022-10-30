package com.nolis.productsearch.Controller;

import com.nolis.productsearch.Request.SearchRequest;
import com.nolis.productsearch.Service.Consumer.AuthService;
import com.nolis.productsearch.Service.Producer.SearchService;
import com.nolis.productsearch.Model.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequestMapping("api/v1/product-search")
public record BestBuyController(
        SearchService searchService,
        AuthService authService
) {


    @GetMapping
    public String hi(HttpServletRequest request, HttpServletResponse response) {
        if(hasAuthority(request, "ROLE_BESTBUY_USER")) {
            return "Hello World";
        }
        return "You are not authorized to access this resource";
    }
    @PostMapping()
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

    private boolean hasAuthority(HttpServletRequest request, String scope) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authService.hasAuthority(authorizationHeader, scope);
    }

}
