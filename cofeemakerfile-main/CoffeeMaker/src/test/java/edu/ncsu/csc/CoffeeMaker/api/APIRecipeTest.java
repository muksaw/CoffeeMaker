package edu.ncsu.csc.CoffeeMaker.api;

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

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( new Ingredient( "Chocolate", 5 ) );
        r.addIngredient( new Ingredient( "Coffee", 3 ) );
        r.addIngredient( new Ingredient( "Milk", 4 ) );
        r.addIngredient( new Ingredient( "Sugar", 8 ) );

        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Chocolate", 10 ) );
        recipe.addIngredient( new Ingredient( "Milk", 20 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 5 ) );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

        // Perform the PUT request to update the recipe
        mvc.perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );
        // Perform the PUT request to update the recipe
        final Recipe recipeFail = recipe;
        recipeFail.update( recipe );

        mvc.perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeFail ) ) );
    }

    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 4 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 4 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 4, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () throws Exception {
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        mvc.perform( delete( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Coffee" ) ).andExpect( status().isNotFound() );
        Assertions.assertEquals( 2, service.count() );
        mvc.perform( delete( "/api/v1/recipes/Mocha" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Mocha" ) ).andExpect( status().isNotFound() );
        Assertions.assertEquals( 1, service.count() );
        mvc.perform( delete( "/api/v1/recipes/Latte" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Latte" ) ).andExpect( status().isNotFound() );
        Assertions.assertEquals( 0, service.count() );
    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () throws Exception {
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        mvc.perform( delete( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testGetRecipe1 () throws Exception {
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        final String s1 = mvc.perform( get( "/api/v1/recipes/Coffee" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final String s2 = mvc.perform( get( "/api/v1/recipes/Mocha" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final String s3 = mvc.perform( get( "/api/v1/recipes/Latte" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( s1.contains( "Coffee" ) );
        Assertions.assertTrue( s2.contains( "Mocha" ) );
        Assertions.assertTrue( s3.contains( "Latte" ) );

        Assertions.assertFalse( s1.contains( "Mocha" ) );
        Assertions.assertFalse( s1.contains( "Latte" ) );
    }

    @Test
    @Transactional
    public void testGetRecipe2 () throws Exception {
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        mvc.perform( get( "/api/v1/recipes/Mocha" ) ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.MANAGER )
    public void testEditRecipe () throws Exception {
        // Save recipe to database
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        // Make recipe that isn't in database
        final Recipe rNull = createRecipe( "Milk", 50, 3, 1, 1, 0 );

        // Try to edit non-saved recipe
        mvc.perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rNull ) ) ).andExpect( status().isNotFound() );

        // Try to edit saved version
        final Recipe updateRecipe = createRecipe( "Coffee", 100, 4, 2, 2, 1 );
        final String response = mvc
                .perform( put( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( updateRecipe ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( response.contains( "Coffee successfully updated" ) );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testCreateRecipe () throws Exception {
        // Try to create recipe with no ingredients
        final Recipe recipeNoIngredients = new Recipe();
        recipeNoIngredients.setName( "Coffee" );
        recipeNoIngredients.setPrice( 100 );

        // Try to create recipe with no ingredients
        String response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( recipeNoIngredients ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Cannot add a Recipe with no Ingredients" ) );

        // Try to create a recipe with a negative price
        final Recipe negRecipe = createRecipe( "Coffee", -5, 1, 1, 1, 1 );
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( negRecipe ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Price must be greater than zero." ) );

        // Try to create a recipe with a negative ingredient amount
        final Recipe negIngAmtRecipe = createRecipe( "Coffee", 5, -1, -1, -1, -1 );
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( negIngAmtRecipe ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Ingredient Amounts must be greater than zero." ) );

        // Save Proper recipe
        final Recipe r1 = createRecipe( "Milk", 100, 1, 2, 1, 1 );
        final Recipe r2 = createRecipe( "Hot Chocolate", 100, 1, 2, 2, 2 );
        final Recipe r3 = createRecipe( "Tea", 100, 1, 1, 1, 1 );
        final Recipe r4 = createRecipe( "Hot Tea", 100, 10, 1, 1, 1 );

        // Save r1
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Milk successfully created" ) );

        // Try to Save r1 again
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r1 ) ) )
                .andExpect( status().isBadRequest() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Recipe with the name Milk already exists" ) );

        // Save r2
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Hot Chocolate successfully created" ) );

        // Save r3
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r3 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Tea successfully created" ) );

        // Try to save r4
        response = mvc
                .perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r4 ) ) )
                .andExpect( status().isInsufficientStorage() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( response.contains( "Insufficient space in recipe book for recipe Hot Tea" ) );

    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( new Ingredient( "Chocolate", chocolate ) );
        recipe.addIngredient( new Ingredient( "Milk", milk ) );
        recipe.addIngredient( new Ingredient( "Sugar", sugar ) );
        recipe.addIngredient( new Ingredient( "Coffee", coffee ) );

        return recipe;
    }
}
