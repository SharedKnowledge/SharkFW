/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s0539710
 */
public class SyncQueueTest {
    
    protected SharkKB _kb;
    protected ContextCoordinates _cc;
    protected SemanticTag _teapotST;
    protected PeerSemanticTag _alicePST, _bobPST, _claraPST;
    
    public SyncQueueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SharkKBException {
        _kb = new InMemoSharkKB();
        
        _teapotST = _kb.createSemanticTag("teapot", "www.teapot.net");
        _alicePST = _kb.createPeerSemanticTag("Alice", "www.alice.net", "mail@alice.net");
        _bobPST = _kb.createPeerSemanticTag("Bob", "www.bob.net", "mail@bob.net");
        _claraPST = _kb.createPeerSemanticTag("Clara", "www.clara.net", "mail@clara.net");
        _cc = _kb.createContextCoordinates(_teapotST, _alicePST, _alicePST, _bobPST, null, null, SharkCS.DIRECTION_INOUT);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_createSyncQueue_allPeersSaved() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncQueue mySyncQueue = new SyncQueue(peers);
        
        assertTrue(SharkCSAlgebra.identical(peers, mySyncQueue.getPeers()));
    }
    
    /**
     * Test of push method, of class SyncQueue.
     */
    @Test
    public void test_pushContextCoordinates_savedForAllPeers() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncQueue mySyncQueue = new SyncQueue(peers);
        
        // Add a cc
        mySyncQueue.push(_cc);
        
        // All peers should now have this CC in their "to-sync" list
        SharkCSAlgebra.identical(_cc, mySyncQueue.pop(_bobPST).get(0));
        SharkCSAlgebra.identical(_cc, mySyncQueue.pop(_claraPST).get(0));
    }

    @Test
    public void test_pushContextCoordinates_noDuplicateSaved() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncQueue mySyncQueue = new SyncQueue(peers);
        
        // Add a cc
        mySyncQueue.push(_cc);
        // Add it again
        mySyncQueue.push(_cc);
        
        // All peers should now have this CC in their "to-sync" list
        SharkCSAlgebra.identical(_cc, mySyncQueue.pop(_bobPST).get(0));
        SharkCSAlgebra.identical(_cc, mySyncQueue.pop(_claraPST).get(0));
    }
    
    /**
     * Test of addPeer method, of class SyncQueue.
     */
    @Test
    public void test_addPeer_peerIsAdded() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        peers.removeSemanticTag(_claraPST);
        SyncQueue mySyncQueue = new SyncQueue(peers);
        
        // Test if only 2 peers were added
        assertTrue(SharkCSAlgebra.identical(peers, mySyncQueue.getPeers()));
        
        mySyncQueue.addPeer(_claraPST);
        Enumeration<PeerSemanticTag> p  = _kb.getPeerSTSet().peerTags();
        // Now we should have 3 peers
        assertTrue(SharkCSAlgebra.identical(_kb.getPeerSTSet(), mySyncQueue.getPeers()));
        
    }

    /**
     * Test of pop method, of class SyncQueue.
     */
    @Test
    public void testPop() {
        System.out.println("pop");
        PeerSemanticTag peer = null;
        SyncQueue instance = null;
        List<ContextCoordinates> expResult = null;
        List<ContextCoordinates> result = instance.pop(peer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
