package com.nolis.productsearch.Model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class BestBuyProduct {

    private UUID uuid;
    private String SKU;
    private String name;
    private String description;
    private Integer rating;
    private Integer ratingCount;
    private String url;
    private Integer regularPrice;
    private Integer salePrice;
    private Date saleEndDate;
    private String image;
    private String category;
    private Boolean hasPromotion;
    private Boolean isVisible;

}
