package edu.ncsu.csc.CoffeeMaker.datagen;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateIngredients {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         recipeService;

    @Autowired
    InventoryService              inventoryService;

    @Autowired
    private IngredientService     ingredientService;

    @Test
    @Transactional
    public void testCreateIngredients () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        // recipeService.deleteAll();
        // inventoryService.deleteAll();
        ingredientService.deleteAll();

        final Ingredient i1 = new Ingredient( "Coffee", 5 );

        ingredientService.save( i1 );

        final Ingredient i2 = new Ingredient( "Milk", 3 );

        ingredientService.save( i2 );

        Assert.assertEquals( 2, ingredientService.count() );

    }
}
