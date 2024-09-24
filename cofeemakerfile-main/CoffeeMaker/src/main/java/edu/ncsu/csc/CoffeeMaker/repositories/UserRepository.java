package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * UserRepository is used to provide CRUD operations for the User model. Spring
 * will generate appropriate code with JPA.
 *
 * @author ajhende4
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a UserDetails object with the provided name. Spring will generate
     * code to make this happen. User extends UserDetails, which is what our
     * system uses
     *
     * @param name
     *            Name of the recipe
     * @return Found recipe, null if none.
     */
    User findByUserName ( String username );
}
