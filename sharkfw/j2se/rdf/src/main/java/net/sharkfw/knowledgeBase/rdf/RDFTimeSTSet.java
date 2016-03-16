package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

/**
 * 
 * @author Barret dfe
 *
 */
public class RDFTimeSTSet implements TimeSTSet {

	private RDFSharkKB kb;
	
	public RDFTimeSTSet(RDFSharkKB kb) {
		
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		dataset.getNamedModel(RDFConstants.TIME_MODEL_NAME);
		dataset.end();		
	}
	
	@Override
	public RDFTimeSemanticTag createTimeSemanticTag(long from, long duration)
			throws SharkKBException {
		return new RDFTimeSemanticTag(kb, from, duration);
	}
	
	@Override
	public Iterator<TimeSemanticTag> tstTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	
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
	public SemanticTag getSemanticTag(String[] arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticTag getSemanticTag(String arg0) throws SharkKBException {
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
	public TimeSTSet contextualize(TimeSTSet arg0, FragmentationParameter arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TimeSTSet fragment(TimeSemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<TimeSemanticTag> timeTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}


}
