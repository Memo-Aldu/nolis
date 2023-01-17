package com.nolis.registeredproudctsearch;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import com.nolis.registeredproudctsearch.service.producer.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import static com.nolis.commondata.constants.Kafka.DELETED_SEARCH_TOPIC;
import static com.nolis.commondata.constants.Kafka.SEARCH_REGISTRY_TOPIC;

@EnableEurekaClient
@SpringBootApplication
@ConfigurationPropertiesScan
@AllArgsConstructor @Slf4j
public class RegisteredProductSearchApplication {
    private final SearchService searchService;
    public static void main(String[] args) {
        SpringApplication.run(RegisteredProductSearchApplication.class, args);
    }

    @KafkaListener(topics = SEARCH_REGISTRY_TOPIC, groupId = "registered-product-search-group-1")
    public void listen(RegisteredSearchDTO data) {
        log.info("Message received: " + data);
        if(isValidMessage(data)){
            searchService.saveSearch(data);
        } else {
            log.info("Message received is not valid");
        }
    }

    @KafkaListener(topics = DELETED_SEARCH_TOPIC, groupId = "registered-product-search-group-2")
    public void listenDelete(RegisteredSearchDTO data) {
        log.info("Delete Message Received: " + data);
        if(isValidMessage(data)) {
            searchService.removeUserFromSearch(data);
        } else {
            log.info("Message received is not valid");
        }
    }

    private boolean isValidMessage(RegisteredSearchDTO data) {
        return data.getIsErrored() != null && !data.getIsErrored()
                && data.getProduct() != null && data.getProduct().getProductId() != null
                && data.getUserEmail() != null;

    }
}