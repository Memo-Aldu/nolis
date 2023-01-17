package com.nolis.registeredproudctsearch.model;

import com.nolis.commondata.dto.RegisteredProduct;
import com.nolis.commondata.enums.ProductType;
import com.nolis.registeredproudctsearch.dto.UserSearchInfoDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "app_registered_Product_search")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class Search {
    @Id
    private String id;
    private RegisteredProduct product;
    private ArrayList<UserSearchInfoDTO> usersSearchInfo;

}
