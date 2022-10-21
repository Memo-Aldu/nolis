package com.nolis.authenticationserver.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor @Component @Slf4j
public class JwtUtils {
    public final JwtConfig jwtConfig;
    public final AppUserService appUserService;
    public Algorithm algorithm() {
        return Algorithm.HMAC256(jwtConfig.secretKey());
    }

    public String createToken(AppUser appUser, HttpServletRequest request) {
        log.info("Creating token for user : {}", appUser);
        return JWT.create()
                .withSubject(appUser.getEmail())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(jwtConfig.tokenExpirationAfterDays())))
                .withClaim("roles", appUser.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm());
    }

    public String createRefreshToken(AppUser appUser, HttpServletRequest request) {
        return JWT.create()
                .withSubject(appUser.getEmail())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(jwtConfig.refreshTokenExpirationAfterDays())))
                .sign(algorithm());
    }

    public Authentication authenticateToken(String authorizationHeader) {
        String token = authorizationHeader.substring(jwtConfig.tokenPrefix().length());
        DecodedJWT decodedJWT = decodeJWT(token);
        String email = decodedJWT.getSubject();
        Collection<SimpleGrantedAuthority> authorities = decodedJWT
                .getClaim("roles").asList(SimpleGrantedAuthority.class);
        log.info("Email is : {} authorities are : {}", email, authorities);
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    public String createTokenWithRefreshToken(String refreshToken, HttpServletRequest request) {
        DecodedJWT decodedJWT = decodeJWT(refreshToken);
        String email = decodedJWT.getSubject();
        AppUser appUser = appUserService.getUserByEmail(email);
        if(appUser != null) {
            log.info("Creating a new token for user " +
                    "id is : {} and authorities {}", appUser.getId(), appUser.getAuthorities());
            return createToken(appUser, request);
        } else{
            log.info("User not found");
            throw new UsernameNotFoundException("User not found with email : " + email);
        }
    }

    public boolean isInvalid(String token) {
        return this.isTokenExpired(token);
    }

    private DecodedJWT decodeJWT(String token) {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    }

    private boolean isTokenExpired(String token) {
        return this.decodeJWT(token).getExpiresAt().before(java.sql.Date.valueOf(LocalDate.now()));
    }

}
