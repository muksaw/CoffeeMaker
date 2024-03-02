package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TestDatabaseInteraction {
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();

        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 1 );
        r.addIngredient( new Ingredient( "Coffee", 1 ) );
        r.addIngredient( new Ingredient( "Milk", 0 ) );
        r.addIngredient( new Ingredient( "Sugar", 0 ) );
        r.addIngredient( new Ingredient( "Chocolate", 0 ) );
        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );
        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( 1, (int) dbRecipe.getPrice() );

        final List<Ingredient> ingredients = dbRecipe.getIngredients();
        final int size = ingredients.size();
        for ( int i = 0; i < size; i++ ) {
            final String type = ingredients.get( i ).getIngredient();
            switch ( type ) {
                case "Coffee":
                    Assertions.assertEquals( 1, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Milk":
                    Assertions.assertEquals( 0, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Sugar":
                    Assertions.assertEquals( 0, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Chocolate":
                    Assertions.assertEquals( 0, (int) ingredients.get( i ).getAmount() );
                    break;
                default:
                    continue;
            }
        }

    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testFindByName () {
        recipeService.deleteAll();

        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 1 );
        r.addIngredient( new Ingredient( "Coffee", 1 ) );
        r.addIngredient( new Ingredient( "Milk", 0 ) );
        r.addIngredient( new Ingredient( "Sugar", 0 ) );
        r.addIngredient( new Ingredient( "Chocolate", 0 ) );
        recipeService.save( r );

        assertEquals( r, recipeService.findByName( "Black Coffee" ) );
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        recipeService.deleteAll();

        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 1 );
        r.addIngredient( new Ingredient( "Coffee", 1 ) );
        r.addIngredient( new Ingredient( "Milk", 0 ) );
        r.addIngredient( new Ingredient( "Sugar", 0 ) );
        r.addIngredient( new Ingredient( "Chocolate", 0 ) );
        recipeService.save( r );

        List<Recipe> dbRecipes = recipeService.findAll();
        assertEquals( 1, dbRecipes.size() );
        Recipe dbRecipe = dbRecipes.get( 0 );

        final List<Ingredient> ingredients = dbRecipe.getIngredients();

        dbRecipe.setPrice( 15 );
        ingredients.get( 0 ).setAmount( 12 );
        recipeService.save( dbRecipe );

        dbRecipes = recipeService.findAll();
        assertEquals( 1, dbRecipes.size() );
        dbRecipe = dbRecipes.get( 0 );

        assertEquals( 15, (int) dbRecipe.getPrice() );
        assertEquals( 12, (int) ingredients.get( 0 ).getAmount() );
    }
}
