package com.nolis.productsearch.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyResponseDTO {
    private Integer currentPage;
    private Integer total;
    private Integer totalPages;
    private Integer pageSize;
    private BestBuyDTO[] products;
    private String[] paths;
    private String facets;
    private String lastSearchDate;
    private String relatedQueries;
    private String[] sscs;
    private String relatedCategories;
    private String selectedFacets;
    private String resources;
    private String redirectUrl;
    private String promotions;
    private Boolean hasBrandStore;
    private String productStatusCode;
    private String sscStatusCode;
    private String StringBrand;
    private String[] breadcrumb;
}
