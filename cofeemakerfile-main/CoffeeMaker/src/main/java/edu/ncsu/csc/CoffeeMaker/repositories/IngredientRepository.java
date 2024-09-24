package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;


public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Finds a Recipe object with the provided name. Spring will generate code
     * to make this happen.
     * 
     * @param name
     *            Name of the recipe
     * @return Found recipe, null if none.
     */
    Ingredient findByName ( String name );
}
