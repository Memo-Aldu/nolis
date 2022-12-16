package com.nolis.authenticationserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfig (
     String secretKey,
     String tokenPrefix,
     String accessHeader,
     String refreshHeader,
     Integer tokenExpirationAfterDays,
     Integer refreshTokenExpirationAfterDays
) {
}
