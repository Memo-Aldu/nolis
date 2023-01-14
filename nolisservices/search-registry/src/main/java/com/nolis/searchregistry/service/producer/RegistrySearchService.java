package com.nolis.searchregistry.service.producer;

import com.nolis.searchregistry.model.RegisteredSearch;

import java.util.ArrayList;
import java.util.List;

public interface RegistrySearchService {
    public RegisteredSearch saveRegisteredSearch(RegisteredSearch registeredSearch);
    public RegisteredSearch updateRegisteredSearch(RegisteredSearch registeredSearch);
    public ArrayList<RegisteredSearch> getRegisteredSearchesByUserIdOrUserEmail(String userId, String userEmail);
    public ArrayList<RegisteredSearch> getRegisteredSearchesByProductId(String productId);
    public List<RegisteredSearch> getAllRegisteredSearch();
    public RegisteredSearch getRegisteredSearchByIdAndUserEmail(String id, String userEmail);
    public void deleteRegisteredSearchByIdAndUserEmail(String id, String userEmail);
    public void deleteRegisteredSearchesByUserIdOrUserEmail(String userId, String userEmail);
    public void deleteRegisteredSearchesByProductId(String productId);

}
