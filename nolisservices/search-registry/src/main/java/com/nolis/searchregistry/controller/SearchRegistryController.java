package com.nolis.searchregistry.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/search-registry")
public record SearchRegistryController() {

    @GetMapping("/")
    public String test() {
        return "test";
    }
}
