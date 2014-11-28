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
class SyncBucketList {
    
    protected List<SyncBucket> _syncList;
        
    public SyncBucketList() {
        _syncList = new ArrayList<>();
    }
    
    public SyncBucketList(PeerSTSet peersToSyncWith) {
        _syncList = new ArrayList<>();
        // Add all possible peers to queue
        Enumeration<PeerSemanticTag> peerEnum = peersToSyncWith.peerTags();
        while (peerEnum.hasMoreElements()) {
            _syncList.add(new SyncBucket(peerEnum.nextElement()));
        }
    }
    
    /**
     * Adds a context coordinate to all peers that do not already have this cc in their sync list.
     * @param cc 
     */
    public void addToBuckets(ContextCoordinates cc)throws SharkKBException {
        for (SyncBucket s : _syncList) {
            s.addCoordinate(cc);
        }
    }
    
    /**
     * Add a peer that will be synced with in the future
     * @param peer 
     */
    public void appendPeer(PeerSemanticTag peer) {
        _syncList.add(new SyncBucket(peer));
    }
    
    public PeerSTSet getPeers() throws SharkKBException {
        PeerSTSet myPeerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        for (SyncBucket s : _syncList) {
            myPeerSTSet.merge(s.getPeer());
        }
        return myPeerSTSet;
    }
    
    /** 
     * Get and remove all context coordinates that should be synced with a peer.
     * @param peer
     * @return 
     */
    public List<ContextCoordinates> popFromBucket(PeerSemanticTag peer) {
        // Find the peer in our list
        Iterator<SyncBucket> i = _syncList.iterator();
        SyncBucket waldo;
        // Lets look for waldo
        while(i.hasNext()) {
            waldo = i.next();
            if(waldo.getPeer().equals(peer))
                return waldo.popCoordinates();
        }
        
        return new ArrayList<>();
    }
    
    class SyncBucket {
        private final ArrayList<ContextCoordinates> _ccList;
        private final PeerSemanticTag _peer;
        
        public SyncBucket(PeerSemanticTag peer) {
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
