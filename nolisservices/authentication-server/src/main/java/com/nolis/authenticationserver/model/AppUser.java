package com.nolis.authenticationserver.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Document("app_users")
@NoArgsConstructor
public class AppUser implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 4525755174865964836L;
    @Id
    private String id;
    @Indexed(unique = true)
    private Long discordId;
    private String username;
    private String password;
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
    private Collection<Role> authorities = new ArrayList<>();

    public AppUser(Long discordId, String username,
                   String password, String email,
                   String phone, boolean isAccountNonExpired,
                   boolean isAccountNonLocked, boolean isCredentialsNonExpired,
                   boolean isEnabled, boolean isEmailVerified, boolean isPhoneVerified,
                   Collection<Role> roles) {
        this.discordId = discordId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.isEmailVerified = isEmailVerified;
        this.isPhoneVerified = isPhoneVerified;
        this.authorities = roles;
    }

    public void setAuthorities(Collection<Role> roles) {
        this.authorities = roles;
    }

    public void addRole(Role role) {
        this.authorities.add(role);
    }

    public void removeRole(Role role) {
        this.authorities.remove(role);
    }

}
