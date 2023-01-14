package com.nolis.searchregistry.service.producer;

import com.nolis.searchregistry.model.RegisteredSearch;

public interface KafkaProducer {
    public void publishMessage(RegisteredSearch registrySearch);
}
