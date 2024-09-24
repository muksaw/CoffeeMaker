package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest
public class InventoryTest {

    @Autowired
    private RecipeService    recipeService;

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
        inventoryService.deleteAll();

        final Inventory ivt = inventoryService.getInventory();

        ivt.addIngredient( "chocolate", 500 );
        ivt.addIngredient( "coffee", 500 );
        ivt.addIngredient( "milk", 500 );
        ivt.addIngredient( "sugar", 500 );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        final Ingredient chocolate = new Ingredient( "chocolate", 10 );
        final Ingredient milk = new Ingredient( "milk", 20 );
        final Ingredient sugar = new Ingredient( "sugar", 5 );
        final Ingredient coffee = new Ingredient( "coffee", 1 );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );
        recipe.addIngredient( coffee );

        // recipe.setChocolate( 10 );
        // recipe.setMilk( 20 );
        // recipe.setSugar( 5 );
        // recipe.setCoffee( 1 );

        recipe.setPrice( 5 );

        assertTrue( i.useIngredients( recipe ) );
        inventoryService.save( i );
        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, i.ingredientAmount( "chocolate" ) );
        Assertions.assertEquals( 480, i.ingredientAmount( "milk" ) );
        Assertions.assertEquals( 495, i.ingredientAmount( "sugar" ) );
        Assertions.assertEquals( 499, i.ingredientAmount( "coffee" ) );
    }

    @Test
    @Transactional
    public void testConsumeInvalid () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        final Ingredient chocolate = new Ingredient( "chocolate", 10 );
        final Ingredient milk = new Ingredient( "milk", 20 );
        final Ingredient sugar = new Ingredient( "cream", 5 ); // invalid
        final Ingredient coffee = new Ingredient( "coffee", 1 );
        recipe.addIngredient( chocolate );
        recipe.addIngredient( milk );
        recipe.addIngredient( sugar );
        recipe.addIngredient( coffee );

        // recipe.setChocolate( 10 );
        // recipe.setMilk( 20 );
        // recipe.setSugar( 5 );
        // recipe.setCoffee( 1 );

        recipe.setPrice( 5 );

        assertFalse( i.useIngredients( recipe ) );
        inventoryService.save( i );
        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 500, i.ingredientAmount( "chocolate" ) );
        Assertions.assertEquals( 500, i.ingredientAmount( "milk" ) );
        Assertions.assertEquals( 500, i.ingredientAmount( "sugar" ) );
        Assertions.assertEquals( 500, i.ingredientAmount( "coffee" ) );
    }

    @Test
    @Transactional
    public void testAddInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        ivt.addIngredient( "Coffee", 500 );
        assertTrue( ivt.stockIngredient( "Coffee", 5 ) );
        ivt.addIngredient( "Milk", 500 );
        ivt.stockIngredient( "Milk", 3 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();

        Assertions.assertEquals( 505, ivt.ingredientAmount( "Coffee" ),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 503, ivt.ingredientAmount( "Milk" ),
                "Adding to the inventory should result in correctly-updated values for milk" );
        // Assertions.assertEquals( 507, (int) ivt.getSugar(),
        // "Adding to the inventory should result in correctly-updated values
        // sugar" );
        // Assertions.assertEquals( 502, (int) ivt.getChocolate(),
        // "Adding to the inventory should result in correctly-updated values
        // chocolate" );

    }

    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            ivt.addIngredient( "coffee", -5 );
            ivt.addIngredient( "coffee", 500 );
            ivt.addIngredient( "milk", 500 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, ivt.ingredientAmount( "coffee" ),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, ivt.ingredientAmount( "milk" ),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
        }
    }

    @Test
    @Transactional
    public void testEnoughIngredients () {
        final Inventory ivt = inventoryService.getInventory();

        final Ingredient coffee = new Ingredient( "Coffee", 50 );
        final Ingredient milk = new Ingredient( "Milk", 50 );
        ivt.addIngredient( "Coffee", 51 );
        ivt.addIngredient( "Milk", 500 );

        final Recipe r = new Recipe();

        r.addIngredient( milk );
        r.addIngredient( coffee );

        assertTrue( ivt.enoughIngredients( r ) );
        // enoughIngredients is only a check
        // assertFalse( ivt.enoughIngredients( r ) );
    }

    /**
     * tests the inventory toString method
     */
    @Test
    @Transactional
    public void testToString () {
        final Inventory ivt = new Inventory();
        ivt.addIngredient( "Coffee", 2 );
        ivt.addIngredient( "Milk", 3 );

        final String expectedToString = "Coffee: 2\n" + "Milk: 3\n";

        Assertions.assertEquals( expectedToString, ivt.toString() );
    }

}
