package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Orders.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author James Kocak
 * @author Justin Kuethe
 * @author Mukul Sauhta
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order model.
     */
    @Autowired
    private OrderService          service;
    /**
     * OrderService object, to be autowired in by Spring to allow for looking up
     * recipes.
     */
    @Autowired
    private RecipeService         recipeService;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * looking up inventory.
     */
    @Autowired
    private InventoryService      inventoryService;

    /** Threads to run and perform busy waiting */
    private final ExecutorService threads = Executors.newCachedThreadPool();

    /**
     * REST API method to provide GET access to a specific order, as indicated
     * by the path variable provided (the order ID of the order desired)
     *
     * @param orderId
     *            The ID associated with the desired order
     * @return Response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/{orderId}" )
    public ResponseEntity getOrderById ( @PathVariable ( "orderId" ) final Integer orderId ) {
        final Order ord = service.findByOrderNumber( orderId );

        if ( null == ord ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ord, HttpStatus.OK );
    }

    /**
     * Rest API method that returns all orders in the system to a staff/manager.
     * Has logic to check if the user provided parameters for the status of
     * orders.
     *
     * @param status
     *            the status of the order the status may be "open", "closed", or
     *            not provided
     * @return all orders if status is not provided, no orders if the user is
     *         not a staff member, filtered list based on status in request
     *         parameter
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public List<Order> getOrders ( @RequestParam ( name = "status", required = false ) final String status ) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if ( isAuthorized( a, User.CUSTOMER ) && !isAuthorized( a, User.STAFF ) ) {
            return service.findByActiveAndUserName( true, a.getName() );
        }

        if ( !isAuthorized( a, User.STAFF ) ) {
            return null;
        }

        if ( status == null ) {
            return service.findAll();
        }

        if ( status.equals( "open" ) ) {
            final List<Order> l = service.findByReadyForPickupFalse();
            return l;
        }
        else if ( status.equals( "closed" ) ) {
            return service.findByActiveFalse();
        }
        else {
            return null;
        }

    }

    /**
     * REST API method to provide GET access for the order with the associated
     * order number. This endpoint serves as a long-polling endpoint that will
     * accept requests and responds with the order as JSON only when the order
     * changes.
     *
     * @param orderNumber
     *            The orderNumber associated with the desired order
     * @return A JSON representation of the order
     */
    @GetMapping ( BASE_PATH + "/orders/status/{orderNumber}" )
    public DeferredResult<ResponseEntity< ? >> orderIsReady (
            @PathVariable ( "orderNumber" ) final Integer orderNumber ) {
        // Create deferred result
        final DeferredResult<ResponseEntity< ? >> deferredResult = new DeferredResult<>();

        // Get order from database
        final Order order = service.findByOrderNumber( orderNumber );
        if ( order == null ) {
            deferredResult.setResult( new ResponseEntity<>(
                    "Order with " + orderNumber + " as the order number does not exist in database.",
                    HttpStatus.NOT_FOUND ) );
            return deferredResult;
        }

        // Check to see if order is ready
        if ( order.isReady() ) {
            deferredResult.setResult( new ResponseEntity<>( order, HttpStatus.OK ) );
            return deferredResult;
        }

        // If order is not ready, wait until it is ready
        threads.submit( () -> {
            // Constantly check until the order becomes ready
            Order o = service.findById( (Long) order.getId() );
            int tries = 0; // quit if the client does not re-request
            final Random r = new Random();
            while ( !o.isReady() && tries < 3 ) {
                try {
                    Thread.sleep( 3000 + r.nextInt( 1000 ) ); // Sleep for
                                                              // between 3 and 4
                                                              // seconds
                }
                catch ( final InterruptedException e ) {
                    deferredResult.setResult( new ResponseEntity( o, HttpStatus.OK ) );
                    Thread.currentThread().interrupt();
                    return;
                }
                o = service.findById( (Long) order.getId() );
                tries++;
            }

            // Once the order is ready, set the result for the DeferredResult
            deferredResult.setResult( new ResponseEntity( o, HttpStatus.OK ) );
        } );

        // Return the order once it is ready
        return deferredResult;
    }

    /**
     * REST API method to provide POST access to the Order model. This method
     * marks the order as fulfilled.
     *
     * @param orderId
     *            The orderId associated with the desired order
     * @return ResponseEntity indicating success if the Order could be updated,
     *         or an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/orders/{orderId}" )
    public ResponseEntity fulfillOrder ( @PathVariable ( "orderId" ) final Long orderId,
            @RequestBody ( required = false ) final Integer payment ) {

        final Order ord = service.findById( orderId );
        if ( ord == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
        // if staff, set order to ready
        if ( isAuthorized( a, User.STAFF ) ) {
            if ( ord.isReady() ) {
                return new ResponseEntity( errorResponse( "Order already Made" ), HttpStatus.BAD_REQUEST );
            }
            if ( payment == null || payment < ord.getTotal() ) {
                return new ResponseEntity( errorResponse( "Insufficient Payment" ), HttpStatus.BAD_REQUEST );
            }

            // Check if enough ingredients for each recipe
            final Inventory inventory = inventoryService.getInventory();
            final List<Recipe> recipes = ord.getRecipes();
            for ( final Recipe recipe : recipes ) {
                if ( !inventory.useIngredients( recipe ) ) {
                    return new ResponseEntity( errorResponse( "Insufficient Inventory" ), HttpStatus.BAD_GATEWAY );
                }
            }

            // Update inventory
            inventoryService.save( inventory );

            // Set ready and save
            ord.setReady( true );
            service.save( ord );
            return new ResponseEntity( successResponse( Integer.toString( payment - ord.getTotal() ) ), HttpStatus.OK );
        } // if customer/guest, the order is picked up
        else {
            ord.setActive( false );
            service.save( ord );
            return new ResponseEntity(
                    successResponse( "Order number " + ord.getOrderNumber() + " has been picked up." ), HttpStatus.OK );
        }
    }

    /**
     * REST API method to provide POST access to the Order model. This is used
     * to create a new Order by automatically converting the JSON RequestBody
     * provided to an Order object. Invalid JSON will fail.
     *
     * @param order
     *            The valid Order to be saved
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final Map<String, Integer> items ) {
        final Order order = new Order();
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserName( username );
        order.setTotal( 0 );
        order.setGuest( username != null );

        for ( final Entry<String, Integer> e : items.entrySet() ) {
            final Recipe r = recipeService.findByName( e.getKey() );
            if ( r == null ) {
                return new ResponseEntity( "Recipe not found", HttpStatus.BAD_REQUEST );
            }
            for ( int i = 0; i < e.getValue(); i++ ) {
                order.addRecipe( r );
                order.setTotal( order.getTotal() + r.getPrice() );
            }
        }

        int i = 0;
        order.assignOrderNumber( i );
        // this is not a good solution at the moment
        while ( i < 1000 && service.findByOrderNumber( order.getOrderNumber() ) != null ) {
            order.assignOrderNumber( ++i );
        }
        if ( i == 1000 ) {
            return new ResponseEntity( "The database is full!", HttpStatus.CONFLICT );
        }

        order.setActive( true );
        service.save( order );
        return new ResponseEntity( order, HttpStatus.CREATED );
    }
}
