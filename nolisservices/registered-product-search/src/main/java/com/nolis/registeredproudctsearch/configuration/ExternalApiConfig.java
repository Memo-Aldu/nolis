package com.nolis.registeredproudctsearch.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.external.api")
public record ExternalApiConfig(
        String bestBuyProductUrl,
        String bestBuyLocationUrl,
        String  bestBuyInventoryUrl,
        String amazonProductUrl
) {
}
