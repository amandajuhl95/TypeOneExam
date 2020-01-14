/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import java.util.List;
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
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    private Person p1;
    private Person p2;
    private Hobby hobby1;
    private Hobby hobby2;
    private Hobby hobby3;
    private Hobby hobby4;
    private Address address1;
    private Address address2;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);
        facade = PersonFacade.getFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {

        EntityManager em = emf.createEntityManager();

        address1 = new Address("Fasanvej", "Frederiksberg", 2000);
        address2 = new Address("Mariebo Møllen", "Maribo", 4930);
        p1 = new Person("Jim", "Fallon", "jim@gmail.com", 40123786, address1);
        p2 = new Person("Bill", "Gates", "bill@gmail.com", 30201456, address2);
        hobby1 = new Hobby("Programming", "The future of mankind is programming");
        hobby2 = new Hobby("Football", "Super fun and easy");
        hobby3 = new Hobby("Handball", "Team sport");
        hobby4 = new Hobby("Swimming", "You will definitely get wet");

        p1.addHobby(hobby1);
        p1.addHobby(hobby2);
        p1.addHobby(hobby3);
        p1.setAddress(address1);

        p2.addHobby(hobby2);
        p2.addHobby(hobby3);
        p2.addHobby(hobby4);
        p2.setAddress(address2);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();

            em.persist(p1);
            em.persist(p2);

            em.getTransaction().commit();

        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getFacade method, of class PersonFacade.
     */
    @Test
    public void testGetFacade() {
        System.out.println("getFacade");

    }

    /**
     * Test of addPerson method, of class PersonFacade.
     */
    @Test
    public void testAddPerson() {
        System.out.println("addPerson");

        int personsbefore = facade.getAllPersons().size();
        PersonDTO p = new PersonDTO("Anton", "Berg", "anton@gmail.com", "12145641", "Elmegade 10", "Nørrebro", "2200");
        facade.addPerson(p);
        int personsafter = facade.getAllPersons().size();

        assertTrue(personsbefore + 1 == personsafter);

    }

    /**
     * Test of editPerson method, of class PersonFacade.
     */
    @Test
    public void testEditPerson() {
        System.out.println("editPerson");

        p1.setEmail("fallon@gmail.com");
        PersonDTO p = new PersonDTO(p1);

        facade.editPerson(p);
        p = facade.getPersonById(p.getId());

        assertEquals("fallon@gmail.com", p.getEmail());
        assertEquals("Jim", p.getFirstname());
        assertEquals("Frederiksberg", p.getCity());

    }

    /**
     * Test of deletePerson method, of class PersonFacade.
     */
    @Test
    public void testDeletePerson() {
        System.out.println("deletePerson");

        int personsbefore = facade.getAllPersons().size();
        facade.deletePerson(p1.getId());
        int personsafter = facade.getAllPersons().size();

        assertTrue(personsbefore > personsafter);

    }

    /**
     * Test of getAllPersons method, of class PersonFacade.
     */
    @Test
    public void testGetAllPersons() {
        System.out.println("getAllPersons");

        List<PersonDTO> persons = facade.getAllPersons();
        assertEquals(2, persons.size());

    }

    /**
     * Test of addHobbyToList method, of class PersonFacade.
     */
    @Test
    public void testAddHobbyToList() {
        System.out.println("addHobbyToList");

        int hobbiesbefore = facade.getAllHobbies().size();
        HobbyDTO h = new HobbyDTO("Gardning", "Water plants");
        facade.addHobbyToList(h);
        int hobbiesafter = facade.getAllHobbies().size();

        assertTrue(hobbiesbefore + 1 == hobbiesafter);

    }

    /**
     * Test of deleteHobbyFromList method, of class PersonFacade.
     */
    @Test
    public void testDeleteHobbyFromList() {
        System.out.println("deleteHobbyFromList");

        int hobbiesbefore = facade.getAllHobbies().size();
        facade.deleteHobbyFromList(hobby1.getId());
        int hobbiesafter = facade.getAllHobbies().size();

        assertTrue(hobbiesbefore > hobbiesafter);
    }

    /**
     * Test of editHobby method, of class PersonFacade.
     */
    @Test
    public void testEditHobby() {
        System.out.println("editHobby");

        hobby3.setDescription("one ball, seven players");
        HobbyDTO h = new HobbyDTO(hobby3);

        facade.editHobby(h);
        h = facade.getHobbyById(h.getId());

        assertEquals("one ball, seven players", h.getDescription());
        assertEquals("Handball", h.getName());
    }

    /**
     * Test of getAllPersonsByHobby method, of class PersonFacade.
     */
    @Test
    public void testGetAllPersonsByHobby() {
        System.out.println("getAllPersonsByHobby");

        List<PersonDTO> personsS = facade.getAllPersonsByHobby(hobby4.getName());
        List<PersonDTO> personsF = facade.getAllPersonsByHobby(hobby2.getName());

        assertEquals(2, personsF.size());
        assertEquals(1, personsS.size());

    }

    /**
     * Test of getAllHobbies method, of class PersonFacade.
     */
    @Test
    public void testGetAllHobbies() {
        System.out.println("getAllHobbies");

        List<HobbyDTO> hobbies = facade.getAllHobbies();
        assertEquals(4, hobbies.size());

    }

}
