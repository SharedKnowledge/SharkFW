package ApiRev1;

import java.io.IOException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Util;
import org.junit.*;

/**
 *
 * @author thsc
 */
public class PropertyTransferTests implements KPListener {
    J2SEAndroidSharkEngine aliceSE, bobSE;
    SharkKB aliceKB, bobKB;
    SemanticTag alicep2p, bobp2p;
    PeerSemanticTag meAlice, meBob, bobAlice, aliceBob;
    PeerSemanticTag anyPeer, anyBobPeer;
    TimeSemanticTag anyTime, anyBobTime;
    SpatialSemanticTag anyWhere, anyBobWhere;
    ContextCoordinates aliceCC, bobCC;
    ContextPoint aliceCP;
    Information i;
    Interest aliceInterest, bobInterest;
    
    String[] bobSI = new String[] {"http://www.sharksystem.net/Bob.html"};
    String[] aliceSI = new String[] {"http://www.sharksystem.net/Alice.html"};
    
    private int alicePort = 5540;
    private int bobPort = 6640;
    
    private String bobAddress;
    private String aliceAddress;
    
    private String testpropertyContent;
    private ContextPoint receivedCP;

    public PropertyTransferTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
       
    
    @Before
    public void setUp() throws SharkKBException {
        this.aliceSE = new J2SEAndroidSharkEngine();
        this.aliceKB = new InMemoSharkKB();

        this.alicePort++;
        this.bobPort++;
        this.aliceAddress = "tcp://localhost:" + new Integer(this.alicePort);
        this.bobAddress = "tcp://localhost:" + new Integer(this.bobPort);
        
        this.aliceBob = aliceKB.createPeerSemanticTag("Bob", 
                "http://www.sharksystem.net/Bob.html", 
                this.bobAddress);

        this.alicep2p = aliceKB.createSemanticTag("P2P", 
                                "http://de.wikipedia.org/wiki/Peer-to-Peer");
        
        this.meAlice = aliceKB.createPeerSemanticTag("Alice", 
                this.aliceSI, 
                this.aliceAddress);
        
        this.anyPeer = null;
        this.anyTime = null;
        this.anyWhere  = null;
                
        this.aliceCC = aliceKB.createContextCoordinates(alicep2p, meAlice, 
                anyPeer, meAlice, anyTime, anyWhere, SharkCS.DIRECTION_OUT);
        
        this.aliceCP = aliceKB.createContextPoint(aliceCC);
        
        this.i = aliceCP.addInformation("P2P ist toll.");
        
        this.aliceInterest = aliceKB.createInterest(aliceCC);
        
        // Bob
        this.bobSE = new J2SEAndroidSharkEngine();
        
        this.bobKB = new InMemoSharkKB();
        
        this.bobp2p = bobKB.createSemanticTag("P2P", 
                                "http://de.wikipedia.org/wiki/Peer-to-Peer");
        
        this.meBob = bobKB.createPeerSemanticTag("Bob", 
                this.bobSI, 
                this.bobAddress);
        
        this.anyBobPeer = null;
        this.anyBobTime = null;
        this.anyBobWhere  = null;
                
        this.bobCC = bobKB.createContextCoordinates(bobp2p, meBob, 
                anyBobPeer, anyBobPeer, anyBobTime, anyBobWhere, SharkCS.DIRECTION_IN);
        
        this.bobAlice = bobKB.createPeerSemanticTag("Alice", 
                "http://www.sharksystem.net/Alice.html", 
                this.aliceAddress);

        this.bobInterest = bobKB.createInterest(bobCC);
    }
    
    @After
    public void tearDown() {
        this.aliceSE.stop();
        this.bobSE.stop();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            // TODO
        }
    }
    
    @Test
    public void cpPropertyTest() throws InterruptedException, SharkSecurityException, SharkKBException, IOException {
        this.aliceCP.addInformation("test");
        this.aliceCP.setProperty("testproperty", "testpropertyvalue");
        
        KnowledgePort aliceKP = new StandardKP(aliceSE, aliceKB, aliceInterest);
        
//        System.out.println(this.getString());
        
        aliceSE.setConnectionTimeOut(1000);
        aliceSE.startTCP(this.alicePort);
        
        // give it a second
        Thread.sleep(500);
        
        // Bob
        KnowledgePort bobKP = new StandardKP(bobSE, bobKB, bobInterest);
        bobKP.addListener(this);
        
        bobSE.setConnectionTimeOut(1000);
        bobSE.startTCP(this.bobPort);
        
        bobSE.publishKP(bobKP, bobAlice);
        
//                Thread.sleep(Long.MAX_VALUE);

        Thread.sleep(2000);

        if(!this.testpropertyContent.equalsIgnoreCase("testpropertyvalue")) {
            Assert.fail();
        }
    }

    @Test
    public void setSender2CPPropertyTest() throws InterruptedException, SharkSecurityException, SharkKBException, IOException {
        this.aliceCP.addInformation("test");
        
        KnowledgePort aliceKP = new StandardKP(aliceSE, aliceKB, aliceInterest);
        
//        System.out.println(this.getString());
        
        aliceSE.setConnectionTimeOut(1000);
        aliceSE.startTCP(this.alicePort);
        
        // give it a second
        Thread.sleep(500);
        
        // Bob
        KnowledgePort bobKP = new StandardKP(bobSE, bobKB, bobInterest);
        bobKP.addListener(this);
        
        bobSE.setConnectionTimeOut(1000);
        bobSE.startTCP(this.bobPort);
        
        bobSE.publishKP(bobKP, bobAlice);
        
//                Thread.sleep(Long.MAX_VALUE);

        Thread.sleep(2000);
        
        // Bob has assimilated something
        String aliceSIString = this.receivedCP.getProperty(KEPInMessage.SENDER_SI_STRING_PROPERTY);
        String[] aliceSI = Util.string2array(aliceSIString);
        
        // at least one SI must fit
        boolean fit = false;
        int counter = this.aliceSI.length;
        while(!fit && counter-- > 0) {
            if(this.aliceSI[counter].equalsIgnoreCase(aliceSI[0]));
            fit = true;
        }
        
        if(!fit) {
            Assert.fail("sender not set after receiving message");
        }
    }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        System.out.println("received something");
        this.receivedCP = newCP;
        this.testpropertyContent = newCP.getProperty("testproperty");
    }
}
