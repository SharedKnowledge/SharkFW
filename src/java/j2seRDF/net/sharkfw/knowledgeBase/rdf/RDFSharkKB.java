package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

public class RDFSharkKB /*extends AbstractSharkKB*/ implements SharkKB {

	private String directory;

	private Dataset dataset;
	
	public RDFSharkKB(String directory) throws SharkKBException {

		this.directory = directory;
		dataset = TDBFactory.createDataset(directory);
		if (dataset == null) {
			throw new SharkKBException(
					"Error while retrieving the RDFSharkKB from directory: "
							+ directory);
		}
	}
	
	public Dataset getDataset()
	{
		return dataset;
	}
	
	public String getDirectory()
	{
		return directory;
	}

	/** 
	 * @deprecated
	 */
	@Override
	public SemanticTag createSemanticTag(String name, String[] si)
			throws SharkKBException {
		return null;
	}
	/** 
	 * @deprecated
	 */
	@Override
	public SemanticTag createSemanticTag(String si, String name)
			throws SharkKBException {
		RDFSemanticTag tag = new RDFSemanticTag(this, si, name);
		return null;
	}
	/** 
	 * @deprecated
	 */
	@Override
	public SemanticTag getSemanticTag(String si) throws SharkKBException {
		return null;
	}
	/** 
	 * @deprecated
	 */
	@Override
	public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
		return null;
	}

	/** AbstractKb */
	@Override
	public Iterator<SemanticTag> getTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ContextPoint> contextPoints(SharkCS arg0, boolean arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextCoordinates createContextCoordinates(SemanticTag arg0,
			PeerSemanticTag arg1, PeerSemanticTag arg2, PeerSemanticTag arg3,
			TimeSemanticTag arg4, SpatialSemanticTag arg5, int arg6)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextPoint createContextPoint(ContextCoordinates arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Interest createInterest() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Interest createInterest(ContextCoordinates arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Knowledge createKnowledge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<ContextPoint> getAllContextPoints()
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextPoint getContextPoint(ContextCoordinates arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSemanticTag getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOwner(PeerSemanticTag arg0) {
		// TODO Auto-generated method stub
		
	}

	/** 
	 * AbstractKB
	 */
	@Override
	public Interest asInterest() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Abstract
	 */
	@Override
	public SharkCS asSharkCS() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Abstract
	 */
	@Override
	public Interest contextualize(SharkCS arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Abstract
	 */
	@Override
	public Interest contextualize(SharkCS arg0, FragmentationParameter[] arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RDFPeerSTSet getPeerSTSet() throws SharkKBException {
		return new RDFPeerSTSet(this);
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag getPeerSemanticTag(String[] arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag getPeerSemanticTag(String arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpatialSTSet getSpatialSTSet() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeSTSet getTimeSTSet() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RDFSTSet getTopicSTSet() throws SharkKBException {
		return new RDFSTSet(this);
	}

	@Override
	public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Abstract
	 */
	@Override
	public Enumeration<SemanticTag> tags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Abstract
	 */
	@Override
	public String getSystemProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Abstract
	 */
	@Override
	public void setSystemProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Abstract
	 */
	@Override
	public String getProperty(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Abstract
	 */
	@Override
	public Enumeration<String> propertyNames() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Abstract
	 */
	@Override
	public Enumeration<String> propertyNames(boolean arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Abstract
	 */
	@Override
	public void removeProperty(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Abstract
	 */
	@Override
	public void setProperty(String arg0, String arg1) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Abstract
	 */
	@Override
	public void setProperty(String arg0, String arg1, boolean arg2)
			throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagChanged(SemanticTag arg0, STSet arg1) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagCreated(SemanticTag arg0, STSet arg1) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagRemoved(SemanticTag arg0, STSet arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addInterest(SharkCS arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<SharkCS> interests() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeInterest(SharkCS arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * abstract
	 */
	@Override
	public void addListener(KnowledgeBaseListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<ContextPoint> contextPoints(SharkCS arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1,
			String[] arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1,
			String arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String[] arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0,
			String[] arg1) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0,
			String[] arg1, SharkGeometry arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public TimeSemanticTag createTimeSemanticTag(long arg0, long arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0, boolean arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * abstract
	 */
	@Override
	public FragmentationParameter[] getStandardFPSet() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * abstract
	 */
	@Override
	public void persist() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeContextPoint(ContextCoordinates arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * abstract
	 */
	@Override
	public void removeListener(KnowledgeBaseListener arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @deprecated
	 */
	@Override
	public void removeSemanticTag(String[] arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @deprecated
	 */
	@Override
	public void removeSemanticTag(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * abstract
	 */
	@Override
	public void setStandardFPSet(FragmentationParameter[] arg0) {
		// TODO Auto-generated method stub
		
	}

	

}
