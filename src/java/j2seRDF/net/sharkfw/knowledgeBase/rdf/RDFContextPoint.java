package net.sharkfw.knowledgeBase.rdf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFContextPoint implements ContextPoint {

	@SuppressWarnings("unused")
	private RDFSharkKB kb;

	private ContextCoordinates coordinates = null;
	
	private AnonId contextPointID;

	/********** RDFKB-CREATE (write in db) CONSTRUCTOR **********/

	public RDFContextPoint(RDFSharkKB kb, ContextCoordinates coordinates) throws SharkKBException {
		this.kb = kb;
		this.coordinates = coordinates;
		Dataset dataset = kb.getDataset();

		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getNamedModel(RDFConstants.CONTEXT_POINT_MODEL_NAME);
		try {
			Resource anchor = m.createResource();
			contextPointID = anchor.getId();
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_TOPIC),
					(coordinates.getTopic() != null) ? coordinates.getTopic().getSI()[0] : "null");

			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_ORIGINATOR),
					(coordinates.getOriginator() != null) ? coordinates.getOriginator().getSI()[0] : "null");
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_PEER),
					(coordinates.getPeer() != null) ? coordinates.getPeer().getSI()[0] : "null");
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_REMOTE_PEER),
					(coordinates.getRemotePeer() != null) ? coordinates.getRemotePeer().getSI()[0] : "null");
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_LOCATION),
					(coordinates.getLocation() != null) ? coordinates.getLocation().getSI()[0] : "null");
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_TIME),
					(coordinates.getTime() != null) ? coordinates.getTime().getSI()[0] : "null");
			anchor.addProperty(m.createProperty(RDFConstants.CONTEXT_POINT_PREDICATE_DIRECTION),
					Integer.toString(coordinates.getDirection()));
			dataset.commit();

		} finally {
			dataset.end();
		}
	}

	/********** RDFKB-GET (read in db) CONSTRUCTOR **********/

	public RDFContextPoint(RDFSharkKB kb, ContextCoordinates coordinates, int i) throws SharkKBException {
		if (coordinates == null || kb == null) {
			throw new IllegalArgumentException();
		}
		this.kb = kb;
		Dataset dataset = kb.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.CONTEXT_POINT_MODEL_NAME);
		boolean pointFound = false;
		Resource anchor = null;
		Statement propertyStmt = null;
		ResIterator contextPoints = null;
		StmtIterator propertiesOfPoint = null;
		try {
			contextPoints = m.listResourcesWithProperty(m.getProperty(RDFConstants.CONTEXT_POINT_PREDICATE_TOPIC));
			while (contextPoints.hasNext() && !pointFound) {
				anchor = contextPoints.next();
				propertiesOfPoint = anchor.listProperties();
				propertyStmt = propertiesOfPoint.next();
				if (coordinates.getTopic() == null
						|| coordinates.getTopic().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;

				}
				propertyStmt = propertiesOfPoint.next();
				if (pointFound && coordinates.getOriginator() == null
						|| coordinates.getOriginator().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;
				}
				propertyStmt = propertiesOfPoint.next();
				if (pointFound && coordinates.getPeer() == null
						|| coordinates.getPeer().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;
				}
				propertyStmt = propertiesOfPoint.next();
				if (pointFound && coordinates.getRemotePeer() == null
						|| coordinates.getRemotePeer().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;
				}
				propertyStmt = propertiesOfPoint.next();
				if (pointFound && coordinates.getLocation() == null
						|| coordinates.getLocation().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;
				}
				propertyStmt = propertiesOfPoint.next();
				if (pointFound && coordinates.getTime() == null
						|| coordinates.getTime().getSI()[0].equals(propertyStmt.getObject().toString())) {
					pointFound = true;
				} else {
					pointFound = false;
				}
				propertyStmt = propertiesOfPoint.next();
			}
		} finally {
			dataset.end();
		}
		if (pointFound) {
			this.coordinates = coordinates;
			this.contextPointID = anchor.getId();
		} else {
			throw new SharkKBException(
					"No ContextPoint with this coordinates can be found in the RDF database with path: "
							+ kb.getDirectory());
		}

	}


	@Override
	public RDFInformation addInformation(byte[] content) {
		try {
			return new RDFInformation(contextPointID, content);
		} catch (SharkKBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public void setContextCoordinates(ContextCoordinates arg0) {
		throw new UnsupportedOperationException("Just create a new RDFContextPoint with the new CC and delete the old one.");
	}

	@Override
	public ContextCoordinates getContextCoordinates() {
		return coordinates;
	}

	@Override
	public Iterator<Information> getInformation() {
		RDFInformation info = null;
		try {
			info = new RDFInformation(contextPointID);
		} catch (SharkKBException e) {			
			e.printStackTrace();
			return null;
		}
		List<Information> list = new ArrayList<Information>();
		list.add(info);
		return list.iterator();
	}
	
	@Override
	public Information addInformation(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInformation(Information arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInformation(Information arg0) {
		// TODO Auto-generated method stub

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
	public Information addInformation() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Information addInformation(InputStream arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Information> enumInformation() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Iterator<Information> getInformation(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberInformation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setListener(ContextPointListener arg0) {
		// TODO Auto-generated method stub

	}

}
