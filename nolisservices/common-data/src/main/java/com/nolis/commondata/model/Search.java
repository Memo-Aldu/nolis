package com.nolis.commondata.model;

import com.nolis.commondata.enums.ProductType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Search implements Serializable {

    private String id;
    private String userId;
    private ProductType productType;
    private String productId;
    private String query;
    private String category;
    private String searchLocation;
    private Integer pageSize;
    private Integer page;
    private Boolean inStockOnly;
}