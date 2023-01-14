package com.nolis.searchregistry.helper;


import com.nolis.commonconfig.security.service.AuthService;
import com.nolis.commondata.dto.JWTAuthDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Component @AllArgsConstructor
public class ControllerHelper {
    private AuthService authService;

    public boolean hasAuthority(HttpServletRequest request, String scope) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authService.hasAuthority(authorizationHeader, scope);
    }

    public JWTAuthDTO decodeJWT(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        return authService.decodeJWT(authorizationHeader);
    }

    public HttpHeaders setupResponseHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, "application/json");
        headers.set(AUTHORIZATION, request.getHeader(AUTHORIZATION));
        headers.add("X-Prev-Path", request.getRequestURI());
        headers.add("X-Request-Path", request.getHeader("X-Request-Path"));
        headers.add("X-Request-Id", request.getHeader("X-Request-Id"));
        return headers;
    }
}
