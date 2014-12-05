package net.sharkfw.knowledgeBase.sync;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
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
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncKPTests {

    private final long connectionTimeOut = 2000;
    private SyncKB _aliceSyncKB = null;
    private SyncKB _bobSyncKB = null;
    private SyncKP _aliceSyncKP, _bobSyncKP;
    private SharkEngine _aliceEngine, _bobEngine;

    SemanticTag teapotST =  InMemoSharkKB.createInMemoSemanticTag("teapot", "www.teapot.de");
    PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice", "tcp://localhost:5555");
    PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob", "tcp://localhost:5556");
    
    @BeforeClass
    public static void setUpClass(){
            L.setLogLevel(L.LOGLEVEL_ALL);
    }

    @Before
    public void setUp() throws SharkKBException{
        // Set up KBs
        SharkKB syncerInternal = new InMemoSharkKB();
        _aliceSyncKB = new SyncKB(syncerInternal);
        SharkKB syncedInternal = new InMemoSharkKB();
        _bobSyncKB = new SyncKB(syncedInternal);
        // Set up engines
        _aliceEngine = new J2SEAndroidSharkEngine();
        _bobEngine = new J2SEAndroidSharkEngine();
        // Set owner
        _aliceSyncKB.setOwner(alice);
        _bobSyncKB.setOwner(bob);
        // Kps
        _aliceSyncKP = new SyncKP(_aliceEngine, _aliceSyncKB);
        _bobSyncKP = new SyncKP(_bobEngine, _bobSyncKB);
    }

    @After
    public void tearDown() throws SharkProtocolNotSupportedException, InterruptedException {
        _aliceEngine.stopTCP();
        _bobEngine.stopTCP();
        _aliceSyncKB = null;
        _bobSyncKB = null;

    }

    @Test
    public void syncKP_meetsNonSyncKP_noCommunication() throws InterruptedException, SharkSecurityException, IOException, SharkKBException, SharkProtocolNotSupportedException {
        // Create some information in context space
        ContextCoordinates teapotAliceCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, alice, bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates teapotBobCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, bob, alice, bob, null, null, SharkCS.DIRECTION_INOUT);

        // Alice will be a sync KP, bob a standard KP
        SharkKB bobKB = new InMemoSharkKB();
        // Bob is interested in anything
        Interest bobAnyInterest = bobKB.createInterest(bobKB.createContextCoordinates(null, null, bob, null, null, null, SharkCS.DIRECTION_INOUT));
        KnowledgePort bobKP = new StandardKP(_bobEngine, bobAnyInterest, bobKB);

        // Create CPs in bobs and alices KB - they are not the same, so they should be exchanged (if both were sync KPs)
        _aliceSyncKB.createContextPoint(teapotAliceCC);
        bobKB.createContextPoint(teapotBobCC);

        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _bobEngine.startTCP(5556);
        Assert.assertNotNull(_aliceSyncKP);
        _aliceEngine.publishAllKP(bob);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1500);

        // Neither KB should now know anything about the other contextPoint
        Assert.assertNull(_aliceSyncKB.getContextPoint(teapotBobCC));
        Assert.assertNull(bobKB.getContextPoint(teapotAliceCC));
    }

    @Test
    public void syncKP_CPIsNotInKB_CPAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotAliceCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, alice, bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates teapotBobCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, bob, alice, bob, null, null, SharkCS.DIRECTION_INOUT);
        
        SyncBucketList aliceQueue = new SyncBucketList();
        aliceQueue.appendPeer(bob);
        _aliceSyncKP.setSyncQueue(aliceQueue);
        
        // Create a CP in alices KB
        _aliceSyncKB.createContextPoint(teapotAliceCC);

        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1500);
