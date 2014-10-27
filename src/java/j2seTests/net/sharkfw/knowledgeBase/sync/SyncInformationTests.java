package net.sharkfw.knowledgeBase.sync;

import java.io.FileInputStream;
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

    SharkKB syncKB;
    
    ContextPoint teapotCP, programmingCP;
    
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
        syncKB = new SyncKB(new InMemoSharkKB());
        
        // And add some vocabulary
        SemanticTag teapotST = syncKB.createSemanticTag("Teapot", "http://de.wikipedia.org/wiki/Teekanne");
        SemanticTag programmingST = syncKB.createSemanticTag("Programming", "http://en.wikipedia.org/wiki/Programming");
        PeerSemanticTag alice = syncKB.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html", "alice@shark.net");
        PeerSemanticTag bob = syncKB.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", "bob@shark.net");
        TimeSemanticTag timeST = syncKB.createTimeSemanticTag(100, 9000);
        SpatialSemanticTag spatialST = syncKB.createSpatialSemanticTag("Berlin", new String[] { "Berlin" }, null);
        
        teapotCP = syncKB.createContextPoint(syncKB.createContextCoordinates(teapotST, bob, bob, alice, timeST, spatialST, SharkCS.DIRECTION_INOUT));
        programmingCP = syncKB.createContextPoint(syncKB.createContextCoordinates(programmingST, alice, alice, bob, timeST, spatialST, SharkCS.DIRECTION_OUT));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void syncInformation_createInformation_hasDefaultVersion() {
        Information teapotInfo = teapotCP.addInformation();
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        teapotInfo = teapotCP.addInformation("Teapots are great.");
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        teapotInfo = teapotCP.addInformation("The best teapots are made from clay".getBytes());
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
        
        // Mock this
//        teapotInfo = teapotCP.addInformation(new Inputstream(), len)
        
        teapotInfo = InMemoSharkKB.createInMemoInformation();
        teapotCP.addInformation(teapotInfo);
        assertNotNull(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInfo.getProperty(SyncInformation.VERSION_PROPERTY_NAME), SyncInformation.VERSION_DEFAULT_VALUE);
    }

    // TODO cant get this to work
//    @Test
//    public void syncInformation_updateWithInputStream_versionIncreased() throws SharkKBException {
//        Information programmingInformation = programmingCP.addInformation();
//        InputStream i = programmingInformation.getInputStream();
//        i.
//    }
    
    @Test
    public void syncInformation_removeContent_versionIncreased() {
        Information programmingInformation = programmingCP.addInformation("Programming is superawesome");
        programmingInformation.removeContent();
        
        assertNotNull(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
    }
    
    @Test
    public void syncInformation_setContent_versionIncreased() {
        Information programmingInformation = programmingCP.addInformation("Programming is best with computers.");
        
        programmingInformation.setContent("Programming is best with fast computers.");
        assertNotNull(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
        
         programmingInformation.setContent("Programming is best with fast computers running on linux.".getBytes());
        assertNotNull(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "3");
        
        // TODO mock this
//        programmingInformation.setContent(new FileInputStream(""), 5);
//        assertNotNull(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
//        assertEquals(programmingInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
        
    }
    
    @Test
    public void syncInformation_setContentType_versionIncreased() {
        Information teapotInformation = teapotCP.addInformation("Teapots are best without holes.");
        teapotInformation.setContentType("image");
        
        assertNotNull(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME));
        assertEquals(teapotInformation.getProperty(SyncInformation.VERSION_PROPERTY_NAME), "2");
    }
}
