package com.nolis.searchregistry.controller;

import com.nolis.commonconfig.security.service.AuthService;
import com.nolis.searchregistry.helper.ControllerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequestMapping("api/v1/search-registry")
public record SearchRegistryController(
        ControllerHelper controllerHelper
) {

    @PostMapping("/amazon/{asin}")
    public String saveAmazonProductSearchWithAsin(@PathVariable String asin, HttpServletRequest request) {
        if(controllerHelper.hasAuthority(request,"ROLE_BESTBUY_USER")){
            return "Amazon product with asin: " + asin + " saved";
        }
        return "test";
    }
}
