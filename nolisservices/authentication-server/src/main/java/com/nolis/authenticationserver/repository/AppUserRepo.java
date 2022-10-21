package com.nolis.authenticationserver.repository;

import com.nolis.authenticationserver.modal.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AppUserRepo extends MongoRepository<AppUser, Long> {
    Optional<AppUser> findAppUserById(String id);
    Optional<AppUser> findAppUserByEmail(String email);
}