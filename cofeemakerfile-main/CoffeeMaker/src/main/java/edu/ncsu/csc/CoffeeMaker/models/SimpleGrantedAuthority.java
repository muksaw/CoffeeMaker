package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * implementation of SimpleGrantedAuthority from spring, but with entity tag so
 * that authorities can be stored in the db
 */
@Entity
public final class SimpleGrantedAuthority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long              id;

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String      role;

    public SimpleGrantedAuthority () {
        this.role = "";
    }

    public SimpleGrantedAuthority ( final String role ) {
        Assert.hasText( role, "A granted authority textual representation is required" );
        this.role = role;
    }

    @Override
    public String getAuthority () {
        return role;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }

        if ( obj instanceof SimpleGrantedAuthority ) {
            return role.equals( ( (SimpleGrantedAuthority) obj ).role );
        }

        return false;
    }

    @Override
    public int hashCode () {
        return this.role.hashCode();
    }

    @Override
    public String toString () {
        return this.role;
    }

}
