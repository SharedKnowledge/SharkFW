import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.sql.SQLSharkKB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thsc
 */
public class SQLSharkKBTests {
    
    public SQLSharkKBTests() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void hello() {
         SharkKB kb = new SQLSharkKB();
     }
}
