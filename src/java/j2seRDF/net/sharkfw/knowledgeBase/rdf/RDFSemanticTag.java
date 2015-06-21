package net.sharkfw.knowledgeBase.rdf;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFSemanticTag implements SemanticTag {

	private String[] si;

	private String topic;
	
	public RDFSemanticTag(String[] si, String topic) {
		this.si = si;
		this.topic = topic;
	}
	
	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/

	public RDFSemanticTag(RDFSharkKB kb, String si, String MODEL) {

		//ResultSet rs = null;
		Iterator<Triple> resultModel = null;
		List<Triple> list = new ArrayList<Triple>();
		String rdfSi = "<" + si + ">";
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		//String qs1 = "SELECT *" + " WHERE {" + "?s " + "?p ?o .}";
		String qs1 = "DESCRIBE " + rdfSi;
		//kb.getDataset().getNamedModel(MODEL).write(System.out);
		try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset.getNamedModel(RDFConstants.ST_MODEL_NAME))) {
			resultModel = qExec.execDescribeTriples();		
			while (resultModel.hasNext()) {
				list.add(resultModel.next());
			}
		} finally {
			dataset.end();
		}
		this.topic = list.get(0).getObject().toString();
		this.si = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			this.si[i] = list.get(i).getSubject().toString();
		}

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
						m.createProperty(si[i] + RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
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

}
