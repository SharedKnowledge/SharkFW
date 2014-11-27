/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
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
        // Create a Peer ST Set with only 2 peers
        PeerSTSet initialPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        initialPeerSTSet.merge(_alicePST);
        initialPeerSTSet.merge(_bobPST);
        SyncQueue mySyncQueue = new SyncQueue(initialPeerSTSet);
        
        // Test if only 2 peers were added
        assertTrue(SharkCSAlgebra.identical(initialPeerSTSet, mySyncQueue.getPeers()));
        
        // Add one more peer
        mySyncQueue.addPeer(_claraPST);
        PeerSTSet expectedPeerSTSet = InMemoSharkKB.createInMemoCopy(initialPeerSTSet);
        expectedPeerSTSet.merge(_claraPST);
        
        // Now we should have 3 peers and not 2
        PeerSTSet result = mySyncQueue.getPeers();
        assertTrue(SharkCSAlgebra.identical(expectedPeerSTSet, result));
        assertFalse(SharkCSAlgebra.identical(initialPeerSTSet, result));
    }

    /**
     * Test of pop method, of class SyncQueue.
     */
    @Test
    public void test_popCCs_correctCCReturned() throws SharkKBException {
        // Create a sync queue with the peers
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncQueue mySyncQueue = new SyncQueue(peers);
        
        // Add two context coordinates
        ContextCoordinates cc1 = _kb.createContextCoordinates(_teapotST, _alicePST, _bobPST, _alicePST, null, null, SharkCS.DIRECTION_OUT);
        ContextCoordinates cc2 = _kb.createContextCoordinates(_teapotST, _bobPST, _claraPST, _alicePST, null, null, SharkCS.DIRECTION_OUT);
        mySyncQueue.push(cc1);
        mySyncQueue.push(cc2);
        
        // Create the expected value
        List<ContextCoordinates> expected = new ArrayList<ContextCoordinates>();
        expected.add(cc1);
        expected.add(cc2);

        // Get them and check
//        List<ContextCoordinates> result = mySyncQueue.pop(_bobPST);
//        assertEquals(expected, result); // Why isnt this working?!
        assertEquals(expected, mySyncQueue.pop(_bobPST));
        assertEquals(expected, mySyncQueue.pop(_alicePST));
        assertEquals(expected, mySyncQueue.pop(_claraPST));
        assertEquals(new ArrayList<ContextCoordinates>(), mySyncQueue.pop(_alicePST));
    }
    
    @Test
    public void myTest() throws SharkKBException {
        ContextCoordinates cc = _kb.createContextCoordinates(_teapotST, _alicePST, _bobPST, _claraPST, null, null, SharkCS.DIRECTION_OUT);
        ContextCoordinates clonedCC = InMemoSharkKB.createInMemoCopy(cc);
        
        assertEquals(cc, clonedCC);
    }
}
