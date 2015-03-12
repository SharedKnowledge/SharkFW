package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
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
public class SyncInformationTests {

    SharkKB syncKB, inMemoKB;
    
    ContextPoint teapotCP;
    
    public SyncInformationTests() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws SharkKBException {
//         Create ourselves a fresh Shark Knowledgebase
//        syncKB = new SyncKB(new InMemoSharkKB());
        inMemoKB = new InMemoSharkKB();
        
        // And add some vocabulary
        SemanticTag teapotST = inMemoKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
        SemanticTag programmingST = inMemoKB.createSemanticTag("Programming", "http://en.wikipedia.org/wiki/Programming");
        PeerSemanticTag alice = inMemoKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
        PeerSemanticTag bob = inMemoKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
        TimeSemanticTag timeST = inMemoKB.createTimeSemanticTag(100, 9000);
        SpatialSemanticTag spatialST = inMemoKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" });
        
        teapotCP = inMemoKB.createContextPoint(inMemoKB.createContextCoordinates(teapotST, bob, bob, alice, timeST, spatialST, SharkCS.DIRECTION_INOUT));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void syncInformation_createInformation_hasDefaultVersion() throws SharkKBException {
        Information teapotInfo = new SyncInformation(InMemoSharkKB.createInMemoInformation());
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        teapotInfo = new SyncInformation(teapotCP.addInformation("Teapots are great."));
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        teapotInfo = new SyncInformation(teapotCP.addInformation("The best teapots are made from clay".getBytes()));
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        teapotInfo = new SyncInformation(teapotCP.addInformation());
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
    }

    @Test
    public void syncInformation_removeContent_versionIncreased() throws SharkKBException {
        Information teapotInformation = new SyncInformation(teapotCP.addInformation("Teapots teapots"));
        teapotInformation.removeContent();
        
        assertNotNull(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
    } 
    
    @Test
    public void syncInformation_setContent_versionIncreased() throws SharkKBException {
        Information teapotInformation = new SyncInformation(teapotCP.addInformation("I like tea."));
        
        teapotInformation.setContent("Especially green and black tea.");
        assertNotNull(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
        
        teapotInformation.setContent("But other tea is good, too.".getBytes());
        assertNotNull(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "3");
        
    }
    
    @Test
    public void syncInformation_setContentType_versionIncreased() throws SharkKBException {
        Information teapotInformation = new SyncInformation(teapotCP.addInformation("Teapots are a formidable invention."));
        teapotInformation.setContentType("image");
        
        assertNotNull(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
    }
}
