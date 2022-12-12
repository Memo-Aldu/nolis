package com.nolis.searchregistry.configuration;

import com.nolis.commonconfig.security.service.AuthService;
import com.nolis.commonconfig.security.service.AuthServiceImp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfiguration {

    @Bean
    public AuthService authServiceBean(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
        return new AuthServiceImp(
                restTemplate
        );
    }
}
