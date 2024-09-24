package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.SimpleGrantedAuthority;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@RestController
public class APIUserController extends APIController {
    @Autowired
    private UserService userService;

    @GetMapping ( BASE_PATH + "users" )
    public List<User> getUsers () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final List<User> list = userService.findAll();
        for ( final User u : list ) {
            u.clearPassword();
        }
        return list;
    }

    @GetMapping ( BASE_PATH + "users/customers" )
    public List<User> getCustomers () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final List<User> userList = userService.findAll();
        final List<User> customerList = new LinkedList<User>();
        final SimpleGrantedAuthority custRole = new SimpleGrantedAuthority( User.CUSTOMER );
        for ( final User u : userList ) {
            if ( u.getAuthorities().contains( custRole ) ) {
                u.clearPassword();
                customerList.add( u );
            }
        }
        return customerList;
    }

    @GetMapping ( BASE_PATH + "users/staff" )
    public List<User> getStaff () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final List<User> userList = userService.findAll();
        final List<User> staffList = new LinkedList<User>();
        final SimpleGrantedAuthority staffRole = new SimpleGrantedAuthority( User.STAFF );

        for ( final User u : userList ) {
            if ( u.getAuthorities().contains( staffRole ) ) {
                u.clearPassword();
                staffList.add( u );
            }
        }
        return staffList;
    }

    @GetMapping ( BASE_PATH + "users/managers" )
    public List<User> getManagers () {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final List<User> userList = userService.findAll();
        final List<User> manList = new LinkedList<User>();
        final SimpleGrantedAuthority managerRole = new SimpleGrantedAuthority( User.MANAGER );
        for ( final User u : userList ) {
            if ( u.getAuthorities().contains( managerRole ) ) {
                u.clearPassword();
                manList.add( u );
            }
        }
        return manList;
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @GetMapping ( BASE_PATH + "users/customer/{userName}" )
    public ResponseEntity getCustomer ( @PathVariable final String userName ) {
        final User usr = userService.loadUserByUsername( userName );
        if ( null == usr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        usr.clearPassword();

        return new ResponseEntity( usr, HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @PostMapping ( BASE_PATH + "users/customer" )
    public ResponseEntity addCustomer ( @RequestBody final User user ) {
        final User u = userService.loadUserByUsername( user.getUsername() );
        if ( u != null ) {
            return new ResponseEntity( "A User with the given username already exists", HttpStatus.CONFLICT );
        }

        userService.registerCustomer( user );

        return new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * endpoint for editing a customer (really, just their password) either by a
     * manager or the customer
     *
     * @param user
     *            the user to edit
     * @return
     */
    @SuppressWarnings ( "rawtypes" )
    @PutMapping ( BASE_PATH + "users/customer" )
    public ResponseEntity editCustomer ( @RequestBody final User user ) {

        final Authentication a = SecurityContextHolder.getContext().getAuthentication();

        if ( a == null
                || ( a.getAuthorities().stream().noneMatch( ( auth ) -> auth.getAuthority().equals( User.MANAGER ) )
                        && !a.getName().equals( user.getUsername() ) ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final User u = userService.loadUserByUsername( user.getUsername() );
        if ( u == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        u.setPassword( user.getPassword() );
        userService.save( u );
        return new ResponseEntity( HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @GetMapping ( BASE_PATH + "users/staff/{userName}" )
    public ResponseEntity getStaff ( @RequestHeader final String userName ) {
        final User u = userService.loadUserByUsername( userName );
        if ( u == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( u, HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @PostMapping ( BASE_PATH + "users/staff" )
    public ResponseEntity createStaff ( @RequestBody final User user ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            // throw new AccessDeniedException( "Access Denied" );

            return new ResponseEntity( "This is where it is messing up.", HttpStatus.CONFLICT );
        }

        final User u = userService.loadUserByUsername( user.getUsername() );
        if ( u != null ) {
            return new ResponseEntity( "A User with the given username already exists", HttpStatus.CONFLICT );
        }
        userService.registerStaff( user );
        return new ResponseEntity( HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @PostMapping ( BASE_PATH + "users/manager" )
    public ResponseEntity createManager ( @RequestBody final User user ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final User u = userService.loadUserByUsername( user.getUsername() );
        if ( u != null ) {
            return new ResponseEntity( "A User with the given username already exists", HttpStatus.CONFLICT );
        }
        userService.registerManager( user );
        return new ResponseEntity( HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @PutMapping ( BASE_PATH + "users/staff" )
    public ResponseEntity editStaff ( @RequestBody final User user ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final User u = userService.loadUserByUsername( user.getUsername() );
        if ( u == null ) {
            return new ResponseEntity( "User not found", HttpStatus.NOT_FOUND );
        }
        u.setPassword( user.getPassword() );
        userService.save( u );
        return new ResponseEntity( HttpStatus.OK );
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    @DeleteMapping ( BASE_PATH + "users/staff/{userName}" )
    public ResponseEntity deleteStaff ( @PathVariable ( "userName" ) final String username ) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( authentication, User.MANAGER ) ) {
            throw new AccessDeniedException( "Access Denied" );
        }

        final User u = userService.loadUserByUsername( username );
        if ( u == null ) {
            return new ResponseEntity( "User not found", HttpStatus.NOT_FOUND );
        }
        userService.delete( u );
        return new ResponseEntity( HttpStatus.OK );
    }
}
