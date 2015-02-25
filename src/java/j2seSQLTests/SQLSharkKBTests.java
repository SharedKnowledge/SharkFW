import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.sql.SQLSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Assert;
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
     public void createDB() throws SharkKBException {
         L.setLogLevel(L.LOGLEVEL_ALL);
         SQLSharkKB kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
         
         // I can close 
         kb.close();
         
         // and reconnect
         kb.reconnect();
         
         // and close again
         kb.close();
     }
     
     @Test
     public void getVocabulary() throws SharkKBException {
         L.setLogLevel(L.LOGLEVEL_ALL);
         SharkKB kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
         
        STSet topics = kb.getTopicSTSet();
        
        topics.createSemanticTag("Shark", "http://sharksystem.net");
        
        SemanticTag semanticTag = topics.getSemanticTag("http://sharksystem.net");
        
        Assert.assertNotNull(semanticTag);
     }
}
