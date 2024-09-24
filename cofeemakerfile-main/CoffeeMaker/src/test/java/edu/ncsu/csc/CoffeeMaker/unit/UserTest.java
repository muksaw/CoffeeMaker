package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.SimpleGrantedAuthority;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class UserTest {

    /**
     * Tests the constructor with just a role param
     */
    @Test
    void testUserConstructor1 () {
        final User u1 = new User( "MANAGER" );
        Assertions.assertTrue( u1.isActive() ); // just making sure it is active
        // as
        // expected
        final User u2 = new User( "STAFF" );
        final User u3 = new User( "CUSTOMER" );

        Assertions.assertThrows( IllegalArgumentException.class, () -> new User( "lol" ) );

        // verifies that the created user objects have just 1 role
        Assertions.assertEquals( u1.getAuthorities().size(), 1 );
        Assertions.assertEquals( u2.getAuthorities().size(), 1 );
        Assertions.assertEquals( u3.getAuthorities().size(), 1 );

        // since they jsut have 1 role, i can just check the 0th index for the
        // roles type
        // here i just make sure everyone was made with the right role
        Assertions.assertTrue( u1.getAuthorities().get( 0 ).getAuthority().equals( "MANAGER" ) );
        Assertions.assertTrue( u2.getAuthorities().get( 0 ).getAuthority().equals( "STAFF" ) );
        Assertions.assertTrue( u3.getAuthorities().get( 0 ).getAuthority().equals( "CUSTOMER" ) );

        u1.setUserName( "Arsalaan" );
        u1.setActive( false );
        Assertions.assertFalse( u1.isActive() ); // making sure it NOT active
        u2.setPassword( "securePassword" );
        Assertions.assertEquals( u1.getUsername(), "Arsalaan" );
        Assertions.assertEquals( u2.getPassword(), "securePassword" );
    }

    /**
     * Tests the constructor with no paramter
     */
    @Test
    void testUserConstructor2 () {
        final User u1 = new User();

        Assertions.assertEquals( u1.getAuthorities().size(), 0 );
        Assertions.assertTrue( u1.isActive() );
    }

    @Test
    void testUserConstructor3 () {
        final User u1 = new User( "Arsalaan", "awesomePass", "MANAGER" );
        final User u2 = new User( "Alec", "awesomePass1", "CUSTOMER" );
        final User u3 = new User( "Afnan", "awesomePass2", "STAFF" );

        // verifying fields for u1
        Assertions.assertTrue( u1.isActive() );
        Assertions.assertEquals( u1.getAuthorities().size(), 1 );
        Assertions.assertTrue( u1.getAuthorities().get( 0 ).getAuthority().equals( "MANAGER" ) );
        Assertions.assertTrue( u1.getUsername().equals( "Arsalaan" ) );
        Assertions.assertTrue( u1.getPassword().equals( "awesomePass" ) );

        // verifying fields for u2
        Assertions.assertTrue( u2.isActive() );
        Assertions.assertEquals( u2.getAuthorities().size(), 1 );
        Assertions.assertTrue( u2.getAuthorities().get( 0 ).getAuthority().equals( "CUSTOMER" ) );
        Assertions.assertTrue( u2.getUsername().equals( "Alec" ) );
        Assertions.assertTrue( u2.getPassword().equals( "awesomePass1" ) );

        // verifying fields for u3
        Assertions.assertTrue( u3.isActive() );
        Assertions.assertEquals( u3.getAuthorities().size(), 1 );
        Assertions.assertTrue( u3.getAuthorities().get( 0 ).getAuthority().equals( "STAFF" ) );
        Assertions.assertTrue( u3.getUsername().equals( "Afnan" ) );
        Assertions.assertTrue( u3.getPassword().equals( "awesomePass2" ) );
    }

    @Test
    void testGetAuthorities () {
        final User user = new User( "MANAGER" );
        Assertions.assertEquals( 1, user.getAuthorities().size() );
        Assertions.assertEquals( "MANAGER", user.getAuthorities().get( 0 ).getAuthority() );
    }

    @Test
    void testGetPassword () {
        final User user = new User();
        user.setPassword( "password123" );
        assertEquals( "password123", user.getPassword() );
    }

    @Test
    void testClearPassword () {
        final User user = new User();
        user.setPassword( "password123" );
        user.clearPassword();
        assertEquals( "", user.getPassword() );
    }

    @Test
    void testGetUsername () {
        final User user = new User();
        user.setUserName( "testUser" );
        assertEquals( "testUser", user.getUsername() );
    }

    @Test
    void testIsAccountNonExpired () {
        final User user = new User();
        user.setActive( true );
        assertTrue( user.isAccountNonExpired() );
    }

    @Test
    void testIsAccountNonLocked () {
        final User user = new User();
        user.setActive( true );
        Assertions.assertTrue( user.isAccountNonLocked() );
    }

    @Test
    void testIsCredentialsNonExpired () {
        final User user = new User();
        user.setActive( true );
        Assertions.assertTrue( user.isCredentialsNonExpired() );
    }

    @Test
    void testIsEnabled () {
        final User user = new User();
        user.setActive( true );
        Assertions.assertTrue( user.isEnabled() );
    }

    @Test
    void testGrantAuthority () {
        final User user = new User();
        final SimpleGrantedAuthority authority = new SimpleGrantedAuthority( "ROLE_ADMIN" );
        user.grantAuthority( authority );

        Assertions.assertTrue( user.getAuthorities().contains( authority ) );
    }

    @Test
    void testGrantManager () {
        final User user = new User();
        user.grantManager();

        Assertions.assertTrue( user.getAuthorities().stream()
                .anyMatch( authority -> authority.getAuthority().equals( User.MANAGER ) ) );
    }

    @Test
    void testGrantCustomer () {
        final User user = new User();
        user.grantCustomer();

        Assertions.assertTrue( user.getAuthorities().stream()
                .anyMatch( authority -> authority.getAuthority().equals( User.CUSTOMER ) ) );
    }

    @Test
    void testGrantStaff () {
        final User user = new User();
        user.grantStaff();

        Assertions.assertTrue(
                user.getAuthorities().stream().anyMatch( authority -> authority.getAuthority().equals( User.STAFF ) ) );
    }

    @Test
    void testRevokeManager () {
        final User user = new User();
        user.grantManager();
        Assertions.assertTrue( user.revokeManager() );

        Assertions.assertFalse( user.getAuthorities().stream()
                .anyMatch( authority -> authority.getAuthority().equals( User.MANAGER ) ) );
    }

    @Test
    void testRevokeStaff () {
        final User user = new User();
        user.grantStaff();
        Assertions.assertTrue( user.revokeStaff() );

        Assertions.assertFalse(
                user.getAuthorities().stream().anyMatch( authority -> authority.getAuthority().equals( User.STAFF ) ) );
    }

    @Test
    void testRevokeCustomer () {
        final User user = new User();
        user.grantCustomer();
        Assertions.assertTrue( user.revokeCustomer() );

        Assertions.assertFalse( user.getAuthorities().stream()
                .anyMatch( authority -> authority.getAuthority().equals( User.CUSTOMER ) ) );
    }

    @Test
    void testGetId () {
        final User user = new User();
        user.setId( 1L );
        Assertions.assertEquals( 1L, user.getId() );
    }

    @Test
    void testSetId () {
        final User user = new User();
        user.setId( 1L );
        Assertions.assertEquals( 1L, user.getId() );
    }

    @Test
    void testGetUserName () {
        final User user = new User();
        user.setUserName( "testUser" );
        Assertions.assertEquals( "testUser", user.getUsername() );
    }

    @Test
    void testSetUserName () {
        final User user = new User();
        user.setUserName( "testUser" );
        Assertions.assertEquals( "testUser", user.getUsername() );
    }

    @Test
    void testIsActive () {
        final User user = new User();
        Assertions.assertTrue( user.isActive() );

        user.setActive( false );
        Assertions.assertFalse( user.isActive() );
    }

    @Test
    void testSetActive () {
        final User user = new User();
        user.setActive( true );
        Assertions.assertTrue( user.isActive() );

        user.setActive( false );
        Assertions.assertFalse( user.isActive() );
    }

    @Test
    void testSetPassword () {
        final User user = new User();
        user.setPassword( "password123" );
        Assertions.assertEquals( "password123", user.getPassword() );
    }

    @Autowired
    UserService userService;

    /**
     * tests saving to and loading from database
     */
    @Test
    @Transactional
    void testDatabaseSave () {
        final User u = new User( "alec", "cookies", User.CUSTOMER );
        userService.save( u );

        Assertions.assertEquals( 1, userService.findAll().size() );

        final User db = userService.loadUserByUsername( "alec" );
        Assertions.assertNotNull( db );
        Assertions.assertEquals( 1, db.getAuthorities().size() );
        Assertions.assertEquals( User.CUSTOMER, db.getAuthorities().get( 0 ).getAuthority() );
    }

    /**
     * tests saving to and loading from database with conflicting user
     */
    @Test
    @Transactional
    void testDatabaseSaveInvalid () {
        final User u = new User( "alec", "cookies", User.CUSTOMER );
        userService.save( u );
        u.setPassword( "apples" );
        userService.save( u );

        Assertions.assertEquals( 1, userService.findAll().size() );

        final User db = userService.loadUserByUsername( "alec" );
        Assertions.assertNotNull( db );
        Assertions.assertEquals( "apples", db.getPassword() );
    }

}
