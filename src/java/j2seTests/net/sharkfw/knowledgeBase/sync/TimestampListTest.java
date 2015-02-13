/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.Date;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s0539710
 */
public class TimestampListTest {
    
    protected SyncKB _kb;
    protected ContextCoordinates _cc;
    protected SemanticTag _teapotST;
    protected PeerSemanticTag _alicePST, _bobPST, _claraPST;
    
    @Before
    public void setUp() throws SharkKBException {
        _kb = new SyncKB(new InMemoSharkKB());
        L.setLogLevel(L.LOGLEVEL_ALL);
        _teapotST = _kb.createSemanticTag("teapot", "www.teapot.net");
        _alicePST = _kb.createPeerSemanticTag("Alice", "www.alice.net", "mail@alice.net");
        _bobPST = _kb.createPeerSemanticTag("Bob", "www.bob.net", "mail@bob.net");
        _claraPST = _kb.createPeerSemanticTag("Clara", "www.clara.net", "mail@clara.net");
        _cc = _kb.createContextCoordinates(_teapotST, _alicePST, _alicePST, _bobPST, null, null, SharkCS.DIRECTION_INOUT);
    }

    @Test
    public void test_createSyncQueue_allPeersSaved() throws SharkKBException {
        PeerSTSet peers = _kb.getPeerSTSet();
        TimestampList mySyncQueue = new TimestampList(peers, _kb);
        
        assertTrue(SharkCSAlgebra.identical(peers, mySyncQueue.getPeers()));
    }
    
    @Test
    public void test_addPeer_timestampZero() throws SharkKBException {
        TimestampList t = new TimestampList(_kb);
        t.newPeer(_alicePST);
        
        assertEquals(t.getTimestamp(_alicePST), new Date(0));
        
    }
    
    @Test
    public void test_removePeer_returnsNull() throws SharkKBException {
        TimestampList t = new TimestampList(_kb);
        t.newPeer(_alicePST);
        t.removePeer(_alicePST);
        assertNull(t.getTimestamp(_alicePST));
        
    }
    
    @Test
    public void test_getPeers_allPeersRetrieved() throws SharkKBException {
        TimestampList t = new TimestampList(_kb);
        t.newPeer(_alicePST);
        t.newPeer(_bobPST);
        PeerSTSet expected = InMemoSharkKB.createInMemoPeerSTSet();
        expected.merge(_alicePST); expected.merge(_bobPST);
        
        assertTrue(SharkCSAlgebra.identical(expected, t.getPeers()));
    }
    
    @Test
    public void test_resetDateGetDate_currentDateRetrieved() throws SharkKBException, InterruptedException {
        TimestampList t = new TimestampList(_kb);
        t.newPeer(_alicePST);
        
        Date expected = new Date();
        Thread.sleep(1);
        
        t.resetTimestamp(_alicePST);

        assertTrue(expected.before(t.getTimestamp(_alicePST)));
    }
    
    @Test
    public void test_persistAndRetrieve_allPeersRetrieved() throws SharkKBException {
        TimestampList t = new TimestampList(_kb);
        assertEquals(0, t.getPeers().size());
        
        // Add some peers 
        t.newPeer(_alicePST);
        t.newPeer(_bobPST);
        
        assertEquals(2, t.getPeers().size());
        
        // Now create a new list, that should also have 2 peers
        t = new TimestampList(_kb);
        
        assertEquals(2, t.getPeers().size());
    }
}
