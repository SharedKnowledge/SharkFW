import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
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
public class UnderConstructionTests {
    
    public UnderConstructionTests() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    

     @Test
     public void vocabularyTests() throws SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        SQLSharkKB kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        kb.drop();
        kb.close();
        kb = new SQLSharkKB("jdbc:postgresql://localhost:5432/SharkKB", "test", "test");
        
        STSet topics = kb.getTopicSTSet();
        
        topics.createSemanticTag("Shark", "http://sharksystem.net");

        PeerSTSet peers = kb.getPeerSTSet();
        peers.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@sharksystem.net");
        
        kb.getTimeSTSet().createTimeSemanticTag(System.currentTimeMillis(), TimeSemanticTag.FOREVER);
        kb.getSpatialSTSet().createSpatialSemanticTag("spatial tag", new String[] {"http://spatialSI"}, (SharkGeometry) null);
        
        SemanticTag semanticTag = topics.getSemanticTag("http://sharksystem.net");
        Assert.assertNotNull(semanticTag);

        String[] sis = new String[] {"http://a.de", "http://b.de"};
        topics.createSemanticTag("A", sis);
        semanticTag = topics.getSemanticTag("http://a.de");
        Assert.assertNotNull(semanticTag);
     }
}
