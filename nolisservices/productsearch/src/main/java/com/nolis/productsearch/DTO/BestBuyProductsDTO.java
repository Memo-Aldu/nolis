package com.nolis.productsearch.DTO;

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
    private Boolean hasBrandStore;
    private String productStatusCode;
    private String brand;
    ArrayList<Product> products;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @ToString @Builder
    public static class Product {
        private BestBuyProductDetailDTO.ProductDetail productDetail;
        private BestBuyAvailabilityDTO.ProductAvailability productAvailability;
    }
}
