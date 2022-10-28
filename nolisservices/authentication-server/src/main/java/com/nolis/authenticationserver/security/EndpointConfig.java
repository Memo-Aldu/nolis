package com.nolis.authenticationserver.security;

import lombok.Data;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.function.Predicate;


@Component @Data
public class EndpointConfig {
    private final String API_PREFIX = "/api/v1/auth/";
    String[] openEndpoints = new String[]{
            API_PREFIX+"login", API_PREFIX+"register",
            API_PREFIX+"token/refresh", API_PREFIX+"token/validate",
            API_PREFIX+"authenticate"};

    String[] userEndpoints = new String[]{
            API_PREFIX+"user/profile", API_PREFIX+"user/update",
            API_PREFIX+"user/delete", API_PREFIX+"user/logout"};
    String[] adminEndpoints =  new String[]{
            API_PREFIX+"user/get-page/**", API_PREFIX+"user/save/**",
            API_PREFIX+"user/update/**", API_PREFIX+"user/delete/**",
            API_PREFIX+"role/get", API_PREFIX+"user/add-role", API_PREFIX+"user/roles",
            API_PREFIX+"user/remove-role", API_PREFIX+"role/get-page/**", API_PREFIX+"role/save/**",
    };

    String[] superAdminEndpoints = new String[]{
            API_PREFIX+"**"
    };

    public Predicate<HttpServletRequest> isSecured =
            request -> Arrays.stream(openEndpoints).toList()
                    .stream()
                    .noneMatch(uri -> request.getRequestURL().toString().contains(uri));
}
