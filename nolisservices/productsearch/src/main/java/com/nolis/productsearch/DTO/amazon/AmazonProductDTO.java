package com.nolis.productsearch.DTO.amazon;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.ArrayList;

@Getter @Setter
@ToString @NoArgsConstructor @AllArgsConstructor @Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AmazonProductDTO {
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
    private Integer pageSize;
    ArrayList<Product> products;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @ToString @Builder
    public static class Product {
        private String asin;
        private String name;
        private String image;
        private String productUrl;
        private String price;
        private String customerReviewCount;
        private Double customerReviewAverage;
        private String availability;
        private Boolean isPrime;
    }
}
