package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.L;
import net.sharkfw.system.Util;

/**
 * @author mfi
 * @author thsc
 */
@SuppressWarnings("unchecked")
public class InMemoSharkKB extends AbstractSharkKB implements SharkKB, SystemPropertyHolder {
    
    ////////////////////////////////////////////////////////////////////////
    //                     in memo copies                                 //
    ////////////////////////////////////////////////////////////////////////
    public static SemanticTag createInMemoCopy(SemanticTag tag) {
        if(tag == null) { return null; }
        SemanticTag st = new InMemoSemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     * 
     * @param tag
     * @return 
     */
    public static SNSemanticTag createInMemoCopyToSNSemanticTag(SemanticTag tag) {
        if(tag == null) { return null; }
        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     * 
     * @param tag
     * @return 
     */
    public static TXSemanticTag createInMemoCopyToTXSemanticTag(SemanticTag tag) {
        if(tag == null) { return null; }
        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    public static SNSemanticTag createInMemoCopy(SNSemanticTag tag) {
        if(tag == null) { return null; }
        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    public static TXSemanticTag createInMemoCopy(TXSemanticTag tag) {
        if(tag == null) { return null; }
        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    public static PeerTXSemanticTag createInMemoCopy(PeerTXSemanticTag tag) {
        if(tag == null) { return null; }
        PeerTXSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    public static PeerSNSemanticTag createInMemoCopy(PeerSNSemanticTag tag) {
        if(tag == null) { return null; }
        PeerSNSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
        return st;
    }
    
    public static PeerSemanticTag createInMemoCopy(PeerSemanticTag pst) {
        if(pst == null) { return null; }
        PeerSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(pst.getName(), pst.getSI(), pst.getAddresses());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(pst, st);
        return st;
    }

    public static TimeSemanticTag createInMemoCopy(TimeSemanticTag tst) {
        if(tst == null) { return null; }
        TimeSemanticTag st = new InMemoTimeSemanticTag(tst.getFrom(), tst.getDuration());
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tst, st);
        return st;
    }

    private static String[] cloneSIs(String[] sis) {
      if(sis == null) {
        return null;
      }
      
      String[] newSIS = new String[sis.length];
      
      for(int i = 0; i < sis.length; i++) {
        newSIS[i] = new String(sis[i]);
      }
      
      return newSIS;
    }
    
    public static SharkGeometry createInMemoCopy(SharkGeometry geom) throws SharkKBException {
      if(geom == null) {
        return null;
      }
      
      return InMemoSharkGeometry.createGeomByEWKT(geom.getEWKT());
    }

    public static SpatialSemanticTag createInMemoCopy(SpatialSemanticTag sst) throws SharkKBException {
        if(sst == null) { return null; }
        // copy each part of original tag
        String name = new String(sst.getName());
        String[] sis = InMemoSharkKB.cloneSIs(sst.getSI());
        SharkGeometry geom;
        geom = InMemoSharkKB.createInMemoCopy(sst.getGeometry());
        
        InMemoSpatialSemanticTag st = new InMemoSpatialSemanticTag(name, sis, geom);
        Util.copyPropertiesFromPropertyHolderToPropertyHolder(sst, st);
        st.refreshStatus(); // important to refresh status right here - it extracts it geometry from properties
        return st;
    }

    ///////////////////////////////////////////////////////////////////
    //    allow creating standalone structures                       //
    ///////////////////////////////////////////////////////////////////

    /**
     * Create an in memory implementation of a semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @param name
     * @param sis
     * @return
     */
    public static SemanticTag createInMemoSemanticTag(String name, String[] sis) {
        return new InMemoSemanticTag(name, sis);
    }

    /**
     * creates an TST covering a period from
     * @param from Start time - milliseconds beginning from 1.1.1970
     * @param duration duration of period in milliseconds
     * @return 
     */
    public static TimeSemanticTag createInMemoTimeSemanticTag(long from, long duration) {
        return new InMemoTimeSemanticTag(from, duration);
    }

    public static Information createInMemoInformation() {
        return new InMemoInformation();
    }
    
    public static Knowledge createInMemoKnowledge(SharkVocabulary background) {
        return new InMemoKnowledge(background);
    }
    
    /**
     * Create an in memory implementation of a semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
    public static SemanticTag createInMemoSemanticTag(String name, String si) {
        return new InMemoSemanticTag(name, new String[] {si});
    }
    
    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String[] sis, 
            String[] addresses) {
        return new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
    }
    
    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String si, 
            String address) {
        
        return new InMemo_SN_TX_PeerSemanticTag(name, new String[] {si}, 
                new String[] {address});
    }
    
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static STSet createInMemoSTSet() {
        return new InMemoSTSet();
    }
    
    public static Knowledge createInMemoKnowledge() {
        return new InMemoKnowledge();
    }
    
    public static ContextPoint createInMemoContextPoint(ContextCoordinates cc) {
        return new InMemoContextPoint(cc);
    }
    
    ///////////////////////////////////////////////////////////////////
    //                       in memo copies                          //
    ///////////////////////////////////////////////////////////////////

    public static STSet createInMemoCopy(STSet stSet) throws SharkKBException {
        STSet copy = InMemoSharkKB.createInMemoSTSet();
        copy.merge(stSet);
        return copy;
    }
    
    public static Taxonomy createInMemoCopy(Taxonomy taxonomy) throws SharkKBException {
        Taxonomy copy = InMemoSharkKB.createInMemoTaxonomy();
        copy.merge(taxonomy);
        return copy;
    }
    
    public static SemanticNet createInMemoCopy(SemanticNet semanticNet) throws SharkKBException {
        SemanticNet copy = InMemoSharkKB.createInMemoSemanticNet();
        copy.merge(semanticNet);
        return copy;
    }
    
    public static PeerSTSet createInMemoCopy(PeerSTSet peerSTSet) throws SharkKBException {
        PeerSTSet copy = InMemoSharkKB.createInMemoPeerSTSet();
        copy.merge(peerSTSet);
        return copy;
    }
    
    public static PeerTaxonomy createInMemoCopy(PeerTaxonomy peerTaxonomy) throws SharkKBException {
        PeerTaxonomy copy = InMemoSharkKB.createInMemoPeerTaxonomy();
        copy.merge(peerTaxonomy);
        return copy;
    }
    
    public static SpatialSTSet createInMemoCopy(SpatialSTSet spatialSTSet) throws SharkKBException {
        SpatialSTSet copy = InMemoSharkKB.createInMemoSpatialSTSet();
        copy.merge(spatialSTSet);
        return copy;
    }
    
    public static TimeSTSet createInMemoCopy(TimeSTSet timeSTSet) throws SharkKBException {
        TimeSTSet copy = InMemoSharkKB.createInMemoTimeSTSet();
        copy.merge(timeSTSet);
        return copy;
    }
    
    @SuppressWarnings("unused")
    public static ContextCoordinates createInMemoCopy(ContextCoordinates cc) 
            throws SharkKBException {
        
        SemanticTag t;
        PeerSemanticTag p, rp, o;
        SpatialSemanticTag l;
        TimeSemanticTag ti;
        
        t = InMemoSharkKB.createInMemoCopy(cc.getTopic());
        p = InMemoSharkKB.createInMemoCopy(cc.getPeer());
        rp = InMemoSharkKB.createInMemoCopy(cc.getRemotePeer());
        o = InMemoSharkKB.createInMemoCopy(cc.getOriginator());
        l = InMemoSharkKB.createInMemoCopy(cc.getLocation());
        ti = InMemoSharkKB.createInMemoCopy(cc.getTime());
        
        ContextCoordinates copy = 
                InMemoSharkKB.createInMemoContextCoordinates(t, o, p, rp, ti, l, cc.getDirection());
        
        return copy;
    }
    
    private static Interest _createInMemoCopy(SharkCS interest) 
            throws SharkKBException {
        
        Interest i = InMemoSharkKB.createInMemoInterest();
        
        i.setTopics(InMemoSharkKB.createInMemoCopy(interest.getTopics()));
        
        i.setOriginator(InMemoSharkKB.createInMemoCopy(interest.getOriginator()));
        
        i.setPeers(InMemoSharkKB.createInMemoCopy(interest.getPeers()));

        i.setRemotePeers(InMemoSharkKB.createInMemoCopy(interest.getRemotePeers()));

        i.setTimes(InMemoSharkKB.createInMemoCopy(interest.getTimes()));
        
        i.setLocations(InMemoSharkKB.createInMemoCopy(interest.getLocations()));

        i.setDirection(interest.getDirection());
        
        return i;
    }
        
    public static Interest createInMemoCopy(Interest interest) 
            throws SharkKBException {
        return InMemoSharkKB._createInMemoCopy(interest);
    }
    
    public static Interest createInMemoCopy(SharkCS cs) 
            throws SharkKBException {
        
        if(cs == null) {
            throw new SharkKBException("cannot make in memo copy from null");
        }
        
        return InMemoSharkKB._createInMemoCopy(cs);
    }
    
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static SemanticNet createInMemoSemanticNet() {
        return new InMemoSemanticNet();
    }
    
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static Taxonomy createInMemoTaxonomy() {
        return new InMemoTaxonomy();
    }
    
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static PeerTaxonomy createInMemoPeerTaxonomy() {
        return new InMemoPeerTaxonomy();
    }

    /**
     * Create any spatial semantic tag. This tag has a geo location but no explicit 
     * meaning.
     * 
     * @param geometry
     * @return 
     */
    public static SpatialSemanticTag createInMemoSpatialSemanticTag(SharkGeometry geometry) {
        return new InMemoSpatialSemanticTag(geometry);
    }

    public static SpatialSemanticTag createInMemoSpatialSemanticTag(String name, String si[], SharkGeometry geom) {
        return new InMemoSpatialSemanticTag(name, si, geom);
    }
        
   /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static PeerSemanticNet createInMemoPeerSemanticNet() {
        return new InMemoPeerSemanticNet();
    }

    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static PeerSTSet createInMemoPeerSTSet() {
        return new InMemoPeerSTSet();
    }
    
    public static Interest createInMemoInterest(STSet topics, 
            PeerSemanticTag originator, PeerSTSet peers, PeerSTSet remotePeers, 
            TimeSTSet times, SpatialSTSet locations, int direction) {
        
        return new InMemoInterest(topics, originator, peers, remotePeers, 
                times, locations, direction);
    }
    
    public static TimeSTSet createInMemoTimeSTSet() {
        return new InMemoTimeSTSet();
    }
    
    public static SpatialSTSet createInMemoSpatialSTSet() {
        return new InMemoSpatialSTSet();
    }
    
    public static ContextCoordinates createInMemoContextCoordinates(
            SemanticTag topic, 
            PeerSemanticTag originator,
            PeerSemanticTag peer,
            PeerSemanticTag remotePeer,
            TimeSemanticTag time,
            SpatialSemanticTag location,
            int direction) { {
                
            }
        return new InMemoContextCoordinates(topic, originator, peer,
                remotePeer, time, location, direction);
    }

    public static Interest createInMemoInterest() {
        return new InMemoInterest();
    }
    ///////////////////////////////////////////////////////////////////////////
    //                 actual kb implementation starts here                  //
    ///////////////////////////////////////////////////////////////////////////

    public static ContextCoordinates getAnyCoordinates() {
        return InMemoSharkKB.createInMemoContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
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
        if (cc1 == cc2) {
            return true;
        }
        if ((cc1 == null) || (cc2 == null)) {
            // one of them is null, we can't compare
            return false;
        }
        // direction
        // Bugfix to avoid recreation/duplicate profiles, it was triggered by OpenCV() or anything similar which led to exact direction match failed
        switch (cc1.getDirection()) {
            case SharkCS.DIRECTION_OUT:
                switch (cc2.getDirection()) {
                    case SharkCS.DIRECTION_IN:
                        /* OUT/IN, incompatible */
                        return false;
                    case SharkCS.DIRECTION_INOUT:
                        /* OUT/INOUT */
                        L.w("relax direction match");
                // fall thru
                    default:
                        /* OUT/OUT is OK */
                        break;
                }
                break;
            case SharkCS.DIRECTION_IN:
                switch (cc2.getDirection()) {
                    case SharkCS.DIRECTION_OUT:
                        /* IN/OUT, incompatible */
                        return false;
                    case SharkCS.DIRECTION_INOUT:
                        /* IN/INOUT */
                        L.w("relax direction match");
                // fall thru
                    default:
                        /* IN/IN  is OK */
                        break;
                }
                break;
            case SharkCS.DIRECTION_INOUT:
                switch (cc2.getDirection()) {
                    case SharkCS.DIRECTION_OUT:
/* INOUT/OUT */
                    case SharkCS.DIRECTION_IN:
                        /* INOUT/IN */
                        L.w("relax direction match");
                // fall thru
                    case SharkCS.DIRECTION_INOUT:
                        /* INOUT/INOUT is OK */
                        break;
                }
                break;
        }
        // originator
        if (InMemoSharkKB.exactMatch(cc1.getOriginator(), cc2.getOriginator())) {
            // topic
            if (InMemoSharkKB.exactMatch(cc1.getTopic(), cc2.getTopic())) {
                // peer
                if (InMemoSharkKB.exactMatch(cc1.getPeer(), cc2.getPeer())) {
                    // remote peer
                    if (InMemoSharkKB.exactMatch(cc1.getRemotePeer(), cc2.getRemotePeer())) {
                        // location
                        if (InMemoSharkKB.exactMatch(cc1.getLocation(), cc2.getLocation())) {
                            // time
                            if (InMemoSharkKB.exactMatch(cc1.getTime(), cc2.getTime())) {
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
        if (s1 == s2) {
            return true;
        }
        // both any - ok
        if (SharkCSAlgebra.isAny(s1) && SharkCSAlgebra.isAny(s2)) {
            return true;
        }
        // just one is any - wrong
        if (SharkCSAlgebra.isAny(s1) || SharkCSAlgebra.isAny(s2)) {
            return false;
        }
        // both not null and both not any
        return SharkCSAlgebra.identical(s1, s2);
    }

    /**
     * Create an empty SharkKB.
     * 
     * The only tag, that will be created are the tags representing IN and OUT on
     * the direction and topic dimension.
     *
     * It is advised to use the SharkEngine to create a new in-memory SharkKB though.
     */
    public InMemoSharkKB() {
        super(new InMemoSemanticNet(),
            new InMemoPeerTaxonomy(),
            new InMemoSpatialSTSet(),
            new InMemoTimeSTSet());
        
        // this as knowledge background.
        Knowledge k = new InMemoKnowledge(this);
        
        this.setKnowledge(k);
    }

    protected InMemoSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times) throws SharkKBException {
        
        super(topics, peers, locations, times);
    }
    
    InMemoSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times,
                 Knowledge k) throws SharkKBException {
        
        super(topics, peers, locations, times, k);
    }
    
    SharkKB createTwin(Knowledge k) throws SharkKBException {
        return new InMemoSharkKB(this.getTopicsAsSemanticNet(),
                this.getPeersAsTaxonomy(), this.getSpatialSTSet(), 
                this.getTimeSTSet(), k);
    }
    
    @Override
    public Knowledge createKnowledge() {
        return new InMemoKnowledge(this);
    }

    @Override
    public Interest createInterest(STSet topics, PeerSemanticTag originator, 
        PeerSTSet peers, PeerSTSet remotePeers, TimeSTSet times, 
        SpatialSTSet locations, int direction) 
    {
        return new InMemoInterest(topics, originator, peers, remotePeers, 
                times, locations, direction);
    }

    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotePeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        this.getTopicSTSet().merge(topic);
        PeerSTSet peers = this.getPeerSTSet();
        peers.merge(originator);
        peers.merge(peer);
        peers.merge(remotePeer);
        this.getTimeSTSet().merge(time);
        this.getSpatialSTSet().merge(location);
        
        return new InMemoContextCoordinates(topic, originator, peer, remotePeer, time, location, direction);
    }
    
    @Override
    public Interest createInterest() throws SharkKBException{
        return new InMemoInterest();
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint cp = this.getContextPoint(coordinates);
        
        if(cp != null) {
            return cp;
        }
        
        cp = new InMemoContextPoint(coordinates);
        this.addContextPoint(cp);
        
        return cp;
    }

    /**
     * Creates an interest with coordinates - coordinates are copied.
     * @param cc
     * @return
     * @throws SharkKBException 
     */
    @Override
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
        return InMemoSharkKB.createInMemoCopy((SharkCS) cc);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        try {
            return super.getPeersAsSemanticNet();
        }
        catch(SharkKBException e) {
            // abstract implementation wasn't successfull
        }
        
        PeerSTSet peers = this.getPeerSTSet();
        
        if(peers instanceof InMemoPeerSTSet) {
            InMemoGenericTagStorage tagStorage = ((InMemoPeerSTSet) peers).getTagStorage();
            return new InMemoPeerSemanticNet(tagStorage); 
        }
        
        throw new SharkKBException("peers dimension isn't a semantic net.");
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Taxonomy getTopicsAsTaxonomy()  throws SharkKBException {
        try {
            return super.getTopicsAsTaxonomy();
        }
        catch(SharkKBException e) {
            // abstract implementation wasn't successfull
        }
        
        STSet topics = this.getTopicSTSet();
        if(topics instanceof InMemoSTSet) {
            InMemoGenericTagStorage tagStorage = ((InMemoSTSet) topics).getTagStorage();
            return new InMemoTaxonomy(tagStorage);
        }
        
        throw new SharkKBException("topic dimension isn't a taxonomy");
    }
    
    @Override
    protected Knowledge getKnowledge() {
        return super.getKnowledge();
    }
    
    /**
     * @return the same kb object acting as knowledge object
     * @throws net.sharkfw.knowledgeBase.SharkKBException if InMemoSharkKB cannot be handled like knowledge
     */
    public Knowledge asKnowledge() throws SharkKBException {
        Knowledge k = this.getKnowledge();
        
        if(k instanceof InMemoKnowledge) {
            InMemoKnowledge imk = (InMemoKnowledge)k;
            return new InMemoKnowledge(this, imk);
        } 
        
        throw new SharkKBException("InMemoSharkKB cannot be handled like knowledge - knowledge isn't stored as InMemoKnowledge - developers need a workaroung if this exception is thrown");
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.owner;
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
    //    public STSet getAnySTSet() throws SharkKBException {
    //        return this.createAnySTSet(SharkCS.DIM_PEER);
    //    }
    //
    //    public STSet getAnyGeoSTSet() throws SharkKBException {
    //        return this.createAnySTSet(SharkCS.DIM_LOCATION);
    //    }
    //
    //    public STSet getAnyTimeSTSet() throws SharkKBException {
    //        return this.createAnySTSet(SharkCS.DIM_TIME);
    //    }
    //
    //    public STSet getAnyDirectionSTSet() throws SharkKBException {
    //        return this.createAnySTSet(SharkCS.DIM_DIRECTION);
    //    }
    //
    //    public STSet createAnySTSet(int dimension) throws SharkKBException {
    //        return null;
    //    }
    
    
    @Override
    public void setOwner(PeerSemanticTag owner) {
        // remove listener from old owner
        if (this.owner != null && this.owner instanceof AbstractSemanticTag) {
            AbstractSemanticTag st = (AbstractSemanticTag) this.owner;
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
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        Enumeration<ContextPoint> cpEnum = this.knowledge.contextPoints();
        while (cpEnum.hasMoreElements()) {
            ContextPoint cp = cpEnum.nextElement();
            ContextCoordinates co = cp.getContextCoordinates();
            if (InMemoSharkKB.exactMatch(co, coordinates)) {
                return cp;
            }
        }
        return null;
    }

    //    @Override
    //    abstract public ContextCoordinates createContextCoordinates(SemanticTag topic,
    //        PeerSemanticTag originator, PeerSemanticTag peer,
    //        PeerSemanticTag remotePeer, TimeSemanticTag time,
    //        SpatialSemanticTag location, int direction)
    //            throws SharkKBException;
    //    @Override
    //    abstract public ContextPoint createContextPoint(ContextCoordinates coordinates)
    //            throws SharkKBException;
    protected void addContextPoint(ContextPoint cp) throws SharkKBException {
        this.knowledge.addContextPoint(cp);
    }

    @Override
    public Iterator contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        if (cs == null) {
            return null;
        }
        HashSet<ContextPoint> result = new HashSet<ContextPoint>();
        HashSet<ContextCoordinates> coo = this.possibleCoordinates(cs);
        if (coo == null) {
            return null;
        }
        Iterator<ContextCoordinates> cooIter = coo.iterator();
        while (cooIter.hasNext()) {
            // next possible coordinate
            ContextCoordinates co = cooIter.next();
            if (!matchAny) {
                // exact match
                ContextPoint cp = this.getContextPoint(co);
                if (cp != null) {
                    // copy cp
                    result.add(cp);
                }
            } else {
                // matchAny - find all matching cps.
                Enumeration<ContextPoint> cpEnum = this.knowledge.contextPoints();
                while (cpEnum.hasMoreElements()) {
                    ContextPoint cp = cpEnum.nextElement();
                    if (SharkCSAlgebra.identical(cp.getContextCoordinates(), co)) {
                        result.add(cp);
                    }
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result.iterator();
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
    public Enumeration getAllContextPoints() throws SharkKBException {
        ContextCoordinates cc = InMemoSharkKB.getAnyCoordinates();
        return this.getContextPoints(cc);
    }

    public HashSet possibleCoordinates(SharkCS cs) throws SharkKBException {
        if (cs == null) {
            return null;
        }
        HashSet<ContextCoordinates> protoCoo = new HashSet<ContextCoordinates>();
        // create first prototype with direction and owner
        if (cs.getDirection() == SharkCS.DIRECTION_INOUT) {
            // two additional coordinates
            protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), null, null, null, null, SharkCS.DIRECTION_IN));
            protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), null, null, null, null, SharkCS.DIRECTION_OUT));
        }
        protoCoo.add(this.createContextCoordinates(null, cs.getOriginator(), null, null, null, null, cs.getDirection()));
        // no combine with other dimensions
        protoCoo = this.coordCombination(protoCoo, cs.getTopics(), SharkCS.DIM_TOPIC);
        protoCoo = this.coordCombination(protoCoo, cs.getPeers(), SharkCS.DIM_PEER);
        protoCoo = this.coordCombination(protoCoo, cs.getRemotePeers(), SharkCS.DIM_REMOTEPEER);
        protoCoo = this.coordCombination(protoCoo, cs.getTimes(), SharkCS.DIM_TIME);
        protoCoo = this.coordCombination(protoCoo, cs.getLocations(), SharkCS.DIM_LOCATION);
        return protoCoo;
    }

    protected HashSet coordCombination(HashSet<ContextCoordinates> protoCoo, STSet set, int dim) throws SharkKBException {
        if (SharkCSAlgebra.isAny(set)) {
            return protoCoo;
        }
        set.setEnumerateHiddenTags(true);
        Enumeration<SemanticTag> tagEnum = set.tags();
        if (tagEnum == null || !tagEnum.hasMoreElements()) {
            return protoCoo;
        }
        HashSet<ContextCoordinates> result = new HashSet<ContextCoordinates>();
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
                    case SharkCS.DIM_TOPIC:
                        topic = tag;
                        break;
                    case SharkCS.DIM_PEER:
                        peer = (PeerSemanticTag) tag;
                        break;
                    case SharkCS.DIM_REMOTEPEER:
                        remotePeer = (PeerSemanticTag) tag;
                        break;
                    case SharkCS.DIM_TIME:
                        time = (TimeSemanticTag) tag;
                        break;
                    case SharkCS.DIM_LOCATION:
                        location = (SpatialSemanticTag) tag;
                        break;
                }
                ContextCoordinates newCC = this.createContextCoordinates(topic, originator, peer, remotePeer, time, location, direction);
                result.add(newCC);
            }
        }
        return result;
    }

    public Iterator getTags() throws SharkKBException {
        EnumerationChain<SemanticTag> tagEnum = new EnumerationChain<SemanticTag>();
        tagEnum.addEnumeration(this.getTopicSTSet().tags());
        tagEnum.addEnumeration(this.getPeerSTSet().tags());
        tagEnum.addEnumeration(this.getSpatialSTSet().tags());
        tagEnum.addEnumeration(this.getTimeSTSet().tags());
        return tagEnum;
    }

    @Override
    public void persist() {
        super.persist();
        // owner
        if (this.owner != null) {
            String ownerSIString = Util.array2string(this.owner.getSI());
            if (ownerSIString != null && ownerSIString.length() > 0) {
                this.setSystemProperty(AbstractSharkKB.OWNER, ownerSIString);
            }
        }
        // default fp
        if (this.defaultFP != null) {
            String defaultFPString = Util.fragmentationParameter2string(defaultFP);
            this.setSystemProperty(AbstractSharkKB.DEFAULT_FP, defaultFPString);
        }
    }

    @Override
    public void refreshStatus() {
        super.refreshStatus();
        // owner
        String ownerSIString = this.getSystemProperty(AbstractSharkKB.OWNER);
        if (ownerSIString != null) {
            String[] ownerSIs = Util.string2array(ownerSIString);
            try {
                PeerSemanticTag storedOwner = this.getPeerSemanticTag(ownerSIs);
                if (storedOwner != null) {
                    this.owner = storedOwner;
                    // listen to changed in owner
                    this.setOwnerListener();
                }
            } catch (SharkKBException ex) {
                L.w("cannot find owner tag while restoring kb status from external memory", this);
            }
        }
        // default fp
        String defaultFPValue = this.getSystemProperty(AbstractSharkKB.DEFAULT_FP);
        if (defaultFPValue != null) {
            this.defaultFP = Util.string2fragmentationParameter(defaultFPValue);
        }
    }
}
