/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author s0539710
 */
class SyncQueue {
    
    protected Map<PeerSemanticTag, List<ContextCoordinates>> _syncQueue;
    
    public SyncQueue(PeerSTSet peersToSyncWith) {
        _syncQueue = new HashMap<>();
        
        // Add all possible peers to queue
        Enumeration<PeerSemanticTag> peerEnum = peersToSyncWith.peerTags();
        while (peerEnum.hasMoreElements()) {
            _syncQueue.put(peerEnum.nextElement(), new Vector<ContextCoordinates>());
        }
    }
    
    /**
     * Adds a context coordinate to all peers that do not already have this cc in their sync list.
     * @param cc 
     */
    public void push(ContextCoordinates cc) {
        for (Map.Entry<PeerSemanticTag, List<ContextCoordinates>> entry : _syncQueue.entrySet()) {
            if (!entry.getValue().contains(cc)) {
                entry.getValue().add(cc);
            }
        }
    }
    
    /**
     * Add a peer that will be synced with in the future
     * @param peer 
     */
    public void addPeer(PeerSemanticTag peer) {
        _syncQueue.put(peer, new Vector<ContextCoordinates>());
    }
    
    public PeerSTSet getPeers() throws SharkKBException {
        PeerSTSet myPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        for (PeerSemanticTag p : _syncQueue.keySet()) {
            myPeerSTSet.merge(p);
        }
        return myPeerSTSet;
    }
    
    /** 
     * Get and remove all context coordinates that should be synced with a peer.
     * @param peer
     * @return 
     */
    public List<ContextCoordinates> pop(PeerSemanticTag peer) {
        List<ContextCoordinates> coordinates = _syncQueue.remove(peer);
        return coordinates;
    }
    
}
