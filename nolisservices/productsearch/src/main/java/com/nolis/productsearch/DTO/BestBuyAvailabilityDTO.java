package com.nolis.productsearch.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyAvailabilityDTO {
    @JsonProperty("availabilities")
    private ArrayList<ProductAvailability> productsAvailable;


    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class ProductAvailability {
        private PickUpInfo pickup;
        private Shipping shipping;
        private String sku;
        private String sellerId;
        private String saleChannelExclusivity;
        private Boolean scheduledDelivery;
    }

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    public static class PickUpInfo {
        private String status;
        private Boolean purchasable;
        private ArrayList<Location> locations;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class Location {
        private String name;
        private String locationKey;
        private Integer quantityOnHand;
        private Boolean isReservable;
        private Boolean hasInventory;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class Shipping {
        private String status;
        private Integer quantityRemaining;
        private Boolean purchasable;
        private Boolean isFreeShippingEligible;
        private Boolean isBackorderable;
    }
}
