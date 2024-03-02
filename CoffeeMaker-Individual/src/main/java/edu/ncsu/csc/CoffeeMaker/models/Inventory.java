package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;

    /** List of ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Use this to create inventory with new implementation of array list.
     * Initialize all values with their starting amount being 0.
     *
     */
    public Inventory () {
        ingredients = new ArrayList<>();
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets and returns the ingredient list for this recipe.
     *
     * @return The list of ingredients contained in this recipe
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Gets and returns the ingredient list for this recipe.
     *
     * @param ingredient
     *            the ingredient whose amount will be returned
     *
     * @return The list of ingredients contained in this recipe
     */
    public Integer getIngredient ( final String ingredient ) {
        for ( final Ingredient h : ingredients ) {
            if ( h.getName().equals( ingredient ) ) {
                return h.getAmount();
            }
        }
        throw new IllegalArgumentException( "This ingredient isn't in the inventory." );
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        boolean isEnough = true;
        final List<Ingredient> ingredientList = r.getIngredients();
        for ( final Ingredient h : ingredientList ) {
            if ( this.getIngredient( h.getIngredient() ) < h.getAmount() ) {
                isEnough = false;
            }
        }
        return isEnough;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        final List<Ingredient> ingredientList = r.getIngredients();
        if ( enoughIngredients( r ) ) {
            for ( final Ingredient h : ingredientList ) {
                for ( final Ingredient invent : ingredients ) {
                    if ( invent.getIngredient() == h.getIngredient() ) {
                        invent.setAmount( invent.getAmount() - h.getAmount() );
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory
     *
     * @param ingredient
     *            the ingredient that will be updated.
     * @param amount
     *            the amount to be added to ingredient.
     * @return true if successful, false if not
     */
    public boolean addIngredient ( final String ingredient, final Integer amount ) {
        try {
            for ( final Ingredient in : ingredients ) {
                if ( in.getName().equals( ingredient ) ) {
                    return false;
                }
            }
            ingredients.add( new Ingredient( ingredient, amount ) );
            return true;
        }
        catch ( final Exception e ) {
            return false;
        }
    }

    /**
     * Adds ingredients to the inventory
     *
     * @param ingredient
     *            the ingredient that will be updated.
     * @param amount
     *            the amount to be added to ingredient.
     * 
     */
    public void addToIngredient ( final String ingredient, final Integer amount ) {
        for ( final Ingredient in : ingredients ) {
            if ( in.getName().equals( ingredient ) ) {
                in.setAmount( in.getAmount() + amount );
            }
        }

    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Ingredients:\n" );
        final int size = ingredients.size();
        for ( int i = 0; i < size; i++ ) {
            sb.append( ingredients.get( i ).getIngredient() + ", Amount: " + ingredients.get( i ).getAmount() + "\n" );
        }

        return sb.toString();
    }

}
