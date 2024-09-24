package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

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

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc

public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    InventoryService              inventoryService;

    /**
     * sets up the tests
     */
    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        inventoryService.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { User.MANAGER, User.STAFF } )
    public void testGetRecipe () {
        String recipe;
        try {
            recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                    .getResponse().getContentAsString();
        }
        catch ( final Exception e ) {
            fail( "GET recipe failed with exception: " + e.getMessage() );
            return; // should never execute, just so compiler shuts up
        }

        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();
            r.addIngredient( new Ingredient( "Chocolate", 5 ) );
            r.addIngredient( new Ingredient( "Coffee", 3 ) );
            r.addIngredient( new Ingredient( "Milk", 4 ) );
            r.addIngredient( new Ingredient( "Sugar", 8 ) );

            r.setPrice( 10 );
            r.setName( "Mocha" );

            try {
                mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

                recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                        .andReturn().getResponse().getContentAsString();
            }
            catch ( final Exception e ) {
                fail( "Could not post recipe: " + e.getMessage() );
                return;
            }
        }
        assertTrue( recipe.contains( "Mocha" ) );
        final Inventory i = new Inventory();
        i.addIngredient( "Chocolate", 50 );
        i.addIngredient( "Coffee", 50 );
        i.addIngredient( "Milk", 50 );
        i.addIngredient( "Sugar", 50 );
        inventoryService.save( i );

        // try to PUT this inventory to the server

        try {
            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail( "PUT inventory failed with exception: " + e.getMessage() );
            return; // should never execute, just so compiler shuts up
        }

        try {
            mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 100 ) ) )
                    .andExpect( status().isOk() ).andDo( print() );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            fail( "POST make coffee failed with exception: " + e.getMessage() );
            return; // yea im getting the same compiler warning
        }

    }

}
