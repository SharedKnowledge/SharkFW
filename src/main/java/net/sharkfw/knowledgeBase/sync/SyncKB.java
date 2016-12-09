package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

/**
 * Created by j4rvis on 19.07.16.
 *
 * @author thsc42
 */
public class SyncKB implements SharkKB{

    public interface SyncChangeListener{
        void onChange();
    }

    private final SyncSTSet topics;
    private final SyncSemanticNet snTopics;
    private final SyncTaxonomy txTopics;
    
    private final SyncSTSet types;
    private final SyncSemanticNet snTypes;
    private final SyncTaxonomy txTypes;
    
    private final SyncPeerSTSet peers;
    private final SyncPeerSemanticNet snPeers;
    private final SyncPeerTaxonomy txPeers;
    
    private final SyncSpatialSTSet locations;
    private final SyncTimeSTSet times;
    
    private final SharkKB targetKB;
    public final static String TIME_PROPERTY_NAME = "Shark_System_Last_Modified";

    private ArrayList<SyncChangeListener> syncChangeListeners = new ArrayList<>();

    public SyncKB(SharkKB target) throws SharkKBException {
        this.targetKB = target;
        
        /* we make a decomposition of target knowledge base into
        its dimensions and information spaces and wrap it
        
        Note: We only work with stSets here because we do not keep track of
        changes in tag relations. That must be implemented asap.
        */
        
        // topics
        this.topics = new SyncSTSet(target.getTopicSTSet());
        this.snTopics = new SyncSemanticNet(target.getTopicsAsSemanticNet());
        this.txTopics = new SyncTaxonomy(target.getTopicsAsTaxonomy());
        
        // types
        this.types = new SyncSTSet(target.getTypeSTSet());
        this.snTypes = new SyncSemanticNet(target.getTypesAsSemanticNet());
        this.txTypes = new SyncTaxonomy(target.getTypesAsTaxonomy());
        
        // peers
        this.peers = new SyncPeerSTSet(target.getPeerSTSet());
        this.snPeers = new SyncPeerSemanticNet(target.getPeersAsSemanticNet());
        this.txPeers = new SyncPeerTaxonomy(target.getPeersAsTaxonomy());
        
        // locations
        this.locations = new SyncSpatialSTSet(target.getSpatialSTSet());
        
        // times
        this.times = new SyncTimeSTSet(target.getTimeSTSet());
        
        /* NOTE also: We don't wrap information at all. Information alread
        get a last modified tag at least in its in memo implementation.
        
        TODO: We have to wrap information as well here. We cannot ensure that
        any kb implementation implements that modified time setting correctly.
        */
    }

    public void addSyncChangeListener(SyncChangeListener listener){
        this.syncChangeListeners.add(listener);
    }

    /**
     * This method will be called after each changes made to the targetKB.
     */
    private void changed() {
        String timeString = Long.toString(System.currentTimeMillis());

        try {
            // set time stamp as non transferable property
            this.targetKB.setProperty(SyncKB.TIME_PROPERTY_NAME, timeString, false);
        }
        catch(SharkKBException e) {
            L.e("cannot write time stamp - sync won't work accordingly");
        }

        for (SyncChangeListener listener : this.syncChangeListeners){
            listener.onChange();
        }
    }

    public Long getTimeOfLastChanges(){
        try {
            return Long.getLong(this.targetKB.getProperty(SyncKB.TIME_PROPERTY_NAME));
        } catch (SharkKBException e) {
            return 0L;
        }
    }
    
    /**
     * That method return all data (tags and information) that has
     * changed since a date. Note: We don't keep track of relations between
     * tags. We only keep track if tags and information are changed.
     * 
     * The resulting knowledge will contain semantic tag sets only but 
     * not semantic nets or taxonomies. Thus, relations won't be harmed
     * when merging those changes into another knowledge base.
     * 
     * That's not a feature. That's a bug. TODO: Must be fixed.
     * 
     * 
     * @param since
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public SharkKB getChanges(Long since) throws SharkKBException {
        // get changes from topics
        SemanticNet cTopics = this.topics.getChangesAsSemanticNet(since);
        
        // get changes from topics
        SemanticNet cTypes = this.types.getChangesAsSemanticNet(since);
        
        // get changes from peers
        PeerTaxonomy cPeers = this.txPeers.getChangesAsTaxonomy(since);
        
        // get changes from locations
        SpatialSTSet cLocations = (SpatialSTSet) this.locations.getChanges(since);
        
        // get changes from times
        TimeSTSet cTimes = (TimeSTSet) this.times.getChanges(since);
        
        // Merge all together - we have a kb containing changed items
        InMemoSharkKB changes = new InMemoSharkKB(cTopics, cTypes, cPeers,
                 cLocations, cTimes);

        // add information
        int infoNumber = this.putKnowledgeChanges(since, changes);
        
        /**
         * I'm not happy with that implementation:
         * In nearly any case, sync is performed between two peers.
         * That implementation creates a copy of all changed information
         * which are transmitted afterwards.
         * 
         * A better implementation would not create a copy but
         * stream the data directly to the other side.
         * 
         * Anyway, that implementation could be made faster.
         * Changing to a stream implementation shouldn't take longer
         * than a day.
         */

