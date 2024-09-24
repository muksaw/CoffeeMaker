package edu.ncsu.csc.CoffeeMaker.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import edu.ncsu.csc.CoffeeMaker.models.User;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess ( final HttpServletRequest request, final HttpServletResponse response,
            final Authentication authentication ) throws IOException, ServletException {
        final boolean isManager = authentication.getAuthorities().stream()
                .anyMatch( ( a ) -> a.getAuthority().equals( User.MANAGER ) );
        if ( isManager ) {
            response.sendRedirect( "/managerhome" );
            return;
        }

        final boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch( ( a ) -> a.getAuthority().equals( User.STAFF ) );
        if ( isStaff ) {
            response.sendRedirect( "/staffhome" );
            return;
        }

        final boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch( ( a ) -> a.getAuthority().equals( User.CUSTOMER ) );
        if ( isCustomer ) {
            response.sendRedirect( "/customerhome" );
            return;
        }

        response.sendRedirect( "/guesthome" );
    }

}
