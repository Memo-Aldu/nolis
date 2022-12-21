package com.nolis.authenticationserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;

@Data
@Document("app_roles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role implements Serializable, GrantedAuthority {
    @Serial
    private static final long serialVersionUID = 4525755174035964836L;
    @Id
    private String id;
    @Indexed(unique = true)
    private String authority;

    public Role(String authorityName) {
        this.authority = authorityName;
    }


    @Override
    public String getAuthority() {
        return this.authority ;
    }
}
