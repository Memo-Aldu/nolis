package com.nolis.registeredproudctsearch.service.producer;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import com.nolis.registeredproudctsearch.model.Search;

import java.util.ArrayList;

public interface SearchService {
    Search findSearchesByProductId(String productId);
    Search saveSearch(RegisteredSearchDTO registeredSearch);
    Search updateSearch(RegisteredSearchDTO registeredSearch);
    void deleteSearchByProductId(String productId);
    void removeUserFromSearch(RegisteredSearchDTO registeredSearch);
}
