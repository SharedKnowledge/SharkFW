package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.knowledgeBase.*;

/**
 * The implementing class for an Interest.
 * 
 * This class just takes semantic tags and sets of semantic tags.
 * It does not copy these tags or sets. Thus, tags or sets used
 * as parameters must not be just after transfering to the interest.
 * Copies can be made with each SharkKB.
 * 
 * This class can be created

* @author mfi, thsc
 */
public class InMemoInterest extends InMemoSharkCS implements Interest, ASIPInterest {
    
    /* 
    We are about migrating from KEP to LASP. We need a mapping
    of LASP concepts to KEP concepts.
    
    That's a technical mapping not a semantical. E.g.
    approver is actually an originator from a semantically
    perspective. Cardinality of approvers fits to peer, though.
    
    WASP        KEP
    topic       topic
    type        --
    approver    peer
    sender      originator
    receiver    remotePeer
    location    location
    time        time
    direction   direction
    */
    
    private STSet topics; // KEP and LASP topics
    private STSet types; // LASP types
    private PeerSemanticTag originator; // also LASP sender
    private PeerSTSet peers; // also LASP approver
    private PeerSTSet remotePeers; // also LASP receiver
    private TimeSTSet times; // both
    private SpatialSTSet locations; // both
    private int direction; // both
    
    /**
     * Creates an any interest.
     */
    public InMemoInterest() {
        this(null, null, null, null, null, null, null, SharkCS.DIRECTION_INOUT); 
    }
    
    /**
     * Creates an interest.
     */
    InMemoInterest(STSet topics, STSet types, PeerSemanticTag sender, 
            PeerSTSet approvers, PeerSTSet receivers, TimeSTSet times, 
            SpatialSTSet locations, int direction) {
        
        // that's an LASP interest
        super(true);

        this.topics = topics;
        this.types = types;
        this.originator = sender;
        this.peers = approvers;
        this.remotePeers = receivers;
        this.times = times;
        this.locations = locations;
        this.direction = direction;
    }

    /**
     * New Shark applications must not use this contructor.
     * creates an any interest.
     * @deprecated 
     */
    InMemoInterest(STSet topics, PeerSemanticTag originator, 
            PeerSTSet peers, PeerSTSet remotePeers, TimeSTSet times, 
            SpatialSTSet locations, int direction) {
        
        super();
        
        this.topics = topics;
        this.originator = originator;
        this.peers = peers;
        this.remotePeers = remotePeers;
        this.times = times;
        this.locations = locations;
        this.direction = direction;
    }

    @Override
    public boolean isAny(int dim) {
        return SharkCSAlgebra.isAny(this, dim);
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
     * This interest will use this set as provided as parameter. No copy is made.
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
     */
    @Override
    public PeerSemanticTag getOriginator() {
        return this.originator;
    }

    /**
     * This interest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     */
    @Override
    public void setOriginator(PeerSemanticTag originator) {
        this.originator = originator;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     */
    @Override
    public PeerSTSet getRemotePeers() {
        return this.remotePeers;
    }

    /**
     * This interest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     */
    @Override
    public void setRemotePeers(PeerSTSet remotePeers) {
        this.remotePeers = remotePeers;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     */
    @Override
    public PeerSTSet getPeers() {
        return this.peers;
    }

    /**
     * This interest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     */
    @Override
    public void setPeers(PeerSTSet peers) {
        this.peers = peers;
    }

    /**
     * Note: the object reference is returned. The set shouldn't be changed outside.
     */
    @Override
    public TimeSTSet getTimes() {
        return this.times;
    }

    /**
     * This interest will use this set as provided as parameter. No copy is made.
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
     * This interest will use this set as provided as parameter. No copy is made.
     * Thus, the parameter must not be changed after calling this methode. Make
     * a copy if necessary. Copies can be made with any SharkKB.
     */
    @Override
    public void setLocations(SpatialSTSet locations) {
        this.locations = locations;
    }

  /**
   * calculates mutual interest. This interest is used as source.
   * @param anchorSet Context of the calcuation.
   * @return Mutual interest or null if there is no match
   */
    @Override
    public Interest contextualize(SharkCS context, FragmentationParameter[] fp) 
            throws SharkKBException {
        // create result
        InMemoInterest mutualInterest = new InMemoInterest();
        
        if(SharkCSAlgebra.contextualize(mutualInterest, this, context, fp)) {
            return mutualInterest;
        } else {
            return null;
        }
    }
    
    ////////////////// LASP mapping //////////////////////////////
    @Override
    public STSet getTypes() {
        return this.types;
    }

    @Override
    public PeerSemanticTag getSender() {
        // we map LASP sender to KEP originator (due to the fitting cardinality, see comments in top of that class)
        return this.getOriginator();
    }

    @Override
    public PeerSTSet getReceivers() {
        return this.getRemotePeers();
    }

    @Override
    public PeerSTSet getApprovers() {
        return this.getPeers();
    }

    @Override
    public void setTypes(STSet types) {
        this.types = types;
    }

    @Override
    public void setApprovers(PeerSTSet approvers) {
        this.peers = approvers;
    }

    @Override
    public void setSender(PeerSemanticTag sender) {
        this.originator = sender;
    }

    @Override
    public void setReceivers(PeerSTSet receivers) {
        this.remotePeers = receivers;
    }
}
