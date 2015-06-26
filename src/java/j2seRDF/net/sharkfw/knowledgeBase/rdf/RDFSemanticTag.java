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
 * 
 * @author Barret dfe
 *
 */
public class RDFSemanticTag implements SemanticTag {

	private String[] si;

	private String topic;
	
	public RDFSemanticTag() {
		si = null;
		topic = null;		
	}
	
	public RDFSemanticTag(String[] si, String topic) {
		this.si = si;
		this.topic = topic;
	}
	
	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/

	public RDFSemanticTag(RDFSharkKB kb, String si, String MODEL) {

		List<String> list = new ArrayList<String>();
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(MODEL);
		StmtIterator statementOfSi = m.listStatements(m.getResource(si),
				m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE),
				(String) null); 
		String topic = statementOfSi.next().getObject().toString();
		this.setTopic(topic);
		StmtIterator sisOfTag = m.listStatements(null,
				m.getProperty(RDFConstants.SEMANTIC_TAG_PREDICATE),
				topic);
		while (sisOfTag.hasNext()) {
			list.add(sisOfTag.next().getSubject().getURI());
		}
		this.setSi(list.toArray(new String [list.size()]));
		dataset.end();
	}	
	
	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/
	
	public RDFSemanticTag(RDFSharkKB kb, String si, String topic, String MODEL) {
		this(kb, new String [] {si}, topic, MODEL);		
	}	
	
	public RDFSemanticTag(RDFSharkKB kb, String[] si, String topic, String MODEL) {
		
		Dataset dataset = kb.getDataset();
		this.si = si;
		this.topic = topic;
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(MODEL);
			try {
				Statement s = m.createStatement(m.createResource(si[i]),
						m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);				
				dataset.commit();
			} finally {
				dataset.end();
			}
		}
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
	public Enumeration<String> propertyNames(boolean arg0)
			throws SharkKBException {
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
	public void setProperty(String arg0, String arg1, boolean arg2)
			throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSI(String arg0) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return topic;
	}

	@Override
	public String[] getSI() {
		return si;
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
	public void removeSI(String arg0) throws SharkKBException {
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
	
	

}
