package edu.ncsu.csc.CoffeeMaker.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * The OrderService is used to handle CRUD operations on the Order model. In
 * addition to all functionality from 'Service', we also have functionality for
 * retrieving a single order by order number, and retrieving a multiple orders
 * associated with a username.
 *
 * @author James Kocak
 */
@Component
@Transactional
public class OrderService extends Service<Order, Long> {

    /**
     * OrderRepository, to be autowired in by Spring and provide CRUD operations
     * on Order model.
     */
    @Autowired
    private OrderRepository orderRepository;

    @Override
    protected JpaRepository<Order, Long> getRepository () {
        return orderRepository;
    }

    /**
     * Find an Order with the provided order number.
     *
     * @param orderNumber
     *            The order number associated with an order
     * @return The found order, null if none
     */
    public Order findByOrderNumber ( final Integer orderNumber ) {
        return orderRepository.findByOrderNumber( orderNumber );
    }

    /**
     * Find all orders where active=true
     *
     * @return a list of all active orders
     */
    public List<Order> findByActiveTrue () {
        return orderRepository.findByActiveTrue();
    }

    /**
     * Find all orders where active=false
     *
     * @return a list of all active orders
     */
    public List<Order> findByActiveFalse () {
        return orderRepository.findByActiveFalse();
    }

    /**
     * Find all orders where readyForPickup=false
     *
     * @return a list of all active orders
     */
    public List<Order> findByReadyForPickupFalse () {
        return orderRepository.findByReadyForPickUpFalse();
    }

    /**
     * find all orders meeting active & username conditions
     *
     * @param active
     *            whether this order is active
     * @param userName
     *            the username to search for
     * @return a list of all orders meeting param conditions in the db
     */
    public List<Order> findByActiveAndUserName ( final boolean active, final String userName ) {
        return orderRepository.findByActiveAndUserName( active, userName );
    }
}
