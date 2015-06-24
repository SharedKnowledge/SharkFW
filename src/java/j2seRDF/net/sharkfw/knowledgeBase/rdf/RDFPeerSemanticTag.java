package knowledgeBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
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
		//AnonId blankNodeId = new AnonId();
		for (int i = 0; i < si.length; i++) {
			dataset.begin(ReadWrite.WRITE);
			Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
			try {
				Statement s = m.createStatement(m.createResource(si[i]),
						m.createProperty(si[i] + RDFConstants.SEMANTIC_TAG_PREDICATE), topic);
				m.add(s);
				m.getResource(si[i]).addProperty(m.createProperty(si[i] + RDFConstants.PEER_TAG_PREDICATE), m.createResource(si[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS));
				
				for (int j = 0; j < addresses.length; j++) {
					m.createResource(si[i] + RDFConstants.PEER_TAG_OBJECT_NAME_ADDRESS).addProperty(m.createProperty(si[i] + RDFConstants.PEER_TAG_ADDRESS_PREDICATE), addresses[j]);
				}
					
				dataset.commit();
				
			} finally {
				dataset.end();
			}
		}
		
	}
	
	public RDFPeerSemanticTag(RDFSharkKB kb, String si) {
		super();
		address = null;
		Dataset dataset = kb.getDataset();
		Iterator<Triple> resultModel = null;
		List<Triple> list = new ArrayList<Triple>();		
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME);
		String qs1 = "DESCRIBE " + "<" + si + ">";		
		try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset.getNamedModel(RDFConstants.PEER_MODEL_NAME))) {
			resultModel = qExec.execDescribeTriples();		
			while (resultModel.hasNext()) {
				list.add(resultModel.next());
			}
		} finally {
			dataset.end();
		}
		setTopic(list.get(1).getObject().toString());
		String[] temp = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			temp[i] = list.get(i).getSubject().toString();
		}
		setSi(temp);
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
