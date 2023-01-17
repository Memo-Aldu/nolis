package com.nolis.registeredproudctsearch.service.producer;

import com.nolis.commondata.dto.RegisteredSearchDTO;
import com.nolis.registeredproudctsearch.dto.UserSearchInfoDTO;
import com.nolis.registeredproudctsearch.model.Search;
import com.nolis.registeredproudctsearch.repository.SearchRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service @Slf4j
@AllArgsConstructor
public class SearchServiceImp implements SearchService {
    private SearchRepository searchRepository;

    @Override
    public Search findSearchesByProductId(String productId) {
        Optional<Search> search = searchRepository.findSearchesByProductId(productId);
        if(search.isPresent()) {
            return search.get();
        }
        log.warn("Search not found for product id: " + productId);
        return null;
    }

    @Override
    public Search saveSearch(RegisteredSearchDTO registeredSearch) {
        Optional<Search> searchOptional = searchRepository
                .findSearchesByProductId(registeredSearch.getProduct().getProductId());
        if(searchOptional.isPresent()) {
            log.warn("Search already exists for product id: {} \nGoing to update it"
                    , registeredSearch.getProduct().getProductId());
             return updateSearch(registeredSearch);
        }
        log.info("Saving search for product id: " + registeredSearch.getProduct().getProductId());
        ArrayList<UserSearchInfoDTO> usersSearchInfo = new ArrayList<>();
        usersSearchInfo.add(new UserSearchInfoDTO(
                registeredSearch.getUserEmail(),
                registeredSearch.getSearchLocation(),
                100));
        return searchRepository.save(
                Search.builder()
                        .usersSearchInfo(usersSearchInfo)
                        .product(registeredSearch.getProduct()).build());
    }

    @Override
    public Search updateSearch(RegisteredSearchDTO registeredSearch) {
        Optional<Search> searchOptional = searchRepository
                .findSearchesByProductId(registeredSearch.getProduct().getProductId());
        if(searchOptional.isPresent()) {
            Search searchToUpdate = searchOptional.get();
            searchToUpdate.setProduct(registeredSearch.getProduct());
            searchToUpdate.setProduct(registeredSearch.getProduct());
            searchToUpdate.setUsersSearchInfo(
                    updateUserSearchInfo(searchToUpdate.getUsersSearchInfo(), registeredSearch));
            log.info("Updating search for product id: " + searchToUpdate.getProduct().getProductId());
            return searchRepository.save(searchToUpdate);
        }
        log.warn("Search not found for product id: " + registeredSearch.getProduct().getProductId());
        return null;
    }

    @Override
    public void deleteSearchByProductId(String productId) {
        searchRepository.deleteSearchByProductId(productId);
    }

    @Override
    public void removeUserFromSearch(RegisteredSearchDTO registeredSearch) {
        Optional<Search> searchOptional = searchRepository
                .findSearchesByProductId(registeredSearch.getProduct().getProductId());
        if(searchOptional.isPresent()) {
            log.info("Removing user {} from product search {}: ",
                    registeredSearch.getUserEmail(), registeredSearch.getProduct().getProductId());
            Search searchToUpdate = searchOptional.get();
            searchToUpdate.setUsersSearchInfo(
                    removeUserFromSearch(searchToUpdate.getUsersSearchInfo(), registeredSearch));
            if(searchToUpdate.getUsersSearchInfo().size() == 0) {
                log.info("No more users for product search {}, deleting it", registeredSearch.getProduct().getProductId());
                searchRepository.delete(searchToUpdate);
            } else {
                log.info("Updating product search {}", registeredSearch.getProduct().getProductId());
                searchRepository.save(searchToUpdate);
            }

        }
    }

    private ArrayList<UserSearchInfoDTO> removeUserFromSearch(
            ArrayList<UserSearchInfoDTO> usersSearchInfo, RegisteredSearchDTO registeredSearch) {
        usersSearchInfo.removeIf(userSearchInfoDTO ->
                userSearchInfoDTO.getUserEmail().equals(registeredSearch.getUserEmail()));
        return usersSearchInfo;
    }

    private ArrayList<UserSearchInfoDTO> updateUserSearchInfo(ArrayList<UserSearchInfoDTO> usersSearchInfo,
            RegisteredSearchDTO registeredSearch) {
        for(UserSearchInfoDTO userSearchInfo : usersSearchInfo) {
            // update the user search info for a given user
            if(userSearchInfo.getUserEmail().equals(registeredSearch.getUserEmail())) {
                userSearchInfo.setSearchLocation(registeredSearch.getSearchLocation());
                userSearchInfo.setWantedPrice(100);
                return usersSearchInfo;
            }
        }
        // add a new user search info
        usersSearchInfo.add(new UserSearchInfoDTO(
                registeredSearch.getUserEmail(),
                registeredSearch.getSearchLocation(),
                100));
        return usersSearchInfo;
    }
}
