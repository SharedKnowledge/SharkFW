package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

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
	}

	@Override
	public RDFSNSemanticTag createSemanticTag(String topic, String[] sis) throws SharkKBException {
		return new RDFSNSemanticTag(kb, sis, topic, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}

	@Override
	public RDFSNSemanticTag createSemanticTag(String topic, String si) throws SharkKBException {
		return new RDFSNSemanticTag(kb, new String[] { si }, topic, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}

	@Override
	public RDFSNSemanticTag getSemanticTag(String si) throws SharkKBException {
		return new RDFSNSemanticTag(kb, si, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}

	@Override
	public RDFSNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
		return new RDFSNSemanticTag(kb, sis[0], RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}

	@Override
	public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
		source.setPredicate(type, target);
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
	public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
		// TODO Auto-generated method stub

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
	public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {
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
