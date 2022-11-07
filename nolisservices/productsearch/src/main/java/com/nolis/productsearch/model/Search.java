package com.nolis.productsearch.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Search {

    private String id;
    private String userId;
    private String query;
    private String category;
    private String searchLocation;
    private String pageSize;
    private String page;
    private String inStockOnly;


}