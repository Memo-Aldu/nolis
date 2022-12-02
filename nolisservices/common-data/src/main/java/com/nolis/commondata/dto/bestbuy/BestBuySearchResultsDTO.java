package com.nolis.commondata.dto.bestbuy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuySearchResultsDTO {
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
    private Integer pageSize;
    ArrayList<BestBuyProductResponseDTO.BestBuyProduct> products;
}