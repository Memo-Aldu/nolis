package com.nolis.productsearch.DTO.amazon;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @ToString @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AmazonProductResponseDTO {
    @JsonProperty("asin")
    private String asin;
    @JsonProperty("html")
    private String html;
    @JsonProperty("index")
    private Integer index;

}
