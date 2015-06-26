package net.sharkfw.knowledgeBase.rdf;

import java.util.ArrayList;
import java.util.List;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

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
public class RDFPeerSemanticTag extends RDFSemanticTag
		implements
			PeerSemanticTag {

	private String[] address;

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/
	
	public RDFPeerSemanticTag(RDFSharkKB kb, String[] si, String topic,
			String[] addresses) {
		super(si, topic);
		this.address = addresses;
		Dataset dataset = kb.getDataset();
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
			try {
				Statement s = m.createStatement(m.createResource(si[i]),
						m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE),
						topic);
				m.add(s);
				m.getResource(si[i]).addProperty(
						m.createProperty(RDFConstants.PEER_TAG_PREDICATE),
						m.createResource(si[i]
								+ RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS));

				for (int j = 0; j < addresses.length; j++) {
					m.createResource(
							si[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS)
							.addProperty(
									m.createProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE),
									addresses[j]);
				}

				dataset.commit();

			} finally {
				dataset.end();
			}
		}

	}

	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/
	
	public RDFPeerSemanticTag(RDFSharkKB kb, String si) {
		super(kb, si, RDFConstants.PEER_MODEL_NAME);
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		StmtIterator statementOfAddress = m.listStatements(m.getResource(si+ RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS),
				m.getProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE),
				(String) null);
		List<String> addressesList = new ArrayList<String>();
		while (statementOfAddress.hasNext()) {
			addressesList.add(statementOfAddress.next().getObject().toString());
		}
		address = addressesList.toArray(new String [addressesList.size()]);		
		dataset.end();

	}

	@Override
	public void addAddress(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getAddresses() {
		return address;
	}

	@Override
	public void removeAddress(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAddresses(String[] arg0) {
		// TODO Auto-generated method stub

	}

}
