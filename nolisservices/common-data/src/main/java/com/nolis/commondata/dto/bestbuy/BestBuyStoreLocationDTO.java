package com.nolis.commondata.dto.bestbuy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyStoreLocationDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964839L;
    @JsonProperty("locations")
    private ArrayList<LocationRoot> location;

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor @ToString
    public static class LocationRoot implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035974839L;
        @JsonProperty("loc")
        private LocationInfo location;
        @JsonProperty("url")
        private String url;
    }
    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor @ToString
    public static class LocationInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035974840L;
        @JsonProperty("address1")
        private String address;
        @JsonProperty("city")
        private String city;
        @JsonProperty("corporateCode")
        private String locationCode;
        @JsonProperty("countryName")
        private String country;
        @JsonProperty("stateName")
        private String state;
    }
}