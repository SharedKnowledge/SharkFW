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

    private long connectionTimeOut = 2000;
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
        // Kps
        _aliceSyncKP = new SyncKP(_aliceEngine, _aliceSyncKB);
        _bobSyncKP = new SyncKP(_bobEngine, _bobSyncKB);
    }

    @After
    public void tearDown() throws SharkProtocolNotSupportedException {
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
        Thread.sleep(1000);

        // Neither KB should now know anything about the other contextPoint
        Assert.assertNull(_aliceSyncKB.getContextPoint(teapotBobCC));
        Assert.assertNull(bobKB.getContextPoint(teapotAliceCC));
    }

    @Test
    public void syncKP_CPIsNotInKB_CPAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotAliceCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, alice, bob, null, null, SharkCS.DIRECTION_INOUT);
        ContextCoordinates teapotBobCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, bob, alice, bob, null, null, SharkCS.DIRECTION_INOUT);

        // Create CPs in bobs and alices KB - they are not the same, so they should be exchanged 
        _aliceSyncKB.createContextPoint(teapotAliceCC);
        _bobSyncKB.createContextPoint(teapotBobCC);

        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _bobEngine.startTCP(5556);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _aliceEngine.publishAllKP(bob);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1000);

        // Each KB should now know anything about the other contextPoint
        Assert.assertEquals(_bobSyncKB.getContextPoint(teapotBobCC), _aliceSyncKB.getContextPoint(teapotBobCC));
        Assert.assertEquals(_bobSyncKB.getContextPoint(teapotAliceCC), _aliceSyncKB.getContextPoint(teapotAliceCC));
    }

    @Test
    public void syncKP_CPIsWithLowerVersionInKB_CPInformationAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, bob, alice, bob, null, null, SharkCS.DIRECTION_INOUT);

        // Create CPs in bobs and alices KB - they ARE the same
        _aliceSyncKB.createContextPoint(teapotCC);
        _bobSyncKB.createContextPoint(teapotCC);
        // However, alice now adds some information to it! The version should be increased and 
        // the information updated in Bobs KB
        _aliceSyncKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
            
        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _bobEngine.startTCP(5556);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _aliceEngine.publishAllKP(bob);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1000);

        // Bob should now have an information attached to his teapot CP!
        Assert.assertEquals(_bobSyncKB.getContextPoint(teapotCC).getNumberInformation(), 1);
        Assert.assertEquals(_bobSyncKB.getContextPoint(teapotCC).getInformation().next().getContentAsString(), "Teapots freakin rock!");
    }

    @Test
    public void syncKP_CPIsWithGreaterEqualVersionInKB_CPInformationNotAssimilated() throws Exception {
        // Create some information in context space
        ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, bob, alice, bob, null, null, SharkCS.DIRECTION_INOUT);

        // Create CPs in bobs and alices KB - they ARE the same
        _aliceSyncKB.createContextPoint(teapotCC);
        _bobSyncKB.createContextPoint(teapotCC);
        // Both add information to it. Alice just adds MORE information and has a higher version!
        _bobSyncKB.getContextPoint(teapotCC).addInformation("Bob does not like teapots.");
        _aliceSyncKB.getContextPoint(teapotCC).addInformation("Teapots freakin rock!");
        _aliceSyncKB.getContextPoint(teapotCC).addInformation("Tea is very healthy.");

            
        // Start engines (and KPs)
        _aliceEngine.startTCP(5555);
        _bobEngine.startTCP(5556);
        _aliceEngine.setConnectionTimeOut(connectionTimeOut);
        _bobEngine.setConnectionTimeOut(connectionTimeOut);
        _aliceEngine.publishAllKP(bob);
        _bobEngine.publishAllKP(alice);

        // wait until communication happened
        Thread.sleep(1000);

        // Alice should NOT get the information from bob so the count stays at 2
        assert(_aliceSyncKB.getContextPoint(teapotCC).getNumberInformation() == 2);
    }


    @Test
    public void syncKP_synchronizeTwoKPs_syncKBsHaveSameCPs()throws Exception { 
        /*
         * Filling syncer with information: noodles
         */
        J2SEAndroidSharkEngine syncerEngine = new J2SEAndroidSharkEngine();

        Taxonomy topicsTX = _aliceSyncKB.getTopicsAsTaxonomy();
        TXSemanticTag noodle = topicsTX.createTXSemanticTag("Noodle", "http://noodle.org");
        TXSemanticTag spaghetti = topicsTX.createTXSemanticTag("Spaghetti", "http://spaghetti.com");
        TXSemanticTag farfalle = topicsTX.createTXSemanticTag("Farfalle", "http://farfalle.de");

        spaghetti.move(noodle);
        farfalle.move(noodle);

        PeerSemanticTag syncerPeer = _aliceSyncKB.createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5555");
        _aliceSyncKB.setOwner(syncerPeer);

        PeerSemanticTag bobPeer = _aliceSyncKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null); // I don't know a single address

        // Next create a contextpoint w/ infos about spaghetti!
        ContextCoordinates spaghettiCoords = _aliceSyncKB.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint spaghettiCp = _aliceSyncKB.createContextPoint(spaghettiCoords);
        spaghettiCp.addInformation("Spaghetti are the best noodles for kids!");

        ContextCoordinates noodleCoords = _aliceSyncKB.createContextCoordinates(noodle, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint noodleCp = _aliceSyncKB.createContextPoint(noodleCoords);
        noodleCp.addInformation("Noodles are yummy!");

        // Now create an interest to speak about spaghetti!
        SharkCS anchor = _aliceSyncKB.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);

        //Create blacklist
        Iterator<PeerSemanticTag> blacklist = null;

        // Create a standard knowledgeport (and interest) from this information
        SyncKP syncerKP = new SyncKP(syncerEngine, _aliceSyncKB);

        syncerEngine.startTCP(5558);
        syncerEngine.setConnectionTimeOut(connectionTimeOut);

        // =========================================
        // Creation and config of _synced

        J2SEAndroidSharkEngine syncedEngine = new J2SEAndroidSharkEngine();

        SemanticTag syncedNoodle = _bobSyncKB.createSemanticTag("Noodle", "http://noodle.org");

        PeerSemanticTag bobLocalPeer = _bobSyncKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null);
        _bobSyncKB.setOwner(bobPeer);

        SharkCS bobAs = _bobSyncKB.createContextCoordinates(syncedNoodle, null, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
        // Not only will bob use the same fp as alice, it will also use it as OTP like alice
        SyncKP syncedKP = new SyncKP(syncedEngine, _bobSyncKB);

        syncedEngine.setConnectionTimeOut(connectionTimeOut);


        // ===========================================
        // Make them talk
//        syncedEngine.publishAllKP(syncedKP);

        Thread.sleep(1000);

        // ============================================
        // Check the results:
        // Bob must now know the topic spaghetti
        // Bob must know that spaghetti is a sub-concept of noodle
        // Bob must posess the contextpoint for spaghetti and the information
        // TODO: Check for "Noodle" information

        Taxonomy bobTopics = _bobSyncKB.getTopicsAsTaxonomy();
        TXSemanticTag bobSpaghetti = bobTopics.getSemanticTag(new String[]{"http://spaghetti.com"});

        PeerSemanticTag bobsAlice = _bobSyncKB.getPeerSemanticTag(syncerPeer.getSI());

        Assert.assertNotNull(bobSpaghetti);
        Assert.assertNotNull(bobsAlice);

        // Check to see if the associations have been learned properly
        TXSemanticTag spaghettiSuperTag = bobSpaghetti.getSuperTag();

        Assert.assertTrue(SharkCSAlgebra.identical(spaghettiSuperTag, syncedNoodle));

        // Create contextcoordinates to extract the received contextpoint from the knowledgebase
        ContextCoordinates extractCoords = _bobSyncKB.createContextCoordinates(bobSpaghetti, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
        ContextPoint bobSpaghettiCp = _bobSyncKB.getContextPoint(extractCoords);

        // The contextpoint must be present
        Assert.assertNotNull(bobSpaghettiCp);

        // The contextpoint must contain the information from alice
        Enumeration<Information> bobSpaghettiInfoEnum = bobSpaghettiCp.enumInformation();
        Assert.assertNotNull(bobSpaghettiInfoEnum);
        while(bobSpaghettiInfoEnum.hasMoreElements())
            Assert.assertNotNull(bobSpaghettiInfoEnum.nextElement());
        Information bobShovelInfo = (Information) bobSpaghettiInfoEnum.nextElement();
        byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
        String bobShovelString = new String(bobShovelContent);
        Assert.assertEquals(bobShovelString, "Spaghetti are the best noodles for kids!");
    }
    
    @Test
    public void test_addCPToKB_CPIsInSyncQueue() throws SharkKBException {
        _aliceSyncKB.setOwner(alice);
        
        PeerSTSet myPSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        myPSTSet.merge(alice);
        SyncQueue mySyncQueue = new SyncQueue(myPSTSet);
        
        _aliceSyncKP.setSyncQueue(mySyncQueue);
        
        ContextCoordinates expected = _aliceSyncKB.createContextCoordinates(teapotST, alice, bob, bob, null, null, SharkCS.DIRECTION_IN);
        _aliceSyncKB.createContextPoint(expected);
        
        Assert.assertEquals(expected, mySyncQueue.pop(alice).get(0));
    }

}
