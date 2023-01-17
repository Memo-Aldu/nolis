package com.nolis.commondata.dto.amazon;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AmazonProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964832L;
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
