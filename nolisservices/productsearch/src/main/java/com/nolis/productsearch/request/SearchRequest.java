package com.nolis.productsearch.request;

import java.util.UUID;

public record SearchRequest(
        String query,
        UUID userId
) {
}
