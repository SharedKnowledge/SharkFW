package net.sharkfw.knowledgeBase.sync;


// JUnit imports
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *  @author Simon Arnold <s0539710@htw-berlin.de>
 *  @author Veit Heller <veit@veitheller.de>
 */
public class SyncContextPointTests {
    
    SharkKB inMemoKB;		

    SemanticTag teapotST, programmingST;
    PeerSemanticTag alice;
    PeerSemanticTag bob;
    TimeSemanticTag timeST;
    SpatialSemanticTag spatialST;
    
    ContextCoordinates teapotCC, programmingCC;
    
    public SyncContextPointTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SharkKBException {
        inMemoKB = new InMemoSharkKB();
    
        teapotST = inMemoKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
        alice = inMemoKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
        bob = inMemoKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
        timeST = inMemoKB.createTimeSemanticTag(100, 9000);
        spatialST = inMemoKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" });
        
        teapotCC = inMemoKB.createContextCoordinates(teapotST, alice, bob, bob, timeST, spatialST, SharkCS.DIRECTION_INOUT);
        programmingCC = inMemoKB.createContextCoordinates(programmingST, bob, alice, alice, timeST, spatialST, SharkCS.DIRECTION_IN);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void SyncCP_addInformation_isSyncInformation()throws SharkKBException {
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));
        Information i = teapotCP.addInformation("Teapots lalala.");
        assert(i instanceof SyncInformation);
    }
    
    /** 
     * Creates a new context point and checks for version.
     * @throws SharkKBException 
     */
    @Test
    public void SyncCP_createNewCP_CPGetsDefaultVersion() throws SharkKBException {
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));
        
        String version = teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME);
        assertNotNull(version);
        assertEquals(version, SyncContextPoint.VERSION_DEFAULT_VALUE);
    }

    /**
     * As Empty, Information, Byte, String etc...
     */
    @Test
    public void SyncCP_addInformation_CPVersionIncremented() throws SharkKBException{
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));

        teapotCP.addInformation();
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "2");

        teapotCP.addInformation(InMemoSharkKB.createInMemoInformation());
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "3");

        teapotCP.addInformation("Teapots can be made from different material.");
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "4");

        teapotCP.addInformation("For example silver, iron or glass.".getBytes());
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "5");

//        byte[] textBytes = "Some were even made from gold.".getBytes();
//        InputStream is = new ByteInputStream(textBytes, textBytes.length + 1);
//        teapotCP.addInformation(is, textBytes.length);
//        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "6");
    }
    
    @Test
    public void SyncCP_removeInformation_CPVersionIncremented() throws SharkKBException {
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));

        // After adding information, the version should be 2
        Information teapotInfo = teapotCP.addInformation("Information about teapot");
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "2");

        // After removing it should be 3
        teapotCP.removeInformation(teapotInfo);
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "3");
    }
    
    @Test
    public void SyncCP_setContextCoordinatesToDifferent_CPGetsDefaultVersion() throws SharkKBException {
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));

        // Increase version somehow
        teapotCP.addInformation();
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "2");

        // Re-define context coordinates
        ContextCoordinates newTeapotCC = inMemoKB.createContextCoordinates(teapotST, bob, bob, alice, timeST, spatialST, SharkCS.DIRECTION_INOUT);
        teapotCP.setContextCoordinates(newTeapotCC);

        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), SyncContextPoint.VERSION_DEFAULT_VALUE);
    }
    
    @Test
    public void SyncCP_setContextCoordinatesToSame_CPKeepsVersion() throws SharkKBException {
        ContextPoint teapotCP = new SyncContextPoint(inMemoKB.createContextPoint(teapotCC));

        // Increase version somehow
        teapotCP.addInformation();
        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "2");

        // Set CC back to same value
        teapotCP.setContextCoordinates(teapotCC);

        assertEquals(teapotCP.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME), "2");
    }

    
}
