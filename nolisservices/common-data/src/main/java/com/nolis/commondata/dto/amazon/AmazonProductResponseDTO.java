package com.nolis.commondata.dto.amazon;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter @ToString @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AmazonProductResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964833L;
    @JsonProperty("asin")
    private String asin;
    @JsonProperty("html")
    private String html;
    @JsonProperty("index")
    private Integer index;

}
