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
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkCS;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

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
    public void addCoordinatesToBuckets(ContextCoordinates cc)throws SharkKBException {
        for (SyncBucket s : _syncList) {
            s.addCoordinate(cc);
        }
    }
    
    /**
     * Adds a context coordinate to a specific peer.
     * @param cc 
     */
    public void addCoordinatesToBuckets(ContextCoordinates cc, PeerSemanticTag peer)throws SharkKBException {
        for (SyncBucket s : _syncList) {
            if(s._peer.equals(peer)) {
                s.addCoordinate(cc);
            }
        }
    }
    
    /**
     * Adds a SemanticTag to a bucket.
     * @param t
     * @throws SharkKBException 
     */
    public void addSemanticTagToBucket(TimeSemanticTag t) throws SharkKBException{
        for(SyncBucket s : _syncList){
            s.addSemanticTag(t);
        }
    }

    public void addSemanticTagToBucket(PeerSemanticTag t) throws SharkKBException{
        for(SyncBucket s : _syncList){
            s.addSemanticTag(t);
        }
    }

    public void addSemanticTagToBucket(SpatialSemanticTag t) throws SharkKBException{
        for(SyncBucket s : _syncList){
            s.addSemanticTag(t);
        }
    }

    public void addSemanticTagToBucket(SemanticTag t) throws SharkKBException{
        for(SyncBucket s : _syncList){
            s.addSemanticTag(t);
        }
    }
    
    /**
     * Add a peer that will be synced with in the future
     * @param peer 
     */
    public void appendPeer(PeerSemanticTag peer) {
        _syncList.add(new SyncBucket(peer));
    }
    
    public void removePeer(PeerSemanticTag peer) {
        // Find index of that peer
        Iterator<SyncBucket> i = _syncList.iterator();
        SyncBucket waldo = null;
        // Lets look for waldo
        while(i.hasNext()) {
            waldo = i.next();
            if(waldo.getPeer().equals(peer)) 
                break;
        }
        if (waldo == null) {
            L.w("Tried to remove an unexisting peer from a SyncKP SyncBucketList.");
        }
        else {
            _syncList.remove(waldo);
        }
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
    public List<ContextCoordinates> popCoordinatesFromBucket(PeerSemanticTag peer) {
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
    
    /** 
     * Get and remove all context coordinates that should be synced with a peer.
     * @param peer
     * @return 
     */
    public SharkCS popContextSpaceFromBucket(PeerSemanticTag peer) {
        // Find the peer in our list
        Iterator<SyncBucket> i = _syncList.iterator();
        SyncBucket waldo;
        // Lets look for waldo
        while(i.hasNext()) {
            waldo = i.next();
            if(waldo.getPeer().equals(peer))
                return waldo.popSemanticTags();
        }
        
        return null;
    }
    
    class SyncBucket {
        private final ArrayList<ContextCoordinates> _ccList;
        private final PeerSemanticTag _peer;
        private SharkCS _cs;
        
        public SyncBucket(PeerSemanticTag peer) {
            _peer = InMemoSharkKB.createInMemoCopy(peer);
            _ccList = new ArrayList<>();
//            _cs = new SharkCS();
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
        
        public void addSemanticTag(TimeSemanticTag t) throws SharkKBException{
            SharkCSAlgebra.merge(_cs.getTimes(), t);
        }
        
        public void addSemanticTag(PeerSemanticTag t) throws SharkKBException{
            SharkCSAlgebra.merge(_cs.getPeers(), t);
        }
        
        public void addSemanticTag(SpatialSemanticTag t) throws SharkKBException{
            SharkCSAlgebra.merge(_cs.getLocations(), t);
        }
        
        public void addSemanticTag(SemanticTag t) throws SharkKBException{
            SharkCSAlgebra.merge(_cs.getTopics(), t);
        }
        
        public SharkCS popSemanticTags() {
            SharkCS c = _cs;
//            _cs = new SharkCS();
            return c;
        }
        
        public PeerSemanticTag getPeer() {
            return _peer;
        }
    }
}
