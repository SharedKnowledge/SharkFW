package net.sharkfw.knowledgeBase.rdf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import net.sharkfw.knowledgeBase.rdf.RDFContextCoordinates;
import net.sharkfw.knowledgeBase.rdf.RDFContextPoint;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSharkKB;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFTimeSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFTimeSemanticTag;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.ReadWrite;

public class TestRDFKB {

	
	private final String KBDIRECTORY = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFolderDataset";
	
	private ContextCoordinates cc;
	
	private Long time = new Long("1437400837574");

	@Before
	public void clearDatasetAndCreateDefaultCC() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		File index = new File(KBDIRECTORY);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
		RDFSTSet topics = kb.getTopicSTSet();
		RDFSemanticTag sharkTag = topics.createSemanticTag("http://sharksystem.net", "Shark");
		RDFPeerSTSet peers = kb.getPeerSTSet();
		RDFPeerSemanticTag alice = peers.createPeerSemanticTag("Alice", "http://www.sharksystem.net/alice.html",
				"alice@sharksystem.net");
		RDFPeerSemanticTag bob = peers.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html",
				"bob@sharksystem.net");
		RDFPeerSemanticTag clara = peers.createPeerSemanticTag("Clara", "http://www.sharksystem.net/clara.html",
				"clara@sharksystem.net");
		RDFTimeSemanticTag tst = kb.getTimeSTSet().createTimeSemanticTag(time,
				TimeSemanticTag.FOREVER);
		RDFSpatialSemanticTag sst = null;
		this.cc = new RDFContextCoordinates(sharkTag, clara, alice, bob, tst, sst,
				SharkCS.DIRECTION_INOUT);
	}

	@Test
	public void testCreateAndGetRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.createSemanticTag("https://jena.apache.org/documentation/tdb", "Jena - TDB");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://jena.apache.org/documentation/tdb"));
	}

	@Test
	public void testGetRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.getSemanticTag("https://jena.apache.org/documentation/tdb");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://jena.apache.org/documentation/tdb"));

	}

	@Test
	public void testCreateRDFSemanticTagWithMultipleSIs() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		String[] si = new String[] { "http://www.htw-berlin.de/1", "http://www.htw-berlin.de/2",
				"http://www.htw-berlin.de/3" };
		SemanticTag tag = stSet.createSemanticTag("HTW-AI", si);
		assertEquals(3, tag.getSI().length);
		assertEquals("http://www.htw-berlin.de/2", tag.getSI()[1]);

	}

	@Test
	public void testCreateRDFPeerSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSTSet peerSet = kb.getPeerSTSet();
		String[] si = new String[] { "https://de.wikipedia.org/wiki/Alpha", "https://de.wikipedia.org/wiki/Beta",
				"https://de.wikipedia.org/wiki/Gamma" };
		String[] addresses = new String[] { "s0540042@htw-berlin.de", "47487271", "Aristotelessteig 6" };
		String topic = "Shark";
		RDFPeerSemanticTag tag = peerSet.createPeerSemanticTag(topic, si, addresses);
		assertEquals(3, tag.getSI().length);
		assertEquals(3, tag.getAddresses().length);
		assertEquals("https://de.wikipedia.org/wiki/Gamma", tag.getSI()[2]);

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
	public void testCreateRDFSpatialSemanticTag() throws SharkKBException {
		String[] si = new String[] { "https://de.wikipedia.org/wiki/Alpha", "https://de.wikipedia.org/wiki/Beta",
				"https://de.wikipedia.org/wiki/Gamma" };
		String topic = "SharkGeometry";
		SharkGeometry sg = InMemoSharkGeometry.createGeomByWKT("POINT (30 10)");
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSpatialSTSet spatialSet = kb.getSpatialSTSet();
		RDFSpatialSemanticTag tag = spatialSet.createSpatialSemanticTag(topic, si, sg);
		assertEquals(3, tag.getSI().length);
		assertEquals("POINT (30 10)", tag.getGeometry().getWKT());
		assertEquals("https://de.wikipedia.org/wiki/Gamma", tag.getSI()[2]);

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
	public void testCreateRDFTimeSemanticTag() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFTimeSTSet timeSet = kb.getTimeSTSet();
		RDFTimeSemanticTag tag = timeSet.createTimeSemanticTag(time, 0);
		assertNotNull(tag);
	}

	@Test
	public void testSetKBOwner() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSTSet peerSet = kb.getPeerSTSet();
		String[] aliceSIs = new String[] { "http://www.sharksystem.net/alice.html" };
		String[] aliceAddr = new String[] { "mail://alice@wonderland.net", "tcp://shark.wonderland.net:7070",
				"alice@sharksystem.net" };
		RDFPeerSemanticTag aliceTag = peerSet.createPeerSemanticTag("Alice", aliceSIs, aliceAddr);
		kb.setOwner(aliceTag);

		RDFPeerSemanticTag ownerTag = kb.getOwner();
		assertEquals("http://www.sharksystem.net/alice.html", ownerTag.getSi()[0]);
		assertEquals(3, ownerTag.getAddresses().length);
		assertEquals("Alice", ownerTag.getTopic());
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("mail://alice@wonderland.net"));
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("tcp://shark.wonderland.net:7070"));

		// kb.getDataset().begin(ReadWrite.READ);
		// kb.getDataset().getNamedModel(RDFConstants.PEER_MODEL_NAME).write(System.out);
		// kb.getDataset().end();
	}


	@Test
	public void testCreateRDFContextPoint() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint cp = kb.createContextPoint(cc);
		assertNotNull(cp);
		assertEquals("bob@sharksystem.net", cp.getContextCoordinates().getRemotePeer().getAddresses()[0]);
		assertNull(cp.getContextCoordinates().getLocation());
		assertEquals("Alice", cp.getContextCoordinates().getPeer().getName());
		assertEquals(TimeSemanticTag.FOREVER, cp.getContextCoordinates().getTime().getDuration());		
	}
	
	@Test
	public void testGetRDFContextPoint() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint result = kb.getContextPoint(cc);
		assertNotNull(result);
		assertEquals("bob@sharksystem.net", result.getContextCoordinates().getRemotePeer().getAddresses()[0]);
		assertNull(result.getContextCoordinates().getLocation());
		assertEquals("Alice", result.getContextCoordinates().getPeer().getName());
		assertEquals(TimeSemanticTag.FOREVER, result.getContextCoordinates().getTime().getDuration());
		
		byte[] content = {1, 0, 1, 0, 0, 1};
		Information info = result.addInformation(content);
		int j = 0;
		
		
	}
	
	
	


}
