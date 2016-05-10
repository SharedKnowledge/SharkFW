package net.sharkfw.knowledgeBase.sync;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import net.sharkfw.asip.ASIPSpace;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.knowledgeBase.*;

/**
 * Implements a synchronized KB. Delegates most of its
 * functionality to the internal KB.
 * @author simonArnold
 * @author hellerve
 */
public class SyncKB implements SharkKB {
    
    SharkKB _localKB = null;

      ////////////////////////////////////////////////////////////////////////
     //                      constructor                                   //
    ////////////////////////////////////////////////////////////////////////
    /**
     * Construct a SyncKB that synchronizes a SharkKB.
     * @param kb The SharkKB that should be synchronized
     * @throws SharkKBException
     */
    public SyncKB(SharkKB kb) throws SharkKBException  {
        _localKB = kb;
        
        Enumeration<ContextPoint> contextPoints = _localKB.getAllContextPoints();
        
        if(contextPoints == null)
        	return;
        
        while (contextPoints.hasMoreElements()) {
            ContextPoint cp = contextPoints.nextElement();
            Enumeration<Information> cpInformations = cp.enumInformation();
            while (cpInformations.hasMoreElements()) {
                Information information = cpInformations.nextElement();
                if (information.getProperty(SyncInformation.VERSION_PROPERTY_NAME) == null) {
                    information.setProperty(SyncInformation.VERSION_PROPERTY_NAME, SyncInformation.VERSION_DEFAULT_VALUE, true); // True for transferable
                }
            }
            if(cp.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME) == null){
                cp.setProperty(SyncContextPoint.VERSION_PROPERTY_NAME, SyncContextPoint.VERSION_DEFAULT_VALUE, true); // True for transferable
            }
        }
    }
    
    
      ///////////////////////////////////////////////////////////////////////
     //              mostly delegated functions                           //
    ///////////////////////////////////////////////////////////////////////
   @Override
    public void setOwner(PeerSemanticTag owner) {
        _localKB.setOwner(owner);
    }

    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        ContextPoint localCP = _localKB.getContextPoint(coordinates);
        if (localCP == null) return null;
    	SyncContextPoint sp = new SyncContextPoint(localCP);
    	return sp;
    }

    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
    	return _localKB.createContextCoordinates(topic, originator, peer, remotepeer, time, location, direction);
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
    	return new SyncContextPoint(_localKB.createContextPoint(coordinates));
    }
    
    /**
     * Replaces contextpoint with another contextpoint
     * @param cp
     * @throws SharkKBException 
     */
    public void replaceContextPoint(ContextPoint cp) throws SharkKBException {
        // remove the old one
        removeContextPoint(cp.getContextCoordinates());
        
        // Create new one and add all information to it
        ContextPoint newCP = createContextPoint(cp.getContextCoordinates());
        Iterator<Information> cpInfo = cp.getInformation();
        while(cpInfo.hasNext()){
            newCP.addInformation(cpInfo.next());
        }
        newCP.setProperty(SyncContextPoint.VERSION_PROPERTY_NAME, cp.getProperty(SyncContextPoint.VERSION_PROPERTY_NAME));
        newCP.setProperty(SyncContextPoint.TIMESTAMP_PROPERTY_NAME, cp.getProperty(SyncContextPoint.TIMESTAMP_PROPERTY_NAME));
    }
    
    @Override
    public Knowledge createKnowledge() {
        return _localKB.createKnowledge();
    }

    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        _localKB.removeContextPoint(coordinates);
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
    	Enumeration<ContextPoint> enumerated = _localKB.getContextPoints(cs);
	Vector<ContextPoint> temp = new Vector<ContextPoint>();
    	while(enumerated.hasMoreElements()){
    		temp.add(new SyncContextPoint(enumerated.nextElement()));
    	}
    	return temp.elements();
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
    	Enumeration<ContextPoint> enumerated = _localKB.getContextPoints(cs, matchAny);
	Vector<ContextPoint> temp = new Vector<ContextPoint>();
    	while(enumerated.hasMoreElements()){
    		temp.add(new SyncContextPoint(enumerated.nextElement()));
    	}
    	return temp.elements();
    }

    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
    	Enumeration<ContextPoint> enumerated = _localKB.getAllContextPoints();
	Vector<ContextPoint> temp = new Vector<ContextPoint>();
    	while(enumerated.hasMoreElements()){
    		temp.add(new SyncContextPoint(enumerated.nextElement()));
    	}
    	return temp.elements();
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        _localKB.addListener(kbl);
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        _localKB.removeListener(kbl);
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        _localKB.setStandardFPSet(fps);
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return _localKB.getStandardFPSet();
    }

    @Override
    public PeerSemanticTag getOwner() {
        return _localKB.getOwner();
    }

    @Override
    public SharkCS asSharkCS() {
    	return _localKB.asSharkCS();
    }

    @Override
    public Interest asInterest() {
    	return _localKB.asInterest();
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        return _localKB.getTopicSTSet();
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        return _localKB.getTopicsAsSemanticNet();
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return _localKB.getTopicsAsTaxonomy();
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return this._localKB.getTypeSTSet();
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return this._localKB.getTypesAsSemanticNet();
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return this._localKB.getTypesAsTaxonomy();
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return _localKB.getPeerSTSet();
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return _localKB.getPeersAsSemanticNet();
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return _localKB.getPeersAsTaxonomy();
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return _localKB.getTimeSTSet();
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return _localKB.getSpatialSTSet();
    }

    @Override
    public Interest contextualize(SharkCS as) throws SharkKBException {
    	return _localKB.contextualize(as);
    }

    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
    	return _localKB.contextualize(as);
    }

    @Override
    public void setSystemProperty(String name, String value) {
        _localKB.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return _localKB.getSystemProperty(name);
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        _localKB.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return _localKB.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        _localKB.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        _localKB.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return _localKB.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return _localKB.propertyNames(all);
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        _localKB.semanticTagCreated(tag, stset);
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        _localKB.semanticTagRemoved(tag, stset);
    }

    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        _localKB.addInterest(interest);
    }

    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        _localKB.removeInterest(interest);
    }

    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        return _localKB.interests();
    }

    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        return _localKB.contextPoints(cs);
    }

    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        return _localKB.contextPoints(cs, matchAny);
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        this._localKB.semanticTagChanged(tag, stset);
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        this._localKB.removeInformation(space);
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return this._localKB.informationSpaces(as, matchAny);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return this._localKB.contextualize(as);
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return this._localKB.contextualize(as, fps);
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return this._localKB.asASIPSpace();
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return this._localKB.asASIPInterest();
    }

//    @Override
//    public Iterator<InformationPoint> informationPoints() throws SharkKBException {
//        return this._localKB.informationPoints();
//    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return this._localKB.informationSpaces();
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return this._localKB.getVocabulary();
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return this._localKB.getNumberInformation();
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        return this._localKB.assimilate(target, interest, backgroundFP, knowledge, learnTags, deleteAssimilated);
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return this._localKB.extract(context);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return this._localKB.extract(context, fp);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return this._localKB.extract(context, backgroundFP, recipient);
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return this._localKB.extract(context, backgroundFP, cutGroups);
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return this._localKB.extract(target, context, backgroundFP, cutGroups, recipient);
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this._localKB.getAllInformationSpaces();
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return this._localKB.mergeInformation(information, space);
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        this._localKB.removeInformation(info, infoSpace);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return this._localKB.getInformation(infoSpace);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return this._localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return this._localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) throws SharkKBException {
        return this._localKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations);
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this._localKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return this._localKB.addInformation(content, semanticAnnotations);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return this._localKB.getInformation(infoSpace, fullyInside, matchAny);
    }
}
