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
    // select from table where RegisteredProduct = ? and (UserId = ? or UserEmail = ?)
    @Query("{ 'product.productId' : ?0, $or: [ { 'userId' : ?1 }, { 'userEmail' : ?2 } ] }")
    Optional<RegisteredSearch> findRegisteredSearchByProductIdAndUser(String productId, String userId, String userEmail);
    Optional<ArrayList<RegisteredSearch>> findRegisteredSearchesByUserIdOrUserEmail(String userId, String userEmail);
    // select from table where RegisteredProduct.id = registeredProductId
    @Query("{ 'product.productId' : ?0 }")
    Optional<ArrayList<RegisteredSearch>> findRegisteredSearchesByProductId(String productId);

}
