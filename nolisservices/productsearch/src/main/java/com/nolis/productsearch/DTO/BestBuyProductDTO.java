package com.nolis.productsearch.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyResponseDTO {
    private Integer currentPage;
    private Integer total;
    private Integer totalPages;
    private Integer pageSize;
    private ArrayList<Product> products;
    private ArrayList<Object> paths;
    private Object facets;
    private Date lastSearchDate;
    private Object relatedQueries;
    private ArrayList<Object> sscs;
    private Object relatedCategories;
    private Object selectedFacets;
    private Object resources;
    private Object redirectUrl;
    private Object promotions;
    private Boolean hasBrandStore;
    private String productStatusCode;
    private Object sscStatusCode;
    @JsonProperty("Brand")
    private String brand;
    private ArrayList<Object> breadcrumb;

    @Getter @AllArgsConstructor @NoArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Product{
        private String sku;
        private String name;
        private String shortDescription;
        private Double customerRating;
        private Integer customerReviewCount;
        private String productUrl;
        private Boolean hideSavings;
        private Double regularPrice;
        private Double salePrice;
        private Long saleEndDate;
        private String thumbnailImage;
        private String categoryName;
        private BestBuySeller seller;
        private String highResImage;
        private Object offerId;
        private Boolean hasPromotion;
        private Boolean isClearance;
        private Boolean isInStoreOnly;
        private Boolean isOnlineOnly;
        private Boolean isVisible;
        private Boolean isPreorderable;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class BestBuySeller {
        private Boolean canSell;
        private String id;
        private String name;
    }
}
