package com.nolis.authenticationserver.DTO;

import javax.validation.constraints.NotNull;

public record EmailPasswordAuthenticationRequest(
        @NotNull
        String email,
        @NotNull
        String password
) {
}
