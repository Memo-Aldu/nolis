package com.nolis.authenticationserver.security;

import lombok.Data;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.function.Predicate;


@Component @Data
public class EndpointConfig {
    private final String AUTH_PREFIX = "/api/v1/auth/";
    String[] openEndpoints = new String[]{
            AUTH_PREFIX+"login", AUTH_PREFIX+"register",
            AUTH_PREFIX+"token/refresh", AUTH_PREFIX+"token/validate",
            AUTH_PREFIX+"authenticate", AUTH_PREFIX+"has-authority"};

    String[] userEndpoints = new String[]{
            AUTH_PREFIX+"user/profile", AUTH_PREFIX+"user/update",
            AUTH_PREFIX+"user/delete", AUTH_PREFIX+"user/logout",
            AUTH_PREFIX+"user/"};
    String[] adminEndpoints =  new String[]{
            AUTH_PREFIX+"user/get-page/**", AUTH_PREFIX+"user/save/**",
            AUTH_PREFIX+"user/update/**", AUTH_PREFIX+"user/delete/**",
            AUTH_PREFIX+"role/get", AUTH_PREFIX+"user/add-role", AUTH_PREFIX+"user/roles",
            AUTH_PREFIX+"user/remove-role", AUTH_PREFIX+"role/get-page/**", AUTH_PREFIX+"role/save/**",
    };

    String[] superAdminEndpoints = new String[]{
            AUTH_PREFIX+"**"
    };

    public Predicate<HttpServletRequest> isSecured =
            request -> Arrays.stream(openEndpoints).toList()
                    .stream()
                    .noneMatch(uri -> request.getRequestURL().toString().contains(uri));
}
