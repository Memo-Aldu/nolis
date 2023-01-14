package com.nolis.searchregistry.service.producer;

import com.nolis.searchregistry.model.RegisteredSearch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j @AllArgsConstructor
public class KafkaProducerImp implements KafkaProducer {
    private static final String TOPIC = "search-registry-topic";
    private final KafkaTemplate<String, RegisteredSearch> kafkaTemplate;

    @Override
    public void publishMessage(RegisteredSearch data) {
        log.info("Publishing to kafka topic: {} with message: {}", TOPIC, data);
        Message<RegisteredSearch> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();
        this.kafkaTemplate.send(message);
    }
}
