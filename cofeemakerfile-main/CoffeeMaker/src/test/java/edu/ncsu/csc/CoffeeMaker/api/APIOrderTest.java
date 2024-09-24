package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests the APIOrderController class.
 *
 * @author Mukul Sauhta
 * @author James Kocak
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIOrderTest {
    @Autowired
    private MockMvc               mvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private OrderService          orderService;
    @Autowired
    private RecipeService         recipeService;
    @Autowired
    private IngredientService     ingredientService;
    @Autowired
    private InventoryService      inventoryService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    @Transactional
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        orderService.deleteAll();
        recipeService.deleteAll();
        ingredientService.deleteAll();
        inventoryService.deleteAll();
    }

    /**
     * Tests the endpoint to grab a single order by order number.
     *
     * @throws Exception
     *             Endpoints can throw an exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.CUSTOMER )
    public void testGetOrderById () throws Exception {
        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipe
        final Recipe recipeOne = new Recipe();
        recipeOne.setName( "Milk Coffee" );
        recipeOne.setPrice( 4 );
        recipeOne.addIngredient( coffee );
        recipeOne.addIngredient( milk );

        // Save recipe
        recipeService.save( recipeOne );

        // Add recipe to order and save it
        final Order order = new Order( "jokocak", 1, 4, true, false, false );
        order.addRecipe( recipeOne );
        orderService.save( order );

        // Call endpoint to grab order number 1
        final String orderString = mvc.perform( get( "/api/v1/orders/1" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check if order is correct
        Assertions.assertTrue( orderString.contains( "jokocak" ) );
        Assertions.assertTrue( orderString.contains( "Milk Coffee" ) );
        Assertions.assertTrue( orderString.contains( "1" ) );
        Assertions.assertTrue( orderString.contains( "4" ) );

        // Try to find an order that does not exist
        mvc.perform( get( "/api/v1/orders/2" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
    }

    /**
     * Tests the endpoint to grab all orders in the system for a staff/manager.
     *
     * @throws Exception
     *             Endpoints can throw an exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testGetOrders () throws Exception {
        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient coffeeTwo = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipes
        final Recipe recipeOne = new Recipe();
        recipeOne.setName( "Milk Coffee" );
        recipeOne.setPrice( 4 );
        recipeOne.addIngredient( coffee );
        recipeOne.addIngredient( milk );
        final Recipe recipeTwo = new Recipe();
        recipeTwo.setName( "Coffee" );
        recipeTwo.setPrice( 2 );
        recipeTwo.addIngredient( coffeeTwo );

        // Save recipes
        recipeService.save( recipeOne );
        recipeService.save( recipeTwo );

        // Add recipe to order and save it
        Order order = new Order( "jokocak", 1, 4, true, false, false );
        order.addRecipe( recipeOne );
        final Order orderTwo = new Order( "bob", 2, 6, true, false, false );
        orderTwo.addRecipe( recipeOne );
        orderTwo.addRecipe( recipeTwo );

        // Save orders
        orderService.save( order );
        orderService.save( orderTwo );

        // Call endpoint to grab orders
        String status = "open";
        String ordersString = mvc
                .perform( get( "/api/v1/orders" ).param( "status", status ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check if orders contains the right information
        Assertions.assertTrue( ordersString.contains( "jokocak" ) );
        Assertions.assertTrue( ordersString.contains( "Milk Coffee" ) );
        Assertions.assertTrue( ordersString.contains( "1" ) );
        Assertions.assertTrue( ordersString.contains( "4" ) );
        Assertions.assertTrue( ordersString.contains( "bob" ) );
        Assertions.assertTrue( ordersString.contains( "Coffee" ) );
        Assertions.assertTrue( ordersString.contains( "2" ) );
        Assertions.assertTrue( ordersString.contains( "6" ) );

        // Close one order and see if they show up
        // Add recipe to order and save it
        order = orderService.findByOrderNumber( 1 );
        order.setReady( true );
        orderService.save( order );

        // Get open orders and check that only order 2 is in there
        ordersString = mvc
                .perform( get( "/api/v1/orders" ).param( "status", status ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check if orders contains the right information
        Assertions.assertFalse( ordersString.contains( "jokocak" ) );
        Assertions.assertTrue( ordersString.contains( "bob" ) );
        Assertions.assertTrue( ordersString.contains( "Milk Coffee" ) );
        Assertions.assertTrue( ordersString.contains( "Coffee" ) );
        Assertions.assertTrue( ordersString.contains( "2" ) );
        Assertions.assertTrue( ordersString.contains( "6" ) );

        // Label order as picked up
        order.setActive( false );
        orderService.save( order );

        // Get closed orders and check that only order 1 is in there
        status = "closed";
        ordersString = mvc
                .perform( get( "/api/v1/orders" ).param( "status", status ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check if orders contains the right information
        Assertions.assertFalse( ordersString.contains( "bob" ) );
        Assertions.assertTrue( ordersString.contains( "jokocak" ) );
        Assertions.assertTrue( ordersString.contains( "Milk Coffee" ) );
        Assertions.assertTrue( ordersString.contains( "1" ) );
        Assertions.assertTrue( ordersString.contains( "4" ) );
    }

    /**
     * Tests the long polling of the orderIsReady endpoint.
     *
     * @throws Exception
     *             Endpoints can throw exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testOrderIsReady () throws Exception {
        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipe
        final Recipe recipe = new Recipe();
        recipe.setName( "Milk Coffee" );
        recipe.setPrice( 4 );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );

        // Save recipe
        recipeService.save( recipe );

        // Add recipe to order and save it
        final Order order = new Order( "jokocak", 1, 4, true, false, false );
        order.addRecipe( recipe );
        orderService.save( order );

        // This calls the orderIsReady endpoint and waits to test timeout
        MvcResult asyncListener = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/orders/status/1" ) )
                .andExpect( request().asyncStarted() ).andReturn();

        ( (MockAsyncContext) asyncListener.getRequest().getAsyncContext() ).getListeners().get( 0 ).onTimeout( null );

        // Request response
        String response = mvc.perform( asyncDispatch( asyncListener ) ).andReturn().getResponse().getContentAsString();

        // Assert there is no response
        Assertions.assertEquals( "", response );

        // Create order that is ready
        final Order orderTwo = new Order( "bob", 2, 4, true, true, false );
        orderTwo.addRecipe( recipe );
        orderService.save( orderTwo );

        // This calls the orderIsReady endpoint and waits to test timeout
        asyncListener = mvc.perform( MockMvcRequestBuilders.get( "/api/v1/orders/status/2" ) )
                .andExpect( request().asyncStarted() ).andReturn();

        // Request response
        response = mvc.perform( asyncDispatch( asyncListener ) ).andReturn().getResponse().getContentAsString();

        // Check for JSON of order in response
        Assertions.assertTrue( response.contains( "bob" ) );
        Assertions.assertTrue( response.contains( "Milk Coffee" ) );
        Assertions.assertTrue( response.contains( "Coffee" ) );
        Assertions.assertTrue( response.contains( "Milk" ) );
        Assertions.assertTrue( response.contains( "5" ) );
        Assertions.assertTrue( response.contains( "4" ) );
        Assertions.assertTrue( response.contains( "2" ) );
    }

    /**
     * This method tests the fulfill order functionality endpoint with a staff.
     *
     * @throws UnsupportedEncodingException
     *             Endpoints can throw an exception
     * @throws Exception
     *             Endpoints can throw an exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testFulfillOrderStaff () throws UnsupportedEncodingException, Exception {
        // Inventory
        Inventory inventory = inventoryService.getInventory();
        inventory.addIngredient( "Coffee", 50 );
        inventory.addIngredient( "Milk", 50 );
        inventoryService.save( inventory );

        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipe
        final Recipe recipe = new Recipe();
        recipe.setName( "Milk Coffee" );
        recipe.setPrice( 4 );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );

        // Save recipe
        recipeService.save( recipe );

        // Add recipe to order and save it
        final Order order = new Order( "jokocak", 1, 4, true, false, false );
        order.addRecipe( recipe );
        orderService.save( order );
        final Serializable id = order.getId();

        // Try to fulfill order that does not exist
        mvc.perform( put( "/api/v1/orders/2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 4 ) ) ).andExpect( status().isNotFound() );

        // Try to pay with nothing
        mvc.perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isBadRequest() );

        // Try to pay with insufficient amount
        mvc.perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 3 ) ) ).andExpect( status().isBadRequest() );

        // Get response from Mock MVC
        final String response = mvc
                .perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( 4 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check if inventory updated
        inventory = inventoryService.getInventory();
        for ( final Ingredient ingredient : inventory.getIngredientList() ) {
            if ( ingredient.getUnits() != 45 ) {
                fail();
            }
        }

        // Check change, contents of response
        Assertions.assertTrue( response.contains( "0" ) );

        // Try to fulfill an order that is already ready
        mvc.perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 4 ) ) ).andExpect( status().isBadRequest() );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.STAFF )
    public void testInventoryUpdate () throws UnsupportedEncodingException, Exception {
        // Inventory
        Inventory inventory = inventoryService.getInventory();
        inventory.addIngredient( "Coffee", 50 );
        inventory.addIngredient( "Milk", 50 );
        inventoryService.save( inventory );

        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient coffeeTwo = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipe
        final Recipe recipe = new Recipe();
        recipe.setName( "Milk Coffee" );
        recipe.setPrice( 4 );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );
        final Recipe recipeTwo = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 4 );
        recipe.addIngredient( coffeeTwo );

        // Save recipe
        recipeService.save( recipe );
        recipeService.save( recipeTwo );

        // Add recipe to order and save it
        final Order order = new Order( "jokocak", 1, 4, true, false, false );
        order.addRecipe( recipe );
        order.addRecipe( recipeTwo );
        orderService.save( order );
        final Serializable id = order.getId();

        // Get response from Mock MVC
        mvc.perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 4 ) ) ).andExpect( status().isOk() );

        // Check if inventory updated
        inventory = inventoryService.getInventory();
        for ( final Ingredient ingredient : inventory.getIngredientList() ) {
            if ( ingredient.getName().equals( "Coffee" ) && ingredient.getUnits() != 40 ) {
                fail();
            }
            else if ( ingredient.getName().equals( "Milk" ) && ingredient.getUnits() != 45 ) {
                fail();
            }
        }
    }

    /**
     * This method tests the fulfill order functionality endpoint with a
     * customer.
     *
     * @throws UnsupportedEncodingException
     *             Endpoints can throw an exception
     * @throws Exception
     *             Endpoints can throw an exception
     */
    @Test
    @Transactional
    @WithMockUser ( authorities = User.CUSTOMER )
    public void testFulfillOrderCustomer () throws UnsupportedEncodingException, Exception {
        // Create ingredients
        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient milk = new Ingredient( "Milk", 5 );

        // Create recipe
        final Recipe recipe = new Recipe();
        recipe.setName( "Milk Coffee" );
        recipe.setPrice( 4 );
        recipe.addIngredient( coffee );
        recipe.addIngredient( milk );

        // Save recipe
        recipeService.save( recipe );

        // Add recipe to order and save it
        final Order order = new Order( "jokocak", 1, 4, true, true, false );
        order.addRecipe( recipe );
        orderService.save( order );
        final Serializable id = order.getId();

        // Get response from Mock MVC
        final String response = mvc.perform( put( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Check change, contents of response
        Assertions.assertTrue( response.contains( "Order number 1 has been picked up." ) );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.CUSTOMER )
    public void testCreateOrder () throws Exception {
        try {
            mvc.perform( get( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON ) )
                    .andExpect( status().isOk() );
        }
        catch ( final IllegalArgumentException e ) {
            // empty
        }

        // Create recipes
        final Recipe recipeOne = new Recipe();
        recipeOne.setName( "Milk Coffee" );
        recipeOne.setPrice( 4 );
        recipeOne.addIngredient( new Ingredient( "Coffee", 5 ) );
        recipeOne.addIngredient( new Ingredient( "Milk", 5 ) );

        final Recipe recipeTwo = new Recipe();
        recipeTwo.setName( "Milk" );
        recipeTwo.setPrice( 2 );
        recipeTwo.addIngredient( new Ingredient( "Milk", 5 ) );

        final Recipe recipeThree = new Recipe();
        recipeThree.setName( "Coffee" );
        recipeThree.setPrice( 2 );
        recipeThree.addIngredient( new Ingredient( "Coffee", 5 ) );

        // Save recipes
        recipeService.save( recipeOne );
        recipeService.save( recipeTwo );
        recipeService.save( recipeThree );

        // Create list of items to order
        final Map<String, Integer> order = new HashMap<>();
        order.put( "Milk Coffee", 1 );
        order.put( "Coffee", 2 );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ) ).andExpect( status().isCreated() );
        assertEquals( 1, orderService.findAll().size() );

        final Map<String, Integer> orderTwo = new HashMap<>();
        orderTwo.put( "Milk", 1 );
        orderTwo.put( "Coffee", 1 );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orderTwo ) ) ).andExpect( status().isCreated() );
        assertEquals( 2, orderService.findAll().size() );
        mvc.perform( get( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/orders" ).param( "status", "open" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/orders" ).param( "status", "closed" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( put( "/api/v1/orders/1" ).contentType( MediaType.APPLICATION_JSON ) );
    }

    @Test
    @Transactional
    @WithMockUser ( authorities = User.CUSTOMER )
    public void testLongPolling () throws Exception {
        // Create and save recipe
        final Recipe recipeOne = new Recipe();
        recipeOne.setName( "Milk Coffee" );
        recipeOne.setPrice( 4 );
        recipeOne.addIngredient( new Ingredient( "Coffee", 5 ) );
        recipeOne.addIngredient( new Ingredient( "Milk", 5 ) );
        recipeService.save( recipeOne );

        final Map<String, Integer> order = new HashMap<>();
        order.put( "Milk Coffee", 1 );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order ) ) ).andExpect( status().isCreated() );
        assertEquals( 1, orderService.findAll().size() );

        // Long polling get
        mvc.perform( get( "/api/v1/orders/status/1" ).contentType( MediaType.APPLICATION_JSON ) );

        // Set order to ready
        mvc.perform( put( "/api/v1/orders/1" ).contentType( MediaType.APPLICATION_JSON ) );

        mvc.perform( get( "/api/v1/orders/status/1" ).contentType( MediaType.APPLICATION_JSON ) );

    }
}
