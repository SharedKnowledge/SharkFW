package ApiRev1;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
            ApiRev1.SimpleKBTest.class, 
            ApiRev1.SerializationTest.class, 
            ApiRev1.Assimilate_ExtractionTests.class, 
            ApiRev1.ExchangeTests.class,
            ApiRev1.DifferentAddresses.class
        })
public class Rev2_0 {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
