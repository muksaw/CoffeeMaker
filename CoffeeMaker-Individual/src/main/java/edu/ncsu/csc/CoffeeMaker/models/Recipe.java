package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long             id;

    /** Recipe name */
    private String           name;

    /** Recipe price */
    @Min ( 0 )
    private Integer          price;

    /** List of ingredients */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        ingredients = new ArrayList<>();
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * This method adds a new ingredient to the ingredient list.
     *
     * @param ingredient
     *            The ingredient to add
     */
    public void addIngredient ( final Ingredient ingredient ) {
        ingredients.add( ingredient );
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
     * Returns the name of the recipe along with the ingredients it contains.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Name: " + name + "\nIngredients:\n" );
        final int size = ingredients.size();
        for ( int i = 0; i < size; i++ ) {
            sb.append( ingredients.get( i ).getIngredient() + "\n" );
        }

        return sb.toString();
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

    /**
     * Check if all ingredient in the recipe are 0
     *
     * @return true if all ingredient fields are 0, otherwise return false
     */
    public boolean checkRecipe () {
        final int size = ingredients.size();
        for ( int i = 0; i < size; i++ ) {
            if ( ingredients.get( i ).getAmount() != 0 ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the fields to be equal to the passed Recipe
     *
     * @param r
     *            with updated fields
     */
    public void updateRecipe ( final Recipe r ) {
        if ( r != null ) {
            this.ingredients = r.getIngredients();
            setPrice( r.getPrice() );
        }
    }
}
