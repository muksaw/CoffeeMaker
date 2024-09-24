/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 *
 */
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
    private RecipeService         recipeService;

    @Autowired
    private InventoryService      inventoryService;

    @Autowired
    private IngredientService     ingredientService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        // recipeService.deleteAll();
        // inventoryService.deleteAll();
        ingredientService.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testIngredientAPIPOST () throws Exception {
        ingredientService.deleteAll();
        System.out.println( ingredientService.findAll() );

        final Ingredient ingr = new Ingredient( "Espresso", 10 );

        // First request should be successful
        mvc.perform( post( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingr ) ) ).andExpect( status().isCreated() );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

        // Second request should result in a conflict
        final MvcResult result = mvc.perform( post( "/api/v1/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingr ) ) ).andExpect( status().isConflict() ).andReturn();

        // Extract and validate the error message
        final String responseBody = result.getResponse().getContentAsString();
        Assertions.assertTrue( responseBody.contains( "Ingredient with the name Espresso already exists" ) );

        // Verify that the count remains the same
        Assertions.assertEquals( 1, (int) ingredientService.count() );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = { User.STAFF } )
    public void testGetIngredient () throws Exception {
        // Save a test ingredient
        final Ingredient testIngredient = new Ingredient();
        testIngredient.setName( "TestIngredient" );
        ingredientService.save( testIngredient );

        // Perform the GET request
        mvc.perform( get( "/api/v1/ingredients/TestIngredient" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

        mvc.perform( get( "/api/v1/ingredients/noingredient" ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

        mvc.perform( delete( "/api/v1/ingredients/TestIngredient" ) ).andExpect( status().isOk() );

        // // Check the count after the DELETE request
        // Assertions.assertEquals(1, (int) ingredientService.count());
        //
        // // Perform another GET request to check if the ingredient is not
        // found
        // mvc.perform(get("/api/v1/ingredients/TestIngredient"))
        // .andExpect(status().isNotFound());

        mvc.perform( delete( "/api/v1/ingredients/noingredient" ) ).andExpect( status().isNotFound() );
    }

}
