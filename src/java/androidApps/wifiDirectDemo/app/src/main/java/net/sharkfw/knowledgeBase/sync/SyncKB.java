package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

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
    public Interest createInterest() throws SharkKBException {
        return _localKB.createInterest();
    }

    @Override
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
        return _localKB.createInterest(cc);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        return _localKB.createSemanticTag(name, sis);
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return _localKB.createSemanticTag(name, si);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        return _localKB.createPeerSemanticTag(name, sis, addresses);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return _localKB.createPeerSemanticTag(name, si, address);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return _localKB.createPeerSemanticTag(name, sis, address);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return _localKB.createPeerSemanticTag(name, si, addresses);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException {
        return _localKB.createSpatialSemanticTag(name, sis);
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        return _localKB.createSpatialSemanticTag(name, sis, geom);
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        return _localKB.createTimeSemanticTag(from, duration);
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        _localKB.removeSemanticTag(sis);
    }

    @Override
    public void removeSemanticTag(SemanticTag st) throws SharkKBException {
        this.removeSemanticTag(st.getSI());
    }

    @Override
    public void persist() {
    	_localKB.persist();
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
    public SemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return _localKB.getSemanticTag(sis);
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        return _localKB.getSemanticTag(si);
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
        return _localKB.getPeerSemanticTag(sis);
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException {
        return _localKB.getPeerSemanticTag(si);
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
    public Enumeration<SemanticTag> tags() throws SharkKBException {
    	return _localKB.tags();
    }

    @Override
    public Iterator<SemanticTag> getTags() throws SharkKBException {
    	return _localKB.getTags();
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
}
