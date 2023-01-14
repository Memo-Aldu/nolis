package com.nolis.searchregistry;

import com.nolis.searchregistry.model.RegisteredSearch;
import com.nolis.searchregistry.service.producer.KafkaProducer;
import com.nolis.searchregistry.service.producer.RegistrySearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableScheduling
@EnableEurekaClient @SpringBootApplication
@AllArgsConstructor @Slf4j
public class SearchRegistryApplication {
    private final KafkaProducer kafkaService;
    private final RegistrySearchService registrySearchService;
    public static void main(String[] args){
        SpringApplication.run(SearchRegistryApplication.class, args);
    }

    @Async
    @Scheduled(fixedRate = 20000) // 20 seconds
    public void scheduleFixedRateTask() throws InterruptedException {
        List<RegisteredSearch> registeredSearches = registrySearchService
                .getAllRegisteredSearch();
        if(registeredSearches != null && !registeredSearches.isEmpty()){
            log.info("Scheduled task to publish {} registered searches to kafka topic",
                    registeredSearches.size());
            registeredSearches.forEach(kafkaService::publishMessage);
        } else {
            log.info("No registered searches to publish to kafka topic");
        }

    }

    @KafkaListener(topics = "search-registry-topic", groupId = "my_group_id_1")
    public void getMessage(RegisteredSearch message){
        System.out.println("Message received: " + message);
    }
}