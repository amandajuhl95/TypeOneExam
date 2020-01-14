package rest;

import DTO.HobbyDTO;
import DTO.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.User;
import entities.Role;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
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
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//@Disabled
public class LoginEndpointTest {

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
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.CREATE);

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

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
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
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();

            em.persist(p1);
            em.persist(p2);

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test");
            user.addRole(userRole);
            User admin = new User("admin", "test");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test");
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

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
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

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        given().when().get("/info").then().statusCode(200);
    }

    @Test
    //Edit and specify this when code is used as startcode
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/info").then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    //Edit and specify this when code is used as startcode
    public void testRestForAdmin() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    //Edit and specify this when code is used as startcode
    public void testRestForUser() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void testAutorizedUserCannotAccessAdminPage() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then() //Call Admin endpoint as user
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccessUserPage() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then() //Call User endpoint as Admin
                .statusCode(401);
    }

    @Test
    //Edit and specify this when code is used as startcode
    public void testRestForMultiRole1() {
        login("user_admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    //Edit and specify this when code is used as startcode
    public void testRestForMultiRole2() {
        login("user_admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .assertThat().contentType(ContentType.JSON);
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
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
     * Test of addPerson method, of class LoginResource.
     */
    @Test
    public void testAddPersonAsUser() {
        System.out.println("addPerson");

        login("user", "test");

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
                .body("code", equalTo(401))
                .body("message", equalTo("You are not authorized to perform the requested operation"));
    }

    /**
     * Test of editPerson method, of class LoginResource.
     */
    @Test
    public void testEditPerson() {
        System.out.println("editPerson");

        login("admin", "test");

        String payload = "{\"firstname\": \"Jim\","
                + "\"lastname\": \"Fallon\","
                + "\"email\": \"jim@hotmail.com\","
                + "\"phone\": \"40123786\","
                + "\"street\": \"Testvej\","
                + "\"city\": \"Testby\","
                + "\"zip\": \"2230\"}";

        given().contentType("application/json")
                .header("x-access-token", securityToken)
                .body(payload).when()
                .put("/info/person/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Jim"), "lastname", equalTo("Fallon"), "email", equalTo("jim@hotmail.com"), "phone", equalTo(40123786), "street", equalTo("Testvej"), "city", equalTo("Testby"), "zip", equalTo(2230));

    }

    /**
     * Test of deletePerson method, of class LoginResource.
     */
    @Test
    public void testDeletePerson() {
        System.out.println("deletePerson");

        login("admin", "test");

        given().contentType("application/json")
                .header("x-access-token", securityToken).when()
                .delete("info/person/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("status", equalTo("Person has been deleted"));
    }

    /**
     * Test of addHobbyToList method, of class LoginResource.
     */
    @Test
    public void testAddHobbyToList() {
        System.out.println("addHobbyToList");

        login("admin", "test");

        String payload = "{\"name\": \"testhobby\","
                + "\"description\": \"A hobby test description\"}";

        given().contentType("application/json")
                .header("x-access-token", securityToken).when()
                .body(payload)
                .post("info/addhobby").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("testhobby"), "description", equalTo("A hobby test description"));

    }

    /**
     * Test of addHobby method, of class LoginResource.
     */
    @Test
    public void testAddHobby() {
        System.out.println("addHobby");

        login("admin", "test");

        String payload = "{\"name\": \"Stress\","
                + "\"description\": \"Never really fun\"}";

        given().contentType("application/json")
                .header("x-access-token", securityToken).when()
                .body(payload)
                .post("info/addhobby/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("firstname", equalTo("Jim"),
                        "lastname", equalTo("Fallon"),
                        "email", equalTo("jim@gmail.com"),
                        "street", equalTo("Fasanvej"),
                        "hobbies.name", hasItems("Programming", "Football", "Handball", "stress"));
    }

    /**
     * Test of editHobby method, of class LoginResource.
     */
    @Test
    public void testEditHobby() {
        System.out.println("editHobby");

        login("admin", "test");

        String payload = "{\"name\": \"Handball\","
                + "\"description\": \"seven players, one ball\"}";

        given().contentType("application/json")
                .header("x-access-token", securityToken)
                .body(payload).when()
                .put("/info/hobby/" + hobby3.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo(hobby3.getName()), "description", equalTo("seven players, one ball"));

    }

    /**
     * Test of deleteHobbyFromList method, of class LoginResource.
     */
    @Test
    public void testDeleteHobbyFromList() {
        System.out.println("deleteHobbyFromList");

        login("admin", "test");

        given().contentType("application/json")
                .header("x-access-token", securityToken).when()
                .delete("/info/hobby/" + hobby1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("status", equalTo("Hobby has been deleted"));

    }

}
