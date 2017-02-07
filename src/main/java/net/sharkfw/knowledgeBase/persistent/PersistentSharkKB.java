package net.sharkfw.knowledgeBase.persistent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.FPSet;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SharkVocabulary;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

/**
 * We implement persistency as decorator combined with a variant of the
 * mememto pattern. This abstract class is the decorater. It keeps an 
 * in memory knowledge base to which most of the calls are delegated.
 * There are methods that fill that inmemory kb with data on demand.
 * Those methods are declared abstract in this class and must be 
 * implemented in derived classes.
 * 
 * @author thsc
 */
public abstract class PersistentSharkKB implements SharkKB {
    private final InMemoSharkKB inmemoKB;
    
    public PersistentSharkKB() {
        // create empty sharkKB
        this.inmemoKB = new InMemoSharkKB();
    }
    
    public InMemoSharkKB getInMemoSharkKB() {
        return this.inmemoKB;
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        this.inmemoKB.setOwner(owner);
        
        try {
            this.storageSetOwner(owner);
        } catch (SharkKBException ex) {
            // TODO
        }
    }

    private boolean restoredOwner = false;
    
    @Override
    public PeerSemanticTag getOwner() {
        try {
            if(this.restoredOwner) {
                return this.inmemoKB.getOwner();
            }
            
            // else
            PeerSemanticTag owner = this.storageGetOwner();
            this.inmemoKB.setOwner(owner);
            this.restoredOwner = true;
            
            return owner;
        } catch (SharkKBException ex) {
            // TODO
        }
        
        return null;
    }
    
    private boolean restoredAll = false;
    
    private void restoreAll() throws SharkKBException {
        if(!this.restoredAll) {
            this.storageRestoreAll(this.inmemoKB);
            this.restoredAll = true;
        }
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        this.restoreAll();
        ArrayList<ASIPSpace> assimilate = this.inmemoKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
        
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPSpaceList(this, assimilate);
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        this.restoreAll();
        Knowledge extract = this.inmemoKB.extract(context);
        
        return this.wrapKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        this.restoreAll();
        Knowledge extract = this.inmemoKB.extract(context, fp);
        
        return this.wrapKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        this.restoreAll();
        Knowledge extract = this.inmemoKB.extract(context, backgroundFP, recipient);
        
        return this.wrapKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        this.restoreAll();
        Knowledge extract = this.inmemoKB.extract(context, backgroundFP, cutGroups);
        
        return this.wrapKnowledge(this, extract);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        this.restoreAll();
        Knowledge extract = this.inmemoKB.extract(target, context, backgroundFP, cutGroups, recipient);
        
        return this.wrapKnowledge(this, extract);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        this.restoreAll();
        return this.wrapASIPInformationSpaces(this.inmemoKB.informationSpaces(as, matchAny));
    }
    
