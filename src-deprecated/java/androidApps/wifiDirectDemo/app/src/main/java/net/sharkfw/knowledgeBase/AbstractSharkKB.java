package net.sharkfw.knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoTaxonomy;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.Iterator2Enumeration;

/**
 * This class implements as much methods from SharkKB as possible
 * by delegates. 
 * 
 * Derived classes must offer methods to create instances of 
 * semantic tag sets, interests and knowledge.
 * 
 * @author thsc
 */
public abstract class AbstractSharkKB extends PropertyHolderDelegate 
                                implements SharkKB, KnowledgeListener, 
                                            InterestStorage
{
    public static String SHARKFW_SENDER_PROPERTY = "sharkfw_sender";
    public static String SHARKFW_TIME_RECEIVED_PROPERTY = "sharkfw_timeReceived";

    protected SemanticNet topics;
    protected PeerTaxonomy peers;
    protected SpatialSTSet locations;
    protected TimeSTSet times;
    protected PeerSemanticTag owner;
    protected Knowledge knowledge;
    protected FragmentationParameter[] defaultFP;
    
    protected AbstractSharkKB() {}
    
    protected AbstractSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times) 
    {
        
        this.topics = topics;
        this.peers = peers;
        this.locations = locations;
        this.times = times;
        
        topics.addListener(this);
        peers.addListener(this);
        locations.addListener(this);
        times.addListener(this);
        
    }
    
    protected AbstractSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times,
                 Knowledge k) throws SharkKBException {
        
        this(topics, peers, locations, times);
        this.knowledge = k;
        this.knowledge.addListener(this);
    }    
     
    @Override
    public SharkCS asSharkCS() {
        return this.asInterest();
    }

    @Override
    public Interest asInterest() {
        // hide semantic tags
        STSet topicsSet = this.topics.asSTSet();
        PeerSTSet peersSet;
        try {
            peersSet = this.peers.asPeerSTSet();
        } catch (SharkKBException ex) {
            return null;
        }
        
        this.locations.setEnumerateHiddenTags(true);
        this.times.setEnumerateHiddenTags(true);
        topicsSet.setEnumerateHiddenTags(true);
        peersSet.setEnumerateHiddenTags(true);
        
//        try {
            return InMemoSharkKB.createInMemoInterest(topicsSet, this.owner,
                    peersSet, peersSet, this.times,
                    this.locations, SharkCS.DIRECTION_INOUT);
            
//            return this.createInterest(topicsSet, this.owner,
//                    peersSet, peersSet, this.times,
//                    this.locations, SharkCS.DIRECTION_INOUT);
//        } catch (SharkKBException ex) {
//            // never happens.
//        }
        
//        return null;
    }
    
    /**
     * Create an interest with given parameter. There is no need to
     * copy each dimension.
     * 
     * @param topics
     * @param originator
     * @param peers
     * @param remotePeers
     * @param times
     * @param locations
     * @param direction
     * @return 
     */
    abstract public Interest createInterest(STSet topics, 
            PeerSemanticTag originator, PeerSTSet peers, PeerSTSet remotePeers, 
            TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException;


    @Override
    /**
     * @deprecated
     */
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        SemanticTag st = this.getTopicSTSet().createSemanticTag(name, sis);
        this.notifySemanticTagCreated(st);
        return st;
    }

    /**
     * @deprecated
     */
    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si});
    }

    /**
     * @deprecated
     */
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerSemanticTag pst = this.getPeerSTSet().createPeerSemanticTag(name, sis, addresses);
        this.notifyPeerCreated(pst);
        return pst;
    }
    
    /**
     * @deprecated
     */
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, new String[] {address});
    }

    /**
     * @deprecated
     */
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, sis, new String[] {address});
    }
    /**
     * @deprecated
     */
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, addresses);
    }
    
    /**
     * @deprecated
     */
    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        SpatialSemanticTag sst = this.getSpatialSTSet().createSpatialSemanticTag(name, sis, geom);
        this.notifyLocationCreated(sst);
        return sst;
    }
    
    /**
     * @deprecated
     */
    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        TimeSemanticTag tst = this.getTimeSTSet().createTimeSemanticTag(from, duration);
        this.notifyTimeCreated(tst);
        return tst;
    }
    
    protected Knowledge getKnowledge() {
        return this.knowledge;
    }
    
