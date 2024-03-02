package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class MappingControllerTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    @Test
    @Transactional
    public void indexPageLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/index" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void addRecipePageLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/recipe" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void deleteRecipeFormLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/deleterecipe" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void inventoryFormLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void makeCoffeeFormLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/makecoffee" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void addIngredientFormLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/addingredients" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

    @Test
    @Transactional
    public void addRecipeFormLoad () throws Exception {
        Assertions.assertNotNull( mvc.perform( get( "/addRecipe" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString() );

    }

}
