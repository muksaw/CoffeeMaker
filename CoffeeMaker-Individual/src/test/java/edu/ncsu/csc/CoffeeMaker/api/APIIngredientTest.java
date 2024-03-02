package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.models.enums.IngredientType;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    public void ensureIngredients () throws Exception {
        service.deleteAll();

        // String to hold ingredients
        String ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        // Determine if ingredient we want is present
        if ( !ingredient.contains( "CARAMEL" ) ) {
            mvc.perform( post( String.format( "/api/v1/ingredients/%s/%d", IngredientType.CARAMEL.toString(), 50 ) )
                    .contentType( MediaType.APPLICATION_JSON )
                    .content( "{\"name\":\"" + IngredientType.CARAMEL.toString() + "\",\"amount\":50}" ) )
                    .andExpect( status().isOk() );
        }

        ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( ingredient.contains( "CARAMEL" ) );

        // Attempt to add Caramel again
        mvc.perform( post( String.format( "/api/v1/ingredients/%s/%d", IngredientType.CARAMEL.toString(), 50 ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"" + IngredientType.CARAMEL.toString() + "\",\"amount\":50}" ) )
                .andExpect( status().isConflict() );

        // Determine if ingredient we want is present
        if ( !ingredient.contains( "MILK" ) ) {
            mvc.perform( post( String.format( "/api/v1/ingredients/%s/%d", IngredientType.MILK.toString(), 50 ) )
                    .contentType( MediaType.APPLICATION_JSON )
                    .content( "{\"name\":\"" + IngredientType.MILK.toString() + "\",\"amount\":50}" ) )
                    .andExpect( status().isOk() );
        }

        ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( ingredient.contains( "CARAMEL" ) );
        assertTrue( ingredient.contains( "MILK" ) );

        // Delete milk
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", IngredientType.MILK.name() ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"" + IngredientType.MILK.toString() + "\",\"amount\":50}" ) )
                .andExpect( status().isOk() );

        // Delete non existent Ingredient
        mvc.perform( delete( String.format( "/api/v1/ingredients/%s", "Invalid" ) )
                .contentType( MediaType.APPLICATION_JSON ).content( "{\"name\":\"" + "Invalid" + "\",\"amount\":50}" ) )
                .andExpect( status().isNotFound() );

        Assertions.assertEquals( 1, service.count() );

    }

    /**
     * Tests the GET route by checking if the correct ingredient is returned.
     *
     * @throws Exception
     *             Any REST routes could throw an exception
     * @author James Kocak
     */
    @Test
    @Transactional
    public void testGet () throws Exception {
        service.deleteAll();

        // String to hold ingredients
        String ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        // Determine if ingredient we want is present
        if ( !ingredient.contains( "CARAMEL" ) ) {
            mvc.perform( post( String.format( "/api/v1/ingredients/%s/%d", IngredientType.CARAMEL.toString(), 50 ) )
                    .contentType( MediaType.APPLICATION_JSON )
                    .content( "{\"name\":\"" + IngredientType.CARAMEL.toString() + "\",\"amount\":50}" ) )
                    .andExpect( status().isOk() );
        }

        ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( ingredient.contains( "CARAMEL" ) );

        final String caramel = mvc
                .perform( get( String.format( "/api/v1/ingredients/%s", IngredientType.CARAMEL.toString() ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( caramel.contains( "CARAMEL" ) );

        // Attempt to grab ingredient that does not exist
        final String invalid = "invalid";
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", invalid ) ) ).andDo( print() )
                .andExpect( status().isNotFound() );
    }

    /**
     * Tests the PUT route by checking if the correct modified ingredient is
     * returned.
     *
     * @throws Exception
     *             Any REST routes could throw an exception
     * @author James Kocak
     */
    @Test
    @Transactional
    public void testPut () throws Exception {
        service.deleteAll();

        // String to hold ingredients
        String ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        // Determine if ingredient we want is present
        if ( !ingredient.contains( "CARAMEL" ) ) {
            mvc.perform( post( String.format( "/api/v1/ingredients/%s/%d", IngredientType.CARAMEL.toString(), 50 ) )
                    .contentType( MediaType.APPLICATION_JSON )
                    .content( "{\"name\":\"" + IngredientType.CARAMEL.toString() + "\",\"amount\":50}" ) )
                    .andExpect( status().isOk() );
        }

        ingredient = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertTrue( ingredient.contains( "CARAMEL" ) );

        // Try to update ingredient that is not present
        final String invalid = "Invalid";
        mvc.perform( put( String.format( "/api/v1/ingredients/%s/%d", invalid, 50 ) )
                .contentType( MediaType.APPLICATION_JSON ).content( "{\"name\":\"" + invalid + "\",\"amount\":50}" ) )
                .andExpect( status().isNotFound() );

        // Update Caramel Ingredient
        mvc.perform( put( String.format( "/api/v1/ingredients/%s/%d", IngredientType.CARAMEL.toString(), 100 ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"" + IngredientType.CARAMEL.toString() + "\",\"amount\":100}" ) )
                .andExpect( status().isOk() );

        final String caramel = mvc
                .perform( get( String.format( "/api/v1/ingredients/%s", IngredientType.CARAMEL.toString() ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( caramel.contains( "100" ) );
    }

}
