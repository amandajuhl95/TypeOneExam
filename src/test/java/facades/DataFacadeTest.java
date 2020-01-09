/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import DTO.QuoteDTO;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author sofieamalielandt
 */
public class DataFacadeTest {

    private static DataFacade facade;

    public DataFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {

        facade = DataFacade.getDataFacade();
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
     
         // Setup the DataBase in a known state BEFORE EACH TEST
    }

    @AfterEach
    public void tearDown() {
        
        // Remove any data after each test was run
    }

    /**
     * Test of getDataFacade method, of class DataFacade.
     */
    @Test
    public void testGetDataFacade() {
        System.out.println("getDataFacade");
        DataFacade expResult = DataFacade.getDataFacade();
        DataFacade result = DataFacade.getDataFacade();
        assertEquals(expResult, result);
    }

    /**
     * Test of getData method, of class DataFacade.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetData() throws Exception {
        System.out.println("getData");
        List<QuoteDTO> result = facade.getData();
        assertTrue(result.size() > 0);

        assertTrue(result.get(1) != null);

    }

}
