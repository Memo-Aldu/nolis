package com.nolis.searchregistry.repository;

import com.nolis.commondata.dto.RegisteredProduct;
import com.nolis.searchregistry.model.RegisteredSearch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface RegisteredSearchRepo extends MongoRepository<RegisteredSearch, String> {

    Optional<RegisteredSearch> findRegisteredSearchByIdAndUserEmail(String id, String userEmail);
    @Query("{ 'product.productId' : ?0, 'userEmail' : ?1 }")
    Optional<RegisteredSearch> findRegisteredSearchByProductIdAndUserEmail(String productId, String userEmail);
    Optional<ArrayList<RegisteredSearch>> findRegisteredSearchesByUserEmail(String userEmail);
    @Query("{ 'product.productId' : ?0 }")
    Optional<ArrayList<RegisteredSearch>> findRegisteredSearchesByProductId(String productId);

}
