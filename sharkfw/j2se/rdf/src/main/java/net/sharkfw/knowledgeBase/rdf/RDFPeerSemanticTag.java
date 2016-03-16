package net.sharkfw.knowledgeBase.rdf;

import java.util.ArrayList;
import java.util.List;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
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
public class RDFPeerSemanticTag extends RDFSemanticTag implements PeerSemanticTag {

	private String[] address;

	private RDFSharkKB kb;

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/

	public RDFPeerSemanticTag(RDFSharkKB kb, String[] si, String topic, String[] addresses, String MODEL) {
		super(si, topic);
		this.address = addresses;
		Dataset dataset = kb.getDataset();
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
			try {
				Statement s = m.createStatement(m.createResource(si[i]), m.createProperty(RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);
				m.getResource(si[i])
						.addProperty(m.createProperty(RDFConstants.PEER_TAG_PREDICATE), m.createResource(si[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS));

				for (int j = 0; j < addresses.length; j++) {
					m.createResource(si[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS).addProperty(m.createProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE),
							addresses[j]);
				}

				dataset.commit();

			} finally {
				dataset.end();
			}
			this.kb = kb;
		}

	}

	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/

	public RDFPeerSemanticTag(RDFSharkKB kb, String si, String MODEL) {
		super(kb, si, RDFConstants.PEER_MODEL_NAME);
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		StmtIterator statementOfAddress = m.listStatements(m.getResource(si + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS),
				m.getProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE), (String) null);
		List<String> addressesList = new ArrayList<String>();
		while (statementOfAddress.hasNext()) {
			addressesList.add(statementOfAddress.next().getObject().toString());
		}
		address = addressesList.toArray(new String[addressesList.size()]);
		dataset.end();

	}

	@Override
	public void addSI(String si) throws SharkKBException {

		addSIModelIndependenent(si, RDFConstants.PEER_MODEL_NAME);
	}

	@Override
	public void removeSI(String si) throws SharkKBException {

		this.removeSIModelIndependenent(si, RDFConstants.PEER_MODEL_NAME);
	}

	@Override
	public void addAddress(String address) {

		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		String[] sis = this.getSI();
		try {
			for (int i = 0; i < sis.length; i++) {
				m.createResource(sis[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS).addProperty(m.createProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE), address);
				dataset.commit();
			}
		} finally {
			dataset.end();
		}
		String[] newSis = new String[sis.length + 1];
		int i;
		for (i = 0; i < sis.length; i++) {
			newSis[i] = sis[i];
		}
		newSis[i] = address;
		this.setAddresses(newSis);
	}

	@Override
	public String[] getAddresses() {
		return address;
	}

	@Override
	public void removeAddress(String address) {

		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		String[] sis = this.getSI();
		try {
			for (int i = 0; i < sis.length; i++) {
				m.remove(m.listStatements(m.getResource(sis[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS), m.getProperty(RDFConstants.PEER_TAG_ADDRESS_PREDICATE),
						address));
				dataset.commit();
			}
		} finally {
			dataset.end();
		}
		String[] newSis = new String[sis.length - 1];
		int j = - 1;
		for (int i = 0; i < sis.length - 1; i++) {
			if (!sis[i].equals(address)) {
				newSis[i] = sis[i];
			}
			else {
				j = i;
			}
		}
		newSis[j] = sis[sis.length - 1];
		this.setAddresses(newSis);
	}

	@Override
	public void setAddresses(String[] arg0) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
