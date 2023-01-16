package com.nolis.registeredproudctsearch.dto;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserSearchInfoDTO {
    private String userId;
    private String userEmail;
}
