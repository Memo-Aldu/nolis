package com.nolis.productsearch.DTO.bestbuy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyLocationDTO {
    @JsonProperty("locations")
    private ArrayList<LocationRoot> location;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
    public static class LocationRoot {
        @JsonProperty("loc")
        private LocationInfo location;
        @JsonProperty("url")
        private String url;
    }
    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor @ToString
    public static class LocationInfo {
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
