package net.sharkfw.knowledgeBase.sync;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.kep.SharkProtocolNotSupportedException;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncKPTests {

    private final long connectionTimeOut = 2000;
    private final long SLEEP_TIMEOUT = 6000;
    private SyncKB _aliceKB, _bobKB;
    private SyncKP _aliceSyncKP, _bobSyncKP;
    private SharkEngine _aliceEngine, _bobEngine;
    
    // have smth to talk about
    SemanticTag _teapotST =  InMemoSharkKB.createInMemoSemanticTag("teapot", "www.teapot.de");
    SemanticTag _noodlesST = InMemoSharkKB.createInMemoSemanticTag("noodles", "www.noodles.de");
    PeerSemanticTag _alice, _bob;
    int _alicePort, _bobPort;
//    PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice", "tcp://localhost:5555");
//    PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob", "tcp://localhost:5556");
    
    static int currentPort = 5555;
    private int getPort() {
        return currentPort++;
    }
    
    
    @BeforeClass
    public static void setUpClass(){
            L.setLogLevel(L.LOGLEVEL_DEBUG);
    }

    private static final String ALICE_IDENTIFIER = "aliceIdentifier";
    private static final String BOB_IDENTIFIER = "bobIdentifier";
    
    @Before
    public void setUp() throws SharkKBException{
        // Set up KBs
        _aliceKB = new SyncKB(new InMemoSharkKB());
        _bobKB = new SyncKB(new InMemoSharkKB());
        // Set up peers with new ports
        _alicePort = getPort();
        _alice = _aliceKB.getPeerSemanticTag(ALICE_IDENTIFIER);
        if(_alice == null) {
            _alice = _aliceKB.createPeerSemanticTag("Alice", ALICE_IDENTIFIER, "tcp://localhost:"+_alicePort);
        } else {
            // alice already defined - change address
            _alice.setAddresses(new String[] {"tcp://localhost:"+_alicePort});
        }
        
        _bobPort = getPort();
        _bob = _bobKB.getPeerSemanticTag(BOB_IDENTIFIER);
        if(_bob == null) {
            _bob = _bobKB.createPeerSemanticTag("Alice", BOB_IDENTIFIER, "tcp://localhost:"+_bobPort);
        } else {
            // alice already defined - change address
            _bob.setAddresses(new String[] {"tcp://localhost:"+_bobPort});
        }
        
        // Let each know about each other
        
        // remove previously defined pst
        PeerSemanticTag tmpPeer = _aliceKB.getPeerSTSet().getSemanticTag(BOB_IDENTIFIER);
        if(tmpPeer != null) {
            _aliceKB.getPeerSTSet().removeSemanticTag(tmpPeer);
        }
        
        // add new peer description
        _aliceKB.getPeerSTSet().merge(_bob);
        
        // remove previously defined pst
        tmpPeer = _bobKB.getPeerSTSet().getSemanticTag(ALICE_IDENTIFIER);
        if(tmpPeer != null) {
            _bobKB.getPeerSTSet().removeSemanticTag(tmpPeer);
        }
        _bobKB.getPeerSTSet().merge(_alice);
        
        // Set up engines
        _aliceEngine = new J2SEAndroidSharkEngine();
        _bobEngine = new J2SEAndroidSharkEngine();
        
        // Set owner
        _aliceKB.setOwner(_alice);
        _bobKB.setOwner(_bob);
        // Kps
        _aliceSyncKP = new SyncKP(_aliceEngine, _aliceKB, 0);
        _bobSyncKP = new SyncKP(_bobEngine, _bobKB, 0);
    }

    @After
    public void tearDown() throws SharkProtocolNotSupportedException, InterruptedException {
        _aliceEngine.stopTCP();
        _bobEngine.stopTCP();
        _aliceEngine = null;
        _bobEngine = null;
        _aliceKB = null;
        _bobKB = null;
        Thread.sleep(2000);
    }

    @Test
    public void syncKP_meetsNonSyncKP_noCommunication() throws Exception {
        // TODO there is no communication at all which is not what the test is about
        
        // Bob will have a standard KP for this and is interested in anything.
        Interest bobAnyInterest = _bobKB.createInterest(_bobKB.createContextCoordinates(null, null, _bob, null, null, null, SharkCS.DIRECTION_INOUT));
        _bobEngine = new J2SEAndroidSharkEngine();
        KnowledgePort bobStandardKP = new StandardKP(_bobEngine, bobAnyInterest, _bobKB);
        
        
        // Create some information in alice Knowledge base - this would be synced IF bob was a sync KP
        ContextCoordinates teapotAliceCC = _aliceKB.createContextCoordinates(_teapotST, _alice, _alice, _bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint teapotAliceCP = _aliceKB.createContextPoint(teapotAliceCC);
        teapotAliceCP.addInformation("Teapots yay");
        // Create some information in bobs knowledge base
        ContextCoordinates noodlesBobCC = _bobKB.createContextCoordinates(_noodlesST, _bob, _alice, _bob, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint noodlesBobCP = _bobKB.createContextPoint(noodlesBobCC);
        noodlesBobCP.addInformation("I like noodles");
        
        doCommunicationStuff();

        // Neither KB should now know anything about the other contextPoint
        assertNull(_aliceKB.getContextPoint(noodlesBobCC));
        assertNull(_bobKB.getContextPoint(teapotAliceCC));
    }

    @Test
    public void syncKP_createCPInKB_CPSynced() throws Exception {
        // Each creates some information in their KB
        ContextCoordinates teapotAliceCC = _aliceKB.createContextCoordinates(_teapotST, _alice, _alice, _bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates noodlesBobCC = _bobKB.createContextCoordinates(_noodlesST, _bob, _alice, _bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint teapotAliceCP = _aliceKB.createContextPoint(teapotAliceCC);
        teapotAliceCP.addInformation("Teapots yay");
        ContextPoint noodlesBobCP = _bobKB.createContextPoint(noodlesBobCC);
        noodlesBobCP.addInformation("I like noodles.");
        
        doCommunicationStuff();

        // Bob should now know about alice's CP and the other way round
        ContextPoint retrievedCPAlice = _aliceKB.getContextPoint(noodlesBobCC);
        assertNotNull(retrievedCPAlice);
        assertEquals(1, retrievedCPAlice.getNumberInformation());
//        
        ContextPoint retrievedCPBob = _bobKB.getContextPoint(teapotAliceCC);
        assertNotNull(retrievedCPBob);
        assertEquals(1, retrievedCPBob.getNumberInformation());
    }
    
    class Publish implements Runnable{
        
        SharkEngine engine;
        PeerSemanticTag tag;
        public Publish(SharkEngine engine, PeerSemanticTag publishTo) {
            this.engine = engine;
            this.tag = publishTo;
        }
        
        @Override
        public void run() {
            try {
                engine.publishAllKP(tag);
            } catch (SharkSecurityException ex) {
                Logger.getLogger(SyncKPTests.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SharkKBException ex) {
                Logger.getLogger(SyncKPTests.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SyncKPTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void doCommunicationStuff() throws Exception {
        _aliceEngine.startTCP(_alicePort);
        _bobEngine.startTCP(_bobPort);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        Thread aliceThread = new Thread(new Publish(_bobEngine, _alice));
        aliceThread.start();
        Thread bobThread = new Thread(new Publish(_aliceEngine, _bob));
        bobThread.start();
        
        Thread.sleep(SLEEP_TIMEOUT);
        
        aliceThread.interrupt();
        bobThread.interrupt();
    }
    
    @Test
    public void syncKP_CPIsWithLowerVersionInKB_CPInformationAssimilated() throws Exception {
        // Create some information in both knowledge bases with the SAME coordinates
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(_teapotST, _alice, _bob, _alice, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(teapotCC);
        _bobKB.createContextPoint(teapotCC);
        
        // However, alice now adds some information to it! The version should be increased.
        _aliceKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        assertEquals(2, ((SyncContextPoint)_aliceKB.getContextPoint(teapotCC)).getVersion());
        
        doCommunicationStuff();

        // Bob should now have an information attached to his teapot CP!
        assertEquals(1, _bobKB.getContextPoint(teapotCC).getNumberInformation());
        // It should have the correct content
        assertEquals("Teapots freakin rock!", _bobKB.getContextPoint(teapotCC).getInformation().next().getContentAsString());
        // And the CP should have version 2
        assertEquals(2, ((SyncContextPoint)_bobKB.getContextPoint(teapotCC)).getVersion());
    }

    @Test
    public void syncKP_CPIsWithGreaterEqualVersionInKB_CPInformationNotAssimilated() throws Exception {
        // Create some information in both knowledge bases with the SAME coordinates
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(_teapotST, _alice, _bob, _alice, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(teapotCC);
        _bobKB.createContextPoint(teapotCC);
        
        // Alice now adds some information to it! The version should be increased.
        _aliceKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        assertEquals(2, ((SyncContextPoint)_aliceKB.getContextPoint(teapotCC)).getVersion());    
        // But Bob too! And he adds more so his version is bigger
        _bobKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        _bobKB.getContextPoint(teapotCC).addInformation("And I also know more about them than Alice.");
        
        doCommunicationStuff();

        // Bob should NOT get the information from alice so the count stays at 2
        assertEquals(2, _bobKB.getContextPoint(teapotCC).getNumberInformation());
        // And his version stays at 3
        assertEquals(3, ((SyncContextPoint)_bobKB.getContextPoint(teapotCC)).getVersion());
    }

    @Test
    public void syncKP_create_allKBPeersAreInBucketList() throws SharkKBException {
        PeerSTSet bucketListPeers = _aliceSyncKP.getTimestamps().getPeers();
        assertNotNull(bucketListPeers.getSemanticTag("bobIdentifier"));
        bucketListPeers = _bobSyncKP.getTimestamps().getPeers();
        assertNotNull(bucketListPeers.getSemanticTag("aliceIdentifier"));
    }
    
    @Test
    public void syncKP_createPeerInKB_peerIsInBucketList() throws SharkKBException {
        // Add a new peer to Alice's knowledge base
        PeerSemanticTag clara = _aliceKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "mail@clara.de");
        
        PeerSTSet bucketListPeers = _aliceSyncKP.getTimestamps().getPeers();
        assertNotNull(bucketListPeers.getSemanticTag("bobIdentifier"));
        assertNotNull(bucketListPeers.getSemanticTag("ClaraIdentifier"));    
    }
    
    @Test(expected=AssertionError.class)
    public void syncKP_mergePeerInKB_peerIsInBucketList() throws SharkKBException {
        // Add a new peer to Alice's knowledge base
        PeerSemanticTag clara = InMemoSharkKB.createInMemoPeerSemanticTag("Clara", "ClaraIdentifier", "mail@clara.de");
        _aliceKB.getPeerSTSet().merge(clara);
        
        PeerSTSet bucketListPeers = _aliceSyncKP.getTimestamps().getPeers();
        assertNotNull(bucketListPeers.getSemanticTag("bobIdentifier"));
        assertNotNull(bucketListPeers.getSemanticTag("ClaraIdentifier"));    
    }
    
    
    /**
     * This test does it from scratch!
    */
    @Test
    public void syncKP_synchronizeTwoPreexistingKBs_syncKBsHaveSameCPs() throws Exception {
        SyncKB aliceKB = new SyncKB(new InMemoSharkKB());
        SyncKB bobKB = new SyncKB(new InMemoSharkKB());
        aliceKB.setOwner(_alice);
        bobKB.setOwner(_bob);
        
        // Create some knowledge. We ain't stupid right?
        // First, each peer should know about each other.
        aliceKB.getPeerSTSet().merge(_bob);
        bobKB.getPeerSTSet().merge(_alice);
        SharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SharkEngine bobEngine = new J2SEAndroidSharkEngine();
        SyncKP aliceSyncKP = new SyncKP(aliceEngine, aliceKB, 1);
        SyncKP bobSyncKP = new SyncKP(bobEngine, bobKB, 1);
        
        // Now Alice gets some knowledge about teapots
        ContextCoordinates aliceCC = aliceKB.createContextCoordinates(_teapotST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint aliceCP = aliceKB.createContextPoint(aliceCC);
        aliceCP.addInformation("I like teapots.");
        // And bob some about noodles 
        SemanticTag noodlesST = bobKB.createSemanticTag("Noodles", "NoodlesIdentifier");
        ContextCoordinates bobCC = bobKB.createContextCoordinates(noodlesST, _bob, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint bobCP = bobKB.createContextPoint(bobCC);
        bobCP.addInformation("Noodles are very good when you're hungry.");
        bobCP.addInformation("Unlike teapots. They suck.");
        // And about sauce
        SemanticTag sauceST = bobKB.createSemanticTag("Sauce", "SauceIdentifier");
        ContextCoordinates bobCC2 = bobKB.createContextCoordinates(sauceST, _bob, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint bobCP2 = bobKB.createContextPoint(bobCC2);
        bobCP2.addInformation("Sauces make noodles even better.");
        
        aliceEngine.startTCP(_alicePort);
        bobEngine.startTCP(_bobPort);
        aliceEngine.setConnectionTimeOut(connectionTimeOut);
        bobEngine.setConnectionTimeOut(connectionTimeOut);
        Thread aliceThread = new Thread(new Publish(bobEngine, _alice));
        aliceThread.start();
        Thread bobThread = new Thread(new Publish(aliceEngine, _bob));
        bobThread.start();
        
        Thread.sleep(SLEEP_TIMEOUT);
        
        aliceThread.interrupt();
        bobThread.interrupt();     
        
            // Make sure really all information is transferred
        assertNotNull(bobKB.getContextPoint(aliceCC));
        assertEquals(1, bobKB.getContextPoint(aliceCC).getNumberInformation());
        
        assertNotNull(aliceKB.getContextPoint(bobCC));
        assertEquals(2, aliceKB.getContextPoint(bobCC).getNumberInformation());
        
        assertNotNull(aliceKB.getContextPoint(bobCC2));
        assertEquals(1, aliceKB.getContextPoint(bobCC2).getNumberInformation());
    }
    
    @Test
    public void test_addCPToKB_CPIsInSyncQueue() throws SharkKBException {
        _aliceKB.setOwner(_alice);
        
        PeerSTSet myPSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        myPSTSet.merge(_alice);
        TimestampList mySyncQueue = new TimestampList(myPSTSet, _aliceKB);
        
        ContextCoordinates expected = _aliceKB.createContextCoordinates(_teapotST, _alice, _bob, _bob, null, null, SharkCS.DIRECTION_IN);
        _aliceKB.createContextPoint(expected);
        
//        assertEquals(expected, mySyncQueue.popCoordinatesFromBucket(_alice).get(0));
        // compliert nicht bei mir - thsc
    }
    
    @Test(expected=AssertionError.class)
    public void test_removePeerFromKB_PeerRemovedFromTimestampList() throws SharkKBException {
        // First bob should be in alice's sync bucket
        PeerSemanticTag peerInSyncBucket = _aliceSyncKP.getTimestamps().getPeers().peerTags().nextElement();
        assertEquals(_bob, peerInSyncBucket);
        
        // Now remove bob from alice's peers
        _aliceKB.removeSemanticTag(_bob.getSI());
//        _aliceKB.getPeerSTSet().removeSemanticTag(_bob);
        
        assertEquals(0, _aliceSyncKP.getTimestamps().getPeers().size());
    }   
    
    @Test
    public void test_changeCPInformationInKB_CPIsSynced() throws Exception {
        // Create context point
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(_teapotST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint teapotCP = _aliceKB.createContextPoint(teapotCC);
        
        // Now change it's information
        teapotCP.addInformation("I like teapots.");
        
        doCommunicationStuff();
        
        // Now Bob should have alice's cp
        assertEquals(teapotCP, _bobKB.getContextPoint(teapotCC));
        
    }
    
    @Test
    public void test_syncAllKnowledge_allCPsInSynced() throws Exception {
        // Create some knowledge
        ContextCoordinates teapotCC = _aliceKB.createContextCoordinates(_teapotST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(teapotCC);
        ContextCoordinates noodlesCC = _aliceKB.createContextCoordinates(_noodlesST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(noodlesCC);
        // Reset the sync bucket so it's like we just freshly created the syncKP
        _aliceSyncKP.resetPeerTimestamps();
        
        // The sync bucket for bob should be empty now
//        assertEquals(0, _aliceSyncKP.getSyncBucketList().popCoordinatesFromBucket(_bob).size());
                // compliert nicht bei mir - thsc

        // Push the button!
        _aliceSyncKP.syncAllKnowledge();
        
        doCommunicationStuff();
        
        assertNotNull(_bobKB.getContextPoint(teapotCC));
        assertNotNull(_bobKB.getContextPoint(noodlesCC));
    }
    
    @Test
    public void test_syncAllKnowledgeWithPeer_CPsOnlySyncedWithPeer() throws Exception {
        // We need a Clara for this
        SyncKB claraKB = new SyncKB(new InMemoSharkKB());
        int claraPort = getPort();
        PeerSemanticTag clara = claraKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "tcp://localhost:"+claraPort);
        claraKB.setOwner(clara);
        SharkEngine claraEngine = new J2SEAndroidSharkEngine();
        SyncKP claraSyncKP = new SyncKP(claraEngine, claraKB, 1);
        // Alice and Clara need to know about each other
        _aliceKB.getPeerSTSet().merge(clara);
        claraKB.getPeerSTSet().merge(_alice);
        
        // Create some knowledge
        ContextCoordinates teapotCC = _aliceKB.createContextCoordinates(_teapotST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(teapotCC);
        ContextCoordinates noodlesCC = _aliceKB.createContextCoordinates(_noodlesST, _alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(noodlesCC);
        // Reset the sync bucket so it's like we just freshly created the syncKP
        _aliceSyncKP.resetPeerTimestamps();
        
        // The sync bucket for bob should be empty now
//        assertEquals(0, _aliceSyncKP.getSyncBucketList().popCoordinatesFromBucket(_bob).size());
                // compliert nicht bei mir - thsc

        
        // Push the button!
        _aliceSyncKP.syncAllKnowledge(_bob);
        
        doCommunicationStuff();
        
        // Only bob should know about the CCs now, not clara! We don't like clara.
        assertNotNull(_bobKB.getContextPoint(teapotCC));
        assertNotNull(_bobKB.getContextPoint(noodlesCC));
        assertNull(claraKB.getContextPoint(teapotCC));
        assertNull(claraKB.getContextPoint(noodlesCC));
        
    }
    
//    @Test
//    public void test_snowballingInactive_CPNotForwarded() throws Exception {
//         // We need a Clara for this
//        SyncKB claraKB = new SyncKB(new InMemoSharkKB());
//        int claraPort = getPort();
//        PeerSemanticTag clara = claraKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "tcp://localhost:"+claraPort);
//        claraKB.setOwner(clara);
//        SharkEngine claraEngine = new J2SEAndroidSharkEngine();
//        SyncKP claraSyncKP = new SyncKP(claraEngine, claraKB);
//        // Let Bob know about Clara
//        claraKB.getPeerSTSet().merge(_bob);
//        _bobKB.getPeerSTSet().merge(clara);
//        
//        // Alice has Knowledge 
//        ContextCoordinates teapotAliceCC = _aliceKB.createContextCoordinates(_teapotST, _alice, _alice, _bob, null, null, SharkCS.DIRECTION_INOUT);
//        ContextPoint teapotAliceCP = _aliceKB.createContextPoint(teapotAliceCC);
//        teapotAliceCP.addInformation("Teapots yay");
//        
//        doCommunicationStuff();
//        _bobEngine.publishAllKP(_alice);
//        
//        Thread.sleep(600);
//        
//        assertEquals(teapotAliceCP, _bobKB.getContextPoint(teapotAliceCC));
//        assertNull(claraKB.getContextPoint(teapotAliceCC));
//    }
//    
//    @Test
//    public void test_snowballingActive_CPForwarded() throws Exception {
//         // We need a Clara for this
//        SyncKB claraKB = new SyncKB(new InMemoSharkKB());
//        int claraPort = getPort();
//        PeerSemanticTag clara = claraKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "tcp://localhost:"+claraPort);
//        claraKB.setOwner(clara);
//        SharkEngine claraEngine = new J2SEAndroidSharkEngine();
//        SyncKP claraSyncKP = new SyncKP(claraEngine, claraKB);
//        // Let Bob know about Clara
//        claraKB.getPeerSTSet().merge(_bob);
//        _bobKB.getPeerSTSet().merge(clara);
//        
//        // Alice has Knowledge 
//        ContextCoordinates teapotAliceCC = _aliceKB.createContextCoordinates(_teapotST, _alice, _alice, _bob, null, null, SharkCS.DIRECTION_INOUT);
//        ContextPoint teapotAliceCP = _aliceKB.createContextPoint(teapotAliceCC);
//        teapotAliceCP.addInformation("Teapots yay");
//        
//        // Bob forwards
//        _bobSyncKP.setSnowballing(true);
////        _bobSyncKP.getSyncBucketList().appendPeer(clara);
//                // compliert nicht bei mir - thsc
//
//        doCommunicationStuff();
//        claraEngine.publishAllKP(_bob);
//        
//        Thread.sleep(600);
//        
//        assertEquals(teapotAliceCP, _bobKB.getContextPoint(teapotAliceCC));
//        assertEquals(teapotAliceCP, claraKB.getContextPoint(teapotAliceCC));
//        
//    }

    @Test
    public void test_clearInformationFromContextPoint_informationRemovalSynced() throws Exception{  
        // Create some information in both knowledge bases with the SAME coordinates
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(_teapotST, _alice, _bob, _alice, null, null, SharkCS.DIRECTION_INOUT);
        _aliceKB.createContextPoint(teapotCC);
        _bobKB.createContextPoint(teapotCC);
        
        // However, alice now adds some information to it! The version should be increased.
        Information aliceInfo = _aliceKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        assertEquals(2, ((SyncContextPoint)_aliceKB.getContextPoint(teapotCC)).getVersion());    
        
        doCommunicationStuff();

        // Bob should now have an information attached to his teapot CP!
        assertEquals(1, _bobKB.getContextPoint(teapotCC).getNumberInformation());
        // It should have the correct content
        assertEquals("Teapots freakin rock!", _bobKB.getContextPoint(teapotCC).getInformation().next().getContentAsString());
        // And the CP should have version 2
        assertEquals(2, ((SyncContextPoint)_bobKB.getContextPoint(teapotCC)).getVersion());
             
        // Alice deletes the information
        _aliceKB.getContextPoint(teapotCC).removeInformation(aliceInfo);
        assertEquals(3, ((SyncContextPoint)_aliceKB.getContextPoint(teapotCC)).getVersion());    
        
        doCommunicationStuff();

        // Bob should now have no information attached to his teapot CP!
        assertEquals(0, _bobKB.getContextPoint(teapotCC).getNumberInformation());
        // And the CP should have version 3
        assertEquals(3, ((SyncContextPoint)_bobKB.getContextPoint(teapotCC)).getVersion());
        
        
    }
    
    @Test
    public void test_createSyncKP_ownerNotInSyncBuckets() throws SharkKBException{
        assertFalse(SharkCSAlgebra.isIn(_aliceSyncKP.getTimestamps().getPeers(), _alice));
    }
    
}
