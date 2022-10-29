package com.nolis.productsearch.Model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Search {

    private UUID uuid;
    private UUID userId;
    private String query;
    private String searchLocation;
    private Integer pageSize;
    private Integer page;


}