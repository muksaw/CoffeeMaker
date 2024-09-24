package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * final User newJustin = new User(); newJustin.setUserName( "justin" );
 * newJustin.setPassword( "carrots" ); newJustin.grantManager(); // should not
 * be allowed by backend
 *
 * mvc.perform( put( "/api/v1/users/customer" ).contentType(
 * MediaType.APPLICATION_JSON ) .content( TestUtils.asJsonString( newJustin ) )
 * ).andExpect( status().isOk() );
 *
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIRecipeController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService service;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipies
     */
    @GetMapping ( BASE_PATH + "/recipes" )
    public List<Recipe> getRecipes () {
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity getRecipe ( @PathVariable ( "name" ) final String name ) {
        final Recipe recipe = service.findByName( name );
        return null == recipe
                ? new ResponseEntity( errorResponse( "No recipe found with name " + name ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( recipe, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param recipe
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity createRecipe ( @RequestBody final Recipe recipe ) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if ( !isAuthorized( a, User.STAFF ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        if ( recipe.getIngredients() == null || recipe.getIngredients().size() == 0 ) {
            return new ResponseEntity( errorResponse( "Cannot add a Recipe with no Ingredients" ),
                    HttpStatus.BAD_REQUEST );
        }

        if ( recipe.getPrice() < 0 ) {
            return new ResponseEntity( errorResponse( "Price must be greater than zero." ), HttpStatus.BAD_REQUEST );
        }
        if ( recipe.getIngredients().stream().anyMatch( ( i ) -> i.getUnits() <= 0 ) ) {
            return new ResponseEntity( errorResponse( "Ingredient Amounts must be greater than zero." ),
                    HttpStatus.BAD_REQUEST );
        }
        if ( recipe.getIngredients().stream().anyMatch( ( i ) -> i.getName() == null || i.getName().length() == 0 ) ) {
            return new ResponseEntity( errorResponse( "Ingredients must all have names" ), HttpStatus.BAD_REQUEST );
        }
        if ( null != service.findByName( recipe.getName() ) ) {
            return new ResponseEntity( errorResponse( "Recipe with the name " + recipe.getName() + " already exists" ),
                    HttpStatus.BAD_REQUEST );
        }
        if ( service.findAll().size() < 3 ) {
            service.save( recipe );
            return new ResponseEntity( successResponse( recipe.getName() + " successfully created" ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for recipe " + recipe.getName() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

    }

    /**
     * REST API method to provide PUT modifications to the Recipe Model. By
     * making a put request to this API endpoint, we indicate that we would like
     * to update the recipe with the new values in the JSON.
     *
     * @param recipe
     *            The VALID modifications to the recipe.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity editRecipe ( @RequestBody final Recipe recipe ) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if ( isAuthorized( a, User.STAFF ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }
        final String name = recipe.getName();
        final Recipe r = service.findByName( name );
        if ( r == null ) {
            return new ResponseEntity( errorResponse( "Recipe with the name " + name + " not found" ),
                    HttpStatus.NOT_FOUND );
        }
        try {

            r.update( recipe );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( errorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
        }
        service.save( r );
        return new ResponseEntity( successResponse( name + " successfully updated" ), HttpStatus.OK );
    }

    /**
     *
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API end-point and indicating
     * the recipe to delete (as a path variable)
     *
     * @param name
     *            The name of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String name ) {
        final Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if ( isAuthorized( a, User.STAFF ) ) {
            return new ResponseEntity( HttpStatus.FORBIDDEN );
        }

        final Recipe recipe = service.findByName( name );
        if ( null == recipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        service.delete( recipe );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }
}
