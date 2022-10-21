package com.nolis.authenticationserver.security;

import lombok.Data;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.function.Predicate;


@Component @Data
public class EndpointConfig {
    String[] openEndpoints = new String[]{
            "/api/v1/auth/login", "/api/v1/auth/register",
                    "/api/v1/auth/logout", "/api/v1/auth/token/refresh/"};
    String[] adminEndpoints =  new String[]{
            "/api/v1/auth/user/**",
    };

    public Predicate<HttpServletRequest> isSecured =
            request -> Arrays.stream(openEndpoints).toList()
                    .stream()
                    .noneMatch(uri -> request.getRequestURL().toString().contains(uri));
}
