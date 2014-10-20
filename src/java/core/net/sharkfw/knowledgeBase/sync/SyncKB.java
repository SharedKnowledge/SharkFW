package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.Geometry;
import net.sharkfw.system.Util;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.inmemory.InMemoContextPoint;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoSpatialSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoTaxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoTimeSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoTimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_SemanticTag;

/**
 * @author simonArnold
 * @author hellerve
 */



@SuppressWarnings("unchecked")
public class SyncKB implements SharkKB {
    
    public static String VERSION_PROPERTY_NAME = "version";
    public static String VERSION_DEFAULT_VALUE = "1";
    
    SharkKB _localKB = null;

    ////////////////////////////////////////////////////////////////////////
    //                      constructor                                   //
    ////////////////////////////////////////////////////////////////////////
    public SyncKB(SharkKB kb) throws SharkKBException{
//        super(kb.getTopicsAsSemanticNet(), kb.getPeersAsTaxonomy(), kb.getSpatialSTSet(), kb.getTimeSTSet());
        
        _localKB = kb;
        
        Enumeration<ContextPoint> contextPoints = _localKB.getAllContextPoints();
        while (contextPoints.hasMoreElements()) {
            ContextPoint cp = contextPoints.nextElement();
            Enumeration<Information> cpInformations = cp.enumInformation();
            while (cpInformations.hasMoreElements()) {
                Information information = cpInformations.nextElement();
                if (information.getProperty(VERSION_PROPERTY_NAME) == null) {
                    information.setProperty(VERSION_PROPERTY_NAME, VERSION_DEFAULT_VALUE, true); // True for transferable
                }
            }
        }
    }
    
////    }
//    
//    @Override
//    protected Knowledge getKnowledge() {
//        return super.getKnowledge();
//    }

    ////////////////////////////////////////////////////////////////////////
    //                     in memo copies                                 //
    ////////////////////////////////////////////////////////////////////////
//    public static SemanticTag createInMemoCopy(SemanticTag tag) {
//        if(tag == null) { return null; }
//        SemanticTag st = new InMemoSemanticTag(tag.getName(), tag.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     * 
     * @param tag
     * @return 
     */
//    public static SNSemanticTag createInMemoCopyToSNSemanticTag(SemanticTag tag) {
//        if(tag == null) { return null; }
//        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
    /**
     * Creates a SNSemantic Tag and copies name and sis from tag into.
     * 
     * @param tag
     * @return 
     */
//    public static TXSemanticTag createInMemoCopyToTXSemanticTag(SemanticTag tag) {
//        if(tag == null) { return null; }
//        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
//    public static SNSemanticTag createInMemoCopy(SNSemanticTag tag) {
//        if(tag == null) { return null; }
//        SNSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
//    public static TXSemanticTag createInMemoCopy(TXSemanticTag tag) {
//        if(tag == null) { return null; }
//        TXSemanticTag st = new InMemo_SN_TX_SemanticTag(tag.getName(), tag.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
//    public static PeerTXSemanticTag createInMemoCopy(PeerTXSemanticTag tag) {
//        if(tag == null) { return null; }
//        PeerTXSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
//    
//    public static PeerSNSemanticTag createInMemoCopy(PeerSNSemanticTag tag) {
//        if(tag == null) { return null; }
//        PeerSNSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(tag.getName(), tag.getSI(), tag.getAddresses());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tag, st);
//        return st;
//    }
//    
//    public static PeerSemanticTag createInMemoCopy(PeerSemanticTag pst) {
//        if(pst == null) { return null; }
//        PeerSemanticTag st = new InMemo_SN_TX_PeerSemanticTag(pst.getName(), pst.getSI(), pst.getAddresses());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(pst, st);
//        return st;
//    }
//
//    public static TimeSemanticTag createInMemoCopy(TimeSemanticTag tst) {
//        if(tst == null) { return null; }
//        TimeSemanticTag st = new InMemoTimeSemanticTag(tst.getFrom(), tst.getDuration());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(tst, st);
//        return st;
//    }
//    public static SpatialSemanticTag createInMemoCopy(SpatialSemanticTag sst) {
//        if(sst == null) { return null; }
//        InMemoSpatialSemanticTag st = new InMemoSpatialSemanticTag(sst.getName(), sst.getSI());
//        Util.copyPropertiesFromPropertyHolderToPropertyHolder(sst, st);
//        st.refreshStatus(); // important to refresh status right here - it extracts it geometry from properties
//        return st;
//    }
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
//    public static SemanticTag createInMemoSemanticTag(String name, String[] sis) {
//        return new InMemoSemanticTag(name, sis);
//    }
    /**
     * creates an TST covering a period from
     * @param from Start time - milliseconds beginning from 1.1.1970
     * @param duration duration of period in milliseconds
     * @return 
     */
//    public static TimeSemanticTag createInMemoTimeSemanticTag(long from, long duration) {
//        return new InMemoTimeSemanticTag(from, duration);
//    }
//
//    public static Information createInMemoInformation() {
//        return new InMemoInformation();
//    }
//    
//    public static Knowledge createInMemoKnowledge(SharkVocabulary background) {
//        return new InMemoKnowledge(background);
//    }
    /**
     * Create an in memory implementation of a semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
//    public static SemanticTag createInMemoSemanticTag(String name, String si) {
//        return new InMemoSemanticTag(name, new String[] {si});
//    }
    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
//    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String[] sis, 
//            String[] addresses) {
//        return new InMemo_SN_TX_PeerSemanticTag(name, sis, addresses);
//    }
    /**
     * Create an in memory implementation of a peer semantic tag. This tag won't be
     * part of a knowledge base. Use {@link merge()} for that task.
     * @return 
     */
//    public static PeerSemanticTag createInMemoPeerSemanticTag(String name, String si, 
//            String address) {
//
//        return new InMemo_SN_TX_PeerSemanticTag(name, new String[] {si}, 
//                new String[] {address});
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static STSet createInMemoSTSet() {
//        return new InMemoSTSet();
//    }
//    public static Knowledge createInMemoKnowledge() {
//        return new InMemoKnowledge();
//    }
//    
//    public static ContextPoint createInMemoContextPoint(ContextCoordinates cc) {
//        return new InMemoContextPoint(cc);
//    }
    ///////////////////////////////////////////////////////////////////
    //                       in memo copies                          //
    ///////////////////////////////////////////////////////////////////
//    public static STSet createInMemoCopy(STSet stSet) throws SharkKBException {
//        STSet copy = InMemoSharkKB.createInMemoSTSet();
//        copy.merge(stSet);
//        return copy;
//    }
//    
//    public static Taxonomy createInMemoCopy(Taxonomy taxonomy) throws SharkKBException {
//        Taxonomy copy = InMemoSharkKB.createInMemoTaxonomy();
//        copy.merge(taxonomy);
//        return copy;
//    }
//    
//    public static SemanticNet createInMemoCopy(SemanticNet semanticNet) throws SharkKBException {
//        SemanticNet copy = InMemoSharkKB.createInMemoSemanticNet();
//        copy.merge(semanticNet);
//        return copy;
//    }
//    
//    public static PeerSTSet createInMemoCopy(PeerSTSet peerSTSet) throws SharkKBException {
//        PeerSTSet copy = InMemoSharkKB.createInMemoPeerSTSet();
//        copy.merge(peerSTSet);
//        return copy;
//    }
//    
//    public static PeerTaxonomy createInMemoCopy(PeerTaxonomy peerTaxonomy) throws SharkKBException {
//        PeerTaxonomy copy = InMemoSharkKB.createInMemoPeerTaxonomy();
//        copy.merge(peerTaxonomy);
//        return copy;
//    }
//    
//    public static SpatialSTSet createInMemoCopy(SpatialSTSet spatialSTSet) throws SharkKBException {
//        SpatialSTSet copy = InMemoSharkKB.createInMemoSpatialSTSet();
//        copy.merge(spatialSTSet);
//        return copy;
//    }
//    
//    public static TimeSTSet createInMemoCopy(TimeSTSet timeSTSet) throws SharkKBException {
//        TimeSTSet copy = InMemoSharkKB.createInMemoTimeSTSet();
//        copy.merge(timeSTSet);
//        return copy;
//    }
//    
//    @SuppressWarnings("unused")
//    public static ContextCoordinates createInMemoCopy(ContextCoordinates cc) 
//            throws SharkKBException {
//        
//        SemanticTag t;
//        PeerSemanticTag p, rp, o;
//        SpatialSemanticTag l;
//        TimeSemanticTag ti;
//        
//        t = InMemoSharkKB.createInMemoCopy(cc.getTopic());
//        p = InMemoSharkKB.createInMemoCopy(cc.getPeer());
//        rp = InMemoSharkKB.createInMemoCopy(cc.getRemotePeer());
//        o = InMemoSharkKB.createInMemoCopy(cc.getOriginator());
//        l = InMemoSharkKB.createInMemoCopy(cc.getLocation());
//        ti = InMemoSharkKB.createInMemoCopy(cc.getTime());
//
//        ContextCoordinates copy = 
//                InMemoSharkKB.createInMemoContextCoordinates(t, o, p, o, ti, l, cc.getDirection());
//        
//        return copy;
//    }
//
//    private static Interest _createInMemoCopy(SharkCS interest) 
//            throws SharkKBException {
//        
//        Interest i = InMemoSharkKB.createInMemoInterest();
//        
//        i.setTopics(InMemoSharkKB.createInMemoCopy(interest.getTopics()));
//        
//        i.setOriginator(InMemoSharkKB.createInMemoCopy(interest.getOriginator()));
//        
//        i.setPeers(InMemoSharkKB.createInMemoCopy(interest.getPeers()));
//
//        i.setRemotePeers(InMemoSharkKB.createInMemoCopy(interest.getRemotePeers()));
//
//        i.setTimes(InMemoSharkKB.createInMemoCopy(interest.getTimes()));
//        
//        i.setLocations(InMemoSharkKB.createInMemoCopy(interest.getLocations()));
//
//        i.setDirection(interest.getDirection());
//        
//        return i;
//    }
//    public static Interest createInMemoCopy(Interest interest) 
//            throws SharkKBException {
//        return InMemoSharkKB._createInMemoCopy(interest);
//    }
//    public static Interest createInMemoCopy(SharkCS cs) 
//            throws SharkKBException {
//        
//        if(cs == null) {
//            throw new SharkKBException("cannot make in memo copy from null");
//        }
//        
//        return InMemoSharkKB._createInMemoCopy(cs);
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static SemanticNet createInMemoSemanticNet() {
//        return new InMemoSemanticNet();
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static Taxonomy createInMemoTaxonomy() {
//        return new InMemoTaxonomy();
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static PeerTaxonomy createInMemoPeerTaxonomy() {
//        return new InMemoPeerTaxonomy();
//    }
    /**
     * Create any spatial semantic tag. This tag has a geo location but no explicit 
     * meaning.
     * 
     * @param geometry
     * @return 
     */
//    public static SpatialSemanticTag createInMemoSpatialSemanticTag(Geometry geometry) {
//        return new InMemoSpatialSemanticTag(geometry);
//    }
//
//    public static SpatialSemanticTag createInMemoSpatialSemanticTag(String name, String si[], Geometry geom) {
//        return new InMemoSpatialSemanticTag(name, si, geom);
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static PeerSemanticNet createInMemoPeerSemanticNet() {
//        return new InMemoPeerSemanticNet();
//    }
    /**
     * Creates an in memory semantic tag set. This set is not part of any knowledge
     * base. It can be merged into an exiting kb, though.
     * @return 
     */
//    public static PeerSTSet createInMemoPeerSTSet() {
//        return new InMemoPeerSTSet();
//    }
//
//    public static Interest createInMemoInterest(STSet topics,
//            PeerSemanticTag originator, PeerSTSet peers, PeerSTSet remotePeers, 
//            TimeSTSet times, SpatialSTSet locations, int direction) {
//
//        return new InMemoInterest(topics, originator, peers, remotePeers, 
//                times, locations, direction);
//    }
//    
//    public static TimeSTSet createInMemoTimeSTSet() {
//        return new InMemoTimeSTSet();
//    }
//    
//    public static SpatialSTSet createInMemoSpatialSTSet() {
//        return new InMemoSpatialSTSet();
//    }
//    
//    public static ContextCoordinates createInMemoContextCoordinates(
//            SemanticTag topic, 
//            PeerSemanticTag originator,
//            PeerSemanticTag peer,
//            PeerSemanticTag remotePeer,
//            TimeSemanticTag time,
//            SpatialSemanticTag location,
//            int direction) { {
//                
//            }
//        return new InMemoContextCoordinates(topic, originator, peer,
//                remotePeer, time, location, direction);
//    }
//
//    public static Interest createInMemoInterest() {
//        return new InMemoInterest();
//    }
    ///////////////////////////////////////////////////////////////////////////
    //                 actual kb implementation starts here                  //
    ///////////////////////////////////////////////////////////////////////////
//    SharkKB createTwin(Knowledge k) throws SharkKBException {
//        return new InMemoSharkKB(this.getTopicsAsSemanticNet(),
//                this.getPeersAsTaxonomy(), this.getSpatialSTSet(), 
//                this.getTimeSTSet(), k);
//    }
//    @Override
//    public Knowledge createKnowledge() {
//        return new InMemoKnowledge(this);
//    }
//
//    @Override
//    public Interest createInterest(STSet topics, PeerSemanticTag originator,
//        PeerSTSet peers, PeerSTSet remotePeers, TimeSTSet times,
//        SpatialSTSet locations, int direction) 
//    {
//        Interest i;
//        try {
//            i =_localKB.createInterest();
//        } catch (SharkKBException e) {
//            return null;
//        }
//        i.setTopics(topics);
//        i.setOriginator(originator);
//        i.setPeers(peers);
//        i.setRemotePeers(remotePeers);
//        i.setTimes(times);
//        return i;
////        return new InMemoInterest(topics, originator, peers, remotePeers, 
////                times, locations, direction);
//    }
//    @Override
//    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotePeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
////        this.getTopicSTSet().merge(topic);
////        PeerSTSet peers = this.getPeerSTSet();
////        peers.merge(originator);
////        peers.merge(peer);
////        peers.merge(remotePeer);
////        this.getTimeSTSet().merge(time);
////        this.getSpatialSTSet().merge(location);
////        
////        return new InMemoContextCoordinates(topic, originator, peer, remotePeer, time, location, direction);
//        return null;
//    }
//    
//    @Override
//    public Interest createInterest() {
////        return new InMemoInterest();
//        return null;
//    }
//
//    @Override
//    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
//        ContextPoint cp = this.getContextPoint(coordinates);
//        
//        if(cp != null) {
//            return cp;
//        }
//        
//        cp = new InMemoContextPoint(coordinates);
//        super.addContextPoint(cp);
//        
//        return cp;
//    }
//
//    /**
//     * Creates an interest with coordinates - coordinates are copied.
//     * @param cc
//     * @return
//     * @throws SharkKBException 
//     */
//    @Override
//    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
//        return InMemoSharkKB.createInMemoCopy((SharkCS) cc);
//    }
//    
////    @SuppressWarnings("rawtypes")
////    @Override
////    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
////        try {
////            return super.getPeersAsSemanticNet();
////        }
////        catch(SharkKBException e) {
////            // abstract implementation wasn't successfull
////        }
////        
////        PeerSTSet peers = this.getPeerSTSet();
////        
////        if(peers instanceof InMemoPeerSTSet) {
////            InMemoGenericTagStorage tagStorage = ((InMemoPeerSTSet) peers).getTagStorage();
////            return new InMemoPeerSemanticNet(tagStorage); 
////        }
////        
////        throw new SharkKBException("peers dimension isn't a semantic net.");
////    }
//    
////    @SuppressWarnings("rawtypes")
////    @Override
////    public Taxonomy getTopicsAsTaxonomy()  throws SharkKBException {
////        try {
////            return super.getTopicsAsTaxonomy();
////        }
////        catch(SharkKBException e) {
////            // abstract implementation wasn't successfull
////        }
////        
////        STSet topics = this.getTopicSTSet();
////        if(topics instanceof InMemoSTSet) {
////            InMemoGenericTagStorage tagStorage = ((InMemoSTSet) topics).getTagStorage();
////            return new InMemoTaxonomy(tagStorage);
////        }
////        
////        throw new SharkKBException("topic dimension isn't a taxonomy");
    @Override
    public void setOwner(PeerSemanticTag owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Knowledge createKnowledge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        return _localKB.getAllContextPoints();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest createInterest() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Geometry geom) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, Double[] spatialCoo, double radius) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void persist() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getOwner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SharkCS asSharkCS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest asInterest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest contextualize(SharkCS as) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SemanticTag> getTags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSystemProperty(String name, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSystemProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
