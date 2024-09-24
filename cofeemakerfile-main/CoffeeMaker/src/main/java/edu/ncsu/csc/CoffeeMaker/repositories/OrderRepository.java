package edu.ncsu.csc.CoffeeMaker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Order;

/**
 * OrderRepository is used to provide CRUD operations for the Order model.
 * Spring will generate appropriate code with JPA.
 *
 * @author James Kocak
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an Order object with the provided order number. Spring will
     * generate code to make this happen.
     *
     * @param orderNumber
     *            Order number associated with order
     * @return Found order, null if none
     */
    Order findByOrderNumber ( Integer orderNumber );

    /**
     * Finds all active Order objects and returns a list of them. Spring will
     * generate code to make this happen.
     *
     * @return Found Orders, null if none
     */
    List<Order> findByActiveTrue ();

    /**
     * Finds all inactive Order objects and returns a list of them. Spring will
     * generate code to make this happen.
     *
     * @return Found Orders, null if none
     */
    List<Order> findByActiveFalse ();

    /**
     * Finds all non-ready Order objects and returns a list of them. Spring will
     * generate code to make this happen.
     *
     * @return Found Orders, null if none
     */
    List<Order> findByReadyForPickUpFalse ();

    /**
     * finds all orders where the status matches the active bool and the
     * username matches the username
     *
     * @param active
     *            whether the requested order is active
     * @param userName
     *            the username of the orders to find
     * @return a list of all orders in the db where these apply, null if none
     */
    List<Order> findByActiveAndUserName ( boolean active, String userName );
}
