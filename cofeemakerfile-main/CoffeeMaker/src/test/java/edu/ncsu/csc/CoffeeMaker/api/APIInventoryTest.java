package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIInventoryTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

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
        service.deleteAll();

        final Inventory ivt = inventoryService.getInventory();

        ivt.addIngredient( "Chocolate", 123 );
        ivt.addIngredient( "Coffee", 234 );
        ivt.addIngredient( "Milk", 345 );
        ivt.addIngredient( "Sugar", 456 );

        inventoryService.save( ivt );

    }

    @Test
    @Transactional
    public void testInventory () throws Exception {
        final String s1 = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( s1.contains( "123" ) );
        assertTrue( s1.contains( "234" ) );
        assertTrue( s1.contains( "345" ) );
        assertTrue( s1.contains( "456" ) );

    }

    // @Test
    // @Transactional
    // @WithMockUser ( authorities = User.MANAGER )
    // public void testUpdateInventory () throws Exception {
    // // Create ingredients
    // // final Ingredient coffee = new Ingredient( "Coffee", 5 );
    // // final Ingredient milk = new Ingredient( "Milk", 5 );
    // // ingredientService.save( coffee );
    // // ingredientService.save( milk );
    //
    // // Define inventory to update ingredients with
    // // final ArrayList<Ingredient> ingredients = new ArrayList<>();
    // // ingredients.add( coffee );
    // // ingredients.add( milk );
    // final Inventory inv = new Inventory();
    // inv.addIngredient( "Coffee", 5 );
    // inv.addIngredient( "Milk", 5 );
    //
    // System.out.println( TestUtils.asJsonString( inv ) );
    //
    // final String s1 = mvc.perform( put( "/api/v1/inventory" ).contentType(
    // MediaType.APPLICATION_JSON )
    // .content( TestUtils.asJsonString( inv ) )
    // ).andReturn().getResponse().getContentAsString();
    //
    // assertTrue( s1.contains( "123" ) );
    // assertTrue( s1.contains( "239" ) );
    // assertTrue( s1.contains( "350" ) );
    // assertTrue( s1.contains( "456" ) );
    // }

}
