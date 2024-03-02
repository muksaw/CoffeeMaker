package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.PositiveOrZero;

/**
 * An Ingredient for the coffee maker. An Ingredient is tied to a recipe.
 *
 * @author James Kocak
 */
@Entity
public class Ingredient extends DomainObject {
    /** The id of this Ingredient. */
    @Id
    @GeneratedValue
    private Long    id;
    /** The name of this Ingredient */
    private String  name;
    /** The amount of this ingredient available. */
    @PositiveOrZero
    private Integer amount;

    /**
     * An empty constructor for Hibernate to use when loading objects from the
     * database.
     */
    public Ingredient () {
        super();
    }

    /**
     * The constructor for an Ingredient, creates an ingredient with the given
     * type and amount.
     *
     * @param name
     *            The type of ingredient this is
     * @param amount
     *            The amount of this ingredient available
     */
    public Ingredient ( final String name, final Integer amount ) {
        super();
        setName( name );
        setAmount( amount );
    }

    /**
     * Gets the type of Ingredient and returns it.
     *
     * @return The type of Ingredient
     */
    public String getIngredient () {
        return name;
    }

    /**
     * Sets the ingredient type.
     *
     * @param ingredientName
     *            The type of ingredient to set this ingredient to.
     */
    public void setIngredient ( final String ingredientName ) {
        setName( ingredientName );
    }

    /**
     * Gets the amount that this ingredient has.
     *
     * @return The amount of this ingredient remaining.
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the amount for this ingredient.
     *
     * @param amount
     *            The new amount to set this ingredient to.
     */
    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }

    /**
     * Gets the name of this ingredient.
     *
     * @return The name of this ingredient
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of this ingredient.
     *
     * @param name
     *            The name to set this ingredient to
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    @Override
    public Serializable getId () {
        return id;
    }
}
