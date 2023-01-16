package com.nolis.searchregistry.service.producer;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import com.nolis.searchregistry.model.RegisteredSearch;

public interface KafkaProducer {
    public void publishMessageSearchRegistry(RegisteredSearchDTO registrySearch);
    public void publishMessageDeletedSearch(RegisteredSearchDTO registrySearch);
}
