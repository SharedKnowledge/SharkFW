package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.knowledgeBase.*;
/**
 * ContextCoordinates describe the coordinates of a ContextPoint.
 * This class holds a reference to one tag one tag on each dimension, and stores
 * its SIs.
 *
 * TODO: Once tags are deserialized, the additional SI-storage becomes obsolete.
 * SIs can then be retrieved using .getAllSI() on the STSets which are stored
 * anyway.
 *
 * ContextCoordinates describe a point or subspace of the 7 dimension
 * <code>ContextSpace</code>. ContextPoints use ContextCoordinates to denote their
 * position in the ContextSpace.
 *
 * @see net.sharkfw.knowledgeBase.ContextPoint
 * @see net.sharkfw.knowledgeBase.ContextSpace
 *
 * @author thsc
 * @author mfi
 */
class InMemoContextCoordinates extends InMemoSharkCS implements ContextCoordinates {
    private final SemanticTag topic;
    private final PeerSemanticTag originator;
    private final PeerSemanticTag peer;
    private final PeerSemanticTag remotePeer;
    private final TimeSemanticTag time;
    private final SpatialSemanticTag location;
    private final int direction;
    
    public InMemoContextCoordinates(SemanticTag topic, 
            PeerSemanticTag originator,
            PeerSemanticTag peer,
            PeerSemanticTag remotePeer,
            TimeSemanticTag time,
            SpatialSemanticTag location,
            int direction) {
        
        this.topic = topic;
        this.originator = originator;
        this.peer = peer;
        this.remotePeer = remotePeer;
        this.time = time;
        this.location = location;
        this.direction = direction;
    }

    @Override
    public SemanticTag getTopic() {
        return this.topic;
    }

    @Override
    public PeerSemanticTag getPeer() {
        return this.peer;
    }

    @Override
    public PeerSemanticTag getRemotePeer() {
        return this.remotePeer;
    }

    @Override
    public PeerSemanticTag getOriginator() {
        return this.originator;
    }

    @Override
    public TimeSemanticTag getTime() {
        return this.time;
    }

    @Override
    public SpatialSemanticTag getLocation() {
        return this.location;
    }

    @Override
    public int getDirection() {
        return this.direction;
    }

    @Override
    public boolean isAny(int dim) {
        return SharkCSAlgebra.isAny(this, dim);
    }

    private InMemoSTSet topics = null;
    @Override
    public STSet getTopics() {
        if(this.topics != null) return this.topics;
        
        if(this.topic == null) return null;
        
        this.topics = new InMemoSTSet();
        try {
            this.topics.add(this.topic);
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.topics;
        
    }

    private InMemoPeerSTSet remotePeers = null;
    @Override
    public PeerSTSet getRemotePeers() {
        if(this.remotePeers != null) return this.remotePeers;
        
        if(this.remotePeer == null) return null;
        
        this.remotePeers = new InMemoPeerSTSet();
        try {
            this.remotePeers.add(this.remotePeer);
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.remotePeers;
        
    }

    private InMemoPeerSTSet peers = null;
    @Override
    public PeerSTSet getPeers() {
        if(this.peers != null) return this.peers;
        
        if(this.peer == null) return null;
        
        this.peers = new InMemoPeerSTSet();
        try {
            this.peers.add(this.peer);
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.peers;
    }

    private InMemoTimeSTSet times = null;
    @Override
    public TimeSTSet getTimes() {
        if(this.times != null) return this.times;
        
        if(this.time == null) return null;
        
        this.times = new InMemoTimeSTSet();
        try {
            this.times.add(this.time);
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.times;
    }

    private InMemoSpatialSTSet locations = null;
    @Override
    public SpatialSTSet getLocations() {
        if(this.locations != null) return this.locations;
        
        if(this.location == null) return null;
        
        this.locations = new InMemoSpatialSTSet();
        try {
            this.locations.add(this.location);
        } catch (SharkKBException ex) {
            // won't happen.
            return null;
        }
        
        return this.locations;
    }
    
    @Override
    public boolean equals(Object obj) {
        return SharkCSAlgebra.identical(this, (ContextCoordinates) obj);
    }
}
