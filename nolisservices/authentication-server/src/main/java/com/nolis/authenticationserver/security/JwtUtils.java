package com.nolis.authenticationserver.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nolis.authenticationserver.modal.AppUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collection;

@AllArgsConstructor @Component @Slf4j
public class JwtUtils {
    public final JwtConfig jwtConfig;

    public Algorithm algorithm() {
        return Algorithm.HMAC256(jwtConfig.secretKey());
    }

    public String createToken(AppUser appUser, HttpServletRequest request) {
        return JWT.create()
                .withSubject(appUser.getEmail())
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(jwtConfig.tokenExpirationAfterDays())))
                .withClaim("roles", appUser.getAuthorities().stream().toList())
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
        JWTVerifier verifier = JWT.require(algorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String email = decodedJWT.getSubject();
        Collection<SimpleGrantedAuthority> authorities = decodedJWT
                .getClaim("roles").asList(SimpleGrantedAuthority.class);
        log.info("Email is : {} \n Authorization are : {}", email, authorities);
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

}
