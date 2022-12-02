package com.nolis.commondata.dto.amazon;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AmazonProductDTO {
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
