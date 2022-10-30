package com.nolis.productsearch.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.client.ClientHttpResponse;

import java.io.Serial;

@Getter @Setter
public class HttpClientErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ClientHttpResponse httpResponse;

    public HttpClientErrorException(String error, ClientHttpResponse httpResponse) {
        super(error);
        this.httpResponse = httpResponse;
    }
}
