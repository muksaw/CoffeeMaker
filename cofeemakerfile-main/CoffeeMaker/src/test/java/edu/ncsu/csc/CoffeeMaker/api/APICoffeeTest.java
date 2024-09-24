package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    @Autowired
    private InventoryService      iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        ivt.addIngredient( "Chocolate", 15 );
        ivt.addIngredient( "Coffee", 15 );
        ivt.addIngredient( "Milk", 15 );
        ivt.addIngredient( "Sugar", 15 );
        iService.save( ivt );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        recipe.addIngredient( new Ingredient( "Coffee", 3 ) );
        recipe.addIngredient( new Ingredient( "Milk", 1 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 1 ) );
        recipe.addIngredient( new Ingredient( "Chocolate", 0 ) );
        service.save( recipe );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage1 () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final String name = "Coffee";
        final Inventory ivt = iService.getInventory();
        final List<Ingredient> ings = ivt.getIngredientList();
        for ( final Ingredient i : ings ) {
            if ( i.getName().equals( name ) ) {
                i.setUnits( 0 );
            }
        }

        iService.save( ivt );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );

    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage4 () throws Exception {

        final String wrongName = "Mocha";
        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", wrongName ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 50 ) ) )
                .andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage5 () throws Exception {

        mvc.perform( post( "/api/v1/makecoffee/" ) ).andExpect( status().isNotFound() );
    }

    /**
     * no recipe selected with correct message thrown
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testPurchaseBeverage6 () throws Exception {
        final String wrongName = "Mocha";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", wrongName ) )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 50 ) ) )
                .andExpect( status().isNotFound() ).andExpect( jsonPath( "$.message" ).value( "No recipe selected" ) );
    }
}