//        Thread.sleep(Integer.MAX_VALUE);

        // Bob should now know about alices CP
        Assert.assertTrue(_bobSyncKB.getContextPoint(teapotAliceCC).equals(_aliceSyncKB.getContextPoint(teapotAliceCC)));
    }

    @Test
    public void syncKP_CPIsWithLowerVersionInKB_CPInformationAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
      
        SyncBucketList aliceQueue = new SyncBucketList();
        aliceQueue.appendPeer(bob);
        _aliceSyncKP.setSyncQueue(aliceQueue);

        // Create CPs in bobs and alices KB - they ARE the same
        _aliceSyncKB.createContextPoint(teapotCC);
        _bobSyncKB.createContextPoint(teapotCC);
//         However, alice now adds some information to it! The version should be increased and 
        _aliceSyncKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        Assert.assertEquals(2, ((SyncContextPoint)_aliceSyncKB.getContextPoint(teapotCC)).getVersion());    
        
        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1500);

        // Bob should now have an information attached to his teapot CP!
        Assert.assertEquals(1, _bobSyncKB.getContextPoint(teapotCC).getNumberInformation());
        // It should have the correct content
        Assert.assertEquals("Teapots freakin rock!", _bobSyncKB.getContextPoint(teapotCC).getInformation().next().getContentAsString());
        // And the CP should have version 2
        Assert.assertEquals(2, ((SyncContextPoint)_bobSyncKB.getContextPoint(teapotCC)).getVersion());
    }

    @Test
    public void syncKP_CPIsWithGreaterEqualVersionInKB_CPInformationNotAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, null, null, null, null, SharkCS.DIRECTION_INOUT);

        SyncBucketList aliceQueue = new SyncBucketList();
        aliceQueue.appendPeer(bob);
        _aliceSyncKP.setSyncQueue(aliceQueue);
        
        // Create CPs in bobs and alices KB - they ARE the same
        _aliceSyncKB.createContextPoint(teapotCC);
        _bobSyncKB.createContextPoint(teapotCC);
        // Both add information to it. Bob just adds MORE information and has a higher version!
        _bobSyncKB.getContextPoint(teapotCC).addInformation("Bob does not like teapots.");
        _bobSyncKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        _aliceSyncKB.getContextPoint(teapotCC).addInformation("Tea is very healthy.");
        
        Assert.assertEquals(2, ((SyncContextPoint)_aliceSyncKB.getContextPoint(teapotCC)).getVersion());
        Assert.assertEquals(3, ((SyncContextPoint)_bobSyncKB.getContextPoint(teapotCC)).getVersion());
            
        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1500);

        // Bob should NOT get the information from alice so the count stays at 2
        Assert.assertEquals(2, _bobSyncKB.getContextPoint(teapotCC).getNumberInformation());
        // And his version stays at 3
        Assert.assertEquals(3, ((SyncContextPoint)_bobSyncKB.getContextPoint(teapotCC)).getVersion());
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
        Assert.assertNotNull(bucketListPeers.getSemanticTag("BobIdentifier"));
        Assert.assertNotNull(bucketListPeers.getSemanticTag("ClaraIdentifier"));
    }
    
    @Test
    public void syncKP_addPeerToKB_peerIsInBucketList() throws SharkKBException {
        // Create a new syncKB
        SyncKB aliceKB = new SyncKB(new InMemoSharkKB());
        
        // Now use a sync KP
        SharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SyncKP aliceKP = new SyncKP(aliceEngine, aliceKB);
        
//        aliceKB.createContextPoint(aliceKB.createContextCoordinates(teapotST, alice, bob, alice, null, null, SharkCS.DIRECTION_IN));
        
        // Add two peers to it
        PeerSemanticTag bob = aliceKB.createPeerSemanticTag("Bob", "BobIdentifier", "mail@bob.de");
        PeerSemanticTag clara = aliceKB.createPeerSemanticTag("Clara", "ClaraIdentifier", "mail@clara.de");
        
        PeerSTSet bucketListPeers = aliceKP.getSyncBucketList().getPeers();
        Assert.assertNotNull(bucketListPeers.getSemanticTag("BobIdentifier"));
        Assert.assertNotNull(bucketListPeers.getSemanticTag("ClaraIdentifier"));
        
    }
    
    
    /**
     * This test does it from scratch!
    */
    @Test
    public void syncKP_synchronizeTwoPreexistingKBs_syncKBsHaveSameCPs() throws Exception {
        SyncKB aliceKB = new SyncKB(new InMemoSharkKB());
        SyncKB bobKB = new SyncKB(new InMemoSharkKB());
        aliceKB.setOwner(alice);
        bobKB.setOwner(bob);
        
        // Create some knowledge. We ain't stupid right?
        // First, each peer should know about each other.
        aliceKB.getPeerSTSet().merge(bob);
        bobKB.getPeerSTSet().merge(alice);
        SharkEngine aliceEngine = new J2SEAndroidSharkEngine();
        SharkEngine bobEngine = new J2SEAndroidSharkEngine();
        SyncKP aliceSyncKP = new SyncKP(aliceEngine, aliceKB);
        SyncKP bobSyncKP = new SyncKP(bobEngine, bobKB);
        
        // Now Alice gets some knowledge about teapots
        ContextCoordinates aliceCC = aliceKB.createContextCoordinates(teapotST, alice, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint aliceCP = aliceKB.createContextPoint(aliceCC);
        aliceCP.addInformation("I like teapots.");
        // And bob some about noodles 
        SemanticTag noodlesST = bobKB.createSemanticTag("Noodles", "NoodlesIdentifier");
        ContextCoordinates bobCC = bobKB.createContextCoordinates(noodlesST, bob, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint bobCP = bobKB.createContextPoint(bobCC);
        bobCP.addInformation("Noodles are very good when you're hungry.");
        bobCP.addInformation("Unlike teapots. They suck.");
        // And about sauce
        SemanticTag sauceST = bobKB.createSemanticTag("Sauce", "SauceIdentifier");
        ContextCoordinates bobCC2 = bobKB.createContextCoordinates(sauceST, bob, null, null, null, null, SharkCS.DIRECTION_INOUT);
        ContextPoint bobCP2 = bobKB.createContextPoint(bobCC2);
        bobCP2.addInformation("Sauces make noodles even better.");
        
        // Alright lets get started.
        aliceEngine.setConnectionTimeOut(connectionTimeOut);
        bobEngine.setConnectionTimeOut(connectionTimeOut);
        
        // First alice listens and bob publishes
        aliceEngine.startTCP(5555);
        bobEngine.startTCP(5556);
        aliceEngine.publishAllKP(bob);
        bobEngine.publishAllKP(alice);
        
        Thread.sleep(1500);
        
        // Make sure really all information is transferred
        Assert.assertNotNull(bobKB.getContextPoint(aliceCC));
        Assert.assertEquals(1, bobKB.getContextPoint(aliceCC).getNumberInformation());
        
        Assert.assertNotNull(aliceKB.getContextPoint(bobCC));
        Assert.assertEquals(2, aliceKB.getContextPoint(bobCC).getNumberInformation());
        
        Assert.assertNotNull(aliceKB.getContextPoint(bobCC2));
        Assert.assertEquals(1, aliceKB.getContextPoint(bobCC2).getNumberInformation());
    }
    
    @Test
    public void test_addCPToKB_CPIsInSyncQueue() throws SharkKBException {
        _aliceSyncKB.setOwner(alice);
        
        PeerSTSet myPSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        myPSTSet.merge(alice);
        SyncBucketList mySyncQueue = new SyncBucketList(myPSTSet);
        
        _aliceSyncKP.setSyncQueue(mySyncQueue);
        
        ContextCoordinates expected = _aliceSyncKB.createContextCoordinates(teapotST, alice, bob, bob, null, null, SharkCS.DIRECTION_IN);
        _aliceSyncKB.createContextPoint(expected);
        
        Assert.assertEquals(expected, mySyncQueue.popFromBucket(alice).get(0));
    }
    
    @Test
    public void test_removePeerFromKB_PeerRemovedFromSyncBuckets() {
        
    }
    
    @Test
    public void test_changeCPInformationInKB_CPIsSynced() {
        
    }

}
