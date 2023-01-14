package com.nolis.searchregistry.controller;

import com.nolis.commondata.dto.CustomHttpResponseDTO;
import com.nolis.commondata.dto.JWTAuthDTO;
import com.nolis.commondata.enums.ProductType;
import com.nolis.commondata.exception.BadRequestException;
import com.nolis.commondata.exception.TokenUnauthorizedToScopeException;
import com.nolis.commondata.exception.UnauthorizedTokenException;
import com.nolis.searchregistry.helper.ControllerHelper;
import com.nolis.searchregistry.helper.ResponseHandler;
import com.nolis.searchregistry.model.RegisteredSearch;
import com.nolis.searchregistry.service.producer.RegistrySearchService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("api/v1/search-registry")
public record SearchRegistryController(
        ControllerHelper controllerHelper,
        ResponseHandler responseHandler,
        RegistrySearchService registrySearchService
) {

    // TODO: Need's to be a POST, but POST mapping is not working with @RequestBody
    @GetMapping (value = "/amazon", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CustomHttpResponseDTO> saveAmazonSearch(
            @RequestBody RegisteredSearch registeredSearch, HttpServletRequest request) {
        log.info("New Search Request {}", registeredSearch);
        if(!registeredSearch.isValidEntity()) {
            log.warn("Invalid Search Request {}", registeredSearch);
            throw new BadRequestException("Invalid request body for " + registeredSearch);
        }
        log.info("New Search Request {}", registeredSearch);
        if(controllerHelper.hasAuthority(request,"ROLE_BESTBUY_USER")){
            registeredSearch.getProduct().setProductType(ProductType.Amazon);
            RegisteredSearch savedRegisteredSearch = registrySearchService
                    .saveRegisteredSearch(registeredSearch);
            return getCustomHttpResponseEntity(registeredSearch, request);
        }
        else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    // TODO: Need's to be a POST, but POST mapping is not working with @RequestBody
    @GetMapping(value = "/best-buy")
    public ResponseEntity<CustomHttpResponseDTO> saveBestBuySearch(@RequestBody RegisteredSearch registeredSearch, HttpServletRequest request) {
        log.info("New Search Request {}", registeredSearch);
        if(!registeredSearch.isValidEntity()) {
            log.warn("Invalid Search Request {}", registeredSearch);
            throw new BadRequestException("Invalid request body for " + registeredSearch);
        }
        log.info("New Search Request {}", registeredSearch);
        if(controllerHelper.hasAuthority(request,"ROLE_BESTBUY_USER")){
            registeredSearch.getProduct().setProductType(ProductType.BestBuy);
            RegisteredSearch savedRegisteredSearch = registrySearchService
                    .saveRegisteredSearch(registeredSearch);
            return getCustomHttpResponseEntity(registeredSearch, request);
        }
        else {
            log.error("User is not authorized to access this resource");
            throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
        }
    }

    @GetMapping()
    public ResponseEntity<CustomHttpResponseDTO> getRegisteredSearches( HttpServletRequest request) {
        log.info("Get Registered Searches");
        JWTAuthDTO jwtAuthDTO = controllerHelper.decodeJWT(request);
        if(jwtAuthDTO != null) {
            if(jwtAuthDTO.getAuthorities().contains("ROLE_BESTBUY_USER")) {
                ArrayList<RegisteredSearch> registeredSearches = registrySearchService
                        .getRegisteredSearchesByUserIdOrUserEmail(null, jwtAuthDTO.getSubject());
                return responseHandler.httpResponse(
                        CustomHttpResponseDTO.builder()
                                .status(HttpStatus.OK)
                                .timestamp(System.currentTimeMillis())
                                .message("Retrieved searches successfully")
                                .data(new HashMap<>(Map.of("search", registeredSearches)))
                                .build(),
                        controllerHelper.setupResponseHeaders(request)
                );
            }
            else {
                log.error("User is not authorized to access this resource");
                throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
            }
        }
        else {
            log.error("User Provided Invalid Token");
            throw new UnauthorizedTokenException("Invalid Token");
        }
    }

    @DeleteMapping()
    public ResponseEntity<CustomHttpResponseDTO> deleteRegisteredSearches(HttpServletRequest request) {
        JWTAuthDTO jwtAuthDTO = controllerHelper.decodeJWT(request);
        if(jwtAuthDTO != null) {
            log.info("Deleting all Registered Searches for {}", jwtAuthDTO.getSubject());
            if(jwtAuthDTO.getAuthorities().contains("ROLE_BESTBUY_USER")) {
                registrySearchService.deleteRegisteredSearchesByUserIdOrUserEmail(null, jwtAuthDTO.getSubject());
                return responseHandler.httpResponse(
                        CustomHttpResponseDTO.builder()
                                .status(HttpStatus.OK)
                                .success(true)
                                .timestamp(System.currentTimeMillis())
                                .message("Deleted all searches successfully")
                                .build(),
                        controllerHelper.setupResponseHeaders(request)
                );
            }
            else {
                log.error("User is not authorized to access this resource");
                throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
            }
        }
        else {
            log.error("User Provided Invalid Token");
            throw new UnauthorizedTokenException("Invalid Token");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomHttpResponseDTO> deleteRegisteredSearches(
            @NonNull @PathVariable String id, HttpServletRequest request) {
        log.info("Delete Registered Searches");
        JWTAuthDTO jwtAuthDTO = controllerHelper.decodeJWT(request);
        if(jwtAuthDTO != null) {
            if(jwtAuthDTO.getAuthorities().contains("ROLE_BESTBUY_USER")) {
                registrySearchService.deleteRegisteredSearchByIdAndUserEmail(
                        id, jwtAuthDTO.getSubject());
                return responseHandler.httpResponse(
                        CustomHttpResponseDTO.builder()
                                .status(HttpStatus.OK)
                                .timestamp(System.currentTimeMillis())
                                .message("Deleted searches successfully")
                                .success(true)
                                .build(),
                        controllerHelper.setupResponseHeaders(request)
                );
            }
            else {
                log.error("User is not authorized to access this resource");
                throw new TokenUnauthorizedToScopeException("Token is not authorized this resource");
            }
        }
        else {
            log.error("User Provided Invalid Token");
            throw new UnauthorizedTokenException("Invalid Token");
        }
    }

    private ResponseEntity<CustomHttpResponseDTO> getCustomHttpResponseEntity(RegisteredSearch registeredSearch, HttpServletRequest request) {
        return responseHandler.httpResponse(
                CustomHttpResponseDTO.builder()
                        .message("Search saved successfully")
                        .data(new HashMap<>(Map.of("search", registeredSearch)))
                        .success(true)
                        .timestamp(System.currentTimeMillis())
                        .status(HttpStatus.OK)
                        .build(),
                controllerHelper.setupResponseHeaders(request));
    }
}

