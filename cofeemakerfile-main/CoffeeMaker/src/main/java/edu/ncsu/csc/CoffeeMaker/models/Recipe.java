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
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                price;

    /**
     * field to hold all Ingredients in a Recipe
     */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        ingredients = new ArrayList<Ingredient>();
    }

    /**
     * adds an ingredient to the recipe
     *
     * @param ing
     *            the ingredient to add, name and units expected
     */
    public void addIngredient ( final Ingredient ing ) {
        ingredients.add( ing );
    }

    /**
     * returns a mutable list of all ingredients in this recipe
     *
     * @return
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    public void update ( final Recipe recipe ) throws IllegalArgumentException {
        if ( !this.getName().equals( recipe.getName() ) ) {
            throw new IllegalArgumentException( "Name does not match" );
        }
        if ( recipe.getPrice() <= 0 ) {
            throw new IllegalArgumentException( "Price must be greater than zero" );
        }
        if ( recipe.getIngredients().size() <= 0 ) {
            throw new IllegalArgumentException( "Recipe must have at least one ingredient" );
        }
        if ( recipe.getIngredients().stream().anyMatch( ( ing ) -> ing.getUnits() <= 0 ) ) {
            throw new IllegalArgumentException( "Ingredient values must be greater than zero" );
        }
        if ( recipe.getIngredients().stream()
                .anyMatch( ( ing ) -> ing.getName() == null || ing.getName().length() == 0 ) ) {
            throw new IllegalArgumentException( "All Ingredients must have a name" );
        }
        this.getIngredients().clear();
        this.getIngredients().addAll( recipe.ingredients );
        this.setPrice( price );
    }

    /**
     * removes an ingredient with the given name from this recipe
     *
     * @param name
     *            the name of the ingredient to remove
     * @return true if the ingredient was removed, false if not found
     */
    public boolean removeIngredient ( final String name ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( name.equals( ingredients.get( i ).getName() ) ) {
                ingredients.remove( i );
                return true;
            }
        }
        return false;
    }

    /**
     * edits the number of units that the named ingredient requires in the
     * recipe
     *
     * @param name
     *            the name of the ingredient to edit the units of
     * @param units
     *            this Ingredient's new units
     * @return true if the units were edited, false if not found
     * @throws IllegalArgumentException
     *             if units are negative
     */
    public boolean editUnits ( final String name, final Integer units ) {
        if ( units < 0 ) {
            throw new IllegalArgumentException( "Units may not be a negative number." );
        }
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( name ) ) {
                i.setUnits( units );
                return true;
            }
        }
        return false;
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
     * Returns the Ingredients in the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        String returnVal = "Delicious Coffee with ingredients ";
        for ( final Ingredient ing : ingredients ) {
            returnVal += ing.toString();
        }
        return returnVal;
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

}
