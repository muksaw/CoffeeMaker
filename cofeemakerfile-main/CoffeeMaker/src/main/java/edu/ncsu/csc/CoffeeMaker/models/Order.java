package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * An order for the coffee maker. An Order is tied to the database using
 * Hibernate libraries. See OrderRepository and OrderService for the other two
 * pieces used for database support.
 *
 * @author James Kocak
 */
@Entity
@Table ( name = "`Order`" )
public class Order extends DomainObject {
    /** The ID of an order */
    @Id
    @GeneratedValue
    private Long               id;

    /** The username tied to this Order */
    private String             userName;

    /** The order number tied to this Order */
    @Min ( 1 )
    private Integer            orderNumber;

    /** The total amount of money spent on this Order */
    @Min ( 0 )
    private Integer            orderTotal;

    /** This boolean shows if this Order is being worked on */
    private boolean            active;

    /** This boolean shows if this Order is ready to be picked up */
    private boolean            readyForPickUp;

    /** This boolean shows if this Order is for a guest without an account */
    private boolean            guest;

    /** This list contains the recipes ordered on this Order */
    @ManyToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Recipe> recipes;

    /**
     * An empty constructor for Hibernate to use when loading objects from the
     * database.
     */
    public Order () {
        super();
        recipes = new ArrayList<>();
    }

    /**
     *
     */
    public Order ( final String userName, final Integer orderNumber, final Integer orderTotal, final boolean active,
            final boolean readyForPickUp, final boolean guest ) {
        super();
        setUserName( userName );
        setOrderNumber( orderNumber );
        setTotal( orderTotal );
        setActive( active );
        setReady( readyForPickUp );
        setGuest( guest );
        recipes = new ArrayList<>();
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName ( final String userName ) {
        this.userName = userName;
    }

    public Integer getOrderNumber () {
        return orderNumber;
    }

    public void setOrderNumber ( final Integer orderNum ) {
        this.orderNumber = orderNum;
    }

    public Integer getTotal () {
        return orderTotal;
    }

    public void setTotal ( final Integer orderTotal ) {
        this.orderTotal = orderTotal;
    }

    public boolean isActive () {
        return active;
    }

    public void setActive ( final boolean active ) {
        this.active = active;
    }

    public boolean isReady () {
        return readyForPickUp;
    }

    public void setReady ( final boolean readyForPickUp ) {
        this.readyForPickUp = readyForPickUp;
    }

    public boolean isGuest () {
        return guest;
    }

    public void setGuest ( final boolean guest ) {
        this.guest = guest;
    }

    public void addRecipe ( final Recipe r ) {
        recipes.add( r );
    }

    // TODO change this at some point to have orderNumbers be their own thing ig
    public void assignOrderNumber ( final int offset ) {
        this.orderNumber = hashCode() % 1000;
    }

    public List<Recipe> getRecipes () {
        return recipes;
    }

    @Override
    public Serializable getId () {
        return id;
    }
}
