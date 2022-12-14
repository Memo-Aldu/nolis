package com.nolis.productsearch.model;

import com.nolis.productsearch.DTO.bestbuy.BestBuyProductResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
@Builder
public class BestBuyProduct {

    private String id;
    private String sku;
    private String name;
    private String description;
    private String productUrl;
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
    private Boolean isPreorderable;
    private Boolean isClearance;
    private Boolean isInStoreOnly;
    private Boolean isOnlineOnly;
    private BestBuyProductResponseDTO.BestBuySeller seller;
    private ArrayList<Search> searches;


}
