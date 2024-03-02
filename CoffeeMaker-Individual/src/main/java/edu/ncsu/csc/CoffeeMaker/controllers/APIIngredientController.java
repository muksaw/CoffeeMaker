package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Ingredients.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author James Kocak
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {
    /**
     * Ingredient Service Object to be wired in by Spring to allow for
     * manipulating the Ingredient model.
     */
    @Autowired
    private IngredientService service;

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all ingredients
     */
    @GetMapping ( BASE_PATH + "ingredients" )
    public List<Ingredient> getIngredients () {
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific ingredient, as
     * indicated by the path variable provided (the name of the ingredient
     * desired)
     *
     * @param name
     *            ingredient name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable ( "name" ) final String name ) {
        // Get Ingredient
        final Ingredient ingredient = service.findByName( name );

        // Check if ingredient was found
        if ( ingredient == null ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        // Return positive respone if ingredient found
        return new ResponseEntity( ingredient, HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting an Ingredient from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the ingredient to delete (as a path variable)
     *
     * @param name
     *            The name of the Ingredient to delete
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @DeleteMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable ( "name" ) final String name ) {
        // Get Ingredient
        final Ingredient ingredient = service.findByName( name );

        // Check if ingredient exists
        if ( ingredient == null ) {
            return new ResponseEntity( errorResponse( "No Ingredient found for name " + name ), HttpStatus.NOT_FOUND );
        }

        // Deletes ingredient and returns positive response
        service.delete( ingredient );
        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail.
     *
     * @param name
     *            The name of the ingredient
     * @param amount
     *            The amount of the ingredient
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "ingredients/{name}/{amount}" )
    public ResponseEntity postIngredient ( @PathVariable ( "name" ) final String name,
            @PathVariable ( "amount" ) final Integer amount ) {
        // Check if ingredient already exists
        if ( service.findByName( name ) != null ) {
            return new ResponseEntity( errorResponse( "Ingredient with the name " + name + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        // Save and return positive response
        service.save( new Ingredient( name, amount ) );
        return new ResponseEntity( successResponse( name + " successfully created" ), HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's Ingredients.
     * This will update the Ingredient of the CoffeeMaker by adding amounts from
     * the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param name
     *            name of ingredient
     *
     * @param amount
     *            amount of ingredient
     * 
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "ingredients/{name}/{amount}" )
    public ResponseEntity putIngredient ( @PathVariable ( "name" ) final String name,
            @PathVariable ( "amount" ) final Integer amount ) {
        // Check if ingredient does not exist
        final Ingredient ingredient = service.findByName( name );
        if ( ingredient == null ) {
            return new ResponseEntity( errorResponse( "Ingredient with the name " + name + " does not exist" ),
                    HttpStatus.NOT_FOUND );
        }

        // Update Ingredient, save, and return positive response
        ingredient.setAmount( amount );
        service.save( ingredient );
        return new ResponseEntity( successResponse( name + " successfully updated" ), HttpStatus.OK );
    }

}
