package com.nolis.authenticationserver.modal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("app_roles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    public Role(String roleName) {
        this.name = roleName;
    }

    public boolean isValidEntity() {
        return name != null;
    }

}
