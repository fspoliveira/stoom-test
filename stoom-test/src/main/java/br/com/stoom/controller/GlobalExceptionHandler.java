package br.com.stoom.controller;

import br.com.stoom.exception.AddressNotFoundInDatabaseException;
import br.com.stoom.exception.GoogleApiInvalidAddressInformation;
import br.com.stoom.model.ErrorModel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AddressNotFoundInDatabaseException.class, EmptyResultDataAccessException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorModel.builder().message("Address not found").build(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {GoogleApiInvalidAddressInformation.class})
    protected ResponseEntity<Object> handleInvalidAddressSentToGoogle(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorModel.builder().message("Invalid Address data!").build(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {InvalidDataAccessApiUsageException.class})
    protected ResponseEntity<Object> invalidQueryStringField(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorModel.builder().message("Invalid field name passed in query string!").build(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
