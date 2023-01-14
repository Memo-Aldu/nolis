package com.nolis.commondata.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class RegisteredSearchDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4525755174865964836L;

    private String id;
    private String userId;
    private String userEmail;
    private String searchLocation;
    private RegisteredProduct product;
    private Boolean isFound;
    private Boolean isErrored;
}
