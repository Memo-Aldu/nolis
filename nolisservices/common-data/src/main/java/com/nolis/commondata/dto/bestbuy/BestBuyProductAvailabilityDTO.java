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
public class BestBuyProductAvailabilityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4555735174035964836L;
    @JsonProperty("availabilities")
    private ArrayList<ProductAvailability> productsAvailable;


    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class ProductAvailability implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035964846L;
        private PickUpInfo pickup;
        private Shipping shipping;
        private String sku;
        private String sellerId;
        private String saleChannelExclusivity;
        private Boolean scheduledDelivery;
    }

    @Getter @Setter
    @AllArgsConstructor @NoArgsConstructor
    public static class PickUpInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035964856L;
        private String status;
        private Boolean purchasable;
        private ArrayList<Location> locations;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class Location implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035964866L;
        private String name;
        private String locationKey;
        private Integer quantityOnHand;
        private Boolean isReservable;
        private Boolean hasInventory;
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class Shipping implements Serializable {
        @Serial
        private static final long serialVersionUID = 4555735174035964876L;
        private String status;
        private Integer quantityRemaining;
        private Boolean purchasable;
        private Boolean isFreeShippingEligible;
        private Boolean isBackorderable;
    }
}
