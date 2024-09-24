package edu.ncsu.csc.CoffeeMaker.datagen;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateUsers {
    @Autowired
    private UserService userService;

    @Before
    @Transactional
    public void setup () {
        userService.deleteAll();
    }

    @Test
    @Transactional
    public void createUsers () {
        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user;
        try {
            user = new User( "balls", encoder.encode( "mcgee" ), User.STAFF );
            user.setActive( true );
            userService.save( user );
        }
        catch ( final Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
