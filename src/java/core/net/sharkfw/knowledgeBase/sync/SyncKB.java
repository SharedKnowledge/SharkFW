package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.Geometry;
import net.sharkfw.system.Util;

/**
 * @author hellerve
 * @author simonArnold
 */
@SuppressWarnings("unchecked")
public class SyncKB implements SharkKB {
    
    SharkKB localKB = null;

    /**
     * initialize the SyncKB with some kind of subclass
     * of SharkKB.
     * @param originalKB the KB that should be synced.
     */
    public SyncKB(SharkKB originalKB){
        localKB = originalKB;
    }

    ///////////////////////////////////////////////////////////////////
    //    allow creating standalone structures                       //
    ///////////////////////////////////////////////////////////////////

    /**
     * Create a semantic tag in the underlying Knowledge Base.
     * Consult its documentation for further information on
     * what you will have to expect.
     * @param name
     * @param sis
     * @return
     */
    public static SemanticTag createSemanticTag(String name, String[] sis) {
        return localKB.createSemanticTag(name, sis);
    }

    /**
     * Create a semantic tag in the underlying Knowledge Base.
     * Consult its documentation for further information on
     * what you will have to expect.
     * @param name
     * @param si
     * @return
     */
    public static SemanticTag createSemanticTag(String name, String si) {
        return localKB.createSemanticTag(name, new String[] {si});
    }
    
    /**
     * creates an TST covering a period:
     * @param from Start time - milliseconds beginning from 1.1.1970
     * @param duration duration of period in milliseconds
     * @return 
     */
    public static TimeSemanticTag createTimeSemanticTag(long from, long duration) {
        return  localKB.createTimeSemanticTag(from, duration);
    }

    /**
     * Create a peer semantic tag in the underlying Knowledge Base.
     * Consult its documentation for further information on 
     * what you will have to expect.
     * @return 
     */
    public static PeerSemanticTag createPeerSemanticTag(String name, String[] sis, 
            String[] addresses) {
        return localKB.createPeerSemanticTag(name, sis, addresses);
    }
    
    /**
     * Create a peer semantic tag in the underlying Knowledge Base.
     * Consult its documentation for further information on 
     * what you will have to expect.
     * @return 
     */
    public static PeerSemanticTag createPeerSemanticTag(String name, String si, 
            String address) {
        return localKB.createPeerSemanticTag(name, new String[] {si}, 
                new String[] {address});
    }
    
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
    public static STSet createSTSet() {
        return localKB.createSTSet();
    }
    
    public static Knowledge createKnowledge() {
        /**
         * TODO: Implementation
         */
        return locaKB.createKnowledge();
    }
    

    public static ContextPoint createContextPoint(ContextCoordinates cc) {
        /**
         * TODO: Implementation
         */
        return localKB.createContextPoint(cc);
    }
    
    ///////////////////////////////////////////////////////////////////
    //                       in memo copies                          //
    ///////////////////////////////////////////////////////////////////
        /**
         * TODO: Implementation
         */

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
                InMemoSharkKB.createInMemoContextCoordinates(t, o, p, o, ti, l, cc.getDirection());
        
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
        
    public static Interest createCopy(Interest interest) 
            throws SharkKBException {
        return localKB._createCopy(interest);
    }
    
    public static Interest createCopy(SharkCS cs) 
            throws SharkKBException {
        if(cs == null)
            throw new SharkKBException("cannot make copy from null");
        return localKB._createCopy(cs);
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
    public static SpatialSemanticTag createInMemoSpatialSemanticTag(Geometry geometry) {
        return new InMemoSpatialSemanticTag(geometry);
    }

    public static SpatialSemanticTag createInMemoSpatialSemanticTag(String name, String si[], Geometry geom) {
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
        /**
         * TODO: Implementation
         */
        return new InMemoContextCoordinates(topic, originator, peer,
                remotePeer, time, location, direction);
    }

    public static Interest createInMemoInterest() {
        return new InMemoInterest();
    }
    ///////////////////////////////////////////////////////////////////////////
    //                 actual kb implementation starts here                  //
    ///////////////////////////////////////////////////////////////////////////

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
                 SpatialSTSet locations, TimeSTSet times) {
        
        super(topics, peers, locations, times);
    }
    
    InMemoSharkKB(SemanticNet topics, PeerTaxonomy peers,
                 SpatialSTSet locations, TimeSTSet times,
                 Knowledge k) {
        
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
    public Interest createInterest() {
        return new InMemoInterest();
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint cp = this.getContextPoint(coordinates);
        
        if(cp != null) {
            return cp;
        }
        
        cp = new InMemoContextPoint(coordinates);
        super.addContextPoint(cp);
        
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
}
