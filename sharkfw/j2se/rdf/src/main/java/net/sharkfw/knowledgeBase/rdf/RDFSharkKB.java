package net.sharkfw.knowledgeBase.rdf;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * An implementation of the Shark Knowledge Base with TDB Jena. All tags and
 * context points are saved in a triple based database. The database will be
 * stored as a folder with the given directory. The content of this KB can be
 * exported as a RDF File. An RDF File can also be used as a parameter for a new
 * RDFSharkKB.
 * 
 * @author Barret dfe
 *
 */
@SuppressWarnings("unchecked")
public class RDFSharkKB extends AbstractSharkKB implements SharkKB {

	/** The directory, which will be the place of the new database */
	private String directory;

	/** An object from TDB Jena, it saves all triples with specific models */
	private Dataset dataset;

	/**
	 * Creates a new RDFSharkKB with the given directory
	 * 
	 * @param directory
	 *          The directory, which will be the place of the new database
	 * @throws SharkKBException
	 */
	public RDFSharkKB(String directory) throws SharkKBException {

		this.directory = directory;
		dataset = TDBFactory.createDataset(directory);
		if (dataset == null) {
			throw new SharkKBException("Error while retrieving the RDFSharkKB from directory: " + directory);
		}
	}

	/**
	 * Creates a new RDFSharkKB and initialize the database with the data from the
	 * RDF File.
	 * 
	 * @param directory
	 *          The directory, which will be the place of the new database
	 * @param file
	 *          The RDF File, which triples/quadruples will be imported into the
	 *          database
	 * @throws SharkKBException
	 */
	public RDFSharkKB(String directory, File file) throws SharkKBException {

		this(directory);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		dataset.begin(ReadWrite.WRITE);
		RDFDataMgr.read(dataset, fis, RDFLanguages.NQUADS);
		dataset.commit();
		dataset.end();

	}

	public Dataset getDataset() {
		return dataset;
	}

	public String getDirectory() {
		return directory;
	}

	/**
	 * Returns the Spatial Model as a SpatialStSet of the knowledge base.
	 */
	@Override
	public RDFSpatialSTSet getSpatialSTSet() throws SharkKBException {
		return new RDFSpatialSTSet(this);
	}

	/**
	 * Returns the Time Model as a TimeSTSet of the knowledge base.
	 */
	@Override
	public RDFTimeSTSet getTimeSTSet() throws SharkKBException {
		return new RDFTimeSTSet(this);
	}

	/**
	 * Returns the Topic Model as a STSet of the knowledge base.
	 */
	@Override
	public RDFSTSet getTopicSTSet() throws SharkKBException {
		return new RDFSTSet(this);
	}

	/**
	 * Returns the peer which was set as the owner of the knowledge base
	 */
	@Override
	public RDFPeerSemanticTag getOwner() {
		String si = null;
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getDefaultModel();
		try {
			si = m.listStatements(null, m.getProperty(RDFConstants.KB_OWNER_PREDICATE), (String) null).next().getSubject().getURI();
		} finally {
			dataset.end();
		}
		if (si != null) {
			return new RDFPeerSemanticTag(this, si, RDFConstants.PEER_MODEL_NAME);
		} else {
			return null;
		}
	}

	/**
	 * Set the owner of the knowledge base
	 */
	@Override
	public void setOwner(PeerSemanticTag owner) {
		new RDFPeerSemanticTag(this, owner.getSI(), owner.getName(), owner.getAddresses(), RDFConstants.PEER_MODEL_NAME);
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getDefaultModel();
		try {
			m.createResource(owner.getSI()[0]).addProperty(m.createProperty(RDFConstants.KB_OWNER_PREDICATE), "SharkKB");
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	@Override
	public RDFContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
		return new RDFContextPoint(this, coordinates);
	}

	@Override
	public RDFContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
		return new RDFContextPoint(this, coordinates, 0);
	}

	/**
	 * Returns the Peer Model as a PeerSTSet of the knowledge base.
	 */
	@Override
	public RDFPeerSTSet getPeerSTSet() throws SharkKBException {
		return new RDFPeerSTSet(this);
	}

