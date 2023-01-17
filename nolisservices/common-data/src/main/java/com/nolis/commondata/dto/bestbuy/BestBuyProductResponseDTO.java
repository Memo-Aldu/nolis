package com.nolis.commondata.dto.bestbuy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@Builder @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyProductResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964837L;
    private Integer currentPage;
    @JsonProperty("total")
    private Integer totalItems;
    private Integer totalPages;
    private Integer pageSize;
    @JsonProperty("products")
    private ArrayList<BestBuyProduct> productDetails;
    private Date lastSearchDate;
    private Boolean hasBrandStore;
    private String productStatusCode;
    @JsonProperty("Brand")
    private String brand;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class BestBuyProduct implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035964937L;
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
        private BestBuyProductAvailabilityDTO.ProductAvailability availability;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class BestBuySeller implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035965937L;
        private Boolean canSell;
        private String id;
        private String name;
    }
}