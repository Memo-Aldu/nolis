package com.nolis.productsearch.DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Getter @ToString
@NoArgsConstructor @AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BestBuyAvailabilityDTO {
    private PickUpInfo pickup;
    private Shipping shipping;
    private String sku;
    private String sellerId;
    private String saleChannelExclusivity;
    private Boolean scheduledDelivery;

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class PickUpInfo {
        private String status;
        private Boolean purchasable;
        private ArrayList<Location> location;
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
