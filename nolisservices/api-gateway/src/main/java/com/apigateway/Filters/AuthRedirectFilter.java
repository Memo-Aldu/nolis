package com.apigateway.Filters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nolis.commondata.dto.CustomHttpResponseDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component @Slf4j
public class AuthRedirectFilter extends AbstractGatewayFilterFactory<AuthRedirectFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthRedirectFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = exchange.getRequest().getHeaders();
            return webClientBuilder.build()
                    .post()
                    .uri("http://authentication-server-service/api/v1/auth/authenticate")
                    .headers(h -> h.addAll(headers))
                    .header("X-Request-Path", path)
                    .body(Mono.empty(), Void.class)
                    .exchangeToMono(clientResponse -> {
                        if(clientResponse.statusCode().is4xxClientError()) {
                            //get body message from response
                            String message = "Authentication Failed";
                            // print headers
                            return onError(response, message,
                                    clientResponse.statusCode(), config,
                                    Map.of(
                                            "error", clientResponse.headers().header("error_type")
                                    )
                            );
                        } else if(clientResponse.statusCode().is5xxServerError()) {
                            //get body message from response
                            String message = "Server Error";
                            // print headers
                            return onError(response, message,
                                    clientResponse.statusCode(), config,
                                    Map.of(
                                            "error", clientResponse.headers().header("error_type")
                                    )
                            );
                        } else {
                            return chain.filter(exchange
                                    .mutate()
                                    .request(exchange.getRequest()
                                            .mutate()
                                            .headers(h -> h.addAll(headers))
                                            .build())
                                    .build());
                        }});
            };
    }
    private Mono<Void> onError(ServerHttpResponse response, String err,
                               HttpStatus httpStatus, Config config, Map<String, Object> data) {
        String json = null;
        try {
            json = config.MAPPER.writeValueAsString(
                    new CustomHttpResponseDTO(
                            false,
                            System.currentTimeMillis(),
                            httpStatus,
                            data,
                            err
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(httpStatus);
        DataBuffer buffer = response
                .bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        response.getHeaders().add("Content-Type", "application/json");
        return response.writeWith(Mono.just(buffer));
    }
    @Getter
    public static class Config {
        private final ObjectMapper MAPPER = new ObjectMapper();

        public Config() {
            MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        }
    }
}
