package com.nolis.registeredproudctsearch;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import static com.nolis.commondata.constants.Kafka.SEARCH_REGISTRY_TOPIC;

@EnableEurekaClient
@SpringBootApplication
public class RegisteredProductSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisteredProductSearchApplication.class, args);
    }

    @KafkaListener(topics = SEARCH_REGISTRY_TOPIC, groupId = "registered-product-search-group-1")
    public void listen(RegisteredSearchDTO data) {
        System.out.println("Received Messasge in group registered-product-search-group: " + data);
    }
}