package net.sharkfw.knowledgeBase.sync;

import static org.junit.Assert.*;

import java.util.Enumeration;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncKPTests {

    private long connectionTimeOut = 2000;
	private SyncKB _syncer = null;
	private SyncKB _synced = null;
	
	@BeforeClass
	public void setUpClass(){
		L.setLogLevel(L.LOGLEVEL_ALL);
	}
	
	@Before
	public void setUp() throws SharkKBException{
		SharkKB syncerInternal = new InMemoSharkKB();
		_syncer = new SyncKB(syncerInternal);
		SharkKB syncedInternal = new InMemoSharkKB();
		_synced = new SyncKB(syncedInternal);
	}
	
	@After
	public void tearDown(){
	    _syncer = null;
		_synced = null;
			
	}
	
	@Test
	public void syncInformation_created_wasSynchronized() { 
      /*
       * Filling syncer with information: noodles
       */
	  J2SEAndroidSharkEngine syncerEngine = new J2SEAndroidSharkEngine();
		
      Taxonomy topicsTX = _syncer.getTopicsAsTaxonomy();
      TXSemanticTag noodle = topicsTX.createTXSemanticTag("Noodle", "http://noodle.org");
      TXSemanticTag spaghetti = topicsTX.createTXSemanticTag("Spaghetti", "http://spaghetti.com");
      TXSemanticTag farfalle = topicsTX.createTXSemanticTag("Farfalle", "http://farfalle.de");

      spaghetti.move(noodle);
      farfalle.move(noodle);

      PeerSemanticTag syncerPeer = _syncer.createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5555");
      _syncer.setOwner(syncerPeer);
      
      PeerSemanticTag bobPeer = _syncer.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null); // I don't know a single address
      
      // Next create a contextpoint w/ infos about spaghetti!
      ContextCoordinates spaghettiCoords = _syncer.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint spaghettiCp = _syncer.createContextPoint(spaghettiCoords);
      spaghettiCp.addInformation("Spaghetti are the best noodles for kids!");

      ContextCoordinates noodleCoords = _syncer.createContextCoordinates(noodle, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint noodleCp = _syncer.createContextPoint(noodleCoords);
      noodleCp.addInformation("Noodles are yummy!");

      // Now create an interest to speak about spaghetti!
      SharkCS anchor = _syncer.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);

      // Create a FragmentationParameter that allows a depth of 1, and follows SUB/SUPER associations
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      FragmentationParameter[] fpArray = KnowledgePort.getZeroFP();
      fpArray[SharkCS.DIM_TOPIC] = fp; // Allow sub/super assocs on topic dimension

      SharkCS interest = _syncer.contextualize(anchor, fpArray);

      // Create a standard knowledgeport (and interest) from this information
      SyncKP syncerKP = new SyncKP(syncerEngine, interest, fpArray, _syncer);
      syncerKP.setOtp(fpArray); // Send infos on related tags as well

      syncerEngine.startTCP(5555);
      syncerEngine.setConnectionTimeOut(connectionTimeOut);

      // =========================================
      // Creation and config of _synced

      J2SEAndroidSharkEngine syncedEngine = new J2SEAndroidSharkEngine();

      SemanticTag syncedNoodle = _synced.createSemanticTag("Noodle", "http://noodle.org");

      PeerSemanticTag bobLocalPeer = _synced.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null);
      _synced.setOwner(bobPeer);

      SharkCS bobAs = _synced.createContextCoordinates(syncedNoodle, null, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
      // Not only will bob use the same fp as alice, it will also use it as OTP like alice
      SyncKP syncedKP = new SyncKP(syncedEngine, bobAs, fpArray, _synced);

      syncedEngine.setConnectionTimeOut(connectionTimeOut);


      // ===========================================
      // Make them talk

      Thread.sleep(1000);

      // ============================================
      // Check the results:
      // Bob must now know the topic spaghetti
      // Bob must know that spaghetti is a sub-concept of noodle
      // Bob must posess the contextpoint for spaghetti and the information
      // TODO: Check for "Noodle" information

      Taxonomy bobTopics = _synced.getTopicsAsTaxonomy();
      TXSemanticTag bobSpaghetti = bobTopics.getSemanticTag(new String[]{"http://spaghetti.com"});
      
      PeerSemanticTag bobsAlice = _synced.getPeerSemanticTag(syncerPeer.getSI());

      Assert.assertNotNull(bobSpaghetti);
      Assert.assertNotNull(bobsAlice);

      // Check to see if the associations have been learned properly
      TXSemanticTag spaghettiSuperTag = bobSpaghetti.getSuperTag();

      Assert.assertTrue(SharkCSAlgebra.identical(spaghettiSuperTag, syncedNoodle));

      // Create contextcoordinates to extract the received contextpoint from the knowledgebase
      ContextCoordinates extractCoords = _synced.createContextCoordinates(bobSpaghetti, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
      ContextPoint bobSpaghettiCp = _synced.getContextPoint(extractCoords);

      // The contextpoint must be present
      Assert.assertNotNull(bobSpaghettiCp);

      // The contextpoint must contain the information from alice
      Enumeration<Information> bobSpaghettiInfoEnum = bobSpaghettiCp.enumInformation();
      Assert.assertNotNull(bobSpaghettiInfoEnum);
      while(bobSpaghettiInfoEnum.hasMoreElements())
    	  Assert.assertNotNull(bobSpaghettiInfoEnum.nextElement());
      Information bobShovelInfo = (Information) bobSpaghettiInfoEnum.nextElement();
      byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
      String bobShovelString = new String(bobShovelContent);
      Assert.assertEquals(bobShovelString, "Spaghetti are the best noodles for kids!");
	}

}
