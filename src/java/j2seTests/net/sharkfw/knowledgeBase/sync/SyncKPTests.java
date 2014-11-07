package net.sharkfw.knowledgeBase.sync;

import java.io.IOException;
import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.Iterator;

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
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SyncKPTests {

    private long connectionTimeOut = 2000;
	private SyncKB _aliceSyncKB = null;
	private SyncKB _bobSyncKB = null;
        private SyncKP _aliceSyncKP, _bobSyncKP;
        private SharkEngine _aliceEngine, _bobEngine;
	
	@BeforeClass
	public void setUpClass(){
		L.setLogLevel(L.LOGLEVEL_ALL);
	}
	
	@Before
	public void setUp() throws SharkKBException{
            // Set up KBs
            SharkKB syncerInternal = new InMemoSharkKB();
            _aliceSyncKB = new SyncKB(syncerInternal);
            SharkKB syncedInternal = new InMemoSharkKB();
            _bobSyncKB = new SyncKB(syncedInternal);
            // Set up engines
            _aliceEngine = new J2SEAndroidSharkEngine();
            _bobEngine = new J2SEAndroidSharkEngine();
            // Kps
            _aliceSyncKP = new SyncKP(_aliceEngine, _aliceSyncKB);
            _bobSyncKP = new SyncKP(_bobEngine, _bobSyncKB);
	}
	
	@After
	public void tearDown(){
	    _aliceSyncKB = null;
            _bobSyncKB = null;
			
	}
	
        @Test
        public void syncKP_meetsNonSyncKP_noCommunication() throws SharkKBException {
            // Create a standard KP with some information in context space
            // Alice will be a sync KP, bob a standard KP
            SemanticTag teapotST = InMemoSharkKB.createInMemoSemanticTag("teapot", "www.teapot.de");
            PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "alice", "mail@alice.de");
            PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "bob", "mail@bob.de");
            ContextCoordinates teapotCC = InMemoSharkKB.createInMemoContextCoordinates(teapotST, alice, alice, bob, null, null, SharkCS.DIRECTION_INOUT);
            
            // Create mutual CP in alices and bobs kb
            _aliceSyncKB.createContextPoint(teapotCC);
            
        }
 
        @Test
        public void syncKP_meetsSyncKP_CommunicationHappening() {
            
        }
        
        @Test
        public void syncKP_CPIsNotInKB_CPAssimilated() {
            
        }
 
        @Test
        public void syncKP_CPIsWithLowerVersionInKB_CPInformationAssimilated() {
            
        }
        
        @Test
        public void syncKP_CPIsWithGreaterEqualVersionInKB_CPInformationNotAssimilated() {
            
        }
        
        
	@Test
	public void syncKP_synchronizeTwoKPs_syncKBsHaveSameCPs()throws Exception { 
            /*
             * Filling syncer with information: noodles
             */
            J2SEAndroidSharkEngine syncerEngine = new J2SEAndroidSharkEngine();
		
            Taxonomy topicsTX = _aliceSyncKB.getTopicsAsTaxonomy();
            TXSemanticTag noodle = topicsTX.createTXSemanticTag("Noodle", "http://noodle.org");
            TXSemanticTag spaghetti = topicsTX.createTXSemanticTag("Spaghetti", "http://spaghetti.com");
            TXSemanticTag farfalle = topicsTX.createTXSemanticTag("Farfalle", "http://farfalle.de");

            spaghetti.move(noodle);
            farfalle.move(noodle);

            PeerSemanticTag syncerPeer = _aliceSyncKB.createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5555");
            _aliceSyncKB.setOwner(syncerPeer);

            PeerSemanticTag bobPeer = _aliceSyncKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null); // I don't know a single address

            // Next create a contextpoint w/ infos about spaghetti!
            ContextCoordinates spaghettiCoords = _aliceSyncKB.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
            ContextPoint spaghettiCp = _aliceSyncKB.createContextPoint(spaghettiCoords);
            spaghettiCp.addInformation("Spaghetti are the best noodles for kids!");

            ContextCoordinates noodleCoords = _aliceSyncKB.createContextCoordinates(noodle, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
            ContextPoint noodleCp = _aliceSyncKB.createContextPoint(noodleCoords);
            noodleCp.addInformation("Noodles are yummy!");

            // Now create an interest to speak about spaghetti!
            SharkCS anchor = _aliceSyncKB.createContextCoordinates(spaghetti, syncerPeer, null, null, null, null, SharkCS.DIRECTION_OUT);

            //Create blacklist
            Iterator<PeerSemanticTag> blacklist = null;
            
            // Create a standard knowledgeport (and interest) from this information
            SyncKP syncerKP = new SyncKP(syncerEngine, _aliceSyncKB);

            syncerEngine.startTCP(5555);
            syncerEngine.setConnectionTimeOut(connectionTimeOut);

            // =========================================
            // Creation and config of _synced

            J2SEAndroidSharkEngine syncedEngine = new J2SEAndroidSharkEngine();

            SemanticTag syncedNoodle = _bobSyncKB.createSemanticTag("Noodle", "http://noodle.org");

            PeerSemanticTag bobLocalPeer = _bobSyncKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null);
            _bobSyncKB.setOwner(bobPeer);

            SharkCS bobAs = _bobSyncKB.createContextCoordinates(syncedNoodle, null, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
            // Not only will bob use the same fp as alice, it will also use it as OTP like alice
            SyncKP syncedKP = new SyncKP(syncedEngine, _bobSyncKB);

            syncedEngine.setConnectionTimeOut(connectionTimeOut);


            // ===========================================
            // Make them talk
            syncedEngine.publishKP(syncedKP);

            Thread.sleep(1000);

            // ============================================
            // Check the results:
            // Bob must now know the topic spaghetti
            // Bob must know that spaghetti is a sub-concept of noodle
            // Bob must posess the contextpoint for spaghetti and the information
            // TODO: Check for "Noodle" information

            Taxonomy bobTopics = _bobSyncKB.getTopicsAsTaxonomy();
            TXSemanticTag bobSpaghetti = bobTopics.getSemanticTag(new String[]{"http://spaghetti.com"});

            PeerSemanticTag bobsAlice = _bobSyncKB.getPeerSemanticTag(syncerPeer.getSI());

            Assert.assertNotNull(bobSpaghetti);
            Assert.assertNotNull(bobsAlice);

            // Check to see if the associations have been learned properly
            TXSemanticTag spaghettiSuperTag = bobSpaghetti.getSuperTag();

            Assert.assertTrue(SharkCSAlgebra.identical(spaghettiSuperTag, syncedNoodle));

            // Create contextcoordinates to extract the received contextpoint from the knowledgebase
            ContextCoordinates extractCoords = _bobSyncKB.createContextCoordinates(bobSpaghetti, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
            ContextPoint bobSpaghettiCp = _bobSyncKB.getContextPoint(extractCoords);

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
