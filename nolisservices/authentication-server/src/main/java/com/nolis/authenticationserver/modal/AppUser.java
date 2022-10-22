package com.nolis.authenticationserver.modal;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Document("app_users")
 @NoArgsConstructor
public class AppUser implements UserDetails {
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
    private Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

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
        this.authorities = roles.stream().map(
                role -> new SimpleGrantedAuthority(
                        role.getName())).collect(Collectors.toList()
        );
    }

    public void setAuthorities(Collection<Role> roles) {
        this.authorities = roles.stream().map(
                role -> new SimpleGrantedAuthority(
                        role.getName())).collect(Collectors.toList()
        );
    }

    public void addRole(Role role) {
        this.authorities.add(new SimpleGrantedAuthority(role.getName()));
    }

    public void removeRole(Role role) {
        this.authorities.remove(new SimpleGrantedAuthority(role.getName()));
    }


}