    private Iterator<ASIPInformationSpace> wrapASIPInformationSpaces(Iterator<ASIPInformationSpace> informationSpaces) {
        if(informationSpaces == null || !informationSpaces.hasNext()) return informationSpaces;
        
        ArrayList<ASIPInformationSpace> pSpaceList = new ArrayList<>();
        // not empty
        while(informationSpaces.hasNext()) {
            ASIPInformationSpace space = informationSpaces.next();
            pSpaceList.add(this.wrapASIPInformationSpace(this, space));
        }
        
        return pSpaceList.iterator();
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        this.restoreAll();
        ASIPSpace aSpace = this.inmemoKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
        
        this.storageSaveAll(inmemoKB);
        
        return this.wrapASIPSpace(this, aSpace);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        this.restoreAll();
        ASIPSpace aSpace = this.inmemoKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
        
        this.storageSaveAll(inmemoKB);
        
        return this.wrapASIPSpace(this, aSpace);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        this.restoreAll();
        return this.wrapASIPInformationSpaces(this.inmemoKB.getAllInformationSpaces());
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        this.inmemoKB.addListener(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        this.inmemoKB.removeListener(kbl);
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        this.inmemoKB.setStandardFPSet(fps);
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return this.inmemoKB.getStandardFPSet();
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        this.restoreAll();
        return this.wrapSTSet(this, this.inmemoKB.getTopicSTSet(), ASIPSpace.DIM_TOPIC);
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        this.restoreAll();
        return this.wrapSemanticNet(this, this.inmemoKB.getTopicsAsSemanticNet(), ASIPSpace.DIM_TOPIC);
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        this.restoreAll();
        return this.wrapTaxonomy(this, this.inmemoKB.getTopicsAsTaxonomy(), ASIPSpace.DIM_TOPIC);
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        this.restoreAll();
        return this.wrapSTSet(this, this.inmemoKB.getTypeSTSet(), ASIPSpace.DIM_TYPE);
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        this.restoreAll();
        return this.wrapSemanticNet(this, this.inmemoKB.getTypesAsSemanticNet(), ASIPSpace.DIM_TYPE);
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        this.restoreAll();
        return this.wrapTaxonomy(this, this.inmemoKB.getTypesAsTaxonomy(), ASIPSpace.DIM_TYPE);
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        this.restoreAll();
        return this.wrapPeerSTSet(this, this.inmemoKB.getPeerSTSet());
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        this.restoreAll();
        return this.wrapPeerSemanticNet(this, this.inmemoKB.getPeersAsSemanticNet());
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        this.restoreAll();
        return this.wrapPeerTaxonomy(this, this.inmemoKB.getPeersAsTaxonomy());
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        this.restoreAll();
        return this.wrapTimeSTSet(this, this.inmemoKB.getTimeSTSet());
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        this.restoreAll();
        return this.wrapSpatialSTSet(this, this.inmemoKB.getSpatialSTSet());
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        this.restoreAll();
        return this.wrapASIPInterest(this, this.inmemoKB.contextualize(as));
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        this.restoreAll();
        return this.wrapASIPInterest(this, this.inmemoKB.contextualize(as, fps));
    }

    private boolean propertiesRestored = false;
    private void restoreProperties() throws SharkKBException {
        if(!this.propertiesRestored) {
            this.storageRestoreProperties(inmemoKB);
            this.propertiesRestored = true;
        }
    }
    
    @Override
    public void setSystemProperty(String name, String value) {
        try {
            this.restoreProperties();
            this.inmemoKB.setSystemProperty(name, value);
            this.storageSaveProperties(this, inmemoKB);
        }
        catch(SharkKBException se) {
            // TODO
        }
    }

    @Override
    public String getSystemProperty(String name) {
        try {
            this.restoreProperties();
            return this.inmemoKB.getSystemProperty(name);
        }
        catch(SharkKBException se) {
            // TODO
        }
        
        return null;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.restoreProperties();
        this.inmemoKB.setProperty(name, value);
        this.storageSaveProperties(this, inmemoKB);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        this.restoreProperties();
        return this.inmemoKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.restoreProperties();
        this.inmemoKB.setProperty(name, value, transfer);
        this.storageSaveProperties(this, inmemoKB);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.restoreProperties();
        this.inmemoKB.removeProperty(name);
        this.storageSaveProperties(this, inmemoKB);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        this.restoreProperties();
        return this.inmemoKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        this.restoreProperties();
        return this.inmemoKB.propertyNames(all);
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        this.restoreAll();
        ASIPInformationSpace mergeInformation = this.inmemoKB.mergeInformation(information, space);
        
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformationSpace(this, mergeInformation);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(content, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(contentIS, numberOfBytes, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(content, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(name, content, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(name, content, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        this.restoreAll();
        ASIPInformation addInformation = this.inmemoKB.addInformation(name, contentIS, numberOfBytes, semanticAnnotations);
        this.storageSaveAll(this.inmemoKB);
        
        return this.wrapASIPInformation(this, addInformation);
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this.restoreAll();
        this.inmemoKB.removeInformation(info, infoSpace);
        this.storageSaveAll(inmemoKB);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        this.restoreAll();
        Iterator<ASIPInformation> information = this.inmemoKB.getInformation(infoSpace);
        return this.wrapInformation(information);
    }
    
    private Iterator<ASIPInformation> wrapInformation(Iterator<ASIPInformation> infoIter) {
        if(infoIter == null || !infoIter.hasNext()) return infoIter;
        
        ArrayList<ASIPInformation> newInfoList = new ArrayList<>();
        // not empty
        while(infoIter.hasNext()) {
            ASIPInformation info = infoIter.next();
            newInfoList.add(this.wrapASIPInformation(this, info));
        }
        
        return newInfoList.iterator();
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        this.restoreAll();
        Iterator<ASIPInformation> information = this.inmemoKB.getInformation(infoSpace, fullyInside, matchAny);
        return this.wrapInformation(information);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        this.restoreAll();
        Iterator<ASIPInformationSpace> informationSpaces = this.inmemoKB.informationSpaces();
        return this.wrapASIPInformationSpaces(informationSpaces);
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this.restoreAll();
        this.inmemoKB.removeInformation(space);
        this.storageSaveAll(inmemoKB);
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        this.restoreAll();
        return this.inmemoKB.getNumberInformation();
    }
    
    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this.asASIPInterest();
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        STSet topicsSet = this.getTopicSTSet();
        STSet typesSet = this.getTypeSTSet();
        PeerSTSet peersSet = this.getPeerSTSet();
        SpatialSTSet locations = this.getSpatialSTSet();
        TimeSTSet times = this.getTimeSTSet();
        

        // hide semantic tags
        locations.setEnumerateHiddenTags(true);
        times.setEnumerateHiddenTags(true);
        topicsSet.setEnumerateHiddenTags(true);
        typesSet.setEnumerateHiddenTags(true);
        peersSet.setEnumerateHiddenTags(true);

        return InMemoSharkKB.createInMemoASIPInterest(
                topicsSet,
                typesSet,
                this.getOwner(),
                peersSet,
                peersSet,
                times,
                locations,
                ASIPSpace.DIRECTION_INOUT);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        this.inmemoKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        this.inmemoKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this.inmemoKB.semanticTagChanged(tag, stset);
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this;
    }
    
    ////////////////////////////////////////////////////////////////////////
    //   abstract methods - to be implemented by actual implementations   //
    ////////////////////////////////////////////////////////////////////////
    
    public abstract void storageSetOwner(PeerSemanticTag owner) throws SharkKBException;

    public abstract PeerSemanticTag storageGetOwner() throws SharkKBException;

    public abstract void storageSaveAll(InMemoSharkKB inmemoKB) throws SharkKBException;

    public abstract void storageRestoreAll(InMemoSharkKB inmemoKB) throws SharkKBException;
    
    public abstract void storageSaveProperties(PersistentSharkKB persistentKB, InMemoSharkKB inmemoKB) throws SharkKBException;

    public abstract void storageRestoreProperties(InMemoSharkKB inmemoKB) throws SharkKBException;

    // wrapper
    public abstract ArrayList<ASIPSpace> wrapASIPSpaceList(PersistentSharkKB persistentKB, ArrayList<ASIPSpace> assimilate);

    public abstract Knowledge wrapKnowledge(PersistentSharkKB persistentKB, Knowledge extract);

    public abstract ASIPInformationSpace wrapASIPInformationSpace(PersistentSharkKB persistentKB, ASIPInformationSpace space);

    public abstract ASIPSpace wrapASIPSpace(PersistentSharkKB persistentKB, ASIPSpace aSpace);

    public abstract STSet wrapSTSet(PersistentSharkKB persistentKB, STSet topicSTSet);

    public abstract SemanticNet wrapSemanticNet(PersistentSharkKB persistentKB, SemanticNet topicsAsSemanticNet);

    public abstract Taxonomy wrapTaxonomy(PersistentSharkKB persistentKB, Taxonomy topicsAsTaxonomy);

    public abstract STSet wrapSTSet(PersistentSharkKB persistentKB, STSet topicSTSet, int dim);

    public abstract SemanticNet wrapSemanticNet(PersistentSharkKB persistentKB, SemanticNet topicsAsSemanticNet, int dim);

    public abstract Taxonomy wrapTaxonomy(PersistentSharkKB persistentKB, Taxonomy topicsAsTaxonomy, int dim);

    public abstract PeerSTSet wrapPeerSTSet(PersistentSharkKB persistentKB, PeerSTSet peerSTSet);

    public abstract PeerSemanticNet wrapPeerSemanticNet(PersistentSharkKB persistentKB, PeerSemanticNet peersAsSemanticNet);

    public abstract PeerTaxonomy wrapPeerTaxonomy(PersistentSharkKB persistentKB, PeerTaxonomy peersAsTaxonomy);

    public abstract TimeSTSet wrapTimeSTSet(PersistentSharkKB persistentKB, TimeSTSet timeSTSet);

    public abstract SpatialSTSet wrapSpatialSTSet(PersistentSharkKB persistentKB, SpatialSTSet spatialSTSet);

    public abstract ASIPInterest wrapASIPInterest(PersistentSharkKB persistentKB, ASIPInterest contextualize);

    public abstract ASIPInformation wrapASIPInformation(PersistentSharkKB persistentKB, ASIPInformation addInformation);

}
