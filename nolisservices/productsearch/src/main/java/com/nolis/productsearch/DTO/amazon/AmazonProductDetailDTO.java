package com.nolis.productsearch.DTO.amazon;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter @Setter
@ToString @NoArgsConstructor @AllArgsConstructor @Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AmazonProductDetailDTO {
    private String asin;
    private String name;
    private String image;
    private String productUrl;
    private String price;
    private String customerReviewCount;
    private String customerReviewAverage;
    private String availability;
    private Boolean isPrime;
}
