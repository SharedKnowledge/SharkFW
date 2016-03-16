package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 * 
 * @author Barret dfe
 *
 */
public class RDFSpatialSTSet implements SpatialSTSet {

	private RDFSharkKB kb;
	
	public RDFSpatialSTSet(RDFSharkKB kb) {
		
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		dataset.getNamedModel(RDFConstants.SPATIAL_MODEL_NAME);
		dataset.end();		
	}
	
	/************************************************* 
	 *  Implemented and tested methods - BEGIN
	 ************************************************/
	
	@Override
	public RDFSpatialSemanticTag createSpatialSemanticTag(String topic,
			String[] si, SharkGeometry sg) throws SharkKBException {
		return new RDFSpatialSemanticTag(kb, si, topic, sg);
	}

	@Override
	public SpatialSemanticTag createSpatialSemanticTag(String name, String[] si, SharkGeometry[] geoms) throws SharkKBException {
		return null;
	}


	@Override
	public RDFSpatialSemanticTag getSpatialSemanticTag(String si)
			throws SharkKBException {
		return new RDFSpatialSemanticTag(kb, si);
	}
	
	@Override
	public RDFSpatialSemanticTag getSpatialSemanticTag(String[] si)
			throws SharkKBException {
		return this.getSpatialSemanticTag(si[0]);
		}

	@Override
	public RDFSemanticTag getSemanticTag(String si) throws SharkKBException {
		return new RDFSemanticTag(kb, si, RDFConstants.SPATIAL_MODEL_NAME);
	}

	@Override
	public RDFSemanticTag getSemanticTag(String[] si) throws SharkKBException {
		return this.getSemanticTag(si[0]);
	}
	
	/************************************************* 
	 *  Implemented and tested methods - END
	 ************************************************/
	
	@Override
	public void addListener(STSetListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public STSet contextualize(Enumeration<SemanticTag> arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet contextualize(STSet arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet contextualize(Enumeration<SemanticTag> arg0,
			FragmentationParameter arg1) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet contextualize(STSet arg0, FragmentationParameter arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticTag createSemanticTag(String arg0, String[] arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticTag createSemanticTag(String arg0, String arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet fragment(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STSet fragment(SemanticTag arg0, FragmentationParameter arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FragmentationParameter getDefaultFP() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Iterator<SemanticTag> getSemanticTagByName(String arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SemanticTag merge(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void merge(STSet arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(STSetListener arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSemanticTag(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSemanticTag(String si) throws SharkKBException {

	}

	@Override
	public void removeSemanticTag(String[] sis) throws SharkKBException {

	}

	@Override
	public void setDefaultFP(FragmentationParameter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnumerateHiddenTags(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<SemanticTag> stTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<SemanticTag> tags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpatialSTSet contextualize(SpatialSTSet arg0,
			FragmentationParameter arg1) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public double getDistance(SpatialSemanticTag arg0, SpatialSemanticTag arg1) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public boolean isInRange(SpatialSemanticTag arg0, SpatialSemanticTag arg1,
			double arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Enumeration<SpatialSemanticTag> spatialTags()
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

}
