package com.nolis.productsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@ConfigurationPropertiesScan
public class ProductSearchApplication {
    public static void main(String[] args){
        SpringApplication.run(ProductSearchApplication.class, args);
    }


}
