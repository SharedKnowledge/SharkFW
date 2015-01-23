package ApiRev1;

import ontology.InMemoAssociatedSTSetTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Testsuite to check the ongiong works on the new API revision for correctness.
 * Run this suite anytime to check if the vital features of Shark are
 * working properly.
 *
 * This class does not contain any tests of its own.
 * 
 * @author mfi
 */
@RunWith(Suite.class)
@SuiteClasses({
    ExchangeTests.class,
    SimpleKBTest.class,
    Assimilate_ExtractionTests.class,
    Dynamics_Notifier_Tests.class,
    SerializationTest.class,
    InMemoAssociatedSTSetTest.class,
    FSSharkKBTest.class,
    StreamTests.class
})

public class Testsuite {

    public Testsuite() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        System.out.println("\n\nDone!\n");

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
}