package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * The implementing class for an Interest.
 * <p>
 * This class just takes semantic tags and sets of semantic tags.
 * It does not copy these tags or sets. Thus, tags or sets used
 * as parameters must not be just after transfering to the kepInterest.
 * Copies can be made with each SharkKB.
 * <p>
 * This class can be created
 *
 * @author mfi, thsc
 */
public class InMemoInterest implements ASIPInterest {
    
    /* 
    We are about migrating from KEP to ASIP. We need a mapping
    of ASIP concepts to KEP concepts.
    
    That's a technical mapping not a semantical. E.g.
    approver is actually an sender from a semantically
    perspective. Cardinality of approvers fits to peer, though.
    
    ASIP        KEP
    topic       topic
    type        --
    approver    peer
    --          sender
    senders     --
    receiver    remotePeer
    location    location
    time        time
    direction   direction
    */

    private STSet topics; // KEP and ASIP topics
    private STSet types; // ASIP types
    private PeerSemanticTag sender; // KEP sender only - can be removed soon
    private PeerSTSet approvers; // also KEP peers
    private PeerSTSet receivers; // also KEP remotePeers
    private TimeSTSet times; // both
    private SpatialSTSet locations; // both
    private int direction; // both

    /**
     * Creates an any kepInterest.
     */
    public InMemoInterest() throws SharkKBException {
        this(null, null, null, null, null, null, null, ASIPSpace.DIRECTION_INOUT);
    }


    /**
     * Creates an kepInterest.
     */
    InMemoInterest(STSet topics, STSet types, PeerSemanticTag sender,
                   PeerSTSet approvers, PeerSTSet receivers, TimeSTSet times,
                   SpatialSTSet locations, int direction) throws SharkKBException {

        this.topics = topics;
        this.types = types;
        this.approvers = approvers;
        this.receivers = receivers;
        this.times = times;
        this.locations = locations;
        this.direction = direction;
        this.sender = sender;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     *
     * @return
     */
    @Override
    public STSet getTopics() {
        return this.topics;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     *
     * @param topics
     */
    @Override
    public void setTopics(STSet topics) {
        this.topics = topics;
    }

    @Override
    public int getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     */
    @Override
    public TimeSTSet getTimes() {
        return this.times;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     */
    @Override
    public void setTimes(TimeSTSet times) {
        this.times = times;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     */
    @Override
    public SpatialSTSet getLocations() {
        return this.locations;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     *
     * @param locations
     */
    @Override
    public void setLocations(SpatialSTSet locations) {
        this.locations = locations;
    }


    public ASIPInterest contextualize(ASIPSpace context, FPSet fps)
            throws SharkKBException {

        // create resultSet
        return SharkAlgebra.contextualize(this, context, fps);
    }

    //////////////////////////////////////////////////////////////////////
    //                             ASIP                                 //
    //////////////////////////////////////////////////////////////////////
    @Override
    public STSet getTypes() {
        return this.types;
    }

    @Override
    public PeerSemanticTag getSender() {
        // we map ASIP sender to KEP sender (due to the fitting cardinality, see comments in top of that class)
        return this.sender;
    }

    @Override
    public PeerSTSet getReceivers() {
        return this.receivers;
    }

    @Override
    public PeerSTSet getApprovers() {
        return this.approvers;
    }

    @Override
    public void setTypes(STSet types) {
        this.types = types;
    }

    @Override
    public void setApprovers(PeerSTSet approvers) {
        this.approvers = approvers;
    }

    @Override
    public void setSender(PeerSemanticTag sender) {
        this.sender = sender;
    }

    @Override
    public void setReceivers(PeerSTSet receivers) {
        this.receivers = receivers;
    }
}
