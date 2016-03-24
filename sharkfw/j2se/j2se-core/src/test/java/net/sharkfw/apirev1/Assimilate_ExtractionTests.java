package net.sharkfw.apirev1;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.system.L;
import org.junit.*;

/**
 *
 * @author thsc
 */
public class Assimilate_ExtractionTests {
    
    public Assimilate_ExtractionTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testGetAllCps() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
      SharkKB kb = new InMemoSharkKB();

      // We need two distinct topics
      SemanticTag topic = kb.getTopicSTSet().createSemanticTag("Topic", "http://topic.de");
      SemanticTag otherTopic = kb.getTopicSTSet().createSemanticTag("Other-Topic", "http://other-topic.de");

      // Also two distinct originator
      PeerSemanticTag peerA = kb.getPeerSTSet().createPeerSemanticTag("PeerA", "http://peerA.com", (String[]) null);
      PeerSemanticTag peerB = kb.getPeerSTSet().createPeerSemanticTag("PeerB", "http://peerB.com", (String[]) null);

      // Create three different coordinates, of which two share a mutual topic
      ContextCoordinates co1 = kb.createContextCoordinates(topic, peerA, null, null, null, null, SharkCS.DIRECTION_INOUT);
      ContextCoordinates co2 = kb.createContextCoordinates(topic, peerB, null, null, null, null, SharkCS.DIRECTION_INOUT);
      ContextCoordinates co3 = kb.createContextCoordinates(otherTopic, peerA, null, null, null, null, SharkCS.DIRECTION_INOUT);

      // Create CPs for each
      ContextPoint cp1 = kb.createContextPoint(co1);
      ContextPoint cp2 = kb.createContextPoint(co2);
      ContextPoint cp3 = kb.createContextPoint(co3);

      cp1.addInformation("CP1");
      cp2.addInformation("CP2");
      cp3.addInformation("CP3");

      // Create a generic coordinate, with only topic set to a defined value
      ContextCoordinates extractCo = kb.createContextCoordinates(topic, null, null, null, null, null, SharkCS.DIRECTION_INOUT);
      
      Knowledge fragment =
        SharkCSAlgebra.extract(kb, extractCo, KnowledgePort.getZeroFP());
      
      Enumeration<ContextPoint> resultEnum = fragment.contextPoints();
      Assert.assertNotNull(resultEnum);
      Assert.assertTrue(resultEnum.hasMoreElements());

      // markers for found CPs in result
      boolean found1 = false, found2 = false, found3 = false;

