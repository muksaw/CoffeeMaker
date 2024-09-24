package edu.ncsu.csc.CoffeeMaker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.CoffeeMaker.controllers.CustomAuthenticationSuccessHandler;
import edu.ncsu.csc.CoffeeMaker.models.User;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity ( securedEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure ( final AuthenticationManagerBuilder auth ) throws Exception {
        final User admin = new User();
        admin.setUserName( "admin" );
        admin.setPassword( passwordEncoder().encode( "password" ) );
        admin.grantManager();
        admin.grantStaff();

        auth.inMemoryAuthentication().withUser( admin );
        auth.userDetailsService( userDetailsService );
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {

        http.authorizeRequests()
                .antMatchers( "/login", "/guesthome.html", "/register.html", "/order.html", "/orders/*", "/orders/",
                        "/api/v1/users/customer", "/menu.html", "/api/v1/recipes", "/api/v1/orders", "/orderstatus*",
                        "/api/v1/orders/*" )
                .permitAll() // Allow
                // access
                // to the
                // login
                // page
                .anyRequest().authenticated() // Require authentication for all
                                              // other requests
                .and().formLogin().loginPage( "/login" ).permitAll()
                .successHandler( new CustomAuthenticationSuccessHandler() ).and().logout().logoutUrl( "/logout" ) // Specify
                // the
                // URL
                // for
                // logout
                .logoutSuccessUrl( "/login" ) // Redirect to login page after
                                              // logout
                .invalidateHttpSession( true ) // Invalidate HTTP session
                .deleteCookies( "JSESSIONID" ) // Delete cookies
                .and().csrf().disable();

    }

}
