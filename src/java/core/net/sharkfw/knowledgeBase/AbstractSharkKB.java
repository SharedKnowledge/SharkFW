package net.sharkfw.knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * This class implements as much methods from SharkKB as possible
 * by delegates. 
 * 
 * Derived classes must offer methods to create instances of 
 * semantic tag sets, interests and knowledge.
 * 
 * @author thsc
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSharkKB extends PropertyHolderDelegate 
                                implements SharkKB, KnowledgeListener, 
                                            InterestStorage
{
    public static String SHARKFW_SENDER_PROPERTY = "sharkfw_sender";
    public static String SHARKFW_TIME_RECEIVED_PROPERTY = "sharkfw_timeReceived";

    private SemanticNet topics;
    private PeerTaxonomy peers;
    private SpatialSTSet locations;
    private TimeSTSet times;
    private PeerSemanticTag owner;
    private Knowledge knowledge;
    private FragmentationParameter[] defaultFP;
    
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
    
    /**
     * It must be proteced - later FSKB get confuses when using FSKnowledge. 
     * It's bit messy.
     * @param knowledge 
     */
    protected void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
        this.knowledge.addListener(this);
    }
    
    ////////////////////////////////////////////////////////////
    //           some additional methods                      //
    ////////////////////////////////////////////////////////////
    
    public STSet getAnySTSet() throws SharkKBException {
        return this.createAnySTSet(SharkCS.DIM_PEER);
    }
    
    public STSet getAnyGeoSTSet() throws SharkKBException {
        return this.createAnySTSet(SharkCS.DIM_LOCATION);
    }
    
    public STSet getAnyTimeSTSet() throws SharkKBException {
        return this.createAnySTSet(SharkCS.DIM_TIME);
    }
    
    public STSet getAnyDirectionSTSet() throws SharkKBException {
        return this.createAnySTSet(SharkCS.DIM_DIRECTION);
    }
    
    public STSet createAnySTSet(int dimension) throws SharkKBException {
        return null;
    }
    
    ////////////////////////////////////////////////////////////////////
    //              implementations by delegate                       //
    ////////////////////////////////////////////////////////////////////
    
    @Override
    public void setOwner(PeerSemanticTag owner) {
        
        // remove listener from old owner
        if(this.owner != null && this.owner instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) this.owner;
            st.setListener(null);
        }
        
        try {
            // owner already known in kb?
            this.owner = (PeerSemanticTag) this.getPeerSTSet().merge(owner);
        } catch (SharkKBException ex) {
            // very strange
            L.e("cannot save kb owner in kb - go ahead with remote owner", this);
            this.owner = owner;
        }
        
        this.setOwnerListener();
        
        this.persist();
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.owner;
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
        
        try {
            return this.createInterest(topicsSet, this.owner,
                    peersSet, peersSet, this.times,
                    this.locations, SharkCS.DIRECTION_INOUT);
        } catch (SharkKBException ex) {
            // never happens.
        }
        
        return null;
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

    /**
     * Iterats context points. If a perfect match is made - this cp ist returned.
     * This methode should be reimplemented in deriving classes. This implementation
     * has a horrible performance.
     * 
     * @param coordinates
     * @return
     * @throws SharkKBException 
     */
    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) 
            throws SharkKBException {
        
        Enumeration<ContextPoint> cpEnum = this.knowledge.contextPoints();
        
        while(cpEnum.hasMoreElements()) {
            ContextPoint cp = cpEnum.nextElement();
            ContextCoordinates co = cp.getContextCoordinates();
            
            if(AbstractSharkKB.exactMatch(co, coordinates)) {
                return cp;
            }
        }
        
        return null;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        SemanticTag st = this.getTopicSTSet().createSemanticTag(name, sis);
        this.notifySemanticTagCreated(st);
        return st;
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si});
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        PeerSemanticTag pst = this.getPeerSTSet().createPeerSemanticTag(name, sis, addresses);
        this.notifyPeerCreated(pst);
        return pst;
    }
    
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, new String[] {address});
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, sis, new String[] {address});
    }
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, addresses);
    }
    
    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException {
        SpatialSemanticTag sst = this.getSpatialSTSet().createSpatialSemanticTag(name, sis, (Double[][]) null);
        this.notifyLocationCreated(sst);
        return sst;
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        SpatialSemanticTag sst = this.getSpatialSTSet().createSpatialSemanticTag(name, sis, geom);
        this.notifyLocationCreated(sst);
        return sst;
    }
    
    @Override
    public TimeSemanticTag createTimeSemanticTag(String name, String[] sis) throws SharkKBException {
        TimeSemanticTag tst = this.getTimeSTSet().createTimeSemanticTag(name, sis);
        this.notifyTimeCreated(tst);
        return tst;
    }
    
    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        TimeSemanticTag tst = this.getTimeSTSet().createTimeSemanticTag(from, duration);
        this.notifyTimeCreated(tst);
        return tst;
    }
    
    @Override
    abstract public ContextCoordinates createContextCoordinates(SemanticTag topic, 
        PeerSemanticTag originator, PeerSemanticTag peer, 
        PeerSemanticTag remotePeer, TimeSemanticTag time, 
        SpatialSemanticTag location, int direction) 
            throws SharkKBException;

    @Override
    abstract public ContextPoint createContextPoint(ContextCoordinates coordinates) 
            throws SharkKBException;
    
    protected void addContextPoint(ContextPoint cp) throws SharkKBException {
        this.knowledge.addContextPoint(cp);
    }
    
    protected Knowledge getKnowledge() {
        return this.knowledge;
    }
    
    /**
     * creates an empty / any interest
     * @return 
     */
    @Override
    abstract public Interest createInterest() throws SharkKBException;
    
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
     * @return
     * @throws SharkKBException 
     */
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        Iterator<ContextPoint> iterCPs = this.contextPoints(cs, matchAny);
        if(iterCPs == null) return null;
        
        // else
        return new Iterator2Enumeration(iterCPs);
    }
    
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        if(cs == null) return null;
        
        HashSet<ContextPoint> result = new HashSet<ContextPoint>();

        HashSet<ContextCoordinates> coo = this.possibleCoordinates(cs);

        if(coo == null) return null;

        Iterator<ContextCoordinates> cooIter = coo.iterator();
        while(cooIter.hasNext()) {

            // next possible coordinate
            ContextCoordinates co = cooIter.next();

            if(!matchAny) { // exact match
                ContextPoint cp = this.getContextPoint(co);
                if(cp != null) {
                    // copy cp
                    result.add(cp);
                }
            } else { // matchAny - find all matching cps.
                Enumeration<ContextPoint> cpEnum = this.knowledge.contextPoints();

                while(cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();

                    if(SharkCSAlgebra.identical(cp.getContextCoordinates(), co)) {
                        result.add(cp);
                    }
                }
            }
        }

        if(result.isEmpty()) return null;
        
        return result.iterator();
    }
    
    
    public static ContextCoordinates getAnyCoordinates() {
        return InMemoSharkKB.createInMemoContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
    }
  /**
   * Returns enumeration of all context points. This actually is the same as
   * getContextPoints with an context space covering anything - which is technically 
   * a null reference. 
   * 
   * Use this methode very carefully. It produces a complete knowledge base dump.
   * This can be a lot.
   * 
   * @return
   * @throws SharkKBException 
   */
    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        ContextCoordinates cc = AbstractSharkKB.getAnyCoordinates();
        return this.getContextPoints(cc);
    }
    
    public HashSet<ContextCoordinates> possibleCoordinates(SharkCS cs) throws SharkKBException {
        if(cs == null) return null;
        HashSet<ContextCoordinates> protoCoo = new HashSet<ContextCoordinates>();
        
        // create first prototype with direction and owner
        if(cs.getDirection() == SharkCS.DIRECTION_INOUT) {
            // two additional coordinates
            protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), 
                    null, null, null, null, SharkCS.DIRECTION_IN));
            protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), 
                    null, null, null, null, SharkCS.DIRECTION_OUT));
        }
        
        protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), 
                null, null, null, null, cs.getDirection()));
        
        // no combine with other dimensions
        protoCoo = this.coordCombination(protoCoo, cs.getTopics(), SharkCS.DIM_TOPIC);
        protoCoo = this.coordCombination(protoCoo, cs.getPeers(), SharkCS.DIM_PEER);
        protoCoo = this.coordCombination(protoCoo, cs.getRemotePeers(), SharkCS.DIM_REMOTEPEER);
        protoCoo = this.coordCombination(protoCoo, cs.getTimes(), SharkCS.DIM_TIME);
        protoCoo = this.coordCombination(protoCoo, cs.getLocations(), SharkCS.DIM_LOCATION);
        
        return protoCoo;
    }
    
    private HashSet<ContextCoordinates> coordCombination(HashSet<ContextCoordinates> protoCoo, 
            STSet set, int dim) throws SharkKBException 
    {
        
        if(SharkCSAlgebra.isAny(set)) return protoCoo;
        
        set.setEnumerateHiddenTags(true);
        Enumeration<SemanticTag> tagEnum = set.tags();
        
        if(tagEnum == null || !tagEnum.hasMoreElements()) return protoCoo;
        
        HashSet<ContextCoordinates> result = new HashSet<ContextCoordinates>();
        
        while(tagEnum.hasMoreElements()) {
            SemanticTag tag = tagEnum.nextElement();
            
            // combine with existing
            Iterator<ContextCoordinates> cooIter = protoCoo.iterator();
            while(cooIter.hasNext()) {
            
                ContextCoordinates oldCC = cooIter.next();
                
                SemanticTag topic = oldCC.getTopic();
                PeerSemanticTag originator = oldCC.getOriginator();
                PeerSemanticTag peer = oldCC.getPeer();
                PeerSemanticTag remotePeer = oldCC.getRemotePeer();
                TimeSemanticTag time = oldCC.getTime();
                SpatialSemanticTag location = oldCC.getLocation();
                int direction = oldCC.getDirection();
                
                switch(dim) {
                    case SharkCS.DIM_TOPIC: topic = tag; break;
                    case SharkCS.DIM_PEER: peer = (PeerSemanticTag)tag; break;
                    case SharkCS.DIM_REMOTEPEER: remotePeer = (PeerSemanticTag)tag; break;
                    case SharkCS.DIM_TIME: time = (TimeSemanticTag)tag; break;
                    case SharkCS.DIM_LOCATION: location = (SpatialSemanticTag)tag; break;
                }
                
                ContextCoordinates newCC = this.createContextCoordinates(
                        topic, originator, peer, remotePeer, time, 
                        location, direction);
                
                result.add(newCC);
            }
        }
        
        return result;
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
            throw new SharkKBException("topic semantic tag set is not a taxonomy");
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
  
    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.getSemanticTag(new String[] {si});
    }
  
  
    @Override
    public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
        return this.getPeerSTSet().getSemanticTag(sis);
    }
    
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

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        EnumerationChain<SemanticTag> tagEnum = new EnumerationChain<SemanticTag>();
        
        tagEnum.addEnumeration(this.getTopicSTSet().tags());
        tagEnum.addEnumeration(this.getPeerSTSet().tags());
        tagEnum.addEnumeration(this.getSpatialSTSet().tags());
        tagEnum.addEnumeration(this.getTimeSTSet().tags());
        
        return tagEnum;
    }
    
    @Override
    public Iterator<SemanticTag> getTags() throws SharkKBException {
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
    
    @Override
    public void removeSemanticTag(SemanticTag st) throws SharkKBException {
        if(st == null) return;
        
        this.removeSemanticTag(st.getSI());
    }    
    
    private void removeSemanticTag(STSet set, String[] sis) throws SharkKBException {
        SemanticTag tag = set.getSemanticTag(sis);
        if(tag != null) set.removeSemanticTag(tag);
        
        this.notifySemanticTagRemoved(tag);
    }
    
    /**
     * Checks wether to coordinates are exactly the same. Means, that two concept
     * are NOT the same if one is ANY and the other is something else. Don't
     * mess up this methode with a similiar one in Shark algebra. If you don't
     * see the difference use shark algebra.
     * 
     * @param co
     * @param coordinates
     * @return 
     */
    public static boolean exactMatch(ContextCoordinates cc1, ContextCoordinates cc2) {
        // if references are the same they are identical
        if(cc1 == cc2) return true;
        
        if ((cc1 == null) || (cc2 == null)) {	// one of them is null, we can't compare 
        	return false;
        }

        // direction
        // Bugfix to avoid recreation/duplicate profiles, it was triggered by OpenCV() or anything similar which led to exact direction match failed
        switch (cc1.getDirection()) {
        	case SharkCS.DIRECTION_OUT:
                switch (cc2.getDirection()) {
                	case SharkCS.DIRECTION_IN:      /* OUT/IN, incompatible */		
                		return false;
                		
                	case SharkCS.DIRECTION_INOUT:   /* OUT/INOUT */		
                		L.w("relax direction match");
                		// fall thru
                	default:						/* OUT/OUT is OK */ 							
                		break;     		
                }
        		break;
        		
        	case SharkCS.DIRECTION_IN:
                switch (cc2.getDirection()) {
            		case SharkCS.DIRECTION_OUT:     /* IN/OUT, incompatible */			
            			return false;	
            		case SharkCS.DIRECTION_INOUT:   /* IN/INOUT */		
                		L.w("relax direction match");
                		// fall thru
            		default:						/* IN/IN  is OK */ 			
            			break;    		
                }
        		break;
        		
        	case SharkCS.DIRECTION_INOUT:				
                switch (cc2.getDirection()) {
            		case SharkCS.DIRECTION_OUT:     /* INOUT/OUT */
            		case SharkCS.DIRECTION_IN:     	/* INOUT/IN */
            			L.w("relax direction match");
            			// fall thru
              		case SharkCS.DIRECTION_INOUT:   /* INOUT/INOUT is OK */				
            			break;    		
                }
        		break;       		
        }

            
            // originator
            if(AbstractSharkKB.exactMatch(cc1.getOriginator(),cc2.getOriginator())) {
                
                // topic
                if(AbstractSharkKB.exactMatch(cc1.getTopic(),cc2.getTopic())) {
                    
                    // peer
                    if(AbstractSharkKB.exactMatch(cc1.getPeer(),cc2.getPeer())) {
                        
                        // remote peer
                        if(AbstractSharkKB.exactMatch(cc1.getRemotePeer(),cc2.getRemotePeer())) {
                            
                            // location
                            if(AbstractSharkKB.exactMatch(cc1.getLocation(),cc2.getLocation())) {
                                
                                // time
                                if(AbstractSharkKB.exactMatch(cc1.getTime(),cc2.getTime())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        
        return false;
    }
    
    /**
     * Checks wether to tags are exactly the same. Means, that two concept
     * are NOT the same if one is ANY and the other is something else. Don't
     * mess up this methode with a similiar one in Shark algebra. If you don't
     * see the difference use shark algebra.
     * 
     */
    public static boolean exactMatch(SemanticTag s1, SemanticTag s2) {
        // same objects - ok
        if(s1 == s2) return true;
        
        // both any - ok
        if(SharkCSAlgebra.isAny(s1) && SharkCSAlgebra.isAny(s2)) return true;

        // just one is any - wrong
        if(SharkCSAlgebra.isAny(s1) || SharkCSAlgebra.isAny(s2)) return false;
        
        // both not null and both not any
        return SharkCSAlgebra.identical(s1, s2);
        
    }
    
    public static final String OWNER = "AbstractKB_owner";
    public static final String DEFAULT_FP = "AbstractKB_defaultFP";

    @Override
    public void persist() {
        super.persist();

        // owner
        if(this.owner != null) {
            String ownerSIString = Util.array2string(this.owner.getSI());
            if(ownerSIString != null && ownerSIString.length() > 0) {
                this.setSystemProperty(OWNER, ownerSIString);
            }
        }
        
        // default fp
        if(this.defaultFP != null) {
            String defaultFPString = Util.fragmentationParameter2string(defaultFP);
            this.setSystemProperty(DEFAULT_FP, defaultFPString);
        }
    }
    
    private void setOwnerListener() {
        if(this.owner instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) this.owner;
            
            st.setListener(this);
        }
    }
    
    @Override
    public void refreshStatus() {
        super.refreshStatus();
        
        // owner
        String ownerSIString = this.getSystemProperty(OWNER);
        if(ownerSIString != null) {
            String[] ownerSIs = Util.string2array(ownerSIString);
            try {
                PeerSemanticTag storedOwner = this.getPeerSemanticTag(ownerSIs);
                if(storedOwner != null) {
                    this.owner = storedOwner;
                    
                    // listen to changed in owner
                    this.setOwnerListener();
                }
            } catch (SharkKBException ex) {
                L.w("cannot find owner tag while restoring kb status from external memory", this);
            }
        }
        
        // default fp
        String defaultFPValue = this.getSystemProperty(DEFAULT_FP);
        if(defaultFPValue != null) {
            this.defaultFP = Util.string2fragmentationParameter(defaultFPValue);
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

    void siChanged(AbstractSemanticTag aThis) {
        // this call can only be made by owner
        
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
