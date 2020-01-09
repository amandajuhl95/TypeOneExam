/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.EMF_Creator;

/**
 *
 * @author aamandajuhl
 */
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;

    private User user;
    private User admin;
    private User both;

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user = new User("user", "test");
            user.addRole(userRole);
            admin = new User("admin", "test");
            admin.addRole(adminRole);
            both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
        
        // Remove any data after each test was run
    }

    /**
     * Test of getUserFacade method, of class UserFacade.
     */
    @Test
    public void testGetUserFacade() {
        System.out.println("getUserFacade");
        UserFacade expResult = facade.getUserFacade(emf);
        UserFacade result = UserFacade.getUserFacade(emf);
        assertEquals(expResult, result);

    }

    /**
     * Test of getVeryfiedUser method, of class UserFacade.
     */
    @Test
    public void testGetVeryfiedUser() throws Exception {
        System.out.println("getVeryfiedUser");
        
        String username = "admin";
        String password = "test";
     
        User result = facade.getVeryfiedUser(username, password);
        assertEquals("admin", result.getUserName());
    }
}