//    /**
//     * creates an empty / any interest
//     * @return 
//     */
//    @Override
//    abstract public Interest createInterest() throws SharkKBException;
    
    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint toRemove = this.getContextPoint(coordinates);
        if(toRemove != null) {
            this.knowledge.removeContextPoint(toRemove);
        }
    }

    /**
     * 
     * @param cs must not be null - use getAllContextPoints in this case.
     * @return
     * @throws SharkKBException 
     */
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        return this.getContextPoints(cs, true);
    }
    
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        return this.contextPoints(cs, true);
    }
    
    /**
     * Return all context points that are in the context space.
     * 
     * <br/><b>Important:</b> This implementation differs from other usage, e.g.
     * when finding mutual interests. In this case, both interests must match
     * in all dimensions.
     * 
     * That's different here. The context space is taken and any possible
     * coordinate combination is calculated. That much might a huge number.
     * 
     * Finally, any context points matching with one of the coordinates are
     * returned.
     * 
     * <b>Important: The set contains references of existing context points.
     * Changes will have impact on the actual knowledge base. Make a copy if 
     * necessary.
     * </b>
     * 
     * @param cs if null (which means any context) all context points are returned.
     * @param matchAny
     * @return
     * @throws SharkKBException 
     * @deprecated us Iterator instead of Enumeration @see contextPoints
     */
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        Iterator<ContextPoint> iterCPs = this.contextPoints(cs, matchAny);
        if(iterCPs == null) return null;
        
        // else
        return new Iterator2Enumeration(iterCPs);
    }
    
    private ArrayList<KnowledgeBaseListener> listeners = new ArrayList<KnowledgeBaseListener>();

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        this.listeners.add(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        this.listeners.remove(kbl);
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return this.topics;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        if(this.topics instanceof SemanticNet) {
            return (SemanticNet) this.topics;
        } else {
            throw new SharkKBException("topic semantic tag set is not a semantic network");
        }
    }

    public void setTopics(SemanticNet topics) {
        this.topics = topics;
    }
    
    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        if(this.topics instanceof Taxonomy) {
            return (Taxonomy) this.topics;
        } else {
            if(this.topics instanceof SemanticNet) {
                return new InMemoTaxonomy((SemanticNet)this.topics);
            } else {
                throw new SharkKBException("topic semantic tag set is not a taxonomy and cannot be used as taxonomy");
            }
        }
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return this.peers.asPeerSTSet();
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        if(this.peers instanceof PeerSemanticNet) {
            return (PeerSemanticNet) this.peers;
        } else {
            throw new SharkKBException("peer dimension is not a PeerSemanticNet");
        }
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return this.peers;
    }

    public void setPeers(PeerTaxonomy  peers) {
        this.peers = peers;
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return this.times;
    }

    public void setTimes(TimeSTSet  times) {
        this.times = times;
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return this.locations;
    }
    
    public void setLocations(SpatialSTSet locations) {
        this.locations = locations;
    }
    
    @Override
    public Interest contextualize(SharkCS as) throws SharkKBException {
        return this.contextualize(as, this.getStandardFPSet());
    }
    
    @Override
    public Interest contextualize(SharkCS context, FragmentationParameter[] fp) throws SharkKBException {
        Interest result = this.createInterest();
        
        SharkCSAlgebra.contextualize(result, this.asSharkCS(), context, fp);
        /* NOTE: contextualize twists peer/remote peer and changes direction
         * Twisting peers has no effect here because there is just a single
         * peer set.
         *
         * Changing direction would have an effect, though. But a kb doesn't
         * care about direction just in it cps. Thus, we can simply set direction
         * after contextualization.
         */
        
        result.setDirection(context.getDirection());
        
        return result;
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        this.defaultFP = fps;
        this.persist();
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        if(this.defaultFP == null) {
            FragmentationParameter topicsFP = new FragmentationParameter(false, true, 2);
            FragmentationParameter peersFP = new FragmentationParameter(true, false, 2);
            FragmentationParameter restFP = new FragmentationParameter(false, false, 0);
            
            this.defaultFP = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
            
            this.defaultFP[SharkCS.DIM_TOPIC] = topicsFP;
            this.defaultFP[SharkCS.DIM_PEER] = peersFP;
            this.defaultFP[SharkCS.DIM_REMOTEPEER] = peersFP;
            this.defaultFP[SharkCS.DIM_ORIGINATOR] = peersFP;
            this.defaultFP[SharkCS.DIM_TIME] = restFP;
            this.defaultFP[SharkCS.DIM_LOCATION] = restFP;
            this.defaultFP[SharkCS.DIM_DIRECTION] = restFP;
            
        }
        
        return this.defaultFP;
    }
  
    /**
     * 
     * @param sis
     * @return
     * @throws SharkKBException 
     * @deprecated
     */
  @Override
  public SemanticTag getSemanticTag(String[] sis) throws SharkKBException {
      SemanticTag tag = this.getTopicSTSet().getSemanticTag(sis);      
      if(tag != null) return tag;
      
      tag = this.getPeerSTSet().getSemanticTag(sis);
      if(tag != null) return tag;
      
      tag = this.getSpatialSTSet().getSemanticTag(sis);
      if(tag != null) return tag;
      
      tag = this.getTimeSTSet().getSemanticTag(sis);
      
      return tag;
      
  }
  
  /**
   * 
   * @param si
   * @return
   * @throws SharkKBException 
     * @deprecated
   */
    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.getSemanticTag(new String[] {si});
    }
  
  
    /**
     * 
     * @param sis
     * @return
     * @throws SharkKBException 
     * @deprecated
     */
    @Override
    public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
        return this.getPeerSTSet().getSemanticTag(sis);
    }

    /**
     * 
     * @param si
     * @return
     * @throws SharkKBException 
     * @deprecated
     */
    @Override
    public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException {
        return this.getPeerSTSet().getSemanticTag(si);
    }

    /**
     * That KB listens to its sets which make up the vocabulary. That methode
     * is called whenever e.g. a tag in the topic dimension is created.
     * That message triggers KB listener.
     * 
     * @param tag
     * @param stset 
     */
    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.notifySemanticTagCreated(tag);
    }

    /**
     * That KB listens to its sets which make up the vocabulary. That methode
     * is called whenever e.g. a tag in the topic dimension is removed.
     * That message triggers KB listener.
     * 
     * @param tag
     * @param stset 
     */
    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.notifySemanticTagRemoved(tag);
    }

    /**
     * 
     * @return
     * @throws SharkKBException 
     * @deprecated
     */
    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        EnumerationChain<SemanticTag> tagEnum = new EnumerationChain<SemanticTag>();
        
        tagEnum.addEnumeration(this.getTopicSTSet().tags());
        tagEnum.addEnumeration(this.getPeerSTSet().tags());
        tagEnum.addEnumeration(this.getSpatialSTSet().tags());
        tagEnum.addEnumeration(this.getTimeSTSet().tags());
        
        return tagEnum;
    }
    
    /**
     * Delete tag in any dimension - if it can be found
     * @param sis
     * @throws SharkKBException 
     * @deprecated
     */
    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        if(sis == null || sis.length == 0) {
            return;
        }
        
        this.removeSemanticTag(this.getTopicSTSet(), sis);
        this.removeSemanticTag(this.getPeerSTSet(), sis);
        this.removeSemanticTag(this.getSpatialSTSet(), sis);
        this.removeSemanticTag(this.getTimeSTSet(), sis);
    }

    /**
     * 
     * @deprecated
     */
    @Override
    public void removeSemanticTag(SemanticTag st) throws SharkKBException {
        if(st == null) return;
        
        this.removeSemanticTag(st.getSI());
    }    
    
    protected void removeSemanticTag(STSet set, String[] sis) throws SharkKBException {
        SemanticTag tag = set.getSemanticTag(sis);
        if(tag != null) set.removeSemanticTag(tag);
        
        this.notifySemanticTagRemoved(tag);
    }
    
    public static final String OWNER = "AbstractKB_owner";
    public static final String DEFAULT_FP = "AbstractKB_defaultFP";

    protected void setOwnerListener() {
        if(this.owner instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) this.owner;
            
            st.setListener(this);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////
    //               kb listener                                          //
    ////////////////////////////////////////////////////////////////////////

    @Override
    public void contextPointAdded(ContextPoint cp) {
        this.notifyCpCreated(cp);
    }

    @Override
    public void cpChanged(ContextPoint cp) {
        this.notifyCpChanged(cp);
    }
    
    @Override
    public void contextPointRemoved(ContextPoint cp) {
        this.notifyCpRemoved(cp);
    }
    
    protected void notifyCpCreated(ContextPoint cp) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.contextPointAdded(cp);
        }
    }

    protected void notifyCpChanged(ContextPoint cp) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.cpChanged(cp);
        }
    }

    protected void notifyCpRemoved(ContextPoint cp) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.contextPointRemoved(cp);
        }
    }

    protected void notifySemanticTagCreated(SemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.topicAdded(tag);
        }
    }

    protected void notifyPeerCreated(PeerSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.peerAdded(tag);
        }
    }

    protected void notifyLocationCreated(SpatialSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.locationAdded(tag);
        }
    }

    protected void notifyTimeCreated(TimeSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.timespanAdded(tag);
        }
    }

    protected void notifySemanticTagRemoved(SemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.topicRemoved(tag);
        }
    }

    protected void notifyPeerRemoved(PeerSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.peerRemoved(tag);
        }
    }

    protected void notifyLocationRemoved(SpatialSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.locationRemoved(tag);
        }
    }

    protected void notifyTimeRemoved(TimeSemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.timespanRemoved(tag);
        }
    }

    protected void notifyPredicateCreated(SNSemanticTag subject, String predicate, SNSemanticTag object) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.predicateCreated(subject, predicate, object);
        }
    }

    protected void notifyPredicateRemoved(SNSemanticTag subject, String predicate, SNSemanticTag object) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.predicateRemoved(subject, predicate, object);
        }
    }

    void siChanged(AbstractSemanticTag aST) {
        // this call can only be made by owner
        // TODO!!
        
        // save data again
        this.persist();
    }
    
    
    /******************************************************************
     *                  Interest storage interface                    * 
     ******************************************************************/
    
    public static final String INTEREST_PROPERTY_NAME = "SharkKB_InterestsString";
    private ArrayList<SharkCS> interestsList = null;
    private static final String INTEREST_DELIMITER = "||";
            
    private void saveInterestsToProperties() throws SharkKBException {
        this.restoreInterestsFromProperties();
        
        XMLSerializer s = new XMLSerializer();
        
        Iterator<SharkCS> interestIter = this.interests();
        if(interestIter == null) {
            // remove property at all
            this.setProperty(INTEREST_PROPERTY_NAME, null);
            return;
        }
        
        StringBuilder interestString = new StringBuilder();
        
        while(interestIter.hasNext()) {
            SharkCS interest = interestIter.next();
            String serializedInterest = s.serializeSharkCS(interest);
            
            interestString.append(serializedInterest);
            interestString.append(INTEREST_DELIMITER);
        }
        
//        this.setProperty(INTEREST_PROPERTY_NAME, interestString.toString());
        this.setProperty(INTEREST_PROPERTY_NAME, interestString.toString(), false);
    }
    
    private void restoreInterestsFromProperties() throws SharkKBException {
        if(this.interestsList == null) {
            this.interestsList = new ArrayList();
            
            String interestsString = this.getProperty(INTEREST_PROPERTY_NAME);
            if(interestsString == null) {
                return;
            }
            
            StringTokenizer st = new StringTokenizer(interestsString, INTEREST_DELIMITER);
            
            XMLSerializer s = new XMLSerializer();
            
            while(st.hasMoreTokens()) {
                String interestString = st.nextToken();
                SharkCS interest = s.deserializeSharkCS(interestString);
                this.interestsList.add(interest);
            }
        }
    }
    
    /**
     * 
     * @param interests
     * @return -1 of no such interest in in the list
     */
    private int findInterestIndex(SharkCS interest) throws SharkKBException {
        for(int index = 0; index < this.interestsList.size(); index++) {
            SharkCS next = this.interestsList.get(index);
            if(SharkCSAlgebra.identical(next, interest)) {
                return index;
            }
        }
        
        // no matching interest found
        return -1;
    }
    
    /**
     * Saves this interest into a list of interests
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        this.restoreInterestsFromProperties();
        // if not already in there - add
        if(this.findInterestIndex(interest) == -1) {
            this.interestsList.add(interest);
            this.saveInterestsToProperties();
        }
    }
    
    /**
     * Removes this interest from the storage
     * @param interest 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        this.restoreInterestsFromProperties();
        
        int index = this.findInterestIndex(interest);
        
        if(index != -1) {
            this.interestsList.remove(index);
            this.saveInterestsToProperties();
        }
    }
    
    /**
     * Return iteration of interests stored in the 
     * interest storage
     * 
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        this.restoreInterestsFromProperties();
        return this.interestsList.iterator();
    }
}
