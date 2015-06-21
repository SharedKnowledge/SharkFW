package knowledgeBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.ReadWrite;

public class TestRDFKB {

	private final String KBDIRECTORY = "C:\\Users\\Barret\\workspace\\RDFSharkKB\\Databases\\Dataset1";

	@Before
	public void clearDatasetBeforeTests() throws SharkKBException {
		File index = new File(KBDIRECTORY);
		String[] entries = index.list();
		for (String s : entries) {
			File currentFile = new File(index.getPath(), s);
			currentFile.delete();
		}
	}

	@Test
	public void testCreateRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet.createSemanticTag(
				"https://jena.apache.org/documentation/tdb", "Jena - TDB");
		assertNotNull(tag);
		assertEquals("https://jena.apache.org/documentation/tdb",
				tag.getSI()[0]);
	}

	@Test
	public void testGetRDFSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		SemanticTag tag = stSet
				.getSemanticTag("https://jena.apache.org/documentation/tdb");
		assertNotNull(tag);
		assertEquals("https://jena.apache.org/documentation/tdb",
				tag.getSI()[0]);
	}

	@Test
	public void testCreateRDFSemanticTagWithMultipleSIs()
			throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		String[] si = new String[] { "http://www.htw-berlin.de/1",
				"http://www.htw-berlin.de/2", "http://www.htw-berlin.de/3" };
		SemanticTag tag = stSet.createSemanticTag("HTW-AI", si);
		assertEquals(3, tag.getSI().length);
		assertEquals("http://www.htw-berlin.de/2", tag.getSI()[1]);

	}

	@Test
	public void testCreateRDFPeerSemanticTag() throws SharkKBException {

		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFPeerSTSet peerSet = kb.getPeerSTSet();
		String[] si = new String[] { "https://de.wikipedia.org/wiki/Alpha",
				"https://de.wikipedia.org/wiki/Beta",
				"https://de.wikipedia.org/wiki/Gamma" };
		String[] addresses = new String[] { "s0540042@htw-berlin.de",
				"47487271", "Aristotelessteig 6" };
		String topic = "Shark";
		RDFPeerSemanticTag tag = peerSet.createPeerSemanticTag(topic, si,
				addresses);
		kb.getDataset().begin(ReadWrite.READ);
		kb.getDataset().getNamedModel(RDFConstants.PEER_MODEL_NAME)
				.write(System.out);
		kb.getDataset().end();
	}



}
