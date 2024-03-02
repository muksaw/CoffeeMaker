package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        r1.addIngredient( new Ingredient( "Coffee", 1 ) );
        r1.addIngredient( new Ingredient( "Milk", 0 ) );
        r1.addIngredient( new Ingredient( "Sugar", 0 ) );
        r1.addIngredient( new Ingredient( "Chocolate", 0 ) );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( 12 );
        r1.addIngredient( new Ingredient( "Coffee", -12 ) );
        r1.addIngredient( new Ingredient( "Milk", 0 ) );
        r1.addIngredient( new Ingredient( "Sugar", 0 ) );
        r1.addIngredient( new Ingredient( "Chocolate", 0 ) );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, -50, 3, 1, 1, 0 );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        try {
            final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        try {
            final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        try {
            final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );

            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        try {
            final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Test how it reacts when we try to delete a recipe that doesn't
        // exist.
        service.delete( r1 );
        service.deleteAll();
        // Now test how deleting and saving consecutively to ensure
        // functionality
        final Recipe r2 = createRecipe( "Latte", 10, 3, 1, 1, 0 );
        service.save( r2 );
        service.delete( r2 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );
        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        final List<Ingredient> ingredients = retrieved.getIngredients();
        final int size = ingredients.size();
        for ( int i = 0; i < size; i++ ) {
            final String type = ingredients.get( i ).getIngredient();
            switch ( type ) {
                case "Coffee":
                    Assertions.assertEquals( 3, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Milk":
                    Assertions.assertEquals( 1, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Sugar":
                    Assertions.assertEquals( 1, (int) ingredients.get( i ).getAmount() );
                    break;
                case "Chocolate":
                    Assertions.assertEquals( 0, (int) ingredients.get( i ).getAmount() );
                    break;
                default:
                    continue;
            }
        }

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @Test
    @Transactional
    public void testCheckRecipe () {

        /** Created by Mukul Sauhta */
        // The function of check recipe is to return true if all the ingredients
        // amounts are equal to 0.
        // coffee, milk, sugar, chocolate
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe recipe = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( recipe );
        Assertions.assertFalse( recipe.checkRecipe() );

        // Now to validate when it's true.
        final Recipe checkTrue = createRecipe( "Check", 0, 0, 0, 0, 0 );
        service.save( checkTrue );
        Assertions.assertTrue( checkTrue.checkRecipe() );

        // Now we must cover the remaining 5 branches by checking when some
        // values of the ingredients are 0 and when others aren't.
        final Recipe recipe2 = createRecipe( "Coffee", 0, 1, 0, 0, 0 );
        service.save( recipe2 );
        Assertions.assertFalse( recipe2.checkRecipe() );

        final Recipe recipe3 = createRecipe( "Coffee", 0, 0, 1, 0, 0 );
        service.save( recipe3 );
        Assertions.assertFalse( recipe3.checkRecipe() );

        final Recipe recipe4 = createRecipe( "Coffee", 0, 0, 0, 1, 0 );
        service.save( recipe4 );
        Assertions.assertFalse( recipe4.checkRecipe() );

        final Recipe recipe5 = createRecipe( "Coffee", 0, 0, 0, 0, 1 );
        service.save( recipe5 );
        Assertions.assertFalse( recipe5.checkRecipe() );

    }

    @Test
    @Transactional
    public void testUpdateRecipe () {
        /** Created by Mukul Sauhta */
        // Need to ensure that the updateRecipe function works correctly, after
        // a recipe has already been created.
        final Recipe recipe = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( recipe );
        final Recipe updateRecipe = createRecipe( "Coffee 2.0", 24, 3, 1, 1, 0 );
        service.save( updateRecipe );

        // These shouldn't be the same, they have separate names.
        Assertions.assertFalse( recipe.equals( updateRecipe ) );
        // Update old recipe to be new recipe now.
        recipe.updateRecipe( updateRecipe );
        // Now we can set the original Coffee's name to match and test they are
        // equal.
        recipe.setName( "Coffee 2.0" );
        Assertions.assertTrue( recipe.equals( updateRecipe ) );

    }

    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    @Transactional
    public void testEquals () {
        /** Created by Mukul Sauhta */
        final Recipe recipe = createRecipe( null, 50, 3, 1, 1, 0 );
        service.save( recipe );

        final Recipe recipe2 = createRecipe( "HelloTeam", 50, 3, 1, 1, 0 );
        service.save( recipe2 );

        final Recipe recipe3 = createRecipe( null, 50, 3, 1, 1, 0 );
        service.save( recipe3 );

        // To cover all parts of the equals method, let's compare it to a null
        // object.
        Assertions.assertFalse( recipe.equals( null ) );

        // Now compare it to a different class
        final int diffClass = 0;
        Assertions.assertFalse( recipe.equals( diffClass ) );

        // Now compare it to a recipe with a non null name
        Assertions.assertFalse( recipe.equals( recipe2 ) );

        // Also compare it to a recipe with a null name, to see if they match
        Assertions.assertTrue( recipe.equals( recipe3 ) );

        final Recipe recipe4 = createRecipe( "HelloTeam", 50, 3, 1, 1, 0 );
        service.save( recipe4 );

        // Get coverage on the toString method by comparing two equal recipes.
        Assertions.assertTrue( recipe2.toString().equals( recipe4.toString() ) );

        // Check that their hashCode is the same.
        Assertions.assertTrue( recipe2.hashCode() == recipe4.hashCode() );

        // Now check the hash code for null names
        Assertions.assertTrue( recipe.hashCode() == recipe3.hashCode() );
    }

    @Test
    @Transactional
    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( new Ingredient( "Coffee", coffee ) );
        recipe.addIngredient( new Ingredient( "Milk", milk ) );
        recipe.addIngredient( new Ingredient( "Sugar", sugar ) );
        recipe.addIngredient( new Ingredient( "Chocolate", chocolate ) );
        return recipe;
    }

    /**
     * This method tests the existsById() method, ensuring that the correct
     * boolean value is returned when checking for an existing or non existing
     * ID.
     *
     * @author James Kocak
     */
    @Test
    @Transactional
    public void testExistsById () {
        // Create recipes for testing existsById
        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        r1.addIngredient( new Ingredient( "Coffee", 1 ) );
        r1.addIngredient( new Ingredient( "Milk", 0 ) );
        r1.addIngredient( new Ingredient( "Sugar", 0 ) );
        r1.addIngredient( new Ingredient( "Chocolate", 0 ) );

        // Check if r1 exists before and after
        Assertions.assertThrows( Exception.class, () -> service.existsById( r1.getId() ) );
        service.save( r1 );
        Assertions.assertEquals( true, service.existsById( r1.getId() ) );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );

        // Check if r2 exists before and after
        Assertions.assertThrows( Exception.class, () -> service.existsById( r2.getId() ) );
        service.save( r2 );
        Assertions.assertEquals( true, service.existsById( r2.getId() ) );
    }

    /**
     * This method tests the findById() method, ensuring that the correct Recipe
     * is found with the corresponding ID.
     *
     * @author James Kocak
     */
    @Test
    @Transactional
    public void testFindById () {
        // Check with null ID
        Assertions.assertNull( service.findById( null ) );

        // Check with nonexistent ID
        Assertions.assertNull( service.findById( -1l ) );

        // Create recipes for testing findById
        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        r1.addIngredient( new Ingredient( "Coffee", 1 ) );
        r1.addIngredient( new Ingredient( "Milk", 0 ) );
        r1.addIngredient( new Ingredient( "Sugar", 0 ) );
        r1.addIngredient( new Ingredient( "Chocolate", 0 ) );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( new Ingredient( "Coffee", 1 ) );
        r2.addIngredient( new Ingredient( "Milk", 1 ) );
        r2.addIngredient( new Ingredient( "Sugar", 1 ) );
        r2.addIngredient( new Ingredient( "Chocolate", 1 ) );
        service.save( r2 );

        // Check Recipes
        Assertions.assertEquals( r1.getName(), service.findById( r1.getId() ).getName() );
        Assertions.assertEquals( r2.getName(), service.findById( r2.getId() ).getName() );
    }

}
