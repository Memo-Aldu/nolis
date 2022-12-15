package com.nolis.productsearch.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration("httpConfigurationBean")
public class HttpConfiguration {

    @Bean
    @LoadBalanced
    @Qualifier("loadBalancedRestTemplate")
    public RestTemplate restTemplate() {
        return getCustomResetTemplate();
    }

    @Bean
    @Qualifier("withoutLoadBalanced")
    public RestTemplate restTemplateWithoutLoadBalancing() {
        return getCustomResetTemplate();
    }

    private RestTemplate getCustomResetTemplate() {
        return com.nolis.commonconfig.security.configuration.HttpConfiguration.customRestTemplate();
    }
}
