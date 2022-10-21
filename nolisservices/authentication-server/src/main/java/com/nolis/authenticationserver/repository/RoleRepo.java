package com.nolis.authenticationserver.repository;

import com.nolis.authenticationserver.modal.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends MongoRepository<Role, Long> {
    Optional<Role> findRoleByName(String name);
}
