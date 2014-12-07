package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;


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

	SharkKB inMemoKB = null;		
        SemanticTag teapotST;
        PeerSemanticTag alice, bob;
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
    public void setUp() {
        inMemoKB = new InMemoSharkKB();
        
        try {
            teapotST = inMemoKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
            alice = inMemoKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
            bob = inMemoKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
            timeST = inMemoKB.createTimeSemanticTag(100, 9000);
            spatialST = inMemoKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" });
        } catch (SharkKBException e) {
            fail(e.toString());
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void syncKB_createContextPoint_isSyncCP() throws SharkKBException {
        SharkKB syncKB = new SyncKB(inMemoKB);
        ContextPoint syncCP = syncKB.createContextPoint(syncKB.createContextCoordinates(teapotST, alice, bob, alice, timeST, spatialST, SharkCS.DIRECTION_OUT));
        assert(syncCP instanceof SyncContextPoint);
    }
    
    @Test
    public void syncKB_createWithEmptySourceKB_syncKBIsCreated() throws SharkKBException {
        SharkKB emptyKB = new InMemoSharkKB();
        SharkKB syncKB = new SyncKB(emptyKB);
        assertNotNull(syncKB);
    }
    
    @Test
    public void syncKB_createWithNonEmptySourceKB_syncKBIsCreated () throws SharkKBException {
        ContextCoordinates teapotCC = inMemoKB.createContextCoordinates(teapotST, alice, alice, bob, timeST, spatialST, SharkCS.DIRECTION_INOUT);
        ContextPoint teapotCP = inMemoKB.createContextPoint(teapotCC);
        teapotCP.addInformation("The first documented mentioning of tea was in 221 B.C.");
        
        // Test that we really have an CP in our KB now
        assertNotNull(inMemoKB.getContextPoint(teapotCC));
        
        SharkKB syncKB = new SyncKB(inMemoKB);
        assertNotNull(syncKB);
    }

    @Test
    public void syncKB_createWithNonEmptySourceKB_allCPAreVersioned () throws SharkKBException {
        ContextCoordinates teapotCC = inMemoKB.createContextCoordinates(teapotST, alice, alice, bob, timeST, spatialST, SharkCS.DIRECTION_INOUT);
        ContextPoint teapotCP = inMemoKB.createContextPoint(teapotCC);
        teapotCP.addInformation("Although teapots were not known until the Ming dynasty.");
        ContextCoordinates teapotCC2 = inMemoKB.createContextCoordinates(teapotST, bob, alice, bob, timeST, spatialST, SharkCS.DIRECTION_IN);
        ContextPoint teapotCP2 = inMemoKB.createContextPoint(teapotCC2);
        teapotCP2.addInformation("I like tea more than I like coffee. Sometimes.");
        
        SyncKB syncKB = new SyncKB(inMemoKB);
        
        Enumeration<ContextPoint> cps = syncKB.getAllContextPoints();
        assertNotNull(cps);
        while (cps.hasMoreElements()) {
            ContextPoint cp = cps.nextElement();
            String version = cp.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME);
            assertNotNull(version);
            assertEquals(version, SyncContextPoint.VERSION_DEFAULT_VALUE);
//            Enumeration<Information> i = cp.nextElement().enumInformation();
//            while(i.hasMoreElements()){
//                String versionProperty = i.nextElement().getProperty("SyncI_version");
//                assertNotNull(versionProperty);
//            }
        }
    }
}
