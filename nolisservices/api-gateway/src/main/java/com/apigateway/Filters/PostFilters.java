package com.apigateway.Filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component @Slf4j
public class PostFilters extends AbstractGatewayFilterFactory<PostFilters.Config> {

    public PostFilters() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                // json response
                response.getHeaders().add("Content-Type", "application/json");
                log.info("Response Status: {}", response.getStatusCode());
            }));
        };
    }

    public static class Config {
        // Put the configuration properties
    }
}
