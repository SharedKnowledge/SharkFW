package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

/**
 * 
 * @author Barret dfe
 *
 */
public class RDFSTSet implements STSet {

	private RDFSharkKB kb;
	
	public RDFSTSet(RDFSharkKB kb) {
		
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		dataset.getNamedModel("Topic");
		dataset.end();		
	}	

	@Override
	public RDFSemanticTag createSemanticTag(String name, String[] si)
			throws SharkKBException {
		return new RDFSemanticTag(kb, si, name, RDFConstants.ST_MODEL_NAME);
	}

	@Override
	public RDFSemanticTag createSemanticTag(String si, String name)
			throws SharkKBException {
		 return new RDFSemanticTag(kb, si, name, RDFConstants.ST_MODEL_NAME);
	}



	@Override
	public RDFSemanticTag getSemanticTag(String[] si) throws SharkKBException {
		return new  RDFSemanticTag(kb, si[0], RDFConstants.ST_MODEL_NAME);
	}

	@Override
	public RDFSemanticTag getSemanticTag(String si) throws SharkKBException {
		return new RDFSemanticTag(kb, si, RDFConstants.ST_MODEL_NAME);
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
	public void removeSemanticTag(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Iterator<SemanticTag> stTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void addListener(STSetListener arg0) {
		
		
	}

	@Override
	public STSet contextualize(Enumeration<SemanticTag> arg0)
			throws SharkKBException {
		
		return null;
	}

	@Override
	public STSet contextualize(STSet arg0) throws SharkKBException {
		
		return null;
	}

	@Override
	public STSet contextualize(Enumeration<SemanticTag> arg0,
			FragmentationParameter arg1) throws SharkKBException {
		
		return null;
	}

	@Override
	public STSet contextualize(STSet arg0, FragmentationParameter arg1)
			throws SharkKBException {
		
		return null;
	}


	@Override
	public SemanticTag merge(SemanticTag arg0) throws SharkKBException {
		
		return null;
	}

	@Override
	public void merge(STSet arg0) throws SharkKBException {
		
		
	}

	@Override
	public void removeListener(STSetListener arg0) throws SharkKBException {
		
		
	}



	@Override
	public void setDefaultFP(FragmentationParameter arg0) {
	
		
	}

	@Override
	public STSet fragment(SemanticTag arg0) throws SharkKBException {
		
		return null;
	}

	@Override
	public STSet fragment(SemanticTag arg0, FragmentationParameter arg1)
			throws SharkKBException {
		
		return null;
	}

	@Override
	public FragmentationParameter getDefaultFP() {
		
		return null;
	}
	
	@Override
	public void setEnumerateHiddenTags(boolean arg0) {
		
		
	}

	@Override
	public int size() {
		return 0;
	}



	@Override
	public Enumeration<SemanticTag> tags() throws SharkKBException {
		
		return null;
	}

}
