package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.List;

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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();

        ivt.addIngredient( "Chocolate", 500 );
        ivt.addIngredient( "Coffee", 500 );
        ivt.addIngredient( "Milk", 500 );
        ivt.addIngredient( "Sugar", 500 );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        Assertions.assertEquals( 500, (int) i.getIngredient( "Chocolate" ) );

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "Chocolate", 10 ) );
        recipe.addIngredient( new Ingredient( "Milk", 20 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 5 ) );
        recipe.addIngredient( new Ingredient( "Coffee", 1 ) );
        recipe.setPrice( 5 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, (int) i.getIngredient( "Chocolate" ) );
        Assertions.assertEquals( 480, (int) i.getIngredient( "Milk" ) );
        Assertions.assertEquals( 495, (int) i.getIngredient( "Sugar" ) );
        Assertions.assertEquals( 499, (int) i.getIngredient( "Coffee" ) );
        i.addIngredient( "Chocolate", 10 );
        Assertions.assertEquals( 490, i.getIngredient( "Chocolate" ) );

    }

    /**
     * This method tests the enoughIngredients method for inventory. This
     * constantly changes the inventory to check if the right boolean values
     * return each time.
     *
     * @author James Kocak
     */
    @Test
    @Transactional
    public void testEnoughIngredients () {
        final Inventory ivt = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Boba" );
        recipe.addIngredient( new Ingredient( "Chocolate", 500 ) );
        recipe.addIngredient( new Ingredient( "Milk", 500 ) );
        recipe.addIngredient( new Ingredient( "Sugar", 500 ) );
        recipe.addIngredient( new Ingredient( "Coffee", 500 ) );
        recipe.setPrice( 5 );

        // Check edge case
        Assertions.assertTrue( ivt.enoughIngredients( recipe ) );

        // List of ingredients
        final List<Ingredient> ingredients = recipe.getIngredients();
        final int size = ingredients.size();

        // Change Chocolate to over to check
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Chocolate" ) {
                ingredients.get( i ).setAmount( 501 );
                break;
            }
        }
        Assertions.assertFalse( ivt.enoughIngredients( recipe ) );
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Chocolate" ) {
                ingredients.get( i ).setAmount( 500 );
                break;
            }
        }

        // Change Milk to over to check
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Milk" ) {
                ingredients.get( i ).setAmount( 501 );
                break;
            }
        }
        Assertions.assertFalse( ivt.enoughIngredients( recipe ) );
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Milk" ) {
                ingredients.get( i ).setAmount( 500 );
                break;
            }
        }

        // Change Sugar to over to check
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Sugar" ) {
                ingredients.get( i ).setAmount( 501 );
                break;
            }
        }
        Assertions.assertFalse( ivt.enoughIngredients( recipe ) );
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Sugar" ) {
                ingredients.get( i ).setAmount( 500 );
                break;
            }
        }

        // Change Coffee to over to check
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Coffee" ) {
                ingredients.get( i ).setAmount( 501 );
                break;
            }
        }
        Assertions.assertFalse( ivt.enoughIngredients( recipe ) );
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getIngredient() == "Coffee" ) {
                ingredients.get( i ).setAmount( 500 );
                break;
            }
        }

        // Check edge case again
        Assertions.assertTrue( ivt.enoughIngredients( recipe ) );
    }

    /**
     * This test verifies the validity of our toString constructor. We can also
     * check that it properly displays the ingredients, and their amount.
     *
     * @author Mukul Sauhta
     */
    @Test
    @Transactional
    public void testToString () {
        final Inventory in = inventoryService.getInventory();
        Assertions.assertEquals( "Ingredients:\n" + "Chocolate, Amount: 500\n" + "Coffee, Amount: 500\n"
                + "Milk, Amount: 500\n" + "Sugar, Amount: 500\n" + "", in.toString() );

    }

    /**
     * This test verifies the validity of our getIngredient method. Other
     * classes verify that it can retrieve ingredients that already are added,
     * so we test that it would return an IllegalArgumentException.
     *
     * @author Mukul Sauhta
     */
    @Test
    @Transactional
    public void testGetNoIngredient () {
        final Inventory in = inventoryService.getInventory();
        Assertions.assertThrows( IllegalArgumentException.class, () -> in.getIngredient( "Caramel" ) );

    }

}
