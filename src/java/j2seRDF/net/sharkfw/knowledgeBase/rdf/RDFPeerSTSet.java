package knowledgeBase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

public class RDFPeerSTSet implements PeerSTSet {

	private RDFSharkKB kb;
	
	private Model model;
	
	public RDFPeerSTSet(RDFSharkKB kb) {
		
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		model = dataset.getNamedModel("Peer");
		dataset.end();		
	}
	
	@Override
	public RDFPeerSemanticTag createPeerSemanticTag(String topic, String[] si,
			String[] addresses) throws SharkKBException {
		return new RDFPeerSemanticTag(kb, si, topic, addresses);
	}

	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1,
			String arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String[] arg2) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String arg2) throws SharkKBException {
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
	public PeerSemanticTag getSemanticTag(String[] arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PeerSemanticTag getSemanticTag(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<PeerSemanticTag> peerTags() {
		// TODO Auto-generated method stub
		return null;
	}

}
