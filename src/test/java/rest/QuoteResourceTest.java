/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.EMF_Creator;

/**
 *
 * @author sofieamalielandt
 */
//@Disabled
public class QuoteResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

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
    public static void tearDownClass() {
        // Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        
        //Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    }

    @AfterEach
    public void tearDown() {
        
          // Remove any data after each test was run
    }

    /**
     * Test of getQoutes method, of class StarwarsResource.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetQoutesSimple() throws Exception {
        System.out.println("getQuotesSimple");
        given()
                .contentType("application/json")
                .when()
                .get("/quotes").then()
                .statusCode(200)
                .assertThat()
                .contentType(ContentType.JSON);
    }
    
    /**
     * Test of getQoutes method, of class StarwarsResource.
     * @throws java.lang.Exception
     */
    @Test
     //Edit and specify this when code is used as startcode
    public void testGetQoutes() throws Exception {
        System.out.println("getQuotes");
        given()
                .contentType("application/json")
                .when()
                .get("/quotes").then()
                .statusCode(200)
                .assertThat()
                .body("quoteText", hasSize(5), "quoteAuthor", hasSize(5));
    }

}
