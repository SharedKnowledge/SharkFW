package net.sharkfw.knowledgeBase.rdf;

import java.util.Enumeration;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;

public class RDFPeerSNSemanticTag extends RDFPeerSemanticTag implements PeerSNSemanticTag  {
	
	/**
	 * CREATE (write in db) constructor
	 * 
	 * @param kb
	 * @param sis
	 * @param topic
	 * @param MODEL
	 * @throws SharkKBException
	 */
	public RDFPeerSNSemanticTag(RDFSharkKB kb, String[] sis, String topic, String[] addresses, String MODEL) throws SharkKBException {
		super(kb, sis, topic, addresses, MODEL);
	}

	/**
	 * GET (read in db) constructor
	 * 
	 * @param kb
	 * @param sis
	 * @param topic
	 * @param MODEL
	 * @throws SharkKBException
	 */
	public RDFPeerSNSemanticTag(RDFSharkKB kb, String si, String MODEL) throws SharkKBException {
		super(kb, si, MODEL);
	}
	
	@Override
	public void setPredicate(String type, SNSemanticTag target) {
		Dataset dataset = this.getKb().getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_PEER_SEMANTIC_TAG_P);
		try {
			Statement s = m.createStatement(m.createResource(this.getSI()[0]),
					m.createProperty(RDFConstants.SEMANTIC_NET_PREDICATE + type), target.getSI()[0]);
			m.add(s);
			dataset.commit();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			dataset.end();
		}
	}


	@Override
	public Enumeration<String> predicateNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> targetPredicateNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<SNSemanticTag> targetTags(String predicateName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePredicate(String type, SNSemanticTag target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(SNSemanticTag toMerge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Enumeration<SemanticTag> subTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TXSemanticTag getSuperTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<TXSemanticTag> getSubTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(TXSemanticTag supertag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(TXSemanticTag toMerge) {
		// TODO Auto-generated method stub
		
	}



}
