package com.nolis.searchregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SearchRegistryApplication {
    public static void main(String[] args){
        SpringApplication.run(SearchRegistryApplication.class, args);
    }
}