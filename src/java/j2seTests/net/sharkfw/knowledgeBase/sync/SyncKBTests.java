package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;


// JUnit imports
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Veit Heller <veit@veitheller.de>
 * @author Simon Arnold <s0539710@htw-berlin.de>
 */
public class SyncKBTests {

    SharkKB inMemoKB;		

    SemanticTag teapotST, programmingST;
    PeerSemanticTag alice;
    PeerSemanticTag bob;
    TimeSemanticTag timeST;
    SpatialSemanticTag spatialST;
    
    public SyncKBTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SharkKBException {
        // Create ourselves a fresh Shark Knowledgebase
        inMemoKB = new InMemoSharkKB();
        
        // And add some vocabulary and knowledge
        teapotST = inMemoKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
        programmingST = inMemoKB.createSemanticTag("Programming", "http://en.wikipedia.org/wiki/Programming");
        alice = inMemoKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
        bob = inMemoKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
        timeST = inMemoKB.createTimeSemanticTag(100, 9000);
        spatialST = inMemoKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" }, null);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void SyncKB_createKB_notNull() throws SharkKBException {
        SharkKB syncKB = new SyncKB(inMemoKB);
        assertNotNull(syncKB);
    }
    
    @Test
    public void SyncKB_createCP_isSyncCP() throws SharkKBException {
        SharkKB syncKB = new SyncKB(inMemoKB);
        ContextCoordinates cc = syncKB.createContextCoordinates(teapotST, alice, bob, alice, timeST, spatialST, SharkCS.DIRECTION_OUT);
        assert(syncKB.createContextPoint(cc) instanceof SyncContextPoint);
    }
    
//    @Test
//    public void testEverythingVersionedAfterCreating() throws SharkKBException{
//        SyncKB testKB = new SyncKB(inMemoKB);
//        assertNotNull(testKB);
//        Enumeration<ContextPoint> cp = testKB.getAllContextPoints();
//        assertNotNull(cp);
//        while(cp.hasMoreElements()){
//            Enumeration<Information> i = cp.nextElement().enumInformation();
//            while(i.hasMoreElements()){
//                String versionProperty = i.nextElement().getProperty("version");
//                assertNotNull(versionProperty);
//                System.out.println(versionProperty);
//            }
//        }
//    }
    

}
