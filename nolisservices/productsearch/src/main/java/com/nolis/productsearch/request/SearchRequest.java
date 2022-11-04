package com.nolis.productsearch.request;


public record SearchRequest(
        String query,
        String userId,
        String userEmail
) {
    public boolean isValidate() {
        return query != null && (userId != null || userEmail != null);
    }
}
