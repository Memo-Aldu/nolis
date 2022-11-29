package com.nolis.productsearch.DTO.bestbuy;

import lombok.*;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString @Builder
public class BestBuyProductsDTO {
    private Integer currentPage;
    private Integer totalItems;
    private Integer totalPages;
    private Integer pageSize;
    ArrayList<BestBuyProductResponseDTO.ProductDetail> products;

}
