package net.sharkfw.knowledgeBase.rdf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.rdf.RDFContextCoordinates;
import net.sharkfw.knowledgeBase.rdf.RDFContextPoint;
import net.sharkfw.knowledgeBase.rdf.RDFInformation;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFPeerSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSNSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSemanticNet;
import net.sharkfw.knowledgeBase.rdf.RDFSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFSharkKB;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSpatialSemanticTag;
import net.sharkfw.knowledgeBase.rdf.RDFTimeSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFTimeSemanticTag;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRDFKB {

	private static final String KBDIRECTORY = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFolderDataset";

	private static final String TEST_FILE_PATH = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFileRDF.nq";

	private RDFContextCoordinates cc;

	private Long time = new Long("1437400837574");

	@Before
	public void clearDatasetC() throws SharkKBException, IOException {
		File index = new File(KBDIRECTORY);
		TestUtils.delete(index);
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet topics = kb.getTopicSTSet();
		RDFSemanticTag sharkTag = topics.createSemanticTag("http://sharksystem.net", "Shark");
		RDFPeerSTSet peers = kb.getPeerSTSet();
		RDFPeerSemanticTag daniel = peers.createPeerSemanticTag("Daniel", "http://www.sharksystem.net/daniel.html",
				"daniel@sharksystem.net");
		RDFPeerSemanticTag bob = peers.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html",
				"bob@sharksystem.net");
		RDFPeerSemanticTag clara = peers.createPeerSemanticTag("Clara", "http://www.sharksystem.net/clara.html",
				"clara@sharksystem.net");
		RDFTimeSemanticTag tst = kb.getTimeSTSet().createTimeSemanticTag(time, TimeSemanticTag.FOREVER);
		RDFSpatialSemanticTag sst = null;
		this.cc = new RDFContextCoordinates(sharkTag, clara, daniel, bob, tst, sst, SharkCS.DIRECTION_INOUT);
	}

	@AfterClass
	public static void writeTestResultsInRDFFile() throws SharkKBException, IOException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		File file = new File(TEST_FILE_PATH);
		Dataset dataset = kb.getDataset();
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

	@Test
	public void testACreateRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.createSemanticTag("https://jena.apache.org/documentation/tdb", "Jena - TDB");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://jena.apache.org/documentation/tdb"));
		assertEquals("Jena - TDB", tag.getName());
	}

	@Test
	public void testBCreateRDFSemanticTagWithMultipleSIs() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		String[] si = new String[] { "http://www.htw-berlin.de/1", "http://www.htw-berlin.de/2",
				"http://www.htw-berlin.de/3" };
		SemanticTag tag = stSet.createSemanticTag("HTW-AI", si);
		assertEquals(3, tag.getSI().length);
		assertEquals("http://www.htw-berlin.de/2", tag.getSI()[1]);
	}

	@Test
	public void testCCreateRDFPeerSemanticTag() throws SharkKBException {

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
	public void testDCreateRDFSpatialSemanticTag() throws SharkKBException {
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
	public void testECreateRDFTimeSemanticTag() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFTimeSTSet timeSet = kb.getTimeSTSet();
		RDFTimeSemanticTag tag = timeSet.createTimeSemanticTag(time, 0);
		assertNotNull(tag);
	}

	@Test
	public void testFCreateRDFContextPoint() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint cp = kb.createContextPoint(cc);
		assertNotNull(cp);
		assertEquals("bob@sharksystem.net", cp.getContextCoordinates().getRemotePeer().getAddresses()[0]);
		assertNull(cp.getContextCoordinates().getLocation());
		assertEquals("Daniel", cp.getContextCoordinates().getPeer().getName());
		assertEquals(TimeSemanticTag.FOREVER, cp.getContextCoordinates().getTime().getDuration());
	}

	@Test
	public void testGSetKBOwner() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSTSet peerSet = kb.getPeerSTSet();
		String aliceSI = "http://www.sharksystem.net/alice.html";
		String[] aliceAddr = new String[] { "mail://alice@wonderland.net", "tcp://shark.wonderland.net:7070" };
		RDFPeerSemanticTag aliceTag = peerSet.createPeerSemanticTag("Alice", aliceSI, aliceAddr);
		kb.setOwner(aliceTag);

		RDFPeerSemanticTag ownerTag = kb.getOwner();
		assertTrue(Arrays.asList(ownerTag.getSI()).contains("http://www.sharksystem.net/alice.html"));
		assertEquals(2, ownerTag.getAddresses().length);
		assertEquals("Alice", ownerTag.getTopic());
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("mail://alice@wonderland.net"));
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("tcp://shark.wonderland.net:7070"));
	}

	@Test
	public void testHGetRDFSpatialSemanticTag() throws SharkKBException {
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
	public void testIGetRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.getSemanticTag("https://jena.apache.org/documentation/tdb");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://jena.apache.org/documentation/tdb"));
		assertEquals("Jena - TDB", tag.getName());
	}

	@Test
	public void testJGetRDFPeerSemanticTag() throws SharkKBException {

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
	public void testKGetKBOwner() throws SharkKBException {
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSemanticTag ownerTag = kb.getOwner();
		assertTrue(Arrays.asList(ownerTag.getSI()).contains("http://www.sharksystem.net/alice.html"));
		assertEquals(2, ownerTag.getAddresses().length);
		assertEquals("Alice", ownerTag.getTopic());
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("mail://alice@wonderland.net"));
		assertTrue(Arrays.asList(ownerTag.getAddresses()).contains("tcp://shark.wonderland.net:7070"));
	}

	@Test
	public void testLGetRDFContextPoint() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint cp = kb.getContextPoint(cc);
		assertNotNull(cp);
		assertEquals("bob@sharksystem.net", cp.getContextCoordinates().getRemotePeer().getAddresses()[0]);
		assertNull(cp.getContextCoordinates().getLocation());
		assertEquals("Daniel", cp.getContextCoordinates().getPeer().getName());
		assertEquals(TimeSemanticTag.FOREVER, cp.getContextCoordinates().getTime().getDuration());
	}

	@Test
	public void testMAddInformation() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint cp = kb.getContextPoint(cc);
		byte[] content = { 1, 0, 1, 0, 1 };
		RDFInformation info = cp.addInformation(content);
		assertNotNull(info);
		assertEquals(1, info.getContentAsByte()[0]);
		assertEquals(0, info.getContentAsByte()[1]);
		assertEquals(1, info.getContentAsByte()[2]);
		assertEquals(0, info.getContentAsByte()[3]);
		assertEquals(1, info.getContentAsByte()[4]);
	}

	@Test
	public void testNGetInformation() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFContextPoint cp = kb.getContextPoint(cc);
		Information info = cp.getInformation().next();
		assertNotNull(info);
		assertEquals(1, info.getContentAsByte()[0]);
		assertEquals(0, info.getContentAsByte()[1]);
		assertEquals(1, info.getContentAsByte()[2]);
		assertEquals(0, info.getContentAsByte()[3]);
		assertEquals(1, info.getContentAsByte()[4]);
	}

	@Test
	public void testOCreateSNSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tag = semanticNet.createSemanticTag("Germany", "https://en.wikipedia.org/wiki/Germany");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://en.wikipedia.org/wiki/Germany"));
		assertEquals("Germany", tag.getName());
	}

	@Test
	public void testPGetSNSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tag = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany");
		assertNotNull(tag);
		assertTrue(Arrays.asList(tag.getSI()).contains("https://en.wikipedia.org/wiki/Germany"));
		assertEquals("Germany", tag.getName());
	}

	@Test
	public void testQSetAndGetPredicatesSemanticNet() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tagBerlin = semanticNet.createSemanticTag("Berlin", "https://en.wikipedia.org/wiki/Berlin");
		RDFSNSemanticTag tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany");
		tagGermany.setPredicate("capital", tagBerlin);

		Enumeration<String> predicates = tagGermany.predicateNames();
		String result = predicates.nextElement();
		assertNotNull(result);
		assertEquals("http://www.sharksystem.net/SemanticNet/capital", result);
	}

	@Test
	public void testRGetTargetTagsSemanticNet() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany");
		Enumeration<SNSemanticTag> targetTags = tagGermany.targetTags("capital");
		assertNotNull(targetTags);
		assertEquals("Berlin", targetTags.nextElement().getName());
	}
	
	@Test
	public void testSGetSourceTagsSemanticNet() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tagBerlin = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Berlin");
		Enumeration<SNSemanticTag> sourceTags = tagBerlin.sourceTags("capital");
		assertNotNull(sourceTags);
		assertEquals("Germany", sourceTags.nextElement().getName());
	}
	
	@Test
	public void testTaddSI() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany");
		assertEquals(1, tagGermany.getSI().length);
		tagGermany.addSI("https://de.wikipedia.org/wiki/Deutschland");
		assertEquals(2, tagGermany.getSI().length);
		assertEquals("https://en.wikipedia.org/wiki/Germany", tagGermany.getSi()[0]);
		assertEquals("https://de.wikipedia.org/wiki/Deutschland", tagGermany.getSi()[1]);		
		
		tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany"); //Tag neu aus der Wissensbasis holen, erneuter Test der Attribute
		assertEquals(2, tagGermany.getSI().length);
		assertEquals("https://en.wikipedia.org/wiki/Germany", tagGermany.getSI()[0]);
		assertEquals("https://de.wikipedia.org/wiki/Deutschland", tagGermany.getSI()[1]);	
	}
	
	@Test
	public void testURemoveSI() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSemanticNet semanticNet = new RDFSemanticNet(kb);
		RDFSNSemanticTag tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany");
		assertEquals(2, tagGermany.getSI().length);

		tagGermany.removeSI("https://de.wikipedia.org/wiki/Deutschland");
		assertEquals(1, tagGermany.getSI().length);
		assertEquals("https://en.wikipedia.org/wiki/Germany", tagGermany.getSI()[0]);
		
		tagGermany = semanticNet.getSemanticTag("https://en.wikipedia.org/wiki/Germany"); //Tag neu aus der Wissensbasis holen, erneuter Test der Attribute
		assertEquals(1, tagGermany.getSI().length);
		assertEquals("https://en.wikipedia.org/wiki/Germany", tagGermany.getSI()[0]);		
	}
	
	

}
