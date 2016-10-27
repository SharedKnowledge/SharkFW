package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.*;
import net.sharkfw.asip.engine.serializer.XMLSerializer;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformationCoordinates;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoTaxonomy;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.Iterator2Enumeration;

import java.util.*;

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
    protected SemanticNet types;
    protected PeerTaxonomy peers;
    protected SpatialSTSet locations;
    protected TimeSTSet times;
    protected PeerSemanticTag owner;
    protected Knowledge knowledge;
    //private ASIPKnowledge asipKnowledge;
    protected FragmentationParameter[] standardFP;
    
    protected AbstractSharkKB() {}
    
    protected AbstractSharkKB(SemanticNet topics, SemanticNet types, 
            PeerTaxonomy peers, SpatialSTSet locations, TimeSTSet times) 
    {
        
        this.topics = topics;
        this.types = types;
        this.peers = peers;
        this.locations = locations;
        this.times = times;
        
        topics.addListener(this);
        
        if(types != null) {
            types.addListener(this);
        }
        
        peers.addListener(this);
        locations.addListener(this);
        times.addListener(this);
        
    }
    
    protected AbstractSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times,
                 Knowledge k) throws SharkKBException {
        this(topics, null, peers, locations, times, k);
        
    }
    
    protected AbstractSharkKB(SemanticNet topics, SemanticNet types, 
            PeerTaxonomy peers, SpatialSTSet locations, TimeSTSet times,
                 Knowledge k) throws SharkKBException {
        
        this(topics, types, peers, locations, times);
        this.knowledge = k;
        this.knowledge.addListener(this);
    }    
    
    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.asASIPInterest();
    }
     
    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        STSet topicsSet = this.topics.asSTSet();
        PeerSTSet peersSet;
        try {
            peersSet = this.peers.asPeerSTSet();
        } catch (SharkKBException ex) {
            return null;
        }
        
        // hide semantic tags
        this.locations.setEnumerateHiddenTags(true);
        this.times.setEnumerateHiddenTags(true);
        topicsSet.setEnumerateHiddenTags(true);
        peersSet.setEnumerateHiddenTags(true);
        
        return InMemoSharkKB.createInMemoASIPInterest(
                topicsSet, 
                this.types, 
                this.owner, 
                peersSet, 
                peersSet, 
                this.times, 
                this.locations, 
                ASIPSpace.DIRECTION_INOUT);
    }
     
    /**
     * @deprecated 
     * @return 
     */
    @Override
    public SharkCS asSharkCS() {
        return this.asInterest();
    }

    /**
     * @deprecated 
     * @return 
     */
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
                    this.locations, ASIPSpace.DIRECTION_INOUT);
            
//            return this.createInterest(topicsSet, this.owner,
//                    peersSet, peersSet, this.times,
//                    this.locations, ASIPSpace.DIRECTION_INOUT);
//        } catch (SharkKBException ex) {
//            // never happens.
//        }
        
//        return null;
    }    
    
