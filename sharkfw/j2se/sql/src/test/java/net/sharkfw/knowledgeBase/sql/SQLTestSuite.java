import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author thsc
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    SQLSharkKBTests.class, 
    SQKKBTests.class}
)
public class SQLTestSuite {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    } 
}