	/**
	 * Export the content of the RDF knowledge base into a simple file with the
	 * RDF syntax NQUADS.
	 * 
	 * @param filePath
	 *          the location of the file
	 * @throws IOException
	 */
	public void exportRDFSharkKB(String filePath) throws IOException {

		File file = new File(filePath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		dataset.begin(ReadWrite.READ);
		RDFDataMgr.write(fos, dataset, RDFFormat.NQUADS);
		dataset.end();
		fos.close();
	}

	/**
	 * Delete the whole content of the knowledge base. Everything including tags,
	 * owner and CPs will be deleted.
	 */
	public void drop() {
		File index = new File(directory);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
	}

	@Override
	public void removeContextPoint(ContextCoordinates cc) throws SharkKBException {
		RDFContextPoint cp = new RDFContextPoint(this, cc, 0);
		cp.removeContextPoint();
	}

	@Override
	public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException { //not tested yet
		Dataset dataset = this.getDataset();
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getNamedModel(RDFConstants.CONTEXT_POINT_MODEL_NAME);
		ResIterator anchors = null;		
		List<ContextPoint> CPs = new ArrayList<ContextPoint>();
		anchors = m.listResourcesWithProperty(m.getProperty(RDFConstants.CONTEXT_POINT_PREDICATE_TOPIC));
		dataset.end();
		while (anchors.hasNext()) {
			CPs.add(new RDFContextPoint(this, anchors.next()));
		}
		Enumeration<ContextPoint> results = Collections.enumeration(CPs);
		return results;	
	}

	@Override
	public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
		return null;
	}

	@Override
	public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
		return null;
	}

	@Override
	public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) throws SharkKBException {
		return null;
	}

	@Override
	public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
		return null;
	}


	//FIXME	method does not override or implement a method from a supertype
	//Override
	public Interest createInterest() throws SharkKBException {
		return this.createInterest();
	}

	//FIXME	method does not override or implement a method from a supertype
	//Override
	public Interest createInterest(ContextCoordinates coordinates) throws SharkKBException {
		return this.createInterest(coordinates);
	}

	@Override
	public Iterator<ContextPoint> contextPoints(SharkCS arg0, boolean arg1) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
		return null;
	}

	@Override
	public Knowledge createKnowledge() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/*******************************************************************************
	 * deprecated methods, will be removed in the next version of the interface
	 ******************************************************************************/

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	//Override
	public SemanticTag createSemanticTag(String name, String[] si) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	//Override
	public SemanticTag createSemanticTag(String si, String name) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	//Override
	public SemanticTag getSemanticTag(String si) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public void removeSemanticTag(String[] arg0) throws SharkKBException {

	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public void removeSemanticTag(SemanticTag arg0) throws SharkKBException {

	}

	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagChanged(SemanticTag arg0, STSet arg1) {
	}

	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagCreated(SemanticTag arg0, STSet arg1) {
	}

	/**
	 * @deprecated
	 */
	@Override
	public void semanticTagRemoved(SemanticTag arg0, STSet arg1) {

	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag getPeerSemanticTag(String[] arg0) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag getPeerSemanticTag(String arg0) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1, String[] arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1, String arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1, String arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1, String[] arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0, String[] arg1) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0, String[] arg1, SharkGeometry arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public TimeSemanticTag createTimeSemanticTag(long arg0, long arg1) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0, boolean arg1) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	@Override
	public ContextCoordinates createContextCoordinates(SemanticTag arg0, PeerSemanticTag arg1, PeerSemanticTag arg2, PeerSemanticTag arg3, TimeSemanticTag arg4,
			SpatialSemanticTag arg5, int arg6) throws SharkKBException {
		throw new SharkKBException("Please use the createRDFContextCoordinates method.");
	}

	//FIXME	method does not override or implement a method from a supertype
	// @Override
	public Interest createInterest(STSet arg0, PeerSemanticTag arg1, PeerSTSet arg2, PeerSTSet arg3, TimeSTSet arg4, SpatialSTSet arg5, int arg6)
			throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	@Override
	public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
		return null;
	}

	@Override
	public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
		return null;
	}

	@Override
	public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
		return null;
	}

	@Override
	public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {

	}

	@Override
	public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
		return null;
	}

	@Override
	public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
		return null;
	}
}