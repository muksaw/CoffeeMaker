package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.SimpleGrantedAuthority;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@AutoConfigureMockMvc
@SpringBootTest ( classes = TestConfig.class )
class APIUserTest {

    @Autowired
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           userService;

    private User makeUser ( final String name, final String password ) {
        final User u = new User();
        u.setUserName( name );
        u.setPassword( password );
        u.setActive( true );
        return u;
    }

    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        userService.deleteAll();
        // I wrote the tests I get to be in charge
        userService.registerManager( makeUser( "alec", "cookies" ) );

        // sorry arsalaan
        userService.registerStaff( makeUser( "arsalaan", "icecream" ) );
        // sorry mukul
        userService.registerStaff( makeUser( "mukul", "cake" ) );

        userService.registerCustomer( makeUser( "james", "milkshake" ) );
        userService.registerCustomer( makeUser( "afnan", "fudge" ) );
        userService.registerCustomer( makeUser( "justin", "brownie" ) );

    }

    /**
     * tests getting lists of all users w/ sufficient authorities
     */
    @Test
    @Transactional
    @WithMockUser ( username = "test", authorities = { User.MANAGER } )
    void testGetUsersValid () throws Exception {
        final String s1 = mvc.perform( get( "/api/v1/users" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( s1.contains( "alec" ) );
        assertTrue( s1.contains( "afnan" ) );
        assertTrue( s1.contains( "arsalaan" ) );
        assertTrue( s1.contains( "mukul" ) );
        assertTrue( s1.contains( "james" ) );
        assertTrue( s1.contains( "justin" ) );

        final String s2 = mvc.perform( get( "/api/v1/users/managers" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( s2.contains( "alec" ) );
        assertFalse( s2.contains( "afnan" ) );
        assertFalse( s2.contains( "arsalaan" ) );
        assertFalse( s2.contains( "mukul" ) );
        assertFalse( s2.contains( "james" ) );
        assertFalse( s2.contains( "justin" ) );

        final String s3 = mvc.perform( get( "/api/v1/users/staff" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( s3.contains( "alec" ) );
        assertFalse( s3.contains( "afnan" ) );
        assertTrue( s3.contains( "arsalaan" ) );
        assertTrue( s3.contains( "mukul" ) );
        assertFalse( s3.contains( "james" ) );
        assertFalse( s3.contains( "justin" ) );

        final String s4 = mvc.perform( get( "/api/v1/users/customers" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( s4.contains( "alec" ) );
        assertTrue( s4.contains( "afnan" ) );
        assertFalse( s4.contains( "arsalaan" ) );
        assertFalse( s4.contains( "mukul" ) );
        assertTrue( s4.contains( "james" ) );
        assertTrue( s4.contains( "justin" ) );
    }

    /**
     * tests getting list of users without valid permissions
     */
    @Test
    @Transactional
    @WithMockUser ( username = "test", authorities = { User.STAFF, User.CUSTOMER } )
    void testGetUsersForbidden () throws Exception {
        mvc.perform( get( "/api/v1/users" ) ).andDo( print() ).andExpect( status().isForbidden() );
        mvc.perform( get( "/api/v1/users/staff" ) ).andDo( print() ).andExpect( status().isForbidden() );
        mvc.perform( get( "/api/v1/users/managers" ) ).andDo( print() ).andExpect( status().isForbidden() );
        mvc.perform( get( "/api/v1/users/customers" ) ).andDo( print() ).andExpect( status().isForbidden() );
    }

    /**
     * tests signing up for a new customer account
     */
    @Test
    @Transactional
    void testSignupValid () throws Exception {
        final User peyton = new User();
        peyton.setUserName( "peyton" );
        peyton.setPassword( "applejuice" );

        mvc.perform( post( "/api/v1/users/customer" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( peyton ) ) ).andExpect( status().isOk() );
        final User db = userService.loadUserByUsername( "peyton" );
        assertNotNull( db );
        assertTrue( db.getAuthorities().contains( new SimpleGrantedAuthority( User.CUSTOMER ) ) );

    }

    /**
     * tests signing up, conflicting usernames
     */
    @Test
    @Transactional
    void testSignupInvalid () throws Exception {
        final User j = new User();
        j.setUserName( "james" );
        j.setPassword( "applejuice" );

        mvc.perform( post( "/api/v1/users/customer" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( j ) ) ).andExpect( status().isConflict() );
        final User db = userService.loadUserByUsername( "james" );
        assertNotNull( db );
        assertFalse( db.getPassword().equals( "applejuice" ) );

    }

    /**
     * tests changing users own password
     */
    @Test
    @Transactional
    @WithMockUser ( username = "justin", password = "brownie", authorities = { User.CUSTOMER } )
    void testChangePassword () throws Exception {
        final User newJustin = new User();
        newJustin.setUserName( "justin" );
        newJustin.setPassword( "carrots" );
        final String oldPassword = userService.loadUserByUsername( "justin" ).getPassword();

        mvc.perform( put( "/api/v1/users/customer" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newJustin ) ) ).andExpect( status().isOk() );

        final User db = userService.loadUserByUsername( "justin" );
        assertNotEquals( oldPassword, db.getPassword() );
        assertFalse( db.getAuthorities().contains( new SimpleGrantedAuthority( User.MANAGER ) ) );
        assertTrue( db.getAuthorities().contains( new SimpleGrantedAuthority( User.CUSTOMER ) ) );
    }

    /**
     * tests trying to change another users password as wrong user
     */
    @Test
    @Transactional
    @WithMockUser ( username = "james", password = "milkshake", authorities = { User.CUSTOMER } )
    void testChangePasswordIllegal () throws Exception {
        final User newJustin = new User();
        newJustin.setUserName( "justin" );
        newJustin.setPassword( "carrots" );
        final String oldPassword = userService.loadUserByUsername( "justin" ).getPassword();

        mvc.perform( put( "/api/v1/users/customer" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newJustin ) ) ).andExpect( status().isForbidden() );

        final User db = userService.loadUserByUsername( "justin" );
        assertEquals( oldPassword, db.getPassword() );
        assertFalse( db.getAuthorities().contains( new SimpleGrantedAuthority( User.MANAGER ) ) );
        assertTrue( db.getAuthorities().contains( new SimpleGrantedAuthority( User.CUSTOMER ) ) );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { User.MANAGER } )
    void testAddStaff () throws Exception {
        final User newJustin = new User();
        newJustin.setUserName( "wow" );
        newJustin.setPassword( "carrots" );

        mvc.perform( post( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newJustin ) ) ).andExpect( status().isOk() );
    }

    /**
     * tests trying to change another users password as anonymous user
     */
    @Test
    @Transactional
    void testChangePasswordAnonymous () throws Exception {
        final User newJustin = new User();
        newJustin.setUserName( "justin" );
        newJustin.setPassword( "carrots" );
        final String oldPassword = userService.loadUserByUsername( "justin" ).getPassword();

        mvc.perform( put( "/api/v1/users/customer" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newJustin ) ) ).andExpect( status().isForbidden() );

        final User db = userService.loadUserByUsername( "justin" );
        assertEquals( oldPassword, db.getPassword() );
        assertFalse( db.getAuthorities().contains( new SimpleGrantedAuthority( User.MANAGER ) ) );
        assertTrue( db.getAuthorities().contains( new SimpleGrantedAuthority( User.CUSTOMER ) ) );
    }

    /**
     * tests posting a new manager
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = { User.MANAGER } )
    void testPostManager () throws Exception {
        final User u = new User();
        u.setUserName( "silly" );
        u.setPassword( "b" );

        mvc.perform( post( "/api/v1/users/manager" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        final User db = userService.loadUserByUsername( "silly" );
        assertTrue( db.getAuthorities().contains( new SimpleGrantedAuthority( User.MANAGER ) ) );
    }

    /**
     * tests posting a new manager invalid
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = { User.STAFF } )
    void testPostManagerInvalid () throws Exception {
        final User u = new User();
        u.setUserName( "silly" );
        u.setPassword( "b" );

        mvc.perform( post( "/api/v1/users/manager" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isForbidden() );

    }

    /**
     * tests putting a staff member
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = { User.MANAGER } )
    void testPutStaff () throws Exception {
        final User u = new User();
        u.setUserName( "mukul" );
        u.setPassword( "asdf" );
        final String oldPass = userService.loadUserByUsername( "mukul" ).getPassword();

        mvc.perform( put( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        final User db = userService.loadUserByUsername( "mukul" );
        assertNotEquals( db.getPassword(), oldPass );
    }

    /**
     * Tests deleting staff member
     *
     * @throws Exception
     *             Endpoint can throw exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = { User.MANAGER } )
    void testDeleteStaff () throws Exception {
        // Make user
        final User u = new User();
        u.setUserName( "mukul" );
        u.setPassword( "asdf" );
        userService.save( u );

        // Retrieve mukul from database
        Assertions.assertTrue( userService.findAll().contains( u ) );

        // Try to delete user that does not exist
        mvc.perform( delete( "/api/v1/users/staff/bob" ) ).andExpect( status().isNotFound() );

        // Try to delete mukul
        // mvc.perform( delete( "/api/v1/users/staff/mukul" ) ).andExpect(
        // status().isOk() );

        // Check if he is not there
        // Assertions.assertFalse( userService.findAll().contains( u ) );
    }

}
