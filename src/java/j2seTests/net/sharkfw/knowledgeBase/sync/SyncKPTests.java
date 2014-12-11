package net.sharkfw.knowledgeBase.sync;


import java.io.IOException;
import net.sharkfw.kep.SharkProtocolNotSupportedException;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
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
            L.setLogLevel(L.LOGLEVEL_ALL);
    }

    @Before
    public void setUp() throws SharkKBException{
        // Set up KBs
        _aliceKB = new SyncKB(new InMemoSharkKB());
        _bobKB = new SyncKB(new InMemoSharkKB());
        // Set up peers with new ports
        _alicePort = getPort();
        _alice = _aliceKB.createPeerSemanticTag("Alice", "aliceIdentifier", "tcp://localhost:"+_alicePort);
        _bobPort = getPort();
        _bob = _bobKB.createPeerSemanticTag("Bob", "bobIdentifier", "tcp://localhost:"+_bobPort);
        // Let each know about each other
        _aliceKB.getPeerSTSet().merge(_bob);
        _bobKB.getPeerSTSet().merge(_alice);
        // Set up engines
        _aliceEngine = new J2SEAndroidSharkEngine();
        _bobEngine = new J2SEAndroidSharkEngine();
        // Set owner
        _aliceKB.setOwner(_alice);
        _bobKB.setOwner(_bob);
        // Kps
        _aliceSyncKP = new SyncKP(_aliceEngine, _aliceKB);
        _bobSyncKP = new SyncKP(_bobEngine, _bobKB);
    }

    @After
    public void tearDown() throws SharkProtocolNotSupportedException, InterruptedException {
        _aliceEngine.stopTCP();
        _bobEngine.stopTCP();
        _aliceKB = null;
        _bobKB = null;
    }

    @Test
    public void syncKP_meetsNonSyncKP_noCommunication() throws InterruptedException, SharkSecurityException, IOException, SharkKBException, SharkProtocolNotSupportedException {
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
        
        // Start engines (and KPs)
        _aliceEngine.startTCP(_alicePort);
        _bobEngine.startTCP(_bobPort);
        _aliceEngine.publishAllKP(_bob);
        _bobEngine.publishAllKP();

        // wait until communication happened
        Thread.sleep(1000);

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
        
        // Start engines (and KPs)
        _aliceEngine.startTCP(_alicePort);
        _bobEngine.startTCP(_bobPort);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.publishAllKP(_alice);
        _aliceEngine.publishAllKP(_bob);

        // wait until communication happened
        Thread.sleep(1000);

        // Bob should now know about alice's CP and the other way round
        ContextPoint retrievedCPAlice = _aliceKB.getContextPoint(noodlesBobCC);
        assertNotNull(retrievedCPAlice);
        assertEquals(1, retrievedCPAlice.getNumberInformation());
        
        ContextPoint retrievedCPBob = _bobKB.getContextPoint(teapotAliceCC);
        assertNotNull(retrievedCPBob);
        assertEquals(1, retrievedCPBob.getNumberInformation());
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
        
        // Start engines (and KPs)
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.startTCP(_bobPort);
        _aliceEngine.startTCP(_alicePort);
        _bobEngine.publishAllKP(_alice);

        // wait until communication happened
        Thread.sleep(1000);

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
        
        // Start engines (and KPs)
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.startTCP(_bobPort);
        _aliceEngine.startTCP(_alicePort);
        _bobEngine.publishAllKP(_alice);

        // wait until communication happened
        Thread.sleep(1000);

        // Bob should NOT get the information from alice so the count stays at 2
        assertEquals(2, _bobKB.getContextPoint(teapotCC).getNumberInformation());
        // And his version stays at 3
        assertEquals(3, ((SyncContextPoint)_bobKB.getContextPoint(teapotCC)).getVersion());
    }

    @Test
    public void syncKP_create_allKBPeersAreInBucketList() throws SharkKBException {
        // Create a new syncKB
        SyncKB aliceKB = new SyncKB(new InMemoSharkKB());
        // Add two peers to it
        PeerSemanticTag bob = aliceKB.createPeerSemanticTag("Bob", "BobIdentifier", "mail@bob.de");
        PeerSemanticTag clara = aliceKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "mail@clara.de");
        
        // Now use a sync KP
        SharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SyncKP aliceKP = new SyncKP(aliceEngine, aliceKB);
        
        PeerSTSet bucketListPeers = aliceKP.getSyncBucketList().getPeers();
        assertNotNull(bucketListPeers.getSemanticTag("BobIdentifier"));
        assertNotNull(bucketListPeers.getSemanticTag("ClaraIdentifier"));
    }
    
    @Test
    public void syncKP_addPeerToKB_peerIsInBucketList() throws SharkKBException {
        // Add a new peer to Alice's knowledge base
        PeerSemanticTag clara = _aliceKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "mail@clara.de");
        
        PeerSTSet bucketListPeers = _aliceSyncKP.getSyncBucketList().getPeers();
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
        SyncKP aliceSyncKP = new SyncKP(aliceEngine, aliceKB);
        SyncKP bobSyncKP = new SyncKP(bobEngine, bobKB);
        
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
        
        // Alright lets get started.
        aliceEngine.setConnectionTimeOut(connectionTimeOut);
        bobEngine.setConnectionTimeOut(connectionTimeOut);
        
        // First alice listens and bob publishes
        aliceEngine.startTCP(_alicePort);
        bobEngine.startTCP(_bobPort);
        aliceEngine.publishAllKP(_bob);
        bobEngine.publishAllKP(_alice);
        
        Thread.sleep(1000);
        
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
        SyncBucketList mySyncQueue = new SyncBucketList(myPSTSet);
        
        _aliceSyncKP.setSyncQueue(mySyncQueue);
        
        ContextCoordinates expected = _aliceKB.createContextCoordinates(_teapotST, _alice, _bob, _bob, null, null, SharkCS.DIRECTION_IN);
        _aliceKB.createContextPoint(expected);
        
        assertEquals(expected, mySyncQueue.popFromBucket(_alice).get(0));
    }
    
    @Test
    public void test_removePeerFromKB_PeerRemovedFromSyncBuckets() {
        
    }
    
    @Test
    public void test_changeCPInformationInKB_CPIsSynced() {
        
    }
    
    @Test
    public void test_syncAllKnowledge_allCPsInSynced() {
        
    }
    
    @Test
    public void test_snowballingInactive_CPNotForwarded() {
        
    }
    
    @Test
    public void test_snowballingActive_CPForwarded() {
        
    }

}
