package com.nolis.productsearch.helper;

import com.nolis.productsearch.exception.HttpClientErrorException;
import com.nolis.productsearch.exception.HttpExternalServerErrorException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Slf4j @Component @AllArgsConstructor
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {

        return (
                (httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR)
                        && httpResponse.getStatusCode() != HttpStatus.PRECONDITION_FAILED);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {
        HttpStatus statusCode = httpResponse.getStatusCode();
        // get response body as string from httpResponse
        switch (statusCode.series()) {
            case CLIENT_ERROR -> throw new HttpClientErrorException("Client Error", httpResponse);
            case SERVER_ERROR -> throw new HttpExternalServerErrorException("Client Error", httpResponse);
            default -> throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }
}
