package com.nolis.authenticationserver.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Document("app_users")
@AllArgsConstructor @NoArgsConstructor
public class AppUser {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long discordId;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phone;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;

    @DocumentReference
    private Collection<Role> roles = new ArrayList<>();
}
