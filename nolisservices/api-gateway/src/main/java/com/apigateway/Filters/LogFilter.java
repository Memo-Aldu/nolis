package com.apigateway.Filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogFilter extends AbstractGatewayFilterFactory<LogFilter.Config> {

    public LogFilter() {
        super(Config.class);
    }

        @Override
        public GatewayFilter apply(Config config) {

            return (exchange, chain) -> {
                log.info("Request Path: {}", exchange.getRequest().getPath());
                return chain.filter(exchange);
            };
        }

        public static class Config {
            // Put the configuration properties
        }
}
