package com.nolis.commondata.dto;

import com.nolis.commondata.enums.ProductType;
import lombok.*;

import java.io.Serializable;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class RegisteredProduct implements Serializable {
    private String productId;
    private String name;
    private String description;
    private String imageUrl;
    private ProductType productType;

    public boolean isValidEntity() {
        return productId != null && !productId.isEmpty();
    }
}
