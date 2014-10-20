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
public class SyncKBTests{

	SharkKB _sharkKB = null;		

    public SyncKBTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        _sharkKB = new InMemoSharkKB();
        
        try {
            SemanticTag teapotST = _sharkKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
            PeerSemanticTag alice = _sharkKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
            PeerSemanticTag bob = _sharkKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
            TimeSemanticTag timeST = _sharkKB.createTimeSemanticTag(100, 9000);
            SpatialSemanticTag spatialST = _sharkKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" }, null);
            ContextCoordinates cc = _sharkKB.createContextCoordinates(teapotST, alice, alice, bob, timeST, spatialST, SharkCS.DIRECTION_INOUT);
            ContextPoint cp = _sharkKB.createContextPoint(cc);
            Information info = cp.addInformation("This is an information.");
        } catch (SharkKBException e) {
            fail(e.toString());
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createSyncKB() throws SharkKBException {
        SyncKB testKB = new SyncKB(_sharkKB);
        assertNotNull(testKB);
        Enumeration<ContextPoint> cp = testKB.getAllContextPoints();
        assertNotNull(cp);
        while(cp.hasMoreElements()){
            Enumeration<Information> i = cp.nextElement().enumInformation();
            while(i.hasMoreElements()){
                String versionProperty = i.nextElement().getProperty("version");
                assertNotNull(versionProperty);
                System.out.println(versionProperty);
            }
        }
    }


}
