import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.sql.SQLSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
     public void basicSTSetTests() throws SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        SQLSharkKB kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        kb.drop();
        kb.close();
        kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        
        TimeSTSet timeSTSet = kb.getTimeSTSet();
        
        TimeSemanticTag tst = timeSTSet.createTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, TimeSemanticTag.FOREVER);
        
        tst.setProperty("p1", "v1");
        
        String property = tst.getProperty("p1");
        
        Assert.assertEquals(property, "v1");
        
        // test persistency
        kb.close();
        kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        timeSTSet = kb.getTimeSTSet();
        tst = timeSTSet.timeTags().nextElement();
        property = tst.getProperty("p1");
        Assert.assertEquals(property, "v1");
     }
     
     @Test
     public void addAndRemoveOnSemanticTag() throws SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        SQLSharkKB kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        kb.drop();
        kb.close();
        kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        
        STSet stSet = kb.getTopicSTSet();
        SemanticTag st = stSet.createSemanticTag("Shark", "http://www.sharksystem.net");
        
        st.setProperty("p1", "v1");
        String property = st.getProperty("p1");
        Assert.assertEquals(property, "v1");
        
        st.setName("SharkFW");
        Assert.assertEquals("SharkFW", st.getName());
        
        st.addSI("http://www.sharkfw.net");
        st.removeSI("http://www.sharksystem.net");
        
        // test persistency
        kb.close();
        kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        
        
        stSet = kb.getTopicSTSet();
        st = stSet.getSemanticTag("http://www.sharksystem.net");
        Assert.assertNull(st);
        
        st = stSet.getSemanticTag("http://www.sharkfw.net");
        Assert.assertNotNull(st);
        
        Assert.assertEquals("SharkFW", st.getName());
        
        property = st.getProperty("p1");
        Assert.assertEquals(property, "v1");
     }
     
}
