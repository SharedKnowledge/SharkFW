package ApiRev1;

import java.io.IOException;
import junit.framework.Assert;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkSecurityException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jacob Zschunke
 */
public class BUG_NoDoublettInformationSuppressionTest {
    
    public BUG_NoDoublettInformationSuppressionTest() {
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
    public void dublicateInformationSupressionTest() throws SharkProtocolNotSupportedException, InterruptedException, SharkKBException, SharkSecurityException, IOException {
        
        // init alice
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
        SharkKB aliceKB = new InMemoSharkKB();
        FragmentationParameter fp = new FragmentationParameter(true, true, 5);
        FragmentationParameter[] fps = aliceKB.getStandardFPSet();
        fps[SharkCS.DIM_TOPIC] = fp;
        
        
        PeerSemanticTag aliceOwnerTag = aliceKB.createPeerSemanticTag("Alice", "http://alice.de", "tcp://localhost:1212");
//        PeerSemanticTag aliceBobTag = aliceKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:2121");
        
        Taxonomy topicsTX = aliceKB.getTopicsAsTaxonomy();
        TXSemanticTag aliceJapanTag = topicsTX.createTXSemanticTag("Japan", "http://www.nippon.jp");
        TXSemanticTag aliceTopicTag = topicsTX.createTXSemanticTag("Kyoto", "http://www.kyoto.jp");        
        aliceTopicTag.move(aliceJapanTag);
        
//        Interest aliceInterest = aliceKB.createInterest(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, null, null, null, ContextSpace.OUT));
        Interest aliceInterest = aliceKB.createInterest(aliceKB.createContextCoordinates(aliceTopicTag, aliceOwnerTag, null, null, null, null, SharkCS.DIRECTION_OUT));
        StandardKP aliceKP = new StandardKP(alice, aliceInterest, aliceKB);
        
//        ContextPoint cp = aliceKB.createContextPoint(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, aliceOwnerTag, null, null, ContextSpace.OUT));
        ContextPoint cp = aliceKB.createContextPoint(aliceKB.createContextCoordinates(aliceTopicTag, aliceOwnerTag, null, aliceOwnerTag, null, null, SharkCS.DIRECTION_OUT));
        cp.addInformation("Kyoto, das Tokyo für Anagramm-liebhaber");
        cp.addInformation("Noch eine weitere Tolle Info über Japan!!!");
        alice.startTCP(1212);
        
        // init Bob
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
        SharkKB bobKB = new InMemoSharkKB();
        bobKB.setStandardFPSet(fps);
        
        PeerSemanticTag bobOwnerTag = bobKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:4131");
        //SemanticTag bobTopicTag = bobKB.createSemanticTag("Nippon", "http://www.nippon.jp");
        
        Interest bobInterest = bobKB.createInterest(bobKB.createContextCoordinates(null, bobOwnerTag, null, null, null, null, SharkCS.DIRECTION_IN));
        
        StandardKP bobKP = bob.createKP(bobInterest, bobKB);
        
        bob.startTCP(4131);
        
        // start communication
        
        alice.publishKP(aliceKP, bobOwnerTag);
        
        Thread.sleep(10000);
        
        alice.publishKP(aliceKP, bobOwnerTag);
        
        Thread.sleep(4000);
       
        alice.publishKP(aliceKP, bobOwnerTag);
        
        Thread.sleep(4000);
        
        ContextPoint cp2 = bobKB.getContextPoint(bobKB.createContextCoordinates(aliceTopicTag, aliceOwnerTag, null, aliceOwnerTag, null, null, SharkCS.DIRECTION_IN));
        
        Assert.assertEquals(2, cp2.getNumberInformation());
    }
}
