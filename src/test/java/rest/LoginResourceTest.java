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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author aamandajuhl
 */
// @Disabled
public class LoginResourceTest {

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
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
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

    private static String securityToken;

    private static void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        given().when().get("/info").then().statusCode(200);
    }

    /**
     * Test of getInfoForAll method, of class LoginResource.
     */
    @Test
    public void testGetInfoForAll() {
        System.out.println("getInfoForAll");
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.getInfoForAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of allUsers method, of class LoginResource.
     */
    @Test
    public void testAllUsers() {
        System.out.println("allUsers");
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.allUsers();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFromUser method, of class LoginResource.
     */
    @Test
    public void testGetFromUser() {
        System.out.println("getFromUser");
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.getFromUser();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFromAdmin method, of class LoginResource.
     */
    @Test
    public void testGetFromAdmin() {
        System.out.println("getFromAdmin");
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.getFromAdmin();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPerson method, of class LoginResource.
     */
    @Test
    public void testAddPerson() {
        System.out.println("addPerson");

        login("admin", "test");

        String payload = "{\"firstname\": \"Test\","
                + "\"lastname\": \"Testen\","
                + "\"email\": \"test@hotmail.com\","
                + "\"phone\": \"40302010\","
                + "\"street\": \"Testvej\","
                + "\"city\": \"Testby\","
                + "\"zip\": \"2230\"}";

        given().contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .body(payload)
                .post("info/addperson/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Test"), "lastname", equalTo("Testen"), "email", equalTo("test@hotmail.com"), "phone", equalTo(40302010), "street", equalTo("Testvej"), "city", equalTo("Testby"), "zip", equalTo(2230));

    }

    /**
     * Test of editPerson method, of class LoginResource.
     */
    @Test
    public void testEditPerson() {
        System.out.println("editPerson");
        long id = 0L;
        String personAsJSON = "";
        LoginResource instance = new LoginResource();
        PersonDTO expResult = null;
        PersonDTO result = instance.editPerson(id, personAsJSON);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deletePerson method, of class LoginResource.
     */
    @Test
    public void testDeletePerson() {
        System.out.println("deletePerson");
        long id = 0L;
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.deletePerson(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addHobbyToList method, of class LoginResource.
     */
    @Test
    public void testAddHobbyToList() {
        System.out.println("addHobbyToList");
        String HobbyAsJSON = "";
        LoginResource instance = new LoginResource();
        HobbyDTO expResult = null;
        HobbyDTO result = instance.addHobbyToList(HobbyAsJSON);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addHobby method, of class LoginResource.
     */
    @Test
    public void testAddHobby() {
        System.out.println("addHobby");
        long person_id = 0L;
        String HobbyAsJSON = "";
        LoginResource instance = new LoginResource();
        PersonDTO expResult = null;
        PersonDTO result = instance.addHobby(person_id, HobbyAsJSON);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of editHobby method, of class LoginResource.
     */
    @Test
    public void testEditHobby() {
        System.out.println("editHobby");
        long id = 0L;
        String hobbyAsJSON = "";
        LoginResource instance = new LoginResource();
        HobbyDTO expResult = null;
        HobbyDTO result = instance.editHobby(id, hobbyAsJSON);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteHobbyFromList method, of class LoginResource.
     */
    @Test
    public void testDeleteHobbyFromList() {
        System.out.println("deleteHobbyFromList");
        long id = 0L;
        LoginResource instance = new LoginResource();
        String expResult = "";
        String result = instance.deleteHobbyFromList(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