        return changes;
    }

    /**
     * That methode takes a kb which is assumed to contain changed or new
     * items. Loosly speaking, that methode is a number of merge calls on the
     * local knowledge base.
     *
     * TODO: We haven't managed yet to implement removal of entities (tags, information). Must be done asap.
     *
     * @param changes
     * @return
     */
    public void putChanges(SharkKB changes) throws SharkKBException {
        // merge tags first
        this.topics.merge(changes.getTopicSTSet());
        this.types.merge(changes.getTypeSTSet());
        this.peers.merge(changes.getPeerSTSet());
        this.locations.merge(changes.getSpatialSTSet());
        this.times.merge(changes.getTimeSTSet());

        // merge information now
        Iterator<ASIPInformationSpace> cInfoSpacesIter = changes.informationSpaces();
        if(cInfoSpacesIter != null) {
            while(cInfoSpacesIter.hasNext()) {
                ASIPInformationSpace cInfoSpace = cInfoSpacesIter.next();
                Iterator<ASIPInformation> cInfoIter = cInfoSpace.informations();
                SharkAlgebra.mergeInformations(targetKB, cInfoIter);
            }
        }
    }

    /**
     * merges all changes information into that knowledge base
     * @param since
     * @return number of added informations (not only info spaces!)
     * @throws SharkKBException 
     */
    private int putKnowledgeChanges(Long since, SharkKB kb) throws SharkKBException {
        int infoNumber = 0;
        
        Iterator<ASIPInformationSpace> infoSpaceIter = 
                this.targetKB.getAllInformationSpaces();
        
        if(infoSpaceIter != null) {
            while(infoSpaceIter.hasNext()) {
                ASIPInformationSpace infoSpace = infoSpaceIter.next();
                
                Iterator<ASIPInformation> infoIter = infoSpace.informations();
                if(infoIter != null) {
                    
                    while(infoIter.hasNext()) {
                        ASIPInformation info = infoIter.next();
                        long changed = SyncKB.getTimeStamp(info);
                        if(changed > since) {
                            // add info - its a copy
                            kb.addInformation(info.getContentAsByte(), 
                                    info.getASIPSpace());
                            
                            infoNumber++;
                        }
                    }
                }
            }
        }
        
        return infoNumber;
    }
    
    public static final long getTimeStamp(PropertyHolder target) throws SharkKBException {
        String timeString = target.getProperty(SyncKB.TIME_PROPERTY_NAME);
        if(timeString == null) {
            return Long.MIN_VALUE;
        }
        
        return Long.parseLong(timeString);
    }
    
    
    /**
     * that method should be removed - we should use only one way to
     * store the last modified time.
     * 
     * @param info
     * @return
     * @throws SharkKBException 
     */
    public static final long getTimeStamp(ASIPInformation info) throws SharkKBException {
        String timeString = info.getProperty(InMemoInformation.INFO_LAST_MODIFED);
        if(timeString == null) {
            timeString = info.getProperty(InMemoInformation.INFO_CREATION_TIME);
            if(timeString == null) {
                return Long.MIN_VALUE;
            }
        }
        
        return Long.parseLong(timeString);
    }
    
    /**
     * Not additional activities required here
     * @param owner
     */
    @Override
    public void setOwner(PeerSemanticTag owner) {
        this.targetKB.setOwner(owner);
        this.changed();
    }

    /**
     * Not additional activities required here
     * @return owner
     */
    @Override
    public PeerSemanticTag getOwner() {
        return this.targetKB.getOwner();
    }

    @Deprecated
    @Override
    public SharkCS asSharkCS() {
        return this.targetKB.asSharkCS();
    }

    @Deprecated
    @Override
    public Interest asInterest() {
        return this.targetKB.asInterest();
    }

    /**
     * Not additional activities required here. Return value is
     * supposed to be copy of data. Changes on that have no 
     * effect on knowledge base.
     * @return
     * @throws SharkKBException 
     */
    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.targetKB.asASIPSpace();
    }

    /**
     * Not additional activities required here. Return value is
     * supposed to be copy of data. Changes on that have no 
     * effect on knowledge base.
     * @return
     * @throws SharkKBException 
     */
    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return this.targetKB.asASIPInterest();
    }

    /**
     * Taht set must be wrapped. That set can be used to
     * add, change or remove tags which has impact on knowledge base.
     * @return
     * @throws SharkKBException 
     */
    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        STSet set = this.targetKB.getTopicSTSet();
        if(set == null) return null;
        
        // wrap it
        return new SyncSTSet(set);
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        SemanticNet net = this.targetKB.getTopicsAsSemanticNet();
        if(net == null) return null;
        
        // wrap it
        return new SyncSemanticNet(net);
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        Taxonomy tx = this.targetKB.getTopicsAsTaxonomy();
        if(tx == null) return null;
        
        // wrap it
        return new SyncTaxonomy(tx);
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return this.targetKB.getTypeSTSet();
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return this.targetKB.getTypesAsSemanticNet();
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return this.targetKB.getTypesAsTaxonomy();
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        PeerSTSet peers = this.targetKB.getPeerSTSet();
        if(peers == null) return null;
        
        return new SyncPeerSTSet(peers);
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return this.targetKB.getPeersAsSemanticNet();
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return this.targetKB.getPeersAsTaxonomy();
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return this.targetKB.getTimeSTSet();
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return this.targetKB.getSpatialSTSet();
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS cs) throws SharkKBException {
        return this.targetKB.contextualize(cs);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return this.targetKB.contextualize(as);
    }

    @Deprecated
    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
        return this.targetKB.contextualize(as, fp);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return this.targetKB.contextualize(as, fps);
    }

    @Deprecated
    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        return this.targetKB.getContextPoint(coordinates);
    }

    @Deprecated
    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        ContextCoordinates contextCoordinates = this.targetKB.createContextCoordinates(topic, originator, peer, remotepeer, time, location, direction);
        this.changed();
        return contextCoordinates;
    }

    @Deprecated
    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint contextPoint = this.targetKB.createContextPoint(coordinates);
        this.changed();
        return contextPoint;
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        ArrayList<ASIPSpace> assimilate = this.targetKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
        this.changed();
        return assimilate;
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return this.targetKB.extract(context);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return this.targetKB.extract(context, fp);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return this.targetKB.extract(context, backgroundFP, recipient);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return this.targetKB.extract(context, backgroundFP, cutGroups);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return this.targetKB.extract(target, context, backgroundFP, cutGroups, recipient);
    }

    @Deprecated
    @Override
    public Knowledge createKnowledge() {
        Knowledge knowledge = this.targetKB.createKnowledge();
        this.changed();
        return knowledge;
    }

    @Deprecated
    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        this.targetKB.removeContextPoint(coordinates);
        this.changed();
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        return this.targetKB.getContextPoints(cs);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        return this.targetKB.contextPoints(cs);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.targetKB.getContextPoints(cs, matchAny);
    }

    @Deprecated
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return this.targetKB.contextPoints(cs, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return this.targetKB.informationSpaces(as, matchAny);
    }

    @Deprecated
    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        return this.targetKB.getAllContextPoints();
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
        this.changed();
        return asipSpace;
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.targetKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
        this.changed();
        return asipSpace;
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        ASIPSpace asipSpace = this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
        this.changed();
        return asipSpace;
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) throws SharkKBException {
        ASIPSpace asipSpace = this.targetKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations);
        this.changed();
        return asipSpace;
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this.targetKB.getAllInformationSpaces();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        this.targetKB.addListener(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        this.targetKB.removeListener(kbl);
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        this.targetKB.setStandardFPSet(fps);
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return this.targetKB.getStandardFPSet();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        ASIPInformationSpace informationSpace = this.targetKB.mergeInformation(information, space);
        this.changed();
        return informationSpace;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = this.targetKB.addInformation(content, semanticAnnotations);
        this.changed();
        return asipInformation;
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = this.targetKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
        this.changed();
        return asipInformation;
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        ASIPInformation asipInformation = this.targetKB.addInformation(content, semanticAnnotations);
        this.changed();
        return asipInformation;
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this.targetKB.removeInformation(info, infoSpace);
        this.changed();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this.targetKB.getInformation(infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return this.targetKB.getInformation(infoSpace, fullyInside, matchAny);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return this.targetKB.informationSpaces();
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this.targetKB.removeInformation(space);
        this.changed();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this.targetKB.getVocabulary();
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return this.targetKB.getNumberInformation();
    }

    @Deprecated
    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        this.targetKB.addInterest(interest);
        this.changed();
    }

    @Deprecated
    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        this.targetKB.removeInterest(interest);
        this.changed();
    }

    @Deprecated
    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        return this.targetKB.interests();
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.targetKB.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.targetKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.targetKB.setProperty(name, value, transfer);
        this.changed();
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.targetKB.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.targetKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.targetKB.propertyNames(all);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.targetKB.semanticTagChanged(tag, stset);
    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.targetKB.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return this.targetKB.getSystemProperty(name);
    }
}
