package com.nolis.searchregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ProductRegistryApplication {
    public static void main(String[] args){
        SpringApplication.run(ProductRegistryApplication.class, args);
    }
}