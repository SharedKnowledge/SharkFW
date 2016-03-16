package net.sharkfw.knowledgeBase.rdf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Implements the Semantic Tag with RDF Jena. The tag will be stored in form of one or
 * multiple RDF triples within the RDFSharkKB.
 * 
 * @author Barret dfe
 *
 */
public class RDFSemanticTag implements SemanticTag {

	private String[] si;

	private String topic;

	private RDFSharkKB kb;

	public RDFSemanticTag() {
		si = null;
		topic = null;
	}

	public RDFSemanticTag(String[] si, String topic) {
		this.si = si;
		this.topic = topic;
	}

	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/
	/**
	 * Search and retrieve a Semantic Tag which has been previously stored in a RDFSharkKB.
	 * 
	 * @param kb the knowledge base from which the tag will be retrieved
	 * @param si one of the SIs from the tag
	 * @param MODEL the model from which the tag will be retrieved
	 */
	public RDFSemanticTag(RDFSharkKB kb, String si, String MODEL) {

		this.kb = kb;
		List<String> list = new ArrayList<String>();
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(MODEL);
		StmtIterator statementOfSi = m.listStatements(m.getResource(si), m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), (String) null);
		String topic = statementOfSi.next().getObject().toString();
		this.setTopic(topic);
		StmtIterator sisOfTag = m.listStatements(null, m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
		while (sisOfTag.hasNext()) {
			list.add(sisOfTag.next().getSubject().getURI());
		}
		this.setSi(list.toArray(new String[list.size()]));
		dataset.end();
	}

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/
	/**
	 * Create a Semantic Tag in a RDFSharkKB in the form of one or multiple RDF triples.
	 * 
	 * @param kb the knowledge base in which the tag will be stored
	 * @param the SI of the tag
	 * @param the topic of the new tag
	 * @param MODEL the model in which the tag will be stored
	 */
	public RDFSemanticTag(RDFSharkKB kb, String si, String topic, String MODEL) {
		this(kb, new String[] { si }, topic, MODEL);
	}

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/
	/**
	 * Create a Semantic Tag in a RDFSharkKB in the form of one or multiple RDF triples.
	 * 
	 * @param kb the knowledge base from which the tag will be retrieved
	 * @param si one of the SIs from the tag
	 * @param MODEL the model from which the tag will be retrieved
	 */
	public RDFSemanticTag(RDFSharkKB kb, String[] si, String topic, String MODEL) {

		this.kb = kb;
		Dataset dataset = this.kb.getDataset();
		this.si = si;
		this.topic = topic;
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(MODEL);
			try {
				Statement s = m.createStatement(m.createResource(si[i]), m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);
				dataset.commit();
			} finally {
				dataset.end();
			}
		}
	}

	@Override
	public void addSI(String si) throws SharkKBException {

		this.addSIModelIndependenent(si, RDFConstants.ST_MODEL_NAME);
	}

	/**
	 * This method can be used from different kinds (deriving tags) of Semantic Tags for adding a new SI 
	 * to the tag. 
	 * 
	 * @param newSI the new SI which will be added to the tag
	 * @param MODEL the model (and thus kind of semantic tag) in which the existing tag shall be changed
	 * @throws SharkKBException
	 */
	public void addSIModelIndependenent(String newSI, String MODEL) throws SharkKBException {

		Dataset dataset = this.kb.getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(MODEL);		
		try {
			Statement s = m.createStatement(m.createResource(newSI), m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
			m.add(s);
			dataset.commit();
		} finally {
			dataset.end();
		}
		String [] newSIs = new String[si.length + 1];
		for (int i = 0; i < newSIs.length - 1; i++)
		{
			newSIs[i] = si[i];
		}
		newSIs[si.length] = newSI;
		setSi(newSIs);
	}
	
	@Override
	public void removeSI(String si) throws SharkKBException {
		
		this.removeSIModelIndependenent(si, RDFConstants.ST_MODEL_NAME);
	}
	
	public void removeSIModelIndependenent(String oldSI, String MODEL) throws SharkKBException {

		Dataset dataset = this.kb.getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(MODEL);		
		try {
			Statement s = m.createStatement(m.getResource(oldSI), m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
			m.remove(s);
			dataset.commit();
		} finally {
			dataset.end();
		}
		
		List<String> list = new ArrayList<String>();
		dataset.begin(ReadWrite.READ);
		StmtIterator sisOfTag = m.listStatements(null, m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
		while (sisOfTag.hasNext()) {
			list.add(sisOfTag.next().getSubject().getURI());
		}
		this.setSi(list.toArray(new String[list.size()]));
		dataset.end();
	}

	public String[] getSi() {
		return si;
	}

	public void setSi(String[] si) {
		this.si = si;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String getName() {
		return topic;
	}

	@Override
	public String[] getSI() {
		return si;
	}

	public RDFSharkKB getKb() {
		return kb;
	}

	@Override
	public String getSystemProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSystemProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProperty(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> propertyNames() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> propertyNames(boolean arg0) throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeProperty(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(String arg0, String arg1) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(String arg0, String arg1, boolean arg2) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hidden() {
		return false;
	}

	@Override
	public boolean identical(SemanticTag arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAny() {
		return false;
	}

	@Override
	public void merge(SemanticTag arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHidden(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String arg0) {
		// TODO Auto-generated method stub

	}

}
