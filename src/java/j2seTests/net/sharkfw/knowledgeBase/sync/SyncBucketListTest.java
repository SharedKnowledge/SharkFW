/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
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
public class SyncBucketListTest {
    
    protected SharkKB _kb;
    protected ContextCoordinates _cc;
    protected SemanticTag _teapotST;
    protected PeerSemanticTag _alicePST, _bobPST, _claraPST;
    
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
        SyncBucketList mySyncQueue = new SyncBucketList(peers);
        
        assertTrue(SharkCSAlgebra.identical(peers, mySyncQueue.getPeers()));
    }
    
    /**
     * Test of push method, of class SyncQueue.
     */
    @Test
    public void test_pushContextCoordinates_savedForAllPeers() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncBucketList mySyncQueue = new SyncBucketList(peers);
        
        // Add a cc
        mySyncQueue.addToBuckets(_cc);
        
        // All peers should now have this CC in their "to-sync" list
        SharkCSAlgebra.identical(_cc, mySyncQueue.popFromBucket(_bobPST).get(0));
        SharkCSAlgebra.identical(_cc, mySyncQueue.popFromBucket(_claraPST).get(0));
    }

    @Test
    public void test_pushContextCoordinates_noDuplicateSaved() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncBucketList mySyncQueue = new SyncBucketList(peers);
        
        // Add a cc
        mySyncQueue.addToBuckets(_cc);
        // Add it again
        mySyncQueue.addToBuckets(_cc);
        
        // All peers should now have this CC in their "to-sync" list
        SharkCSAlgebra.identical(_cc, mySyncQueue.popFromBucket(_bobPST).get(0));
        SharkCSAlgebra.identical(_cc, mySyncQueue.popFromBucket(_claraPST).get(0));
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
        SyncBucketList mySyncQueue = new SyncBucketList(initialPeerSTSet);
        
        // Test if only 2 peers were added
        assertTrue(SharkCSAlgebra.identical(initialPeerSTSet, mySyncQueue.getPeers()));
        
        // Add one more peer
        mySyncQueue.appendPeer(_claraPST);
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
        SyncBucketList mySyncQueue = new SyncBucketList(peers);
        
        // Add two context coordinates
        ContextCoordinates cc1 = _kb.createContextCoordinates(_teapotST, _alicePST, _bobPST, _alicePST, null, null, SharkCS.DIRECTION_OUT);
        ContextCoordinates cc2 = _kb.createContextCoordinates(_teapotST, _bobPST, _claraPST, _alicePST, null, null, SharkCS.DIRECTION_OUT);
        mySyncQueue.addToBuckets(cc1);
        mySyncQueue.addToBuckets(cc2);
        
        // Create the expected value
        List<ContextCoordinates> expected = new ArrayList<ContextCoordinates>();
        expected.add(cc1);
        expected.add(cc2);

        // Get them and check
//        List<ContextCoordinates> result = mySyncQueue.pop(_bobPST);
//        assertEquals(expected, result); // Why isnt this working?!
        assertEquals(expected, mySyncQueue.popFromBucket(_bobPST));
        assertEquals(expected, mySyncQueue.popFromBucket(_alicePST));
        assertEquals(expected, mySyncQueue.popFromBucket(_claraPST));
        assertEquals(new ArrayList<ContextCoordinates>(), mySyncQueue.popFromBucket(_alicePST));
    }
    
    @Test
    public void test_popCCs_whenEmptyEmptyListReturned() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        SyncBucketList sbl = new SyncBucketList(peers);
        
        assertEquals(new ArrayList<>(), sbl.popFromBucket(_bobPST));
    }
}
