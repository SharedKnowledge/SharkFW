package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
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
public class RDFPeerSTSet implements PeerSTSet {

	private RDFSharkKB kb;
	
	public RDFPeerSTSet(RDFSharkKB kb) {
		
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		dataset.end();		
	}
	

	@Override
	public RDFPeerSemanticTag getSemanticTag(String[] si)
			throws SharkKBException {
		return getSemanticTag(si[0]);
	}

	@Override
	public RDFPeerSemanticTag getSemanticTag(String si) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, si);
	}
	
	@Override
	public RDFPeerSemanticTag createPeerSemanticTag(String topic, String[] si,
			String[] addresses) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, si, topic, addresses);
	}

	@Override
	public RDFPeerSemanticTag createPeerSemanticTag(String topic, String[] si,
			String address) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, si, topic, new String[] {address});
	}
	
	@Override
	public RDFPeerSemanticTag createPeerSemanticTag(String topic, String si,
			String[] addresses) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, new String[] {si}, topic, addresses);
	}
	
	@Override
	public RDFPeerSemanticTag createPeerSemanticTag(String topic, String si,
			String address) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, new String[] {si}, topic,new String[] {address});
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
	public void addListener(STSetListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSemanticTag(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		
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
	public PeerSTSet contextualize(Enumeration<SemanticTag> arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet contextualize(STSet arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet contextualize(Enumeration<SemanticTag> arg0,
			FragmentationParameter arg1) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet contextualize(STSet arg0, FragmentationParameter arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public PeerSTSet fragment(SemanticTag arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSTSet fragment(SemanticTag arg0, FragmentationParameter arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Enumeration<PeerSemanticTag> peerTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