      while(resultEnum.hasMoreElements()) {
        ContextPoint cp = resultEnum.nextElement();
        if(SharkCSAlgebra.identical(cp.getContextCoordinates(), cp1.getContextCoordinates())) {
          found1 = true;
        }

        if(SharkCSAlgebra.identical(cp.getContextCoordinates(), cp2.getContextCoordinates())) {
          found2 = true;
        }

        if(SharkCSAlgebra.identical(cp.getContextCoordinates(), cp3.getContextCoordinates())) {
          found3 = true;
        }
      }
      // CP one and two must be found, three must not be found.
      Assert.assertTrue(found1);
      Assert.assertTrue(found2);
      Assert.assertFalse(found3);
    }
    
    @Test
    public void testOTPAndFPInExchange() throws SharkKBException, InterruptedException {

      ///////////////////////////////////////////////////////////////////////
      //                          Alice                                    //
      ///////////////////////////////////////////////////////////////////////

      /*
       * Creating a peer with a vocabulary about tools.
       */
      J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
      SharkKB aliceKB = new InMemoSharkKB();

      Taxonomy topicsTX = aliceKB.getTopicsAsTaxonomy();
      TXSemanticTag tools = topicsTX.createTXSemanticTag("Tools", "http://tools.org");
      TXSemanticTag shovel = topicsTX.createTXSemanticTag("Shovel", "http://shovel.org");
      TXSemanticTag spade = topicsTX.createTXSemanticTag("Spade", "http://spade.org");

      shovel.move(tools);
      spade.move(tools);

      PeerSemanticTag alicePeer = aliceKB.getPeerSTSet().createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5555");
      PeerSemanticTag bobPeer = aliceKB.getPeerSTSet().createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5556"); // Our future comm partner

      // Next create a contextpoint w/ infos about shovels!
      ContextCoordinates shovelCoords = aliceKB.createContextCoordinates(shovel, alicePeer, alicePeer, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint shovelCp = aliceKB.createContextPoint(shovelCoords);
      shovelCp.addInformation("A shovel is a cool tool!");

      ContextCoordinates toolCoords = aliceKB.createContextCoordinates(tools, alicePeer, alicePeer, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint toolsCp = aliceKB.createContextPoint(toolCoords);
      shovelCp.addInformation("Tools are cool!");

      // Create a FragmentationParameter that allows a depth of 1, and follows SUB/SUPER associations
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      FragmentationParameter[] fpArray = KnowledgePort.getZeroFP();
      fpArray[SharkCS.DIM_TOPIC] = fp; // Allow sub/super assocs on topic dimension
      
      // Now create an kepInterest to speak about shovels!
      ContextCoordinates aliceAnchor = aliceKB.createContextCoordinates(shovel, alicePeer, alicePeer, null, null, null, SharkCS.DIRECTION_OUT);
      SharkCS aliceInterest = aliceKB.contextualize(aliceAnchor, fpArray);
      
      ////////////// Tests 
      // should contain a little ontology
      STSet aliceInterestTopics = aliceInterest.getTopics();
      SemanticTag aliceInterestShovel = aliceInterestTopics.getSemanticTag(shovel.getSI()); 
      Assert.assertNotNull(aliceInterestShovel);
      
      // must be in a taxonomy
      Assert.assertTrue(aliceInterestShovel instanceof TXSemanticTag);
      TXSemanticTag txst = (TXSemanticTag)aliceInterestShovel;
      TXSemanticTag superTXSt = txst.getSuperTag();
      Assert.assertNotNull(superTXSt);
      Assert.assertTrue(SharkCSAlgebra.identical(superTXSt, tools));

      // spade must no in the kepInterest
      SemanticTag st = aliceInterestTopics.getSemanticTag(spade.getSI()); 
      Assert.assertNull(st);
      
      SemanticTag interestTool = aliceInterestTopics.getSemanticTag(tools.getSI());
      txst = (TXSemanticTag)interestTool;
      Assert.assertNotNull(txst);
      TXSemanticTag subTX = txst.getSubTags().nextElement();
      Assert.assertTrue(SharkCSAlgebra.identical(subTX, shovel));
      
      ///////////////////////////////////////////////////////////////////////
      //                          Bob                                      //
      ///////////////////////////////////////////////////////////////////////

      // =========================================
      // Creation and config of Bob

      J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
      SharkKB bobKb = new InMemoSharkKB();

      SemanticTag bobTools = bobKb.getTopicSTSet().createSemanticTag("Tools", "http://tools.org");

      PeerSemanticTag bobLocalPeer = bobKb.getPeerSTSet().createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5556");

      SharkCS bobInterest = bobKb.createContextCoordinates(bobTools, null, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
      // Not only will bob use the same fp as alice, it will also use it as OTP like alice

      
      // bob uses same fp
      
      ///////////////////////////////////////////////////////////////////////
      //                          Communication                            //
      ///////////////////////////////////////////////////////////////////////
      
      // Alice sends kepInterest - Bob creates effective kepInterest:
      SharkCS bobAliceInterest = SharkCSAlgebra.contextualize(aliceInterest, bobInterest, fpArray);
      
      // should be: 
      STSet interestTopics = bobAliceInterest.getTopics();
      SemanticTag interestShovel = interestTopics.getSemanticTag(shovel.getSI()); 
      Assert.assertNotNull(interestShovel);
      
      // must be in a taxonomy
      Assert.assertTrue(interestShovel instanceof TXSemanticTag);
      txst = (TXSemanticTag)interestShovel;
      superTXSt = txst.getSuperTag();
      Assert.assertNotNull(superTXSt);
      Assert.assertTrue(SharkCSAlgebra.identical(superTXSt, tools));

      // this kepInterest is sent back to alice - she contextualizes
      SharkCS aliceBobInterest = SharkCSAlgebra.contextualize(bobAliceInterest, aliceInterest, fpArray);
      
      System.out.println("Alice extracts with mutual her kepInterest");
      System.out.println(L.contextSpace2String(aliceBobInterest));
      
      Knowledge alice2BobKnowledge = SharkCSAlgebra.extract(aliceKB, aliceBobInterest, fpArray); 
      
      Enumeration<ContextPoint> cpEnum = alice2BobKnowledge.contextPoints();
      while(cpEnum.hasMoreElements()) {
          ContextPoint cp = cpEnum.nextElement();
          System.out.println("CP with cc: " + L.contextSpace2String(cp.getContextCoordinates()));
      }
      
      // this knowledge is sent to Bob - he assimilates and likes to learn semantic tags
      SharkCSAlgebra.assimilate(bobKb, bobInterest, fpArray, alice2BobKnowledge, true, true);

      ///////////////////////////////////////////////////////////////////////
      //                          Test                                     //
      ///////////////////////////////////////////////////////////////////////

      Taxonomy bobTopics = bobKb.getTopicsAsTaxonomy();
      TXSemanticTag bobShovel = bobTopics.getSemanticTag("http://shovel.org");

      PeerTaxonomy bobPeers = bobKb.getPeersAsTaxonomy();
      PeerTXSemanticTag bobsAlice = bobPeers.getSemanticTag(alicePeer.getSI());

      Assert.assertNotNull(bobShovel);
      Assert.assertNotNull(bobsAlice);

      // Check to see if the associations have been learned properly
      TXSemanticTag shovelSuperTag = bobShovel.getSuperTag();
      Assert.assertNotNull(shovelSuperTag);

      // Bob learnt about shovel which is a subtopic of tools
      Assert.assertTrue(SharkCSAlgebra.identical(shovelSuperTag, bobTools));

      // Create contextcoordinates to extract the received contextpoint from the knowledgebase - direction out - still Alice perspective
      ContextCoordinates extractCos = bobKb.createContextCoordinates(bobShovel, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
      cpEnum = bobKb.getContextPoints(extractCos);

      // The contextpoint must be present
      Assert.assertNotNull(cpEnum);
      
      ContextPoint bobShovelCp = cpEnum.nextElement();
      
      // The contextpoint must contain the information from alice
      Enumeration bobShovelInfoEnum = bobShovelCp.enumInformation();
      Assert.assertNotNull(bobShovelInfoEnum);
      Information bobShovelInfo = (Information) bobShovelInfoEnum.nextElement();
      byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
      String bobShovelString = new String(bobShovelContent);
      Assert.assertEquals(bobShovelString, "A shovel is a cool tool!");
    }
    
    @Test
    public void phnxTest() throws SharkKBException, InterruptedException {
        SharkKB kb = new InMemoSharkKB();
        SemanticTag topic = kb.getTopicSTSet().createSemanticTag("asdad", "http://phnx.berlin/shark/equipment.html/asdsad");
        PeerSemanticTag mustermann = kb.getPeerSTSet().createPeerSemanticTag("PHNX_BC_SI", "mustermann@htw-berlin.de", (String) null);
        PeerSemanticTag phnx = kb.getPeerSTSet().createPeerSemanticTag("PHNX_Organization_SI", "www.phnx.de", (String) null);
        
        ContextCoordinates cpcc = kb.createContextCoordinates(topic, phnx, mustermann, null, null, null, SharkCS.DIRECTION_NOTHING);
        
        ContextPoint cp = kb.createContextPoint(cpcc);
        cp.addInformation("huhu phnx");
        
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(null, phnx, null, null, null, null, SharkCS.DIRECTION_NOTHING);
        
        FragmentationParameter[] fps = new FragmentationParameter[SharkCS.MAXDIMENSIONS];
        FragmentationParameter fp = new FragmentationParameter(false, true, 1);
        fps[SharkCS.DIM_TOPIC] = fp;
        
        Knowledge k = SharkCSAlgebra.extract(kb, cc, fps);
        
        Assert.assertNotNull(k);
        
//        System.out.println(L.knowledge2String(k));
    }
}