//    /**
//     * That's a default implementation. It splits the information space
//     * into information points and merges each point into the KB.
//     * 
//     * That's probably sufficient for an in memo implementation. 
//     * Reasonable KB should be able to offer a better solution.
//     * 
//     * @param iSpace
//     * @throws SharkKBException 
//     */
//    public void mergeInformationSpace(ASIPInformationSpace iSpace) 
//            throws SharkKBException {
//        
//        ASIPSpace asipSpace = iSpace.getASIPSpace();
//        if(asipSpace == null) return;
//        
//        // create coordinates for all points inside that space
//        HashSet<InformationCoordinates> infoCoordSet = 
//                this.possibleInformationCoordinates(asipSpace);
//        
//        if(infoCoordSet == null || infoCoordSet.isEmpty()) return;
//        
//        /* create an infoPoint for each coordinate and add
//        information - that is the dreadful part of that algorithm:
//        it produces redundancy
//         */
//        Iterator<InformationCoordinates> iCoordIter = infoCoordSet.iterator();
//        while(iCoordIter.hasNext()) {
//            InformationCoordinates ic = iCoordIter.next();
//            InformationPoint ip = this.createInformationPoint(ic);
//            
//            // no copy information
//            Iterator<Information> infoIter = iSpace.informations();
//            while(infoIter != null && infoIter.hasNext()) {
//                Information info = infoIter.next();
//                if(info != null) {
//                    ip.addInformation(info);
//                }
//            }
//        }
//    }    
    
    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, 
            FragmentationParameter[] backgroundFP, Knowledge knowledge, 
            boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        
        return null;
        // TODO
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                ASIP extraction support                              //
    /////////////////////////////////////////////////////////////////////////
    
   /**
     * Most simple version of extraction: Zero fragmentation parameter are used,
     * no recipient or groups are used
     * @param context
     * @return
     * @throws SharkKBException 
     */
    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        FragmentationParameter[] fps = FragmentationParameter.getZeroFPs();
        
        return this.extract(target, context, fps, false, null);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) 
            throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return this.extract(target, context, fp, false, null);
    }

    @Override
    public Knowledge extract(ASIPSpace context, 
            FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) 
                throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return this.extract(target, context, backgroundFP, true, recipient);
    }
    
    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, 
            boolean cutGroups) 
                throws SharkKBException {
        
        SharkKB target = new InMemoSharkKB();
        
        return this.extract(target, context, backgroundFP, true, null);
    }
    
    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, 
            boolean cutGroups, PeerSemanticTag recipient) 
                throws SharkKBException {
        
        // TODO
        return null;
    }
    
    protected Knowledge getKnowledge() {
        return this.knowledge;
    }
    
    protected ASIPKnowledge getASIPKnowledge() {
        return this.knowledge;
    }
    
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
     * @deprecated 
     */
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        return this.getContextPoints(cs, true);
    }
    
    /**
     * @param cs
     * @return
     * @throws SharkKBException 
     * @deprecated 
     */
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
    
    public InformationCoordinates createInformationCoordinates(
            SemanticTag topic, SemanticTag type, 
            PeerSemanticTag approver, PeerSemanticTag sender, 
            PeerSemanticTag receiver, TimeSemanticTag time, 
            SpatialSemanticTag location, int direction) 
            throws SharkKBException {
        
        SemanticTag to = this.getTopicSTSet().merge(topic);
        SemanticTag ty = this.getTypeSTSet().merge(type);
        PeerSTSet peerDimension = this.getPeerSTSet();
        PeerSemanticTag a = (PeerSemanticTag) peerDimension.merge(approver);
        PeerSemanticTag s = (PeerSemanticTag) peerDimension.merge(sender);
        PeerSemanticTag r = (PeerSemanticTag) peerDimension.merge(receiver);
        TimeSemanticTag ti = (TimeSemanticTag) this.getTimeSTSet().merge(time);
        SpatialSemanticTag lo = (SpatialSemanticTag) this.getSpatialSTSet().merge(location);
        
        return new InMemoInformationCoordinates(
                to, ty, a, s, r, ti, lo, direction);
    }
    
    HashSet<InformationCoordinates> possibleInformationCoordinates(ASIPSpace space) throws SharkKBException {
        if (space == null) {
            return null;
        }
        HashSet<InformationCoordinates> icList = new HashSet<>();
        
        /* create first prototype with direction and owner: 
           (-,-,owner,-,-,-,-,direction)
        */
        
        if (space.getDirection() == ASIPSpace.DIRECTION_INOUT) {
            /* if INOUT: we already have two points: 
              (-,-,owner,-,-,-,-,IN) and
              (-,-,owner,-,-,-,-,OUT)
            */
            
            // topic, type, approver, sender, receiver, time, location, direction
            
            // add prototype OUT kepInterest
            icList.add(this.createInformationCoordinates(null, null, null, 
                    owner, null, null, null, ASIPSpace.DIRECTION_IN));
            
            // add prototype OUT kepInterest
            icList.add(this.createInformationCoordinates(null, null, null, 
                    owner, null, null, null, ASIPSpace.DIRECTION_OUT));
            
        }
        
        // in any case - add prototyp with original direction
        icList.add(this.createInformationCoordinates(null, null, null, 
                    owner, null, null, null, space.getDirection()));
        
        /* now combine with other dimensions 
           sender and direction are already combined
        */
        
        // combine topic dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getTopics(), ASIPSpace.DIM_TOPIC);
        
        // combine type dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getTypes(), ASIPSpace.DIM_TYPE);
        
        // combine approver dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getApprovers(), ASIPSpace.DIM_APPROVERS);
        
        // combine receiver dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getReceivers(), ASIPSpace.DIM_RECEIVER);
        
        // combine time dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getTimes(), ASIPSpace.DIM_TIME);
        
        // combine location dimension
        icList = this.informationCoordinatesCombination(icList, 
                space.getLocations(), ASIPSpace.DIM_LOCATION);
        
        return icList;
    }

    /**
     * @param space
     * @return
     * @throws SharkKBException 
     * @deprecated 
     */
    HashSet<ContextCoordinates> possibleCoordinates(ASIPSpace space) throws SharkKBException {
        if (space == null) {
            return null;
        }
        HashSet<ContextCoordinates> ccList = new HashSet<>();
        
        // create first prototype with direction and owner
        if (space.getDirection() == ASIPSpace.DIRECTION_INOUT) {
            // if INOUT: there are two additional coordinates:

            //topic,originator,peer,remotepeer,time,location,direction
            // we match LASP sender with KEP peer
            ccList.add(this.createContextCoordinates(null, null, space.getSender(), null, null, null, ASIPSpace.DIRECTION_IN));
            ccList.add(this.createContextCoordinates(null, null, space.getSender(), null, null, null, ASIPSpace.DIRECTION_OUT));
        }
        
        ccList.add(this.createContextCoordinates(null, null, space.getSender(), null, null, null, space.getDirection()));
        
        // no combine with other dimensions
        
        // LASP topics go with KEP topics
        ccList = this.coordCombination(ccList, space.getTopics(), ASIPSpace.DIM_TOPIC);
        
        // LASP types are ignored here
        
        // LASP approvers matches with KEP originator
        ccList = this.coordCombination(ccList, space.getApprovers(), ASIPSpace.DIM_APPROVERS);
        
        // LASP receivers go with KEP remote peers
        ccList = this.coordCombination(ccList, space.getReceivers(), ASIPSpace.DIM_RECEIVER);
        
        // time and location is the same in both protocols
        ccList = this.coordCombination(ccList, space.getTimes(), ASIPSpace.DIM_TIME);
        ccList = this.coordCombination(ccList, space.getLocations(), ASIPSpace.DIM_LOCATION);
        
        return ccList;
    }

    protected HashSet informationCoordinatesCombination(
            HashSet<InformationCoordinates> protoCoo, STSet set, int dim) 
            throws SharkKBException {
        
        // if stset is empty - no new combination can be created - done
        if (SharkCSAlgebra.isAny(set)) {
            return protoCoo;
        }
        
        set.setEnumerateHiddenTags(true);
        
        // iterate all tags in set - if empty - we are done.
        Iterator<SemanticTag> stTags = set.stTags();
        if (stTags == null || !stTags.hasNext()) {
            return protoCoo;
        }
        
        // lets combine - create container for results first
        HashSet<InformationCoordinates> result = new HashSet<>();
        while (stTags.hasNext()) {
            // take next tag to combine
            SemanticTag tag = stTags.next();
            
            // if this tag is any - continue: no need to create combinations.
            if(SharkCSAlgebra.isAny(tag)) continue;
            
            // create new combination which each already existing coordinate
            Iterator<InformationCoordinates> cooIter = protoCoo.iterator();
            while (cooIter.hasNext()) {
                InformationCoordinates oldCC = cooIter.next();
                
                // take all eight parts from coordinate:
                // topic,type,approver,sender,receiver,time,location,direction
                SemanticTag topic = oldCC.getTopic();
                SemanticTag type = oldCC.getType();
                PeerSemanticTag approver = oldCC.getApprover();
                PeerSemanticTag sender = oldCC.getSender();
                PeerSemanticTag receiver = oldCC.getReceiver();
                TimeSemanticTag time = oldCC.getTime();
                SpatialSemanticTag location = oldCC.getLocation();
                int direction = oldCC.getDirection();
                
                /* now: what dimension do we combine ?
                   set our tag instead of the odl one (which is presumably null)
                */
                switch (dim) {
                    case ASIPSpace.DIM_TOPIC:
                        topic = tag;
                        break;
                    case ASIPSpace.DIM_TYPE:
                        type = tag;
                        break;
                    case ASIPSpace.DIM_APPROVERS:
                        approver = (PeerSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_SENDER:
                        sender = (PeerSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_RECEIVER:
                        receiver = (PeerSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_TIME:
                        time = (TimeSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_LOCATION:
                        location = (SpatialSemanticTag) tag;
                        break;
                }
                
                // we have a new coordinate - create an object
                InformationCoordinates newIC = 
                        this.createInformationCoordinates(topic, type, approver, 
                                sender, receiver, time, location, direction);
                
                // add to list
                result.add(newIC);
                
                // combine with next existing coordinate
            }
        }
        return result;
    }

    /**
     * @deprecated 
     * @param protoCoo
     * @param set
     * @param dim
     * @return
     * @throws SharkKBException 
     */
    protected HashSet coordCombination(HashSet<ContextCoordinates> protoCoo, STSet set, int dim) throws SharkKBException {
        if (SharkCSAlgebra.isAny(set)) {
            return protoCoo;
        }
        set.setEnumerateHiddenTags(true);
        Enumeration<SemanticTag> tagEnum = set.tags();
        if (tagEnum == null || !tagEnum.hasMoreElements()) {
            return protoCoo;
        }
        HashSet<ContextCoordinates> result = new HashSet<>();
        while (tagEnum.hasMoreElements()) {
            SemanticTag tag = tagEnum.nextElement();
            // combine with existing
            Iterator<ContextCoordinates> cooIter = protoCoo.iterator();
            while (cooIter.hasNext()) {
                ContextCoordinates oldCC = cooIter.next();
                SemanticTag topic = oldCC.getTopic();
                PeerSemanticTag originator = oldCC.getOriginator();
                PeerSemanticTag peer = oldCC.getPeer();
                PeerSemanticTag remotePeer = oldCC.getRemotePeer();
                TimeSemanticTag time = oldCC.getTime();
                SpatialSemanticTag location = oldCC.getLocation();
                int direction = oldCC.getDirection();
                switch (dim) {
                    case ASIPSpace.DIM_TOPIC:
                        topic = tag;
                        break;
                    case ASIPSpace.DIM_SENDER:
                        peer = (PeerSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_RECEIVER:
                        remotePeer = (PeerSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_TIME:
                        time = (TimeSemanticTag) tag;
                        break;
                    case ASIPSpace.DIM_LOCATION:
                        location = (SpatialSemanticTag) tag;
                        break;
                }
                ContextCoordinates newCC = this.createContextCoordinates(topic, originator, peer, remotePeer, time, location, direction);
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
    
    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return this.types;
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        if(this.types instanceof SemanticNet) {
            return (SemanticNet) this.types;
        } else {
            throw new SharkKBException("type semantic tag set is not a semantic network");
        }
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        if(this.types instanceof Taxonomy) {
            return (Taxonomy) this.types;
        } else {
            if(this.types instanceof SemanticNet) {
                return new InMemoTaxonomy((SemanticNet)this.types);
            } else {
                throw new SharkKBException("types semantic tag set is not a taxonomy and cannot be used as taxonomy");
            }
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
    
    /**
     * 
     * @param as
     * @return
     * @throws SharkKBException 
     * @deprecated 
     */
    @Override
    public Interest contextualize(SharkCS as) throws SharkKBException {
        return this.contextualize(as, this.getStandardFPSet());
    }
    
    /**
     * Should use methods in the algebra!
     * @param context
     * @param fp
     * @return
     * @throws SharkKBException 
     * @deprecated 
     */
    @Override
    public Interest contextualize(SharkCS context, FragmentationParameter[] fp) throws SharkKBException {
        Interest result = new net.sharkfw.knowledgeBase.inmemory.InMemoInterest();
        
//        SharkCSAlgebra.contextualize(result, this.asSharkCS(), context, fp);
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
        this.standardFP = fps;
        this.persist();
    }

    /**
     * Use getDefaultFPSet instead.
     * @return 
     * @deprecated
     */
    @Override
    public FragmentationParameter[] getStandardFPSet() {
        if(this.standardFP == null) {
            FragmentationParameter topicsFP = new FragmentationParameter(false, true, 2);
            FragmentationParameter peersFP = new FragmentationParameter(true, false, 2);
            FragmentationParameter restFP = new FragmentationParameter(false, false, 0);
            
            this.standardFP = new FragmentationParameter[ASIPSpace.MAXDIMENSIONS];
            
            this.standardFP[ASIPSpace.DIM_TOPIC] = topicsFP;
            this.standardFP[ASIPSpace.DIM_TYPE] = topicsFP;
            this.standardFP[ASIPSpace.DIM_APPROVERS] = peersFP;
            this.standardFP[ASIPSpace.DIM_SENDER] = peersFP;
            this.standardFP[ASIPSpace.DIM_RECEIVER] = peersFP;
            this.standardFP[ASIPSpace.DIM_TIME] = restFP;
            this.standardFP[ASIPSpace.DIM_LOCATION] = restFP;
            this.standardFP[ASIPSpace.DIM_DIRECTION] = restFP;
            
        }
        
        return this.standardFP;
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
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.notifySemanticTagChanged(tag);
    }

    public Iterator getTags() throws SharkKBException {
        EnumerationChain<SemanticTag> tagEnum = new EnumerationChain<SemanticTag>();
        tagEnum.addEnumeration(this.getTopicSTSet().tags());
        tagEnum.addEnumeration(this.getPeerSTSet().tags());
        tagEnum.addEnumeration(this.getSpatialSTSet().tags());
        tagEnum.addEnumeration(this.getTimeSTSet().tags());
        return tagEnum;
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

    protected void notifySemanticTagChanged(SemanticTag tag) {
        Iterator<KnowledgeBaseListener> listenerIterator = this.listeners.iterator();
        while(listenerIterator.hasNext()) {
            KnowledgeBaseListener listener = listenerIterator.next();
            listener.tagChanged(tag);
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
     * @return -1 of no such kepInterest in in the list
     */
    private int findInterestIndex(SharkCS interest) throws SharkKBException {
        for(int index = 0; index < this.interestsList.size(); index++) {
            SharkCS next = this.interestsList.get(index);
            if(SharkCSAlgebra.identical(next, interest)) {
                return index;
            }
        }
        
        // no matching kepInterest found
        return -1;
    }
    
    /**
     * Saves this kepInterest into a list of interests
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
     * Removes this kepInterest from the storage
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
     * kepInterest storage
     * 
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        this.restoreInterestsFromProperties();
        return this.interestsList.iterator();
    }
    
    /**
     * That method should be overwritten. This default implementation assumes
     * that only points are stored in the KB. Removing a space is a multiple
     * call or removing information points. Other implementations might implement
     * information space directly and should overwrite that method.
     * 
     * @param cs
     * @throws SharkKBException 
     */
//    @Override
//    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {
//        if(space == null) return;
//        
//        HashSet<InformationCoordinates> possibleInfoCC = 
//                this.possibleInformationCoordinates(space);
//        
//        if(possibleInfoCC == null || possibleInfoCC.isEmpty()) return;
//        
//        // there are possible points
//        Iterator<InformationCoordinates> infoCCIter = possibleInfoCC.iterator();
//        
//        // remove each thinkable point
//        while(infoCCIter.hasNext()) {
//            InformationCoordinates ic = infoCCIter.next();
//            this.removeInformationPoint(ic);
//        }
//    }

    /**
     * 
     * @param cs
     * @return
     * @throws SharkKBException 
     */
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace cs) throws SharkKBException {
        return this.informationSpaces(cs, true);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return this.contextualize(as, this.getDefaultFPSet());
        
    }
    
    private FPSet defaultFPSet = null;

    public FPSet getDefaultFPSet() {
        if(this.defaultFPSet == null) {
            FragmentationParameter topicsFP = new FragmentationParameter(false, true, 2);
            FragmentationParameter typesFP = new FragmentationParameter(false, true, 2);
            FragmentationParameter peersFP = new FragmentationParameter(true, false, 2);
            FragmentationParameter restFP = FragmentationParameter.getZeroFP();
            
            this.defaultFPSet = new FPSet(topicsFP, typesFP, // topics and types
                    peersFP, peersFP, peersFP, // all peers same
                    restFP, restFP, restFP); // time / location / direction
            
        }
        
        return this.defaultFPSet;
    }

    /**
     *
     * @param as
     * @param fps
     * @return
     * @throws SharkKBException
     */
    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return SharkAlgebra.contextualize(this.asASIPSpace(), as, fps);
    }
    
    @Override
    public int getNumberInformation() throws SharkKBException {
        if(this.knowledge == null) return 0;
        return this.knowledge.getNumberInformation();
    }
    
    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this.knowledge.removeInformation(space);
    }

//    @Override
//    public Iterator<InformationPoint> informationPoints() throws SharkKBException {
//        return this.asipKnowledge.informationPoints();
//    }
    
    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return this.knowledge.informationSpaces();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this;
    }
    
    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return this.knowledge.mergeInformation(information, space);
    }
    
}
