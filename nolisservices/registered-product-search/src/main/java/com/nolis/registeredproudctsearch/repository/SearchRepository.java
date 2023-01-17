package com.nolis.registeredproudctsearch.repository;

import com.nolis.registeredproudctsearch.model.Search;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SearchRepository extends MongoRepository<Search, String> {
    // select from table where Search.product.id = productId
    @Query("{ 'product.productId' : ?0 }")
    Optional<Search> findSearchesByProductId(String productId);
    @Query("{ 'product.id' : ?0}")
    void deleteSearchByProductId(String productId);

}
