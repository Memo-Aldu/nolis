package com.nolis.commondata.dto.amazon;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AmazonSearchResultsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964835L;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
    private Integer pageSize;
    ArrayList<AmazonProductDTO> products;
}
