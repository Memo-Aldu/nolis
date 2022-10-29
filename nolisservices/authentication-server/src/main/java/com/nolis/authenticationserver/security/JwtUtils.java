package com.nolis.authenticationserver.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nolis.authenticationserver.exception.AppEntityNotFoundException;
import com.nolis.authenticationserver.exception.InvalidTokenException;
import com.nolis.authenticationserver.modal.AppUser;
import com.nolis.authenticationserver.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor @Component @Slf4j
public class JwtUtils {
    public final JwtConfig jwtConfig;
    public final AppUserService appUserService;

    private final String ISSUER = "Nolis-Authentication-Server";
    public Algorithm algorithm() {
        return Algorithm.HMAC256(jwtConfig.secretKey());
    }

    public String createToken(AppUser appUser) {
        log.info("Creating token for user : {}", appUser);
        return JWT.create()
                .withSubject(appUser.getEmail())
                .withIssuer(ISSUER)
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(jwtConfig.tokenExpirationAfterDays())))
                .withClaim("authorities", appUser.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm());
    }

    public String createRefreshToken(AppUser appUser) {
        log.info("Creating refresh token for user : {}", appUser);
        return JWT.create()
                .withSubject(appUser.getEmail())
                .withIssuer(ISSUER)
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(jwtConfig.refreshTokenExpirationAfterDays())))
                .sign(algorithm());
    }

    public Authentication authenticateToken(String token) {
        try {
            DecodedJWT decodedJWT = decodeAndVerifyJWT(token);
            String email = decodedJWT.getSubject();
            Collection<SimpleGrantedAuthority> authorities = decodedJWT
                    .getClaim("authorities").asList(SimpleGrantedAuthority.class);
            log.info("Email is : {} authorities are : {}", email, authorities);
            return new UsernamePasswordAuthenticationToken(email, null, authorities);
            // catcj InvalidTokenException or SignatureVerificationException
        } catch (Exception e) {
            log.warn("Invalid token : {}", e.getMessage());
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }

    }

    public String createTokenWithRefreshToken(String refreshToken) {
        DecodedJWT decodedJWT = decodeAndVerifyJWT(refreshToken);
        String email = decodedJWT.getSubject();
        AppUser appUser = appUserService.getUserByEmail(email);
        if(appUser != null) {
            log.info("Creating a new token for user " +
                    "id is : {} and authorities {}", appUser.getId(), appUser.getAuthorities());
            return createToken(appUser);
        } else{
            log.info("User not found");
            throw new AppEntityNotFoundException("User not found with email : " + email);
        }
    }

    public String getTokenFromHeader(String authorizationHeader) {
        return authorizationHeader.substring(jwtConfig.tokenPrefix().length());
    }

    public Boolean tokenStartsWithPrefix(String authorizationHeader) {
        return authorizationHeader.startsWith(jwtConfig.tokenPrefix());
    }

    public void isValidToken(String token) {
        try {
            decodeAndVerifyJWT(token);
        } catch (InvalidTokenException e) {
            log.error("Invalid token : {}", e.getMessage());
            throw new InvalidTokenException("Invalid token: " + e.getMessage());
        }
    }

    public String[] getAuthoritiesFromToken(String token) {
        DecodedJWT decodedJWT = decodeAndVerifyJWT(token);
        return decodedJWT.getClaim("authorities").asArray(String.class);
    }

    private DecodedJWT decodeAndVerifyJWT(String token) throws SignatureVerificationException {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    }

}
