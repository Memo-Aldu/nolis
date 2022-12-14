package com.nolis.authenticationserver.repository;

import com.nolis.authenticationserver.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findRoleByName(String name);
    Optional<Role> findRoleByIdOrName(String id, String name);
    void deleteRoleByNameOrId(String name, String id);
    boolean existsRoleByNameOrId(String name, String id);


}
