package com.nolis.productsearch.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @AllArgsConstructor @NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyResponseDTO {
    private String sku;
    private String name;
    private String shortDescription;
    private Double customerRating;
    private Double customerRatingCount;
    private Double customerReviewCount;
    private String productUrl;
    private String currentRegion;
    private String hideSavings;
    private String hideSaleEndDate;
    private String productType;
    private Double regularPrice;
    private Double salePrice;
    private Integer saleEndDate;
    private String thumbnailImage;
    private Integer primaryParentCategoryId;
    private String categoryName;
    private Double ehf;
    private String seoText;
    private Integer sellerId;
    private String seller;
    private String highResImage;
    private String altLangSeoText;
    private Integer offerId;
    private String priceUnit;
    private Boolean requiresAgeVerification;
    private Boolean isPriceEndsLabel;
    private Boolean hasPromotion;
    private Boolean isAdvertised;
    private Boolean isClearance;
    private Boolean isInStoreOnly;
    private Boolean isOnlineOnly;
    private Boolean isVisible;
    private Boolean isPreorderable;
    private Boolean isFrenchCompliant;
    private Boolean isMarketplace;
    private Boolean hasFrenchContent;
    private String[] categoryIds;
}
