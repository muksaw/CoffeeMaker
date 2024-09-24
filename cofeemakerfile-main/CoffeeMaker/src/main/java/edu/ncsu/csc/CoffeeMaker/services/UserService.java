package edu.ncsu.csc.CoffeeMaker.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

@Component
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername ( final String username ) throws UsernameNotFoundException {
        final User user = userRepository.findByUserName( username );
        // TODO sanity checks
        return user;
    }

    /**
     * returns a list of all users
     *
     * @return a list of all users
     */
    public List<User> findAll () {
        return userRepository.findAll();
    }

    /**
     * registers the given user as a customer
     *
     * @param user
     *            the user to register
     * @return true if successful, false if username conflicts
     */
    public boolean registerCustomer ( final User user ) {
        user.grantCustomer();
        return register( user );
    }

    /**
     * registers the given user as a staff member
     *
     * @param user
     *            the user to register
     * @return true if successful, false if username conflicts
     */
    public boolean registerStaff ( final User user ) {
        user.grantStaff();
        return register( user );
    }

    /**
     * registers the given user as a manager
     *
     * @param user
     *            the user to register
     * @return true if successful, false if username conflicts
     */
    public boolean registerManager ( final User user ) {
        user.grantManager();
        user.grantStaff();
        user.grantCustomer();
        return register( user );
    }

    /**
     * registers a new user to the system iff a user with that username DNE
     * already
     *
     * @param user
     *            the user to save to the system
     * @return false if a user with the same username already exists, true
     *         otherwise
     */
    private boolean register ( final User user ) {
        final User check = userRepository.findByUserName( user.getUsername() );
        if ( null != check ) {
            return false;
        }
        final PasswordEncoder p = new BCryptPasswordEncoder();
        user.setPassword( p.encode( user.getPassword() ) );
        save( user );
        return true;
    }

    /**
     * saves the user to the database & flushes
     *
     * @param user
     *            the user to save
     */
    public void save ( final User user ) {
        userRepository.saveAndFlush( user );
    }

    /**
     * deletes the user from the database
     *
     * @param user
     *            the user to delete (should be from db)
     */
    public void delete ( final User user ) {
        userRepository.delete( user );
    }

    /**
     * deletes all users from the database ONLY USE FOR TESTING
     */
    public void deleteAll () {
        userRepository.deleteAll();
    }
}
