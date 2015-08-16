package net.sharkfw.knowledgeBase.rdf.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.rdf.RDFConstants;
import net.sharkfw.knowledgeBase.rdf.RDFSTSet;
import net.sharkfw.knowledgeBase.rdf.RDFSharkKB;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * This class tests the functionality of the RDFSharkKB with the four main types 
 * of SPARQL.
 * 
 * @author Barret dfe
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRDFKBSparql {

	/** The path in which the database will be stored */
	private static final String KBDIRECTORY = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFolderDataset";
	
	/** The path in which the exported RDF file will be exported*/
	private static final String TEST_FILE_PATH = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/test/testFileRDF.nq";
	
	@Test 
	public void testBGetSemanticTagWithSELECT() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		RDFSTSet stSet = kb.getTopicSTSet();
		stSet.createSemanticTag("https://jena.apache.org/documentation/tdb", "Jena - TDB");		
		
		Query query = QueryFactory.create("SELECT ?s ?p ?o WHERE { ?s ?p ?o  }");		
		QueryExecution qexec = QueryExecutionFactory.create(query, kb.getTopicSTSet().getModel());
		kb.getDataset().begin(ReadWrite.READ);
		ResultSet results = qexec.execSelect();
		kb.getDataset().end();
		QuerySolution result = results.next();
		assertEquals("https://jena.apache.org/documentation/tdb", result.getResource("s").getURI());	
		assertEquals(RDFConstants.SEMANTIC_TAG_PREDICATE, result.getResource("p").getURI());
		assertEquals("Jena - TDB", result.getLiteral("o").toString());
	}
	
	@Test
	public void testAGetSemanticTagWithDESCRIBE() throws SharkKBException {
				
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		Query query = QueryFactory.create("DESCRIBE <https://jena.apache.org/documentation/tdb>");
		kb.getDataset().begin(ReadWrite.READ);
		QueryExecution qexec = QueryExecutionFactory.create(query, kb.getDataset());
		Model model = qexec.execDescribe();
		kb.getDataset().end();
		assertEquals("https://jena.apache.org/documentation/tdb", model.getResource("https://jena.apache.org/documentation/tdb").getURI());				
	}
	
	@Test
	public void testCCheckSemanticTagWithASK() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		Query query = QueryFactory.create("ASK WHERE { <https://jena.apache.org/documentation/tdb> ?p \"Jena - TDB\" . }");
		QueryExecution qexec = QueryExecutionFactory.create(query, kb.getTopicSTSet().getModel());
		kb.getDataset().begin(ReadWrite.READ);
		assertTrue(qexec.execAsk()); //Abfrage ausführen + überprüfen
	}
	
	@Test
	public void testDCreateTripleWithCONSTRUCT() throws SharkKBException {
		
		RDFSharkKB kb = new RDFSharkKB(KBDIRECTORY);
		Query query = QueryFactory.create("CONSTRUCT { <https://jena.apache.org> ?p ?o } " + "WHERE { <https://jena.apache.org/documentation/tdb> ?p ?o . }"); //Abfrage erzeugen
		QueryExecution qexec = QueryExecutionFactory.create(query, kb.getTopicSTSet().getModel()); //Model zuweisen
		kb.getDataset().begin(ReadWrite.WRITE);
		qexec.execConstruct(kb.getDataset().getDefaultModel()); //Abfrage ausführen
		kb.getDataset().end();
	}

	
	
	
	@Before
	public void clearDataset() throws IOException, SharkKBException {
		File index = new File(KBDIRECTORY);
		TestUtils.delete(index);
		new RDFSharkKB(KBDIRECTORY);
	}
	
	@AfterClass
	public static void writeTestResultsInRDFFile() throws SharkKBException {
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
	}
	
}
