package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

/**
 * The implementing class for an Interest.
 * 
 * This class just takes semantic tags and sets of semantic tags.
 * It does not copy these tags or sets. Thus, tags or sets used
 * as parameters must not be just after transfering to the kepInterest.
 * Copies can be made with each SharkKB.
 * 
 * This class can be created

* @author mfi, thsc
 */
public class InMemoInterest extends InMemoSharkCS implements Interest, ASIPInterest {
    
    /* 
    We are about migrating from KEP to ASIP. We need a mapping
    of ASIP concepts to KEP concepts.
    
    That's a technical mapping not a semantical. E.g.
    approver is actually an originator from a semantically
    perspective. Cardinality of approvers fits to peer, though.
    
    ASIP        KEP
    topic       topic
    type        --
    approver    peer
    --          originator
    senders     --
    receiver    remotePeer
    location    location
    time        time
    direction   direction
    */
    
    private STSet topics; // KEP and ASIP topics
    private STSet types; // ASIP types
    private PeerSTSet senders; // ASIP senders
    private PeerSemanticTag originator; // KEP originator only - can be removed soon
    private PeerSTSet approvers; // also KEP peers
    private PeerSTSet receivers; // also KEP remotePeers
    private TimeSTSet times; // both
    private SpatialSTSet locations; // both
    private int direction; // both
    
    /**
     * Creates an any kepInterest.
     */
    public InMemoInterest() {
        this(null, null, (PeerSTSet) null, null, null, null, null, ASIPSpace.DIRECTION_INOUT);
    }
    
    /**
     * Creates an kepInterest.
     */
    InMemoInterest(STSet topics, STSet types, PeerSTSet senders,
            PeerSTSet approvers, PeerSTSet receivers, TimeSTSet times, 
            SpatialSTSet locations, int direction) {
        
        // that's an ASIP kepInterest
        super(true);

        this.topics = topics;
        this.types = types;
        this.senders = senders;
        this.approvers = approvers;
        this.receivers = receivers;
        this.times = times;
        this.locations = locations;
        this.direction = direction;
    }
    
    /**
     * Creates an kepInterest.
     */
    InMemoInterest(STSet topics, STSet types, PeerSemanticTag sender, 
            PeerSTSet approvers, PeerSTSet receivers, TimeSTSet times, 
            SpatialSTSet locations, int direction) throws SharkKBException {
        
        this(topics, types, (PeerSTSet) null, approvers, receivers, times,
                locations, direction);

        this.originator = sender;
        this.senders = InMemoSharkKB.createInMemoPeerSTSet();
        this.senders.merge(sender);
    }

    /**
     * New Shark applications must not use this contructor.
     * creates an any kepInterest.
     * @deprecated 
     */
    InMemoInterest(STSet topics, PeerSemanticTag originator, 
            PeerSTSet peers, PeerSTSet remotePeers, TimeSTSet times, 
            SpatialSTSet locations, int direction) {
        
        super();
        
        this.topics = topics;
        this.originator = originator;
        this.approvers = peers;
        this.receivers = remotePeers;
        this.times = times;
        this.locations = locations;
        this.direction = direction;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
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
     * @return 
     * @deprecated 
     */
    @Override
    public PeerSemanticTag getOriginator() {
        return this.originator;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     * @deprecated 
     */
    @Override
    public void setOriginator(PeerSemanticTag originator) {
        this.originator = originator;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     * @deprecated 
     */
    @Override
    public PeerSTSet getRemotePeers() {
        return this.receivers;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     * @deprecated 
     */
    @Override
    public void setRemotePeers(PeerSTSet remotePeers) {
        this.receivers = remotePeers;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     * @deprecated 
     */
    @Override
    public PeerSTSet getPeers() {
        return this.approvers;
    }

    /**
     * This kepInterest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     * @deprecated 
     */
    @Override
    public void setPeers(PeerSTSet peers) {
        this.approvers = peers;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     * 
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
     * @param locations
     */
    @Override
    public void setLocations(SpatialSTSet locations) {
        this.locations = locations;
    }

  /**
   * calculates mutual kepInterest. This kepInterest is used as source.
     * @param fp
   * @return Mutual kepInterest or null if there is no match
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     * @deprecated 
   */
    @Override
    public Interest contextualize(SharkCS context, FragmentationParameter[] fp) 
            throws SharkKBException {
        // create result
        InMemoInterest mutualInterest = new InMemoInterest();
        
        return null;
    }
    
    public ASIPInterest contextualize(ASIPSpace context, FPSet fps) 
            throws SharkKBException {
        
        // create result
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
        // we map ASIP sender to KEP originator (due to the fitting cardinality, see comments in top of that class)
        return this.originator;
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
        this.originator = sender;
    }

    @Override
    public void setReceivers(PeerSTSet receivers) {
        this.receivers = receivers;
    }

    @Override
    public void setSenders(PeerSTSet senders) {
        this.senders = senders;
    }

    @Override
    public PeerSTSet getSenders() {
        return this.senders;
    }
}
