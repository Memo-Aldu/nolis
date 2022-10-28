package com.nolis.productsearch.Request;

import java.util.UUID;

public record SearchRequest(
        String query,
        UUID userId
) {
}
