import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sql.SQLSharkKB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thsc
 */
public class SQKKBTests extends ApiRev1.SimpleKBTest {
    private SQLSharkKB sqkKB;
    
    public SQKKBTests() {
    }
    
    @Before
    public void setUp() {
        try {
            this.sqkKB = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
            this.kb = this.sqkKB;
        } catch (SharkKBException ex) {
            // ing
        }
    }
    
    @After
    public void tearDown() {
        try {
            this.sqkKB.drop();
            this.sqkKB.close();
        } catch (SharkKBException ex) {
            // ignore
        }
    }
    
    
    


    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
