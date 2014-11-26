/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 *
 * @author s0539710
 */
class SyncQueue {
    
    protected List<SyncQueueObject> _syncQueue;
    
    public SyncQueue(PeerSTSet peersToSyncWith) {
        _syncQueue = new ArrayList<>();
        // Add all possible peers to queue
        Enumeration<PeerSemanticTag> peerEnum = peersToSyncWith.peerTags();
        while (peerEnum.hasMoreElements()) {
            _syncQueue.add(new SyncQueueObject(peerEnum.nextElement()));
        }
    }
    
    /**
     * Adds a context coordinate to all peers that do not already have this cc in their sync list.
     * @param cc 
     */
    public void push(ContextCoordinates cc)throws SharkKBException {
        for (SyncQueueObject s : _syncQueue) {
            s.addCoordinate(cc);
        }
    }
    
    /**
     * Add a peer that will be synced with in the future
     * @param peer 
     */
    public void addPeer(PeerSemanticTag peer) {
        _syncQueue.add(new SyncQueueObject(peer));
    }
    
    public PeerSTSet getPeers() throws SharkKBException {
        PeerSTSet myPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        for (SyncQueueObject s : _syncQueue) {
            myPeerSTSet.merge(s.getPeer());
        }
        return myPeerSTSet;
    }
    
    /** 
     * Get and remove all context coordinates that should be synced with a peer.
     * @param peer
     * @return 
     */
    public List<ContextCoordinates> pop(PeerSemanticTag peer) {
        // Find the peer in our list
        Iterator<SyncQueueObject> i = _syncQueue.iterator();
        SyncQueueObject waldo;
        // Lets look for waldo
        do {
            waldo = i.next();
        } while (!waldo.getPeer().equals(peer));
        
        return waldo.popCoordinates();
    }
    
    class SyncQueueObject {
        private ArrayList<ContextCoordinates> _ccList;
        private PeerSemanticTag _peer;
        
        public SyncQueueObject(PeerSemanticTag peer) {
            _peer = InMemoSharkKB.createInMemoCopy(peer);
            _ccList = new ArrayList<>();
        }
        
        public void addCoordinate(ContextCoordinates cc) throws SharkKBException {
            if (!_ccList.contains(cc)) {
                _ccList.add(InMemoSharkKB.createInMemoCopy(cc));
            }
        }
        
        public List<ContextCoordinates> popCoordinates() {
            // Clone a tmp list
            List<ContextCoordinates> tmpList = (List<ContextCoordinates>)_ccList.clone();
            // Clear the old one
            _ccList.clear();
            return tmpList;
        }
        
        public PeerSemanticTag getPeer() {
            return _peer;
        }
    }
}
