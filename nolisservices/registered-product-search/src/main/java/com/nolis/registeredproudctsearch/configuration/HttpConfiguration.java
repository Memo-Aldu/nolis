package com.nolis.registeredproudctsearch.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration("httpConfigurationBean")
public class HttpConfiguration {

    @Bean
    public RestTemplate restTemplateBean() {
        return com.nolis.commonconfig.security
                .configuration.HttpConfiguration.customRestTemplate();
    }
}
