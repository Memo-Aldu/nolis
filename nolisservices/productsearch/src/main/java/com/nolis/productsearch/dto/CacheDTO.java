package com.nolis.productsearch.dto;

public record CacheDTO(
        String name,
        String ttl,
        String unit
) {
}
