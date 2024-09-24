package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * An abstract class for User objects. This holds functionality shared between
 * Customers, Managers, and Staff. Most of the shared functionality has to do
 * with login behaviour/security Method comments are from import documentation
 *
 */
@Entity
public class User implements UserDetails {

    /**
     * A users ID
     */
    @Id
    @GeneratedValue
    private Long                         id;

    /**
     * A users user name
     */
    private String                       userName;

    /**
     * A users password (needs to be hashed I think for security)
     */
    private String                       password;

    /**
     * A list of authorities
     */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<SimpleGrantedAuthority> authorities;

    /**
     * A boolean to keep track of whether a user is active. Can be used to get
     * rid of excess info in database
     */
    private boolean                      active;

    /**
     * A final variable for the manager string
     */
    public static final String           MANAGER  = "MANAGER";

    /**
     * A final variable for the STAFF string
     */
    public static final String           STAFF    = "STAFF";

    /**
     * A final variable for the customer string
     */
    public static final String           CUSTOMER = "CUSTOMER";

    /**
     * Constructor for User objects valid inputs are "MANAGER", "STAFF",
     * "CUSTOMER"
     */
    public User ( final String role ) {
        setAuthorities( authorities ); // instantiates the authorites list, is
                                       // initially empty
        if ( role.equals( CUSTOMER ) ) {
            grantCustomer();
        }
        else if ( role.equals( STAFF ) ) {
            grantStaff();
        }
        else if ( role.equals( MANAGER ) ) {
            grantManager();
        }
        else {
            throw new IllegalArgumentException( "What the heck is even going on around here!" );
        }
        setActive( true ); // all accounts are active upon initialization
    }

    /**
     * Parameterless constructor Makes a user without any authorization, setting
     * the authorites field to an empty array list
     */
    public User () {
        setAuthorities( authorities ); //// instantiates the authorites list, is
                                       //// initially empty
        // all accounts are active upon initialization
        setActive( true );

    }

    public User ( final String userName, final String password, final String role ) {
        setAuthorities( authorities ); // instantiates the authorites list, is
                                       // initially empty
        setUserName( userName ); // sets username field
        setPassword( password ); // sets password field

        // sets authority based on what is given as the users role, eg. staff
        // get staff authorities
        if ( role.equals( CUSTOMER ) ) {
            grantCustomer();
        }
        else if ( role.equals( STAFF ) ) {
            grantStaff();
        }
        else if ( role.equals( MANAGER ) ) {
            grantManager();
        }
        else {
            throw new IllegalArgumentException( "What the heck is even going on around here!" );
        }

        // all accounts are active upon initialization
        setActive( true );

    }

    /**
     * Returns the authorities granted to the user. Cannot return null.
     */
    @Override
    public List<SimpleGrantedAuthority> getAuthorities () {
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     */
    @Override
    public String getPassword () {
        return password;
    }

    /**
     * sets this password's value to the empty string for server responses NEVER
     * call this method then save to the database
     */
    public void clearPassword () {
        this.password = "";
    }

    /**
     * Returns the username used to authenticate the user. Cannot return null.
     */
    @Override
    public String getUsername () {
        return userName;
    }

    /**
     * Indicates whether the user's account has expired. An expired account
     * cannot be authenticated. true if the user's account is valid (ie
     * non-expired), false if no longer valid (ie expired)
     *
     */
    @Override
    public boolean isAccountNonExpired () {
        if ( active ) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     */
    @Override
    public boolean isAccountNonLocked () {
        if ( active ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     */
    @Override
    public boolean isCredentialsNonExpired () {
        if ( active ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot
     * be authenticated.
     */
    @Override
    public boolean isEnabled () {
        if ( active ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * General grantAuthority method
     *
     * @param auth
     *            the authority type
     */
    public void grantAuthority ( final SimpleGrantedAuthority auth ) {
        authorities.add( auth );
    }

    /**
     * grants manager permission to the current user, by adding the manager auth
     * to the list of authorities
     */
    public void grantManager () {
        authorities.add( new SimpleGrantedAuthority( MANAGER ) );
    }

    /**
     * grants customer permission to the current user, by adding the customer
     * auth to the list of authorities
     */
    public void grantCustomer () {
        authorities.add( new SimpleGrantedAuthority( CUSTOMER ) );
    }

    /**
     * grants staff permission to the current user, by adding the staff auth to
     * the list of authorities
     */
    public void grantStaff () {
        authorities.add( new SimpleGrantedAuthority( STAFF ) );
    }

    /**
     * Removes the manager permissions from the current user, by removing it
     * from the authorites list
     *
     * @return true if removal was successful
     */
    public boolean revokeManager () {
        for ( final SimpleGrantedAuthority auth : authorities ) {
            if ( auth.getAuthority().equals( MANAGER ) ) { // if the manager is
                                                           // in the list
                return authorities.remove( auth ); // remove it, and return true
                                                   // for sucessful removal
            }
        }
        return false; // didnt have manager perms, return false
    }

    /**
     * Removes the staff permissions from the current user, by removing it from
     * the authorites list
     *
     * @return true if removal was successful
     */
    public boolean revokeStaff () {
        for ( final SimpleGrantedAuthority auth : authorities ) {
            if ( auth.getAuthority().equals( STAFF ) ) { // if the staff is in
                                                         // the list
                return authorities.remove( auth ); // remove it, and return true
                                                   // for sucessful removal
            }
        }
        return false; // didnt have staff perms, return false
    }

    /**
     * Removes the customer permissions from the current user, by removing it
     * from the authorites list
     *
     * @return true if removal was successful
     */
    public boolean revokeCustomer () {
        for ( final SimpleGrantedAuthority auth : authorities ) {
            if ( auth.getAuthority().equals( CUSTOMER ) ) { // if the customer
                                                            // is in the list
                return authorities.remove( auth ); // remove it, and return true
                                                   // for sucessful removal
            }
        }
        return false; // didnt have customer perms, return false
    }

    // getters and setters
    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public void setUserName ( final String userName ) {
        this.userName = userName;
    }

    public boolean isActive () {
        return active;
    }

    public void setActive ( final boolean active ) {
        this.active = active;
    }

    public void setPassword ( final String password ) {
        this.password = password;
    }

    public void setAuthorities ( final List<SimpleGrantedAuthority> authorities ) {
        this.authorities = new ArrayList<>();
    }
}
