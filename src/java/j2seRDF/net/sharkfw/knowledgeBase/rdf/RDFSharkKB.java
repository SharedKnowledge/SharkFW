package net.sharkfw.knowledgeBase.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import net.sharkfw.knowledgeBase.AbstractSharkKB;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
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
	 *            The directory, which will be the place of the new database
	 * @throws SharkKBException
	 */
	public RDFSharkKB(String directory) throws SharkKBException {

		this.directory = directory;
		dataset = TDBFactory.createDataset(directory);
		if (dataset == null) {
			throw new SharkKBException(
					"Error while retrieving the RDFSharkKB from directory: "
							+ directory);
		}
	}

	/**
	 * Creates a new RDFSharkKB and initialize the database with the data from
	 * the RDF File
	 * 
	 * @param directory
	 *            The directory, which will be the place of the new database
	 * @param file
	 *            The RDF File, which triples/quadruples will be imported into
	 *            the database
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

	@Override
	public RDFSpatialSTSet getSpatialSTSet() throws SharkKBException {
		return new RDFSpatialSTSet(this);
	}

	@Override
	public RDFTimeSTSet getTimeSTSet() throws SharkKBException {
		return new RDFTimeSTSet(this);
	}

	@Override
	public RDFSTSet getTopicSTSet() throws SharkKBException {
		return new RDFSTSet(this);
	}

	@Override
	public RDFPeerSemanticTag getOwner() {
		String si = null;
		dataset.begin(ReadWrite.READ);
		Model m = dataset.getDefaultModel();
		try {
			si = m.listStatements(null,
					m.getProperty(RDFConstants.KB_OWNER_PREDICATE),
					(String) null).next().getSubject().getURI();
		} finally {
			dataset.end();
		}
		if (si != null) {
			return new RDFPeerSemanticTag(this, si, RDFConstants.PEER_MODEL_NAME);
		} else {
			return null;
		}
	}

	@Override
	public void setOwner(PeerSemanticTag owner) {
		new RDFPeerSemanticTag(this, owner.getSI(), owner.getName(),
				owner.getAddresses(), RDFConstants.PEER_MODEL_NAME);
		dataset.begin(ReadWrite.WRITE);
		Model m = dataset.getDefaultModel();
		try {
			m.createResource(owner.getSI()[0]).addProperty(
					m.createProperty(RDFConstants.KB_OWNER_PREDICATE),
					"SharkKB");
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	@Override
	public RDFContextPoint createContextPoint(ContextCoordinates coordinates)
			throws SharkKBException {
		return new RDFContextPoint(this, coordinates);
	}

	@Override
	public Interest createInterest() throws SharkKBException {
		return this.createInterest();
	}

	@Override
	public Interest createInterest(ContextCoordinates coordinates)
			throws SharkKBException {
		return this.createInterest(coordinates);
	}

	@Override
	public RDFContextPoint getContextPoint(ContextCoordinates coordinates)
			throws SharkKBException {
		return new RDFContextPoint(this, coordinates, 0);
	}

	@Override
	public RDFPeerSTSet getPeerSTSet() throws SharkKBException {
		return new RDFPeerSTSet(this);
	}

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

	public void drop() {
		File index = new File(directory);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
	}

	@Override
	public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RDFSemanticNet getTopicsAsSemanticNet() throws SharkKBException {
		return new RDFSemanticNet(this);
	}

	@Override
	public Iterator<ContextPoint> contextPoints(SharkCS arg0, boolean arg1)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInterest(SharkCS arg0) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<SharkCS> interests() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeInterest(SharkCS arg0) throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeContextPoint(ContextCoordinates arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<ContextPoint> contextPoints(SharkCS arg0)
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Knowledge createKnowledge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<ContextPoint> getAllContextPoints()
			throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<SemanticTag> getTags() throws SharkKBException {
		// TODO Auto-generated method stub
		return null;
	}

	/*******************************************************************************
	 * deprecated methods, will be removed in the next version of the interface
	 ******************************************************************************/

	/**
	 * @deprecated
	 */
	@Override
	public SemanticTag createSemanticTag(String name, String[] si)
			throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}
	/**
	 * @deprecated
	 */
	@Override
	public SemanticTag createSemanticTag(String si, String name)
			throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}
	/**
	 * @deprecated
	 */
	@Override
	public SemanticTag getSemanticTag(String si) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}
	/**
	 * @deprecated
	 */
	@Override
	public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	/**
	 * @deprecated
	 */
	@Override
	public void removeSemanticTag(String[] arg0) throws SharkKBException {

	}
	/**
	 * @deprecated
	 */
	@Override
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
	@Override
	public PeerSemanticTag getPeerSemanticTag(String[] arg0)
			throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag getPeerSemanticTag(String arg0)
			throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}

	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1,
			String[] arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String[] arg1,
			String arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public PeerSemanticTag createPeerSemanticTag(String arg0, String arg1,
			String[] arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0,
			String[] arg1) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public SpatialSemanticTag createSpatialSemanticTag(String arg0,
			String[] arg1, SharkGeometry arg2) throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public TimeSemanticTag createTimeSemanticTag(long arg0, long arg1)
			throws SharkKBException {
		throw new SharkKBException("Please use the TagSet for creating a tag");
	}
	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0)
			throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}
	/**
	 * @deprecated
	 */
	@Override
	public Enumeration<ContextPoint> getContextPoints(SharkCS arg0, boolean arg1)
			throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

	@Override
	public ContextCoordinates createContextCoordinates(SemanticTag arg0,
			PeerSemanticTag arg1, PeerSemanticTag arg2, PeerSemanticTag arg3,
			TimeSemanticTag arg4, SpatialSemanticTag arg5, int arg6)
			throws SharkKBException {
		throw new SharkKBException(
				"Please use the createRDFContextCoordinates method.");
	}

	@Override
	public Interest createInterest(STSet arg0, PeerSemanticTag arg1,
			PeerSTSet arg2, PeerSTSet arg3, TimeSTSet arg4, SpatialSTSet arg5,
			int arg6) throws SharkKBException {
		throw new SharkKBException("Deprecated method");
	}

}