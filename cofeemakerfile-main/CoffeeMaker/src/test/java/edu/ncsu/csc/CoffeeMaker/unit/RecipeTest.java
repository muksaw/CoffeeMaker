package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    // @Test
    // @Transactional
    // public void testAddRecipe () {
    //
    // // Making some ingredients for testing
    // final Ingredient coffee = new Ingredient( "Coffee", 5 );
    // final Ingredient milk = new Ingredient( "Milk", 3 );
    // final Ingredient chocolate = new Ingredient( "Chocolate", 3 );
    //
    // // Creates 2 recipes, and saves them to the database
    // final Recipe r1 = new Recipe();
    // r1.setName( "Black Coffee" );
    // r1.setPrice( 1 );
    // r1.addIngredient( coffee );
    // r1.addIngredient( milk );
    // service.save( r1 );
    //
    // final Recipe r2 = new Recipe();
    // r2.setName( "Mocha" );
    // r2.setPrice( 1 );
    // r2.addIngredient( coffee );
    // r2.addIngredient( chocolate );
    // service.save( r2 );
    //
    // final List<Recipe> recipes = service.findAll();
    // Assertions.assertEquals( 2, recipes.size(),
    // "Creating two recipes should result in two recipes in the database" );
    //
    // Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe
    // should match the created one" );
    //
    // }

    /**
     * Tests the constructor
     */
    @Test
    @Transactional
    public void testConstructor () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );
        assertEquals( "", r.getName() );
    }

    /**
     * Tests addIngredient
     */
    @Test
    @Transactional
    public void testAddIngredient () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );

        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        final Ingredient milk = new Ingredient( "Milk", 2 );

        r.addIngredient( coffee );
        assertEquals( 1, r.getIngredients().size() );
        r.addIngredient( milk );
        assertEquals( 2, r.getIngredients().size() );
        r.addIngredient( sugar );
        assertEquals( 3, r.getIngredients().size() );
    }

    /**
     * Tests removeIngredient
     */
    @Test
    @Transactional
    public void testRemoveIngredient () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );

        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        final Ingredient milk = new Ingredient( "Milk", 2 );

        r.addIngredient( coffee );
        assertEquals( 1, r.getIngredients().size() );
        r.addIngredient( milk );
        assertEquals( 2, r.getIngredients().size() );
        r.addIngredient( sugar );
        assertEquals( 3, r.getIngredients().size() );

        r.removeIngredient( "Coffee" );
        assertEquals( 2, r.getIngredients().size() );
        r.removeIngredient( "Sugar" );
        assertEquals( 1, r.getIngredients().size() );
    }

    /**
     * Tests editUnits
     */
    @Test
    @Transactional
    public void testEditUnits () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );

        final Ingredient coffee = new Ingredient( "Coffee", 5 );
        final Ingredient sugar = new Ingredient( "Sugar", 2 );
        final Ingredient milk = new Ingredient( "Milk", 2 );

        r.addIngredient( coffee );
        assertEquals( 1, r.getIngredients().size() );
        r.addIngredient( milk );
        assertEquals( 2, r.getIngredients().size() );
        r.addIngredient( sugar );
        assertEquals( 3, r.getIngredients().size() );

        assertEquals( 5, r.getIngredients().get( 0 ).getUnits() );
        r.editUnits( "Coffee", 4 ); // changes units
        assertEquals( 4, r.getIngredients().get( 0 ).getUnits() );
    }

    /**
     * Tests setting name, and indirectly getting name
     */
    @Test
    @Transactional
    public void testSetRecipe () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );

        r.setName( "Recipe!" );

        assertEquals( "Recipe!", r.getName() );
    }

    /**
     * Tests setting price, and indirectly getting price
     */
    @Test
    @Transactional
    public void testSetPrice () {
        final Recipe r = new Recipe();
        assertEquals( 0, r.getIngredients().size() );

        r.setPrice( 500 );

        assertEquals( 500, r.getPrice() );
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
        final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );

        try {
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
        final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );

        try {
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
        final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );

        try {
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
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );

        try {
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
    public void testDeleteRecipe3 () {
        // adding some recipes
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        // verifying that there are 3 recipes
        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.delete( r2 );
        Assertions.assertEquals( 2, service.count(), "There should be 2 recipes in the database" );

        // multiple deletions of the same object should result in no change
        service.delete( r2 );
        Assertions.assertEquals( 2, service.count(), "There should be 2 recipes in the database" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        // adding some recipes
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        // verifying that there are 3 recipes
        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();
        Assertions.assertEquals( 0, service.count(), "There should be 0 recipes in the database" );

        // multiple deletions of the all objects should result in no change
        service.deleteAll();
        Assertions.assertEquals( 0, service.count(), "There should be 0 recipes in the database" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );

        service.delete( r1 );
        Assertions.assertNull( service.findByName( "Coffee" ) );
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
        final List<Ingredient> rList = retrieved.getIngredients();
        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertTrue( rList.get( 0 ).getName().equals( "Coffee" ) );
        Assertions.assertTrue( rList.get( 1 ).getName().equals( "Milk" ) );
        Assertions.assertTrue( rList.get( 2 ).getName().equals( "Sugar" ) );
        Assertions.assertTrue( rList.get( 3 ).getName().equals( "Chocolate" ) );
        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @Test
    @Transactional
    public void testEditRecipe2 () {

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );

        r1.editUnits( "Coffee", 5 );
        assertEquals( 5, r1.getIngredients().get( 0 ).getUnits() );
    }

    /**
     * testing recipe equals
     */
    @Test
    @Transactional
    public void testEqualsMethod () {
        // if its the same object
        final Recipe r1 = createRecipe( "Coffee", 11, 0, 1, 2, 3 );
        Assertions.assertTrue( r1.equals( r1 ) );

        // if its a null object
        Assertions.assertFalse( r1.equals( null ) );

        // if its a different class
        final Object differentObject = new Object();
        Assertions.assertFalse( r1.equals( differentObject ) );

        // the recipe has a diffrent name
        final Recipe r22 = createRecipe( "Tea", 10, 9, 8, 7, 6 );
        service.save( r1 );
        service.save( r22 );
        Assertions.assertFalse( r1.equals( r22 ) );

        // the recipe has a null name
        final Recipe r3 = createRecipe( null, 11, 0, 1, 2, 3 );
        service.save( r3 );
        final Recipe r4 = createRecipe( "Non-Null Name", 10, 9, 8, 7, 6 );
        service.save( r4 );
        Assertions.assertFalse( r3.equals( r4 ) );

        // the objects are equal!
        final Recipe r5 = createRecipe( "Coffee", 11, 0, 1, 2, 3 );
        service.save( r5 );
        final Recipe r6 = createRecipe( "Coffee", 11, 0, 1, 2, 3 );
        service.save( r6 );
        Assertions.assertTrue( r5.equals( r6 ) );
    }

    // /**
    // * Tests editing all fields of a recipe
    // */
    // @Test
    // @Transactional
    // public void testEditRecipe2 () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
    // service.save( r1 );
    //
    // r1.setPrice( 10 );
    // r1.setCoffee( 10 );
    // r1.setMilk( 10 );
    // r1.setSugar( 10 );
    // r1.setChocolate( 10 );
    //
    // service.save( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    // Assertions.assertEquals( 10, (int) retrieved.getPrice() );
    // Assertions.assertEquals( 10, (int) retrieved.getCoffee() );
    // Assertions.assertEquals( 10, (int) retrieved.getMilk() );
    // Assertions.assertEquals( 10, (int) retrieved.getSugar() );
    // Assertions.assertEquals( 10, (int) retrieved.getChocolate() );
    //
    // Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't
    // duplicate it" );
    //
    // }
    //
    // /**
    // * Tests editing a recipe after deletion
    // */
    // @Test
    // @Transactional
    // public void testEditRecipe3 () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
    // service.save( r1 );
    // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
    // service.save( r2 );
    //
    // r1.setPrice( 10 );
    // r1.setCoffee( 10 );
    // r1.setMilk( 10 );
    // r1.setSugar( 10 );
    // r1.setChocolate( 10 );
    //
    // service.save( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    // Assertions.assertEquals( 10, (int) retrieved.getPrice() );
    // Assertions.assertEquals( 10, (int) retrieved.getCoffee() );
    // Assertions.assertEquals( 10, (int) retrieved.getMilk() );
    // Assertions.assertEquals( 10, (int) retrieved.getSugar() );
    // Assertions.assertEquals( 10, (int) retrieved.getChocolate() );
    //
    // Assertions.assertEquals( 2, service.count(), "Editing a recipe shouldn't
    // duplicate it" );
    //
    // service.delete( r1 );
    // Assertions.assertEquals( 1, service.count(), "Deletion of r1 failed" );
    // r1.setPrice( 11 );
    // r1.setCoffee( 11 );
    // r1.setMilk( 11 );
    // Assertions.assertEquals( 1, service.count(), "Editing a recipe that
    // doesn't exist should do nothing" );
    // }

    // /**
    // * Tests editing a recipe to have negative values
    // */
    // @Test
    // @Transactional
    // public void testEditRecipe4 () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
    // service.save( r1 );
    // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
    // service.save( r2 );
    //
    // r1.setPrice( 10 );
    // r1.setCoffee( 10 );
    // r1.setMilk( 10 );
    // r1.setSugar( 10 );
    // r1.setChocolate( 10 );
    //
    // service.save( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    // Assertions.assertEquals( 10, (int) retrieved.getPrice() );
    // Assertions.assertEquals( 10, (int) retrieved.getCoffee() );
    // Assertions.assertEquals( 10, (int) retrieved.getMilk() );
    // Assertions.assertEquals( 10, (int) retrieved.getSugar() );
    // Assertions.assertEquals( 10, (int) retrieved.getChocolate() );
    //
    // Assertions.assertEquals( 2, service.count(), "Editing a recipe shouldn't
    // duplicate it" );
    //
    // r1.setPrice( -10 );
    // r1.setCoffee( -10 );
    // r1.setMilk( -10 );
    // r1.setSugar( -10 );
    // r1.setChocolate( -10 );
    //
    // // trying to save a recipe with a negative value should throw an
    // // exception
    // Assertions.assertThrows( ConstraintViolationException.class, () ->
    // service.save( r1 ) );
    // }

    // /**
    // * Tests the checkRecipe method, validating that the ingredient fields are
    // 0
    // */
    // @Test
    // @Transactional
    // public void testCheckRecipe () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 0, 0, 0, 0, 0 );
    // service.save( r1 );
    //
    // Assertions.assertTrue( r1.checkRecipe() );
    // }
    //
    // /**
    // * Tests updating a recipe to match the values of another recipe
    // */
    // @Test
    // @Transactional
    // public void testUpdateRecipe () {
    // final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
    // service.save( r1 );
    // final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
    // service.save( r2 );
    //
    // r2.updateRecipe( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    // Assertions.assertEquals( 50, (int) retrieved.getPrice() );
    // Assertions.assertEquals( 3, (int) retrieved.getCoffee() );
    // Assertions.assertEquals( 1, (int) retrieved.getMilk() );
    // Assertions.assertEquals( 1, (int) retrieved.getSugar() );
    // Assertions.assertEquals( 0, (int) retrieved.getChocolate() );
    //
    // // Name should not be changed!
    // Assertions.assertEquals( "Mocha", r2.getName() );
    //
    // // wrapping in a .equals method test
    //
    // Assertions.assertTrue( r1.equals( retrieved ) );
    // }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {

        final Ingredient coffeeIng = new Ingredient( "Coffee", coffee );
        final Ingredient milkIng = new Ingredient( "Milk", milk );
        final Ingredient sugarIng = new Ingredient( "Sugar", sugar );
        final Ingredient chocolateIng = new Ingredient( "Chocolate", chocolate );
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( coffeeIng );
        recipe.addIngredient( milkIng );
        recipe.addIngredient( sugarIng );
        recipe.addIngredient( chocolateIng );

        return recipe;
    }

}
