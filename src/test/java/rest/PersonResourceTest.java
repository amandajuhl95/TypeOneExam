/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;

/**
 *
 * @author aamandajuhl
 */
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    private Person p1;
    private Person p2;
    private Hobby hobby1;
    private Hobby hobby2;
    private Hobby hobby3;
    private Hobby hobby4;
    private Address address1;
    private Address address2;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        // Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    //Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    // TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
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

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

    /**
     * Test of getAllPersons method, of class PersonResource.
     */
    @Test
    public void testGetAllPersons() {
        System.out.println("getAllPersons");

        given().contentType("application/json")
                .get("/person/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", hasItems("Jim", "Bill"),
                        "lastname", hasItems("Fallon", "Gates"),
                        "email", hasItems("jim@gmail.com", "bill@gmail.com"),
                        "street", hasItems("Mariebo Møllen", "Fasanvej"));

    }

    /**
     * Test of getPerson method, of class PersonResource.
     */
    @Test
    public void testGetPersonById() {
        System.out.println("getPersonById");

        given().contentType("application/json")
                .get("/person/id/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Jim"),
                        "lastname", equalTo("Fallon"),
                        "email", equalTo("jim@gmail.com"),
                        "street", equalTo("Fasanvej"),
                        "hobbies.name", hasItems("Programming", "Football", "Handball"));
    }

    /**
     * Test of getPerson method, of class PersonResource.
     */
    @Test
    public void testGetPersonByNumber() {
        System.out.println("getPersonByNumber");

        given().contentType("application/json")
                .get("/person/" + p2.getPhone()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Bill"),
                        "lastname", equalTo("Gates"),
                        "email", equalTo("bill@gmail.com"));

    }

    /**
     * Test of getPersonsByHobby method, of class PersonResource.
     */
    @Test
    public void testGetPersonsByHobby() {
        System.out.println("getPersonsByHobby");

        given().contentType("application/json")
                .get("/person/hobby/" + hobby2.getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", hasItems("Bill", "Jim"));

    }

    /**
     * Test of getAllHobbies method, of class PersonResource.
     */
    @Test
    public void testGetAllHobbies() {
        System.out.println("getAllHobbies");

        given().contentType("application/json")
                .get("/person/hobby/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", hasItems("Programming", "Handball", "Football", "Swimming"),
                        "description", hasItems("The future of mankind is programming", "Super fun and easy", "Team sport", "You will definitely get wet"));
    }

}
