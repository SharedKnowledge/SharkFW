package net.sharkfw.knowledgeBase.rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFSNSemanticTag extends RDFSemanticTag implements SNSemanticTag {

	/**
	 * CREATE (write in db) constructor
	 * 
	 * @param kb
	 * @param sis
	 * @param topic
	 * @param MODEL
	 * @throws SharkKBException
	 */
	public RDFSNSemanticTag(RDFSharkKB kb, String[] sis, String topic, String MODEL) throws SharkKBException {

		super(kb, sis, topic, MODEL);
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
	public RDFSNSemanticTag(RDFSharkKB kb, String si, String MODEL) throws SharkKBException {

		super(kb, si, MODEL);
	}

	@Override
	public void setPredicate(String type, SNSemanticTag target) {
		Dataset dataset = this.getKb().getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG_P);
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
		Dataset dataset = this.getKb().getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG_P);
		StmtIterator result = null;
		List<String> predicates = new ArrayList<String>();
		try {
			result = m.listStatements(m.getResource(this.getSi()[0]), null, (String) null);
			while (result.hasNext()) {
				predicates.add(result.next().getPredicate().toString());
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		} finally {
			dataset.end();
		}
		return Collections.enumeration(predicates);
	}

	@Override
	public Enumeration<SNSemanticTag> targetTags(String predicateName) {
		Dataset dataset = this.getKb().getDataset();
		dataset.begin(ReadWrite.READ);
		Model mP = dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG_P);
		String si = null;
		StmtIterator result = null;
		List<SNSemanticTag> targetTags = new ArrayList<SNSemanticTag>();
		try {
			result = mP.listStatements(mP.getResource(this.getSi()[0]), mP.getProperty(RDFConstants.SEMANTIC_NET_PREDICATE + predicateName), (String) null);
			dataset.end();
			while (result.hasNext()) {
				si = result.next().getObject().toString();
				targetTags.add(new RDFSNSemanticTag(this.getKb(), si, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG));
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		finally {
			dataset.end();
		}		
		return Collections.enumeration(targetTags);
	}

	@Override
	public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
		Dataset dataset = this.getKb().getDataset();
		dataset.begin(ReadWrite.READ);
		Model mP = dataset.getNamedModel(RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG_P);
		String si = null;
		StmtIterator result = null;
		List<SNSemanticTag> targetTags = new ArrayList<SNSemanticTag>();
		try {
			result = mP.listStatements(null, mP.getProperty(RDFConstants.SEMANTIC_NET_PREDICATE + predicateName), this.getSi()[0]);
			dataset.end();
			while (result.hasNext()) {
				si = result.next().getSubject().toString();
				targetTags.add(new RDFSNSemanticTag(this.getKb(), si, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG));
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		finally {
			dataset.end();
		}		
		return Collections.enumeration(targetTags);
	}
	
	@Override
	public void addSI(String si) throws SharkKBException {

		addSIModelIndependenent(si, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}
	
	@Override
	public void removeSI(String si) throws SharkKBException {
		
		this.removeSIModelIndependenent(si, RDFConstants.SEMANTIC_NET_MODEL_SEMANTIC_TAG);
	}

	@Override
	public Enumeration<String> targetPredicateNames() {
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

}
