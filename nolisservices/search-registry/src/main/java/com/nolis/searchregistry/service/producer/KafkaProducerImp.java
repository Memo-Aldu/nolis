package com.nolis.searchregistry.service.producer;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import com.nolis.searchregistry.model.RegisteredSearch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.nolis.commondata.constants.Kafka.SEARCH_REGISTRY_TOPIC;

@Service
@Slf4j @AllArgsConstructor
public class KafkaProducerImp implements KafkaProducer {
    private final KafkaTemplate<String, RegisteredSearchDTO> kafkaTemplate;

    @Override
    public void publishMessage(RegisteredSearchDTO data) {
        log.info("Publishing to kafka topic: {} with message: {}",
                SEARCH_REGISTRY_TOPIC, data);
        Message<RegisteredSearchDTO> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, SEARCH_REGISTRY_TOPIC)
                .build();
        this.kafkaTemplate.send(message);
    }
}
