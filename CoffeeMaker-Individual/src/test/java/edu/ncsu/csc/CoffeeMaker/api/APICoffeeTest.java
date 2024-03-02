package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final Recipe r = new Recipe();
        /* Figure out if the recipe we want is present */
        if ( !recipe.contains( "Mocha" ) ) {

            r.addIngredient( new Ingredient( "Coffee", 5 ) );
            r.addIngredient( new Ingredient( "Milk", 2 ) );
            r.addIngredient( new Ingredient( "Sugar", 4 ) );
            r.addIngredient( new Ingredient( "Chocolate", 8 ) );
            r.setPrice( 10 );
            r.setName( "Mocha" );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        }

        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( recipe.contains(
                "Mocha" ) ); /* Make sure that now our recipe is there */

        final Inventory i = new Inventory();
        i.addIngredient( "Chocolate", 50 );
        i.addIngredient( "Coffee", 50 );
        i.addIngredient( "Milk", 50 );
        i.addIngredient( "Sugar", 50 );

        final Integer fifty = 50;
        Assertions.assertEquals( fifty, i.getIngredient( "Chocolate" ) );
        Assertions.assertTrue( i.enoughIngredients( r ) );
        i.setId( (long) 267 );
        mvc.perform( post( "/api/v1/inventory/{name}/{amount}", "Chocolate", 50 ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/inventory/{name}/{amount}", "Coffee", 50 ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/inventory/{name}/{amount}", "Milk", 50 ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/inventory/{name}/{amount}", "Sugar", 50 ) ).andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() ).andDo( print() );
        // test that doing null doesn't work.
        final String placeholder = null;
        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", placeholder ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 100 ) ) );

        final String inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        Assertions.assertNotNull( inventory );

    }

    @Test
    @Transactional
    public void testMakeCoffeeErrors () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final Recipe r = new Recipe();
        /* Figure out if the recipe we want is present */
        if ( !recipe.contains( "Mocha" ) ) {

            r.addIngredient( new Ingredient( "Chocolate", 5 ) );
            r.addIngredient( new Ingredient( "Coffee", 3 ) );
            r.addIngredient( new Ingredient( "Milk", 4 ) );
            r.addIngredient( new Ingredient( "Sugar", 8 ) );
            r.setPrice( 10 );
            r.setName( "Mocha" );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        }

        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( recipe.contains(
                "Mocha" ) ); /* Make sure that now our recipe is there */

        // first test that there aren't enough ingredients.
        final Inventory i = new Inventory();
        i.addIngredient( "Chocolate", 0 );
        i.addIngredient( "Coffee", 0 );
        i.addIngredient( "Milk", 0 );
        i.addIngredient( "Sugar", 0 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

        try {
            mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 100 ) ) );
        }
        catch ( final Exception e ) {
            // expected
        }

        // now that there are enough ingredients, lets pay with no money.
        i.addIngredient( "Chocolate", 50 );
        i.addIngredient( "Coffee", 50 );
        i.addIngredient( "Milk", 50 );
        i.addIngredient( "Sugar", 50 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

        try {
            mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 0 ) ) );
        }
        catch ( final Exception e ) {

        }
    }
}
