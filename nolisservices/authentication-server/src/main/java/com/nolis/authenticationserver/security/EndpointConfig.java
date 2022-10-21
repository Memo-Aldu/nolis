package com.nolis.authenticationserver.security;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component @Data
public class EndpointConfig {
    List<String> openEndpoints = Stream.of
            ("/api/v1/auth/login", "/api/v1/auth/refresh-token",
                    "/api/v1/auth/register", "/api/v1/auth/logout").collect(Collectors.toList());
    List<String> adminEndpoints = Stream.of
            ("/api/v1/auth/user").collect(Collectors.toList());
}
