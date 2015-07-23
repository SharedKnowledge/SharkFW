package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

public class RDFSemanticNet implements SemanticNet {
	
	private RDFSharkKB kb;

		public RDFSemanticNet(RDFSharkKB kb) {
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_NAME);
		dataset.end();	
		}
		

	@Override
	public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEnumerateHiddenTags(boolean hide) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Enumeration<SemanticTag> tags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SemanticTag> stTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FragmentationParameter getDefaultFP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultFP(FragmentationParameter fp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(STSet stSet) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(STSetListener listen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(STSetListener listener) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public STSet asSTSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SNSemanticTag createSemanticTag(String topic, String[] sis) throws SharkKBException {
		RDFSemanticTag tag = new RDFSemanticTag(kb, sis, topic, RDFConstants.SEMANTIC_NET_MODEL_NAME);
		return new RDFSNSemanticTag(tag);
	}

	@Override
	public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticNet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SemanticNet contextualize(STSet context) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SNSemanticTag merge(SemanticTag source) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(SemanticTag tag) throws SharkKBException {
		// TODO Auto-generated method stub
		
	}

}
