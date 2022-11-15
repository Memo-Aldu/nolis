package com.nolis.productsearch.DTO.bestbuy;

import lombok.*;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString @Builder
public class BestBuyProductsDTO {
    private Integer currentPage;
    private Integer total;
    private Integer totalPages;
    private Integer pageSize;
    ArrayList<Product> products;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @ToString @Builder
    public static class Product {
        private BestBuyProductResponseDTO.ProductDetail productDetail;
        private BestBuyAvailabilityDTO.ProductAvailability productAvailability;
    }
}
