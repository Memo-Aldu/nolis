package com.nolis.searchregistry.model;

import com.nolis.commondata.dto.RegisteredProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@Document("app_registered_searches")
@NoArgsConstructor
@AllArgsConstructor @Builder
public class RegisteredSearch implements Serializable {

    @Serial
    private static final long serialVersionUID = 4525755174865964836L;

    @Id
    private String id;
    private String userEmail;
    private String searchLocation;
    private RegisteredProduct product;
    private Boolean isFound = false;
    private Boolean isErrored = false;

    public boolean isValidEntity() {

        return userEmail != null && !userEmail.isEmpty()
                && product != null && product.isValidEntity();
    }
}
