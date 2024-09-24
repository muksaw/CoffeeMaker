package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

public class TestDatabaseInteraction {

    @Autowired
    private RecipeService     recipeService;

    @Autowired
    InventoryService          inventoryService;

    @Autowired
    private IngredientService ingredientService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    @Transactional
    public void setup () {
        inventoryService.deleteAll();
        recipeService.deleteAll();
        ingredientService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        final Recipe r = new Recipe();
        r.setName( "Latte" );
        r.setPrice( 400 );
        final Ingredient coffee = new Ingredient( "Coffee", 10 );
        final Ingredient milk = new Ingredient( "Milk", 3 );

        r.addIngredient( coffee );
        r.addIngredient( milk );
        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = recipeService.findByName( "Latte" );
        assertNotNull( dbRecipe );

        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        assertEquals( r.getIngredients().size(), 2 );
        assertTrue( r.getIngredients().contains( coffee ) );
        assertTrue( r.getIngredients().contains( milk ) );

        assertTrue( dbRecipe.equals( dbRecipes.get( 0 ) ) );

        assertTrue( ingredientService.findAll().contains( coffee ) );
        assertTrue( ingredientService.findAll().contains( milk ) );
        assertEquals( 2, ingredientService.findAll().size() );

    }

    /**
     * tests edit recipe
     */
    @Test
    @Transactional
    public void testEditRecipe () {
        // TODO functionality not supported yet. we need to change this when
        // updating
        final Recipe r = new Recipe();
        r.setName( "vanilla latte" );
        r.addIngredient( new Ingredient( "Vanilla", 1 ) );
        r.addIngredient( new Ingredient( "Espresso", 2 ) );
        r.addIngredient( new Ingredient( "Milk", 3 ) );
        r.addIngredient( new Ingredient( "Sugar", 4 ) );

        assertTrue( r.getIngredients().stream()
                .anyMatch( i -> i.getName().equals( "Vanilla" ) && 1 == (int) i.getUnits() ) );
        assertTrue( r.getIngredients().stream()
                .anyMatch( i -> i.getName().equals( "Espresso" ) && 2 == (int) i.getUnits() ) );
        assertTrue(
                r.getIngredients().stream().anyMatch( i -> i.getName().equals( "Milk" ) && 3 == (int) i.getUnits() ) );
        assertTrue(
                r.getIngredients().stream().anyMatch( i -> i.getName().equals( "Sugar" ) && 4 == (int) i.getUnits() ) );

        r.setPrice( 5 );
        recipeService.save( r );

        assertEquals( 1, recipeService.count() );

        final Recipe dbRecipe = recipeService.findByName( "vanilla latte" );

        dbRecipe.setPrice( 12 );
        dbRecipe.editUnits( "Vanilla", 6 );
        dbRecipe.editUnits( "Espresso", 7 );
        dbRecipe.editUnits( "Milk", 8 );
        dbRecipe.editUnits( "Sugar", 9 );

        recipeService.save( dbRecipe );

        assertEquals( 1, recipeService.count() ); // verifies that it returns a
                                                  // list of only a single
                                                  // element because it
        // should be updating the coffee not making a new one
        final Recipe editedRecipe = recipeService.findByName( "vanilla latte" );
        assertEquals( 12, (int) editedRecipe.getPrice() );
        final List<Ingredient> ingredients = editedRecipe.getIngredients();
        // passing a cool lambda here
        // essentially asking if the list contains an element where the lambda
        // returns true
        // that is, to ask if the element with the matching name and id exist
        // in the list
        assertTrue( ingredients.stream().anyMatch( i -> i.getName().equals( "Vanilla" ) && 6 == (int) i.getUnits() ) );
        assertTrue( ingredients.stream().anyMatch( i -> i.getName().equals( "Espresso" ) && 7 == (int) i.getUnits() ) );
        assertTrue( ingredients.stream().anyMatch( i -> i.getName().equals( "Milk" ) && 8 == (int) i.getUnits() ) );
        assertTrue( ingredients.stream().anyMatch( i -> i.getName().equals( "Sugar" ) && 9 == (int) i.getUnits() ) );

    }
}
