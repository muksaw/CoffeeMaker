package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@RestController
public class APIIngredientController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private IngredientService ingredientService;
    
    @Autowired
    private InventoryService inventoryService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = ingredientService.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ingr, HttpStatus.OK );
    }
    
    
    @SuppressWarnings("rawtypes")
	@PostMapping ( BASE_PATH + "/ingredient" )
    public ResponseEntity createIngredient(@RequestBody final Ingredient ingredient) {
            // check if the ingredient with the same name already exists
            if (ingredientService.findByName(ingredient.getName()) != null) {
                return new ResponseEntity<>("Ingredient with the name " + ingredient.getName() + " already exists",
                        HttpStatus.CONFLICT);
            }  // we might need to rremove this 
            
            // get inventory
           
//            if(inventoryService.getInventory() == null) {
//            	
//            }
            
            // make sure the ingredient doesnt exist in the list
            // then add it to the list
            // save it to the list
            Inventory inv = inventoryService.getInventory();
            List<Ingredient> ingredients = inv.getIngredientList();
            for(int i = 0; i < ingredients.size(); i++) {
            	if(ingredients.get(i).getName().equals(ingredient.getName())){
                    return new ResponseEntity<>("Ingredient with the name " + ingredient.getName() + " already exists",
                            HttpStatus.CONFLICT);
            	} 
            }
            inv.addIngredient(ingredient.getName(), ingredient.getUnits());
            inventoryService.save(inv);

            


            return new ResponseEntity<>(ingredient, HttpStatus.CREATED);
    }
    
    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param name
     *            The name of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@DeleteMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name ) {
        final Ingredient ingr = ingredientService.findByName( name );
        if ( null == ingr ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        ingredientService.delete( ingr );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }
    
   
}
