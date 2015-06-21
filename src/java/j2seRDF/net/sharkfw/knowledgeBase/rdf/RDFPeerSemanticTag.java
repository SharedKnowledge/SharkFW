package knowledgeBase;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFPeerSemanticTag extends RDFSemanticTag implements PeerSemanticTag{

	private String[] address;
	
	public RDFPeerSemanticTag(RDFSharkKB kb, String[] si, String topic, String[] addresses) {
		super(si, topic);
		this.address = addresses;
		Dataset dataset = kb.getDataset();
		AnonId blankNodeId = new AnonId();
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
			try {
				Statement s = m.createStatement(m.createResource(si[i]),
						m.createProperty(si[i] + RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);
				m.getResource(si[i]).addProperty(m.createProperty(si[i] + RDFConstants.PEER_TAG_PREDICATE), m.createResource(blankNodeId));
				
				for (int j = 0; j < addresses.length; j++) {
					m.createResource(blankNodeId).addProperty(m.createProperty(si[i] + RDFConstants.PEER_TAG_ADDRESS_PREDICATE), addresses[j]);
				}
					
				dataset.commit();
				
			} finally {
				dataset.end();
			}
		}
		
	}
	
	@Override
	public void addAddress(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getAddresses() {
		// TODO Auto-generated method stub
		return null;
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
