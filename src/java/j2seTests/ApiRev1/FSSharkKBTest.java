package ApiRev1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mfi
 */
public class FSSharkKBTest {
    
    private static final String FOLDER = "Temp/sharkkb";
    private static final String ALICE_SIS = "http://www.sharksystem.net/alice.html";
    private static final String ALICE_ADDR = "mail://alice@sharksystem.net";

    public FSSharkKBTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSingleSemanticTag() throws IOException, FileNotFoundException, SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        // ensure empty folder.
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();
        
        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        
        String javaSI = "http://www.java.net";
        SemanticTag javaTag = kb.createSemanticTag("Java", javaSI);
        javaTag.setProperty("testProp", "testPropValue");
        
        SemanticTag javaTagAgain = kb.createSemanticTag("Java", javaSI);
        
        // should be the same
        Assert.assertEquals(javaTag, javaTagAgain);
        

        PeerSemanticTag p1 = kb.createPeerSemanticTag("Alice", FSSharkKBTest.ALICE_SIS, FSSharkKBTest.ALICE_ADDR);
        PeerSemanticTag p2 = kb.createPeerSemanticTag("Alice", FSSharkKBTest.ALICE_SIS, FSSharkKBTest.ALICE_ADDR);
        Assert.assertEquals(p1, p2);
        
        String nonPersistentSI = "http://aSI.de";
        SemanticTag nonPersistentTag = FSSharkKB.createInMemoSemanticTag("aTag", nonPersistentSI);
        
        // recreate
        kb = new FSSharkKB(FOLDER);
        
        SemanticTag javaTag2 = kb.getSemanticTag(javaSI);
        Assert.assertTrue(SharkCSAlgebra.identical(javaTag, javaTag2));
        
        SemanticTag tag3 = kb.getSemanticTag(nonPersistentSI);
        Assert.assertNull(tag3);
    }

    @Test
    public void testLinks() throws IOException, FileNotFoundException, SharkKBException {
        
        // ensure empty folder.
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();

        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        
        Taxonomy topicsTX = kb.getTopicsAsTaxonomy();
        
        TXSemanticTag javaTag;
        TXSemanticTag plTag;
        
        String javaSI = "http://www.java.net";
        String plSI = "http://www.sharknet.net/programmingLanguage.html";
        
        plTag = topicsTX.createTXSemanticTag("ProgrammingLanguage", plSI);
        javaTag = topicsTX.createSemanticTag(plTag, "Java", new String[]{javaSI});
        
        // recreate
        kb = new FSSharkKB(FOLDER);
        
        topicsTX = kb.getTopicsAsTaxonomy();
        
        TXSemanticTag javaTag2 = topicsTX.getSemanticTag(javaSI);
        
        Assert.assertNotNull(javaTag2);
        
        TXSemanticTag plTag2 = javaTag2.getSuperTag();
        
        Assert.assertTrue(SharkCSAlgebra.identical(plTag2, plTag));
    }

    @Test
    public void testCP() throws IOException, FileNotFoundException, SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        // ensure empty folder.
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();
        
        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        
        String javaSI = "http://www.java.net";
        SemanticTag javaTag = kb.createSemanticTag("Java", javaSI);
        
        ContextCoordinates cc = kb.createContextCoordinates(javaTag, null, null, null, null, null, SharkCS.DIRECTION_OUT);
        
        ContextPoint cp = kb.createContextPoint(cc);
        
        String infoContent = "hallo";
        cp.addInformation(infoContent);
        
        // drop kb and re-create
        kb = new FSSharkKB(FOLDER);
        
        javaTag = kb.getSemanticTag(javaSI);
        cc = kb.createContextCoordinates(javaTag, null, null, null, null, null, SharkCS.DIRECTION_OUT);
        
        cp = kb.getContextPoint(cc);
        
        Information i = cp.enumInformation().nextElement();
        
        String content = new String(i.getContentAsByte());
        
        Assert.assertTrue(content.equals(infoContent));
    }
    
    @Test
    public void testInformatioInputStream() throws IOException, FileNotFoundException, SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        // ensure empty folder.
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();
        
        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        
        String javaSI = "http://www.java.net";
        SemanticTag javaTag = kb.createSemanticTag("Java", javaSI);
        
        ContextCoordinates cc = kb.createContextCoordinates(javaTag, null, null, null, null, null, SharkCS.DIRECTION_OUT);
        
        ContextPoint cp = kb.createContextPoint(cc);
        
        String infoContent = "hallo";
        
        byte[] infoByteContent = infoContent.getBytes();
        Information i = cp.addInformation(infoByteContent);
        
        // get it back
        byte[] retrievedContent = new byte[infoByteContent.length];
        
        InputStream infoInputStream = i.getInputStream();
        infoInputStream.read(retrievedContent);
        
        // must be identical
        Assert.assertArrayEquals(infoByteContent, retrievedContent);
    }
    
    @Test
    public void identicalObjects() throws IOException, FileNotFoundException, SharkKBException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        // ensure empty folder.
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();
        
        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        

        PeerSemanticTag alice = kb.createPeerSemanticTag("Alice", ALICE_SIS, ALICE_ADDR);
        
        PeerSemanticTag alice2 = kb.createPeerSemanticTag("Alice", ALICE_SIS, ALICE_ADDR);
        
        Assert.assertTrue(alice == alice2);
    }
    
     @Test
     public void ownerPersistent() throws SharkKBException {
        FSSharkKB.removeFSStorage(FOLDER);
        File kbFolder = new File(FOLDER);
        kbFolder.mkdirs();
        
        FSSharkKB kb;
        kb = new FSSharkKB(FOLDER);
        
        // set owner
        PeerSemanticTag aliceTag = kb.createPeerSemanticTag("Alice", ALICE_SIS, ALICE_ADDR);
        kb.setOwner(aliceTag);
        
        // drop and restore
        kb = new FSSharkKB(FOLDER);
        
        PeerSemanticTag owner = kb.getOwner();
        
        Assert.assertTrue(SharkCSAlgebra.identical(owner, aliceTag));
     }
}