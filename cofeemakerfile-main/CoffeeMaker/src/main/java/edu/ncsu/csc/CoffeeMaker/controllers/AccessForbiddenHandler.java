package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AccessForbiddenHandler extends ResponseEntityExceptionHandler {

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @ExceptionHandler ( AccessDeniedException.class )
    public final ResponseEntity handleAccessDeniedException ( final AccessDeniedException ex,
            final WebRequest request ) {
        return new ResponseEntity( "Access Denied.", HttpStatus.FORBIDDEN );
    }

}
