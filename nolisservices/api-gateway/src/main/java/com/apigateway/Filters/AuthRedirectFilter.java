package com.apigateway.Filters;

import com.apigateway.DTO.CustomHttpResponseDTO;
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
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            return webClientBuilder.build()
                    .post()
                    .uri("http://authentication-server-service/api/v1/auth/authenticate")
                    .header("Authorization", token)
                    .retrieve().bodyToMono(CustomHttpResponseDTO.class)
                     .flatMap(response -> {
                         //TODO: find how to respond with the server response
                            if ((response.getStatus() == HttpStatus.OK.value())) {

                                return chain.filter(exchange
                                        .mutate()
                                        .request(exchange.getRequest().mutate().header("Authorization", token).build())
                                        .build());
                            } else {
                                //TODO return json object response
                             return Mono.defer(() -> {
                                 ServerHttpResponse serverHttpResponse = exchange.getResponse();
                                 serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                                 DataBuffer buffer = exchange.getResponse()
                                         .bufferFactory().wrap(response.toString().getBytes());
                                 return serverHttpResponse.writeWith(Flux.just(buffer));
                             });
                            }
                      });
        };
    }
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    public static class Config {
        // Put the configuration properties for your filter here
    }
}
