package net.sharkfw.knowledgeBase.rdf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.rdf.RDFContextCoordinates;
import net.sharkfw.knowledgeBase.rdf.RDFContextPoint;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSharkKB;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFTimeSemanticTag;

import org.junit.Before;
import org.junit.Test;

/**
 * This class imports a RDF file into a new knowledge base. After the import, the content
 * of the knowledge base will be tested. 
 * 
 * IF tests are failing: Run the TestRDFKB BEFORE the TestImportRDFKB. This class expects the
 * content within the RDF File which was previously generated and exported by the TestRDFKB
 * 
 * @author Barret dfe
 *
 */
public class TestImportRDFKB {

	/** The path in which the database will be stored */
	private static final String KBDIRECTORY = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFolderDataset";

	/** The path in which the exported RDF file will be exported*/
	private static final String TEST_FILE_PATH = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFileRDF.nq";
	
	/** The file which will be used for initializing the new RDFSharkKB */
	private File importFile;


	@Before
	public void clearDatasetCAndImportFile() throws SharkKBException, IOException {
		File index = new File(KBDIRECTORY);
		TestUtils.delete(index);
		importFile = new File(TEST_FILE_PATH);
		new RDFSharkKB(KBDIRECTORY, importFile);
	}
	
	@Test
	public void testGetRDFPeerSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSTSet peerSet = kb.getPeerSTSet();
		RDFPeerSemanticTag tag = peerSet.getSemanticTag("https://de.wikipedia.org/wiki/Alpha");
		assertEquals("Shark", tag.getTopic());
		assertEquals(3, tag.getSI().length);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Alpha"));
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Beta"));
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Gamma"));
		assertEquals(3, tag.getAddresses().length);
		assertTrue(Arrays.asList(tag.getAddresses()).contains("s0540042@htw-berlin.de"));
		assertTrue(Arrays.asList(tag.getAddresses()).contains("47487271"));
		assertTrue(Arrays.asList(tag.getAddresses()).contains("Aristotelessteig 6"));
	}
	
	@Test
	public void testGetRDFSpatialSemanticTag() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSpatialSTSet spatialSet = kb.getSpatialSTSet();
		RDFSpatialSemanticTag tag = spatialSet.getSpatialSemanticTag("https://de.wikipedia.org/wiki/Alpha");
		assertEquals("SharkGeometry", tag.getTopic());
		assertEquals(3, tag.getSI().length);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Alpha"));
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Beta"));
		assertTrue(Arrays.asList(tag.getSI()).contains("https://de.wikipedia.org/wiki/Gamma"));
		assertEquals("POINT (30 10)", tag.getGeometry().getWKT());
	}

	@Test
	public void testGetRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.getSemanticTag("https://jena.apache.org/documentation/tdb");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://jena.apache.org/documentation/tdb"));
		assertEquals("Jena - TDB", tag.getName());
	}

	@Test
	public void testGetKBOwner() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSemanticTag ownerTag = kb.getOwner();
		assertTrue(Arrays.asList(ownerTag.getSI()).contains("http://www.sharksystem.net/alice.html"));
		assertEquals(2, ownerTag.getAddresses().length);
		assertEquals("Alice", ownerTag.getTopic());
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("mail://alice@wonderland.net"));
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("tcp://shark.wonderland.net:7070"));
	}

	
	@Test
	public void testGetRDFContextPoint() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet topics = kb.getTopicSTSet();
		RDFSemanticTag sharkTag = topics.getSemanticTag("http://sharksystem.net");
		RDFPeerSTSet peers = kb.getPeerSTSet();
		RDFPeerSemanticTag daniel = peers.getSemanticTag("http://www.sharksystem.net/daniel.html");
		RDFPeerSemanticTag bob = peers.getSemanticTag("http://www.sharksystem.net/bob.html");
		RDFPeerSemanticTag clara = peers.getSemanticTag("http://www.sharksystem.net/clara.html");
		RDFTimeSemanticTag tst = null;
		RDFSpatialSemanticTag sst = null;
		RDFContextCoordinates cc= new RDFContextCoordinates(sharkTag, clara, daniel, bob, tst, sst, SharkCS.DIRECTION_INOUT);
	
		RDFContextPoint cp = kb.getContextPoint(cc);
		assertNotNull(cp);
		assertEquals("bob@sharksystem.net", cp.getContextCoordinates().getRemotePeer().getAddresses()[0]);
		assertNull(cp.getContextCoordinates().getLocation());
		assertEquals("Daniel", cp.getContextCoordinates().getPeer().getName());
	}
	
}
