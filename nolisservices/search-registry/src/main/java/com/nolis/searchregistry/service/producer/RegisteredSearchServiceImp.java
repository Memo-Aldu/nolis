package com.nolis.searchregistry.service.producer;

import com.nolis.commondata.exception.AppEntityAlreadyExistException;
import com.nolis.commondata.exception.AppEntityNotFoundException;
import com.nolis.searchregistry.model.RegisteredSearch;
import com.nolis.searchregistry.repository.RegisteredSearchRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor @Service @Slf4j
public class RegisteredSearchServiceImp implements RegistrySearchService {
    private final RegisteredSearchRepo registeredSearchRepo;

    @Override
    public RegisteredSearch saveRegisteredSearch(RegisteredSearch registeredSearch) {
        log.info("Saving registered search: {}", registeredSearch);
        Optional<RegisteredSearch> optionalRegisteredSearch = registeredSearchRepo
                .findRegisteredSearchByProductIdAndUser(
                        registeredSearch.getProduct().getProductId(),
                        registeredSearch.getUserId(),
                        registeredSearch.getUserEmail());
        if(optionalRegisteredSearch.isPresent()) {
            throw new AppEntityAlreadyExistException("Registered search already exists");
        }
        return registeredSearchRepo.save(registeredSearch);
    }

    @Override
    public RegisteredSearch updateRegisteredSearch(RegisteredSearch registeredSearch) {
        log.info("Updating registered search: {}", registeredSearch);
        Optional<RegisteredSearch> optionalRegisteredSearch = registeredSearchRepo
                .findRegisteredSearchByIdAndUserEmail(
                        registeredSearch.getId(), registeredSearch.getUserEmail());
        if(optionalRegisteredSearch.isEmpty()) {
            optionalRegisteredSearch = registeredSearchRepo
                    .findRegisteredSearchByProductIdAndUser(
                            registeredSearch.getProduct().getProductId(),
                            registeredSearch.getUserId(),
                            registeredSearch.getUserEmail()
                    );
            if(optionalRegisteredSearch.isEmpty()) {
                throw new AppEntityNotFoundException("Registered search not found");
            }
        }
        return registeredSearchRepo.save(registeredSearch);
    }

    @Override
    public ArrayList<RegisteredSearch> getRegisteredSearchesByUserIdOrUserEmail(String userId, String userEmail) {
        log.info("Getting registered searches by user id: {} or user email: {}", userId, userEmail);
        Optional<ArrayList<RegisteredSearch>> registeredSearches = registeredSearchRepo
                .findRegisteredSearchesByUserIdOrUserEmail(userId, userEmail);
        if(registeredSearches.isEmpty()) {
            log.info("No registered searches found for user id: {} or user email: {}", userId, userEmail);
            return new ArrayList<>();
        }
        return registeredSearches.get();
    }

    @Override
    public ArrayList<RegisteredSearch> getRegisteredSearchesByProductId(String productId) {
        log.info("Getting registered searches by product id: {}", productId);
        Optional<ArrayList<RegisteredSearch>> registeredSearches = registeredSearchRepo
                .findRegisteredSearchesByProductId(productId);
        if(registeredSearches.isEmpty()) {
            log.info("No registered searches found for product id: {}", productId);
            return new ArrayList<>();
        }
        return registeredSearches.get();
    }

    @Override
    public List<RegisteredSearch> getAllRegisteredSearch() {
        return registeredSearchRepo.findAll();
    }

    @Override
    public RegisteredSearch getRegisteredSearchByIdAndUserEmail(String id, String userEmail) {
        log.info("Getting registered search by id: {}", id);
        Optional<RegisteredSearch> registeredSearch = registeredSearchRepo
                .findRegisteredSearchByIdAndUserEmail(id, userEmail);
        if (registeredSearch.isEmpty()) {
            log.info("No registered search found for id: {}", id);
            throw new AppEntityNotFoundException("Registered search not found");
        }
        return registeredSearch.get();
    }

    @Override
    public void deleteRegisteredSearchByIdAndUserEmail(String id, String userEmail) {
        log.info("Deleting registered search with id: {}", id);
        RegisteredSearch registeredSearch = getRegisteredSearchByIdAndUserEmail(id, userEmail);
        registeredSearchRepo.delete(registeredSearch);
    }

    @Override
    public void deleteRegisteredSearchesByUserIdOrUserEmail(String userId, String userEmail) {
        log.info("Deleting registered search with userId {} or userEmail {}", userId, userEmail);
        ArrayList<RegisteredSearch> registeredSearches = getRegisteredSearchesByUserIdOrUserEmail(userId, userEmail);
        if(registeredSearches.isEmpty()) {
            throw new AppEntityNotFoundException("Registered search not found");
        }
        else {
            registeredSearchRepo.deleteAll(registeredSearches);
        }
    }

    @Override
    public void deleteRegisteredSearchesByProductId(String productId) {
        log.info("Deleting registered search with productId {}", productId);
        ArrayList<RegisteredSearch> registeredSearches = getRegisteredSearchesByProductId(productId);
        if(registeredSearches.isEmpty()) {
            throw new AppEntityNotFoundException("Registered search not found");
        }
        else {
            registeredSearchRepo.deleteAll(registeredSearches);
        }
    }
}
