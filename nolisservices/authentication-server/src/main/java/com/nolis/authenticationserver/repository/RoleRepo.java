package com.nolis.authenticationserver.repository;

import com.nolis.authenticationserver.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findRoleByAuthority(String authority);
    Optional<Role> findRoleByIdOrAuthority(String id, String authority);
    void deleteRoleByAuthorityOrId(String authority, String id);
    boolean existsRoleByAuthorityOrId(String authority, String id);


}
