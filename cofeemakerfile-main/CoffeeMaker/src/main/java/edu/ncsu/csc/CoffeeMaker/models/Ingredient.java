package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * @author askhan6
 *
 */
@Entity
public class Ingredient extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue

    private Long    id;

    /** Recipe name */
    private String  name;

    /** Recipe price */
    @Min ( 0 )
    private Integer units;

    /**
     *
     * @param ingredient
     *            Ingredient enum object
     * @param amount
     *            amount of each ingredient in a recipe
     */
    public Ingredient ( final String name, @Min ( 0 ) final Integer amount ) {
        this.name = name;
        this.units = amount;
    }

    /**
     * parameterless contructor Sets fields to defaults of "no name given" and 0
     */
    public Ingredient () {
        this( "no name given", 0 ); // TODO change this to something better?
    }

    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", Ingredient=" + name + ", amount=" + units + "]";
    }

    /**
     * Set the ID of the Ingredient (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * @return the amount
     */
    public Integer getUnits () {
        return units;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setUnits ( final Integer amount ) {
        this.units = amount;
    }

    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Returns name of the ingredient.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the ingredient name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }
}
