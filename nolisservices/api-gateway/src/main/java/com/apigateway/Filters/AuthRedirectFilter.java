package com.apigateway.Filters;

import com.apigateway.DTO.CustomHttpResponseDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
public class AuthRedirectFilter extends AbstractGatewayFilterFactory<AuthRedirectFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthRedirectFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // redirect to authentication server, if user is authenticated then redirect to the requested url, else redirect to login page
        return (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey("Authorization")) {
                try {
                    return onError(exchange, "No Authorization Header Provided"
                            , HttpStatus.UNAUTHORIZED, config);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            System.out.println("token: " + token);
            return webClientBuilder.build()
                    .post()
                    .uri("http://authentication-server-service/api/v1/auth/authenticate")
                    .header("Authorization", token)
                    .retrieve().bodyToMono(CustomHttpResponseDTO.class)
                     .flatMapMany(response -> {
                         System.out.println("response: " + response);
                         //TODO: find how to respond with the server response
                            if ((response.getStatus() == HttpStatus.OK)) {

                                return chain.filter(exchange
                                        .mutate()
                                        .request(exchange.getRequest().mutate().header("Authorization", token).build())
                                        .build());
                            } else {
                                //TODO return json object response
                             return Mono.defer(() -> {
                                 ServerHttpResponse serverHttpResponse = exchange.getResponse();
                                 serverHttpResponse.getHeaders().set("Content-Type", "application/json");
                                 serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                                 DataBuffer buffer = exchange.getResponse()
                                         .bufferFactory().wrap(response.toString().getBytes());
                                 return serverHttpResponse.writeWith(Flux.just(buffer));
                             });
                            }
                      }).then();
        };
    }
    private Mono<Void> onError(ServerWebExchange exchange,
                               String err, HttpStatus httpStatus, Config config) throws JsonProcessingException {
        ServerHttpResponse response = exchange.getResponse();
        String json = config.MAPPER.writeValueAsString(
                new CustomHttpResponseDTO(
                        false,
                        System.currentTimeMillis(),
                        httpStatus,
                        new HashMap<>(),
                        err
                )
        );
        response.setStatusCode(httpStatus);
        DataBuffer buffer = response
                .bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        response.getHeaders().add("Content-Type", "application/json");
        return response.writeWith(Mono.just(buffer));
    }
    @Getter
    public static class Config {
        // Put the configuration properties for your filter here
        private final ObjectMapper MAPPER = new ObjectMapper();

        public Config() {
            MAPPER.setVisibility(MAPPER.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        }
    }
}
