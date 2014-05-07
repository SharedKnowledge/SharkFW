package ApiRev1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Enumeration;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;
import net.sharkfw.system.Util;
import org.junit.*;

/**
 *
 * @author mfi
 */
public class ExchangeTests {
    private long connectionTimeOut = 2000;

    public ExchangeTests() {
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


    /**
     * Two peers define their vocabulary. One peer knows a number of "tools", and
     * is willing to send information about "shovel".
     *
     * This peer has Information about "shovel" in its KB.
     *
     * The other peer only knows the general term "tools", and would like to
     * receive information about "tools". It known nothing of shovels, spades,
     * or the the sending peer.
     *
     * The sending peer publishes its interest to the receiving peer.
     *
     * The receiving peer then must answer with an appropriate interest,
     * which must lead to knowledge being sent by the sending peer.
     *
     * The receiving peer must assimilate that knowledge and learn the tag
     * "shovel" and "alice" on the correct dimensions.
     *
     * In order to receive information about a related tag, both peers need
     * to configure their standard knowledge port with an OTP that allows
     * a depth of 1 and "SUB/SUPER" associations on the topic dimension.
     *
     * @throws SharkKBException
     * @throws InterruptedException
     */
    @Test
    public void testOTPAndFPInExchange() throws SharkKBException, InterruptedException, SharkSecurityException, IOException {
        L.setLogLevel(L.LOGLEVEL_ALL);
        
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

      PeerSemanticTag alicePeer = aliceKB.createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5555");
      aliceKB.setOwner(alicePeer);
      
//      PeerSemanticTag bobPeer = aliceKB.createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5556"); // Our future comm partner
      PeerSemanticTag bobPeer = aliceKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null); // I don't know a single address
      
      // Next create a contextpoint w/ infos about shovels!
      ContextCoordinates shovelCoords = aliceKB.createContextCoordinates(shovel, alicePeer, null, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint shovelCp = aliceKB.createContextPoint(shovelCoords);
      shovelCp.addInformation("A shovel is a cool tool!");

      ContextCoordinates toolCoords = aliceKB.createContextCoordinates(tools, alicePeer, null, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint toolsCp = aliceKB.createContextPoint(toolCoords);
      toolsCp.addInformation("Tools are cool!");

      // Now create an interest to speak about shovels!

      SharkCS anchor = aliceKB.createContextCoordinates(shovel, alicePeer, null, null, null, null, SharkCS.DIRECTION_OUT);

      // Create a FragmentationParameter that allows a depth of 1, and follows SUB/SUPER associations
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      FragmentationParameter[] fpArray = KnowledgePort.getZeroFP();
      fpArray[SharkCS.DIM_TOPIC] = fp; // Allow sub/super assocs on topic dimension

      SharkCS interest = aliceKB.contextualize(anchor, fpArray);

      // Create a standard knowledgeport (and interest) from this information
      StandardKP aliceKp = new StandardKP(alice, interest, fpArray, aliceKB);
      aliceKp.setOtp(fpArray); // Send infos on related tags as well

      alice.startTCP(5555);
      alice.setConnectionTimeOut(connectionTimeOut);

      // =========================================
      // Creation and config of Bob

      J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
      SharkKB bobKB = new InMemoSharkKB();

      SemanticTag bobTools = bobKB.createSemanticTag("Tools", "http://tools.org");

//      PeerSemanticTag bobLocalPeer = bobKB.createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5556");
      PeerSemanticTag bobLocalPeer = bobKB.createPeerSemanticTag("Bob", "http://bob.org", (String[]) null);
      bobKB.setOwner(bobPeer);

      SharkCS bobAs = bobKB.createContextCoordinates(bobTools, null, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
      // Not only will bob use the same fp as alice, it will also use it as OTP like alice
      StandardKP bobKp = new StandardKP(bob, bobAs, fpArray, bobKB);

//      bob.startTCP(5556);
      bob.setConnectionTimeOut(connectionTimeOut);


      // ===========================================
      // Make them talk

//      alice.publishAllKp(bobPeer);
      bob.publishAllKP(alicePeer);

      Thread.sleep(1000);
//      Thread.sleep(Integer.MAX_VALUE);

      // ============================================
      // Check the results:
      // Bob must now know the topic shovel
      // Bob must know that shovel is a sub-concept of tools
      // Bob must posess the contextpoint for shovel and the information
      // TODO: Check for "Tools" information

      Taxonomy bobTopics = bobKB.getTopicsAsTaxonomy();
      TXSemanticTag bobShovel = bobTopics.getSemanticTag(new String[]{"http://shovel.org"});
      
      PeerSemanticTag bobsAlice = bobKB.getPeerSemanticTag(alicePeer.getSI());

      Assert.assertNotNull(bobShovel);
      Assert.assertNotNull(bobsAlice);

      // Check to see if the associations have been learned properly
      TXSemanticTag shovelSuperTag = bobShovel.getSuperTag();

      Assert.assertTrue(SharkCSAlgebra.identical(shovelSuperTag, bobTools));

      // Create contextcoordinates to extract the received contextpoint from the knowledgebase
      ContextCoordinates extractCos = bobKB.createContextCoordinates(bobShovel, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
      ContextPoint bobShovelCp = bobKB.getContextPoint(extractCos);

      // The contextpoint must be present
      Assert.assertNotNull(bobShovelCp);

      // The contextpoint must contain the information from alice
      Enumeration bobShovelInfoEnum = bobShovelCp.enumInformation();
      Assert.assertNotNull(bobShovelInfoEnum);
      Information bobShovelInfo = (Information) bobShovelInfoEnum.nextElement();
      byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
      String bobShovelString = new String(bobShovelContent);
      Assert.assertEquals(bobShovelString, "A shovel is a cool tool!");
    }

    /**
     * Create a setting as in Exchange test.
     *
     * Switch learning to "false" in KnowledgePort, so that no new tags will be learned.
     *
     * The received ContextPoint should be created with locally known tags only,
     * except for alice's tag of course, which was learned due to "ANY" on the
     * REMOTEPEER dimension.
     *
     */
//    @Test
    public void testLearnFalseInExchange() throws SharkKBException, InterruptedException, SharkSecurityException, IOException {

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

      PeerSemanticTag alicePeer = aliceKB.createPeerSemanticTag("Alice", "http://alice.org", "tcp://localhost:5557");
      aliceKB.setOwner(alicePeer);
      
      PeerSemanticTag bobPeer = aliceKB.createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5558"); // Our future comm partner

      // Next create a contextpoint w/ infos about shovels!
      ContextCoordinates shovelCoords = aliceKB.createContextCoordinates(shovel, alicePeer, null, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint shovelCp = aliceKB.createContextPoint(shovelCoords);
      shovelCp.addInformation("A shovel is a cool tool!");

      // Now create an interest to speak about shovels!

      // Create a FragmentationParameter that allows a depth of 1, and follows SUB/SUPER associations
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      FragmentationParameter[] fpArray = KnowledgePort.getZeroFP();
      fpArray[SharkCS.DIM_TOPIC] = fp; // Allow sub/super assocs on topic dimension

      // Create a standard knowledgeport (and interest) from this information
      StandardKP aliceKp = new StandardKP(alice, shovelCoords, fpArray, aliceKB);
      aliceKp.setOtp(fpArray); // Send infos on related tags as well

      alice.startTCP(5557);


      // =========================================
      // Creation and config of Bob

      J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
      SharkKB bobKb = new InMemoSharkKB();

      SemanticTag bobTools = bobKb.createSemanticTag("Tools", "http://tools.org");

      PeerSemanticTag bobLocalPeer = bobKb.createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5558");

      SharkCS bobAs = bobKb.createContextCoordinates(bobTools, bobLocalPeer, null, null, null, null, SharkCS.DIRECTION_IN);
      // Not only will bob use the same fp as alice, it will also use it as OTP like alice
      StandardKP bobKp = new StandardKP(bob, bobAs, fpArray, bobKb);
      bobKp.learnSTs(false); // Don't learn new Tags. Une locally known tags if possible.

      bobKp.setOtp(fpArray);

      bob.startTCP(5558);


      // ===========================================
      // Make them talk

      bob.publishAllKP(alicePeer);
//      alice.publishAllKP(bobPeer);

      Thread.sleep(1000);

      // ============================================
      // Check the results:
      // Bob must now know the topic shovel
      // Bob must know that shovel is a sub-concept of tools
      // Bob must posess the contextpoint for shovel and the information

      SNSemanticTag bobShovel = (SNSemanticTag) bobKb.getSemanticTag(new String[]{"http://shovel.org"});
      PeerSNSemanticTag bobsAlice = (PeerSNSemanticTag) bobKb.getPeerSemanticTag(alicePeer.getSI());

      Assert.assertNull(bobShovel);
      Assert.assertNotNull(bobsAlice);

     
      // Create contextcoordinates to extract the received contextpoint from the knowledgebase
      ContextCoordinates extractCos = bobKb.createContextCoordinates(bobTools, bobsAlice, null, null, null, null, SharkCS.DIRECTION_IN);
      ContextPoint bobShovelCp = bobKb.getContextPoint(extractCos);

      // The contextpoint must be present
      Assert.assertNotNull(bobShovelCp);

      // The contextpoint must contain the information from alice
      Enumeration bobShovelInfoEnum = bobShovelCp.enumInformation();
      Assert.assertNotNull(bobShovelInfoEnum);
      Information bobShovelInfo = (Information) bobShovelInfoEnum.nextElement();
      byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
      String bobShovelString = new String(bobShovelContent);
      Assert.assertEquals(bobShovelString, "A shovel is a cool tool!");
    }


     /**
     * Two peers define their vocabulary. One peer knows a number of "tools", and
     * is willing to send information about "shovel".
     *
     * This peer has Information about "shovel" in its KB.
     *
     * The other peer only knows the general term "tools", and would like to
     * receive information about "tools". It known nothing of shovels, spades,
     * or the the sending peer.
     *
     * The sending peer stores its information on a contextpoint which is bound
     * to a hidden tag (a group i.e.). This group has one member (the receiving peer).
     *
     * The sending peer publishes its interest to the receiving peer without
     * sending the grouptag itself.
     *
     * The receiving peer then must answer with an appropriate interest,
     * which must lead to knowledge being sent by the sending peer.
     *
     * The receiving peer must assimilate that knowledge and learn the tag
     * "shovel" and "alice" on the correct dimensions.
     *
     * In order to receive information about a related tag, both peers need
     * to configure their standard knowledge port with an OTP that allows
     * a depth of 1 and "SUB/SUPER" associations on the topic dimension.
     *
     * @throws SharkKBException
     * @throws InterruptedException
     */
//    @Test
    public void testOTPAndFPInExchangeWithGroups() throws SharkKBException, InterruptedException, SharkSecurityException, IOException {

      /*
       * Creating a peer with a vocabulary about tools.
       */
      J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
      SharkKB aliceKb = new InMemoSharkKB();
      SemanticNet aliceTopics = aliceKb.getTopicsAsSemanticNet();

      SNSemanticTag tools = aliceTopics.createSemanticTag("Tools", "http://tools.org");
      SNSemanticTag shovel = aliceTopics.createSemanticTag("Shovel", "http://shovel.org");
      SNSemanticTag spade = aliceTopics.createSemanticTag("Spade", "http://spade.org");

      shovel.setPredicate(SemanticNet.SUPERTAG, tools);
      spade.setPredicate(SemanticNet.SUPERTAG, tools);

      PeerSemanticNet alicePeers = aliceKb.getPeersAsSemanticNet();
      PeerSNSemanticTag alicePeer = alicePeers.createSemanticTag("Alice", "http://alice.org", "tcp://localhost:5560");
      PeerSNSemanticTag bobPeer = alicePeers.createSemanticTag("Bob", "http://bob.org", "tcp://localhost:5561"); // Our future comm partner
      PeerSNSemanticTag bobOrg = alicePeers.createSemanticTag("BobOrg", "http://bobOrg.de", new String[]{});

      // BobOrg must never leave alice's system.

      bobOrg.setHidden(true);
      // make bob a member of bobOrg
      bobPeer.move(bobOrg);

      // Next create a contextpoint w/ infos about shovels! The information on the cp is for all members of bobOrg.
      ContextCoordinates shovelCoords = aliceKb.createContextCoordinates(shovel, alicePeer, bobOrg, null, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint shovelCp = aliceKb.createContextPoint(shovelCoords);
      shovelCp.addInformation("A shovel is a cool tool!");

      // Now create an interest to speak about shovels!

      // Create an interest headed for the organization
      SharkCS interest = aliceKb.createContextCoordinates(shovel, alicePeer, bobOrg, null, null, null, SharkCS.DIRECTION_OUT);

      // Create a FragmentationParameter that allows a depth of 1, and follows SUB/SUPER associations
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      FragmentationParameter[] fpArray = Util.getZeroFP();
      fpArray[SharkCS.DIM_TOPIC] = fp; // Allow sub/super assocs on topic dimension
      fpArray[SharkCS.DIM_REMOTEPEER] = fp; // Allow traversal of peer tags for bobOrg.

      // Create a standard knowledgeport (and interest) from this information
      StandardKP aliceKp = new StandardKP(alice, interest, fpArray, aliceKb);
      aliceKp.setOtp(fpArray); // Send infos on related tags as well

      alice.startTCP(5560);


      // =========================================
      // Creation and config of Bob

      J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
      SharkKB bobKb = new InMemoSharkKB();

      SemanticTag bobTools = bobKb.createSemanticTag("Tools", "http://tools.org");

      PeerSemanticTag bobLocalPeer = bobKb.createPeerSemanticTag("Bob", "http://bob.org", "tcp://localhost:5561");

      SharkCS bobAs = bobKb.createContextCoordinates(bobTools, bobLocalPeer, null, null, null, null, SharkCS.DIRECTION_IN);
      // Not only will bob use the same fp as alice, it will also use it as OTP like alice
      StandardKP bobKp = new StandardKP(bob, bobAs, fpArray, bobKb);
      bobKp.setOtp(fpArray);

      bob.startTCP(5561);


      // ===========================================
      // Make them talk

      alice.publishAllKP(bobPeer);

      Thread.sleep(3000);

      // ============================================
      // Check the results:
      // Bob must now know the topic shovel
      // Bob must know that shovel is a sub-concept of tools
      // Bob must posess the contextpoint for shovel and the information

      SNSemanticTag bobShovel = bobKb.getTopicsAsSemanticNet().getSemanticTag(new String[]{"http://shovel.org"});
      PeerSNSemanticTag bobsAlice = bobKb.getPeersAsSemanticNet().getSemanticTag(alicePeer.getSI());

      Assert.assertNotNull(bobShovel);
      Assert.assertNotNull(bobsAlice);

      // Check to see if the associations have been learned properly
      Enumeration<SNSemanticTag> shovelSuperTags = bobShovel.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(shovelSuperTags);
      Assert.assertTrue(shovelSuperTags.hasMoreElements());
      SNSemanticTag shovelSuperTag = shovelSuperTags.nextElement();

      Assert.assertTrue(Util.sameEntity(tools.getSI(), shovelSuperTag.getSI()));

      // Create contextcoordinates to extract the received contextpoint from the knowledgebase. Knowledge must be filed under "bobLocalPeer" and NOT under "bobCorp".
      ContextCoordinates extractCos = bobKb.createContextCoordinates(bobShovel, bobsAlice, bobLocalPeer, null, null, null, SharkCS.DIRECTION_IN);
      ContextPoint bobShovelCp = bobKb.getContextPoint(extractCos);

      // The contextpoint must be present
      Assert.assertNotNull(bobShovelCp);

      // The contextpoint must contain the information from alice
      Enumeration bobShovelInfoEnum = bobShovelCp.enumInformation();
      Assert.assertNotNull(bobShovelInfoEnum);
      Information bobShovelInfo = (Information) bobShovelInfoEnum.nextElement();
      byte[] bobShovelContent = bobShovelInfo.getContentAsByte();
      String bobShovelString = new String(bobShovelContent);
      Assert.assertEquals(bobShovelString, "A shovel is a cool tool!");
    }
    
    
    /**
     * Create alice peer with a vocabulary for topics, peers and times.
     * Alice knows Bob as a peer from the start.
     * 
     * Alice has two contextpoints, with the same coordinates except for the time
     * dimension. One CP is valid today. One CP has been valid yesterday.
     * 
     * Alice defines an interest is sending something about 'topic' which is valid today,
     * alice is the peer and the originator.
     * 
     * Alice starts its TCP engine.
     * 
     * Bob is created with a (small) vocabulary for topics, peers and times.
     * Bob knows alice from the start.
     * Bob has no ContextPoints.
     * 
     * Bob creates an interest in 'topic' which is valid today and requests information from alice
     * as a peer and as originator.
     * 
     * Bob starts its tcp engine.
     * 
     * Next alice publishes all of its KPs.
     * 
     * Wait 5 seconds.
     * 
     * Check bob's kb for the contextpoint which must have been received by alice.
     * Check that the time tag of the contextpoint is set to 'bobToday' and
     * that the information attached to the contextpoint matches the information
     * from alice.
     * 
     * @throws InterruptedException
     * @throws UnsupportedEncodingException 
     */
//    @Test
    public void exchangeTestUsingTimeDim() throws InterruptedException, UnsupportedEncodingException, SharkKBException, SharkSecurityException, IOException {
      J2SEAndroidSharkEngine aliceSE = new J2SEAndroidSharkEngine();
      SharkKB aliceKB = new InMemoSharkKB();
      
      // Create topic vocabulary
      SemanticNet aliceTopics = aliceKB.getTopicsAsSemanticNet();
      SNSemanticTag topic = aliceTopics.createSemanticTag("Topic", "http://topic.de");
      SNSemanticTag subtopic = aliceTopics.createSemanticTag("Subtopic", "http://subtopic.de");
      
      subtopic.setPredicate(SemanticNet.SUPERTAG, topic);
      
      // Create Peer vocabulary
      PeerSemanticNet alicePeers = aliceKB.getPeersAsSemanticNet();
      PeerSNSemanticTag alice = alicePeers.createSemanticTag("Alice", "http://alice.de", "tcp://localhost:6661");
      PeerSNSemanticTag bob = alicePeers.createSemanticTag("Bob", "http://bob.de", "tcp://localhost:6662");
      
      // Create Time vocabulary
      Calendar cal = Calendar.getInstance();
      int weekday = cal.get(Calendar.DAY_OF_WEEK);
      
      TimeSemanticTag today = aliceKB.createTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, TimeSemanticTag.FOREVER);
      TimeSemanticTag yesterday = aliceKB.createTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, TimeSemanticTag.FOREVER);
      
      // Create two contextpoints, of which one is valid today, the other was valid yesterday
      ContextCoordinates co = aliceKB.createContextCoordinates(topic, alice, bob, alice, today, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp = aliceKB.createContextPoint(co);
      cp.addInformation("Today is a good day!");
      
      ContextCoordinates co2 = aliceKB.createContextCoordinates(topic, alice, bob, alice, yesterday, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp2 = aliceKB.createContextPoint(co2);
      cp2.addInformation("Yesterday was even better!");
      
      // Create an interest. 
      Interest interest = InMemoSharkKB.createInMemoInterest();

      STSet topics = InMemoSharkKB.createInMemoSTSet();
      topics.merge(topic);
      interest.setTopics(topics);
      
      PeerSTSet remotepeers = InMemoSharkKB.createInMemoPeerSTSet();
      remotepeers.merge(bob);
      interest.setRemotePeers(remotepeers);
      
      interest.setOriginator(alice);
      
      TimeSTSet times = InMemoSharkKB.createInMemoTimeSTSet();
      times.merge(today);
      interest.setTimes(times);
      
      StandardKP kp = aliceSE.createKP(interest, aliceKB);
      
      // Start server
      aliceSE.startTCP(6661);
      
      
      // Now Bob
      J2SEAndroidSharkEngine bobSE = new J2SEAndroidSharkEngine();
      SharkKB bobKB = new InMemoSharkKB();
      
      // Create topic vocab
      SNSemanticTag bobTopic = bobKB.getTopicsAsSemanticNet().createSemanticTag("Thema", "http://topic.de");
      
      // Create Peer vocab
      PeerSNSemanticTag bobBob = bobKB.getPeersAsSemanticNet().createSemanticTag("Bob", "http://bob.de", "tcp://localhost:6662");
      PeerSNSemanticTag bobAlice = bobKB.getPeersAsSemanticNet().createSemanticTag("Alice", "http://alice.de", "tcp://localhost:6661");
      
      // Create Time vocab
      TimeSemanticTag bobToday = bobKB.createTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, TimeSemanticTag.FOREVER);
      
      // No CPs are created, bob only wants to receive information
      
      // Create interest.
      SharkCS bobAs = bobKB.createContextCoordinates(bobTopic, bob, bobAlice, bobAlice, bobToday, null, SharkCS.DIRECTION_IN);
      
      StandardKP bobKP = new StandardKP(bobSE, bobAs, bobKB);
      
      // Start server
      bobSE.startTCP(6662);
      
      
      // Start communication
      
      aliceSE.publishAllKP();
      
      // Wait 5 sec for the exchange to finish
      Thread.sleep(3000);
      
      // Create extraction coordinates for bob, to check if the contextpoint in question has been received.
      ContextCoordinates extractionCoords = bobKB.createContextCoordinates(bobTopic, null, null, null, null, null, SharkCS.DIRECTION_IN);
      
      Enumeration cps = bobKB.getContextPoints(extractionCoords);
      Assert.assertNotNull(cps);
      
      while(cps.hasMoreElements()) {
        ContextPoint resultCp = (ContextPoint) cps.nextElement();
        
        // The coordinate must have 'bobToday' set as time coordinate
        Assert.assertEquals(resultCp.getContextCoordinates().getTime(), bobToday);
        
        Assert.assertEquals(cp.getNumberInformation(), 1);
        
        Enumeration infoEnum = cp.enumInformation();
        Assert.assertNotNull(infoEnum);
        while(infoEnum.hasMoreElements()) {
          Information info = (Information) infoEnum.nextElement();
          byte[] content = info.getContentAsByte();
          String contentString = new String(content, "UTF-8");
          Assert.assertEquals(contentString, "Today is a good day!");
        }
      }
    }
    
    
    /**
     * Test shows if the NO_DIRECTION property does work or not.
     */
//    @Test
    public void noDirectionTest() throws InterruptedException, SharkKBException, SharkSecurityException, IOException {
        int alicePort = 5211, bobPort = 5212;
        
        // setup Alice. She has two ContextPoints: One she dont want to share and one she wants to share
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine(); 
        SharkKB aliceKB = new InMemoSharkKB();
        
        PeerSNSemanticTag aPeer = aliceKB.getPeersAsSemanticNet().createSemanticTag("Alice", "http://alice.de", "tcp://localhost:" + alicePort);
        PeerSNSemanticTag aBob = aliceKB.getPeersAsSemanticNet().createSemanticTag("Bob", "http://bob.de", "tcp://localhost:" + bobPort);
        SNSemanticTag aTopic = aliceKB.getTopicsAsSemanticNet().createSemanticTag("Japan", "http://www.nippon.jp");

        SharkCS aAS = aliceKB.createContextCoordinates(aTopic, aPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
        Interest aInterest = aliceKB.contextualize(aAS);

        ContextCoordinates noDirCO = aliceKB.createContextCoordinates(aTopic, aPeer, null, null, null, null, SharkCS.DIRECTION_NOTHING);
        aliceKB.createContextPoint(noDirCO).addInformation("Anime");

        ContextCoordinates co = aliceKB.createContextCoordinates(aTopic, aPeer, null, null, null, null, SharkCS.DIRECTION_OUT);
        aliceKB.createContextPoint(co).addInformation("Manga");

        StandardKP aKP = new StandardKP(alice, aInterest, aliceKB);
        
        // setup Bob. He want's to receive everything he can
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();        
        SharkKB bobKB = new InMemoSharkKB();  

        PeerSNSemanticTag bAlice = bobKB.getPeersAsSemanticNet().createSemanticTag("Alice", "http://alice.de", "tcp://localhost:" + alicePort);
        PeerSNSemanticTag bPeer = bobKB.getPeersAsSemanticNet().createSemanticTag("Bob", "http://bob.de", "tcp://localhost:" + bobPort);
        SNSemanticTag bTopic = bobKB.getTopicsAsSemanticNet().createSemanticTag("Japan", "http://www.nippon.jp");

        SharkCS bAS = bobKB.createContextCoordinates(bTopic, bPeer, null, null, null, null, SharkCS.DIRECTION_IN);
        Interest bInterest = bobKB.contextualize(bAS);

        StandardKP bKP = new StandardKP(bob, bInterest, bobKB);
        
        bob.startTCP(bobPort);
        alice.startTCP(alicePort);
        
        bob.publishAllKP(bobKB.getPeerSemanticTag(new String[] {"http://alice.de"}));
        Thread.sleep(3000);
        
        Enumeration<ContextPoint> cpEnum = 
                bobKB.getContextPoints(
                    InMemoSharkKB.createInMemoContextCoordinates(
                    null, null, null, null, null, null, SharkCS.DIRECTION_IN)
                );
        
        int count = 0;
        while(cpEnum != null && cpEnum.hasMoreElements()) {
            count++;
            cpEnum.nextElement();
        }        
        
        Assert.assertEquals(1, count);
    }
    
    // TODO: Exchangetest with FP depth > 1
    
//    @Test
    public void learnTest() throws SharkProtocolNotSupportedException, InterruptedException, SharkKBException, SharkSecurityException, IOException {
        
        // init alice
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
        SharkKB aliceKB = new InMemoSharkKB();
        FragmentationParameter fp = new FragmentationParameter(true, true, 5);
        FragmentationParameter[] fps = aliceKB.getStandardFPSet();
        fps[SharkCS.DIM_TOPIC] = fp;
        
        PeerSemanticTag aliceOwnerTag = aliceKB.getPeerSTSet().createPeerSemanticTag("Alice", "http://alice.de", "tcp://localhost:2222");
//        PeerSemanticTag aliceBobTag = aliceKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:2121");
        TXSemanticTag aliceJapanTag = aliceKB.getTopicsAsTaxonomy().createTXSemanticTag("Japan", "http://www.nippon.jp");
        TXSemanticTag aliceTopicTag = aliceKB.getTopicsAsTaxonomy().createTXSemanticTag("Kyoto", "http://www.kyoto.jp");        
        aliceTopicTag.move(aliceJapanTag);
        
//        Interest aliceInterest = aliceKB.createInterest(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, null, null, null, ContextSpace.OUT));
        Interest aliceInterest = aliceKB.createInterest(
                InMemoSharkKB.createInMemoContextCoordinates(aliceTopicTag, 
                aliceOwnerTag, null, null, null, null, SharkCS.DIRECTION_OUT));
        
        StandardKP aliceKP = new StandardKP(alice, aliceInterest, aliceKB);
        
//        ContextPoint cp = aliceKB.createContextPoint(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, aliceOwnerTag, null, null, ContextSpace.OUT));
        ContextPoint cp = aliceKB.createContextPoint(
                InMemoSharkKB.createInMemoContextCoordinates(aliceTopicTag, 
                aliceOwnerTag, null, aliceOwnerTag, null, null, SharkCS.DIRECTION_OUT)
                );
        
        cp.addInformation("Kyoto, das Tokyo für Anagramm-liebhaber");
        cp.addInformation("Noch eine weitere Tolle Info über Japan!!!");
        alice.startTCP(2222);
        
        // init Bob
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
        SharkKB bobKB = new InMemoSharkKB();
        bobKB.setStandardFPSet(fps);
        
        PeerSemanticTag bobOwnerTag = bobKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:2121");
        //SemanticTag bobTopicTag = bobKB.createSemanticTag("Nippon", "http://www.nippon.jp");
        
        Interest bobInterest = bobKB.createInterest(
                InMemoSharkKB.createInMemoContextCoordinates(null, bobOwnerTag, 
                null, null, null, null, SharkCS.DIRECTION_IN)
                );
        StandardKP bobKP = new StandardKP(bob, bobInterest, bobKB);
        
        bob.startTCP(2121);
        
        // start communication
        
        alice.publishKP(aliceKP, bobOwnerTag);
        
        Thread.sleep(2000);
        
        Enumeration<PeerSemanticTag> peerEnum = bobKB.getPeerSTSet().peerTags();
        boolean foundAlice = false;
        while (peerEnum != null && peerEnum.hasMoreElements()) {
            PeerSemanticTag peer = peerEnum.nextElement();
            if(Util.sameEntity(peer.getSI(), aliceOwnerTag.getSI())) {
                foundAlice = true;
            }
        }
        
        Assert.assertTrue(foundAlice);
        
        Enumeration<SemanticTag> topicEnum = bobKB.getTopicSTSet().tags();
        boolean foundNippon = false;
        while (topicEnum != null && topicEnum.hasMoreElements()) {
            SemanticTag topic = topicEnum.nextElement();
            if(Util.sameEntity(topic.getSI(), aliceJapanTag.getSI())) {
                foundNippon = true;
            }
        }
        
        Assert.assertTrue(foundNippon);
        
        ContextPoint cp2 = bobKB.getContextPoint(bobKB.createContextCoordinates(aliceTopicTag, aliceOwnerTag, null, aliceOwnerTag, null, null, SharkCS.DIRECTION_IN));
        
        Assert.assertEquals(2, cp2.getNumberInformation());
    }
    
//    @Test
    public void dublicateInformationSupressionTest() throws SharkProtocolNotSupportedException, InterruptedException, SharkKBException, SharkSecurityException, IOException {
        
        // init alice
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
        SharkKB aliceKB = new InMemoSharkKB();
        FragmentationParameter fp = new FragmentationParameter(true, true, 5);
        FragmentationParameter[] fps = aliceKB.getStandardFPSet();
        fps[SharkCS.DIM_TOPIC] = fp;
        
        
        PeerSemanticTag aliceOwnerTag = aliceKB.getPeerSTSet().createPeerSemanticTag("Alice", "http://alice.de", "tcp://localhost:1212");
//        PeerSemanticTag aliceBobTag = aliceKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:2121");
        TXSemanticTag aliceJapanTag = aliceKB.getTopicsAsTaxonomy().createTXSemanticTag("Japan", "http://www.nippon.jp");
        TXSemanticTag aliceTopicTag = aliceKB.getTopicsAsTaxonomy().createTXSemanticTag("Kyoto", "http://www.kyoto.jp");        
        aliceTopicTag.move(aliceJapanTag);
        
//        Interest aliceInterest = aliceKB.createInterest(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, null, null, null, ContextSpace.OUT));
        Interest aliceInterest = aliceKB.createInterest(
                InMemoSharkKB.createInMemoContextCoordinates(
                aliceTopicTag, aliceOwnerTag, null, null, null, null, 
                SharkCS.DIRECTION_OUT));
        
        StandardKP aliceKP = new StandardKP(alice, aliceInterest, aliceKB);
        
//        ContextPoint cp = aliceKB.createContextPoint(new InMemoContextCoordinates(aliceTopicTag, aliceOwnerTag, aliceBobTag, aliceOwnerTag, null, null, ContextSpace.OUT));
        ContextPoint cp = aliceKB.createContextPoint(
                InMemoSharkKB.createInMemoContextCoordinates(aliceTopicTag, 
                aliceOwnerTag, null, aliceOwnerTag, null, null, 
                SharkCS.DIRECTION_OUT));
        
        cp.addInformation("Kyoto, das Tokyo für Anagramm-liebhaber");
        cp.addInformation("Noch eine weitere Tolle Info über Japan!!!");
        alice.startTCP(1212);
        
        // init Bob
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
        SharkKB bobKB = new InMemoSharkKB();
        bobKB.setStandardFPSet(fps);
        
        PeerSemanticTag bobOwnerTag = bobKB.createPeerSemanticTag("Bob", "http://bob.de", "tcp://localhost:4131");
        //SemanticTag bobTopicTag = bobKB.createSemanticTag("Nippon", "http://www.nippon.jp");
        
        Interest bobInterest = bobKB.createInterest(
                InMemoSharkKB.createInMemoContextCoordinates(null, bobOwnerTag, 
                null, null, null, null, SharkCS.DIRECTION_IN));
        StandardKP bobKP = new StandardKP(bob, bobInterest, bobKB);
        
        bob.startTCP(4131);
        
        // start communication
        
        alice.publishKP(aliceKP, bobOwnerTag);
        
        Thread.sleep(2000);
        
        ContextPoint cp2 = bobKB.getContextPoint(bobKB.createContextCoordinates(aliceTopicTag, aliceOwnerTag, null, aliceOwnerTag, null, null, SharkCS.DIRECTION_IN));
        
        Assert.assertEquals(2, cp2.getNumberInformation());
    }

//    @Test
    public void sendKnowledgeTest() throws InterruptedException, SharkKBException, SharkSecurityException, IOException {
        String aliceAddress = "tcp://localhost:1212";
        int alicePort = 1212;
        String aliceURL = "http://alice.de";
        String bobAddress = "tcp://localhost:1213";
        int bobPort = 1213;
        String bobURL = "http://bob.de";
        
        String testText = "Java toll";
        
        // init alice
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
        SharkKB aliceKB = new InMemoSharkKB();
        
        PeerSemanticTag aliceOwnerTag = aliceKB.createPeerSemanticTag("Alice", aliceURL, aliceAddress);
        SemanticTag aliceJavaTag = aliceKB.createSemanticTag("Japan", "http://www.java.de");
        ContextCoordinates aliceCC = 
                InMemoSharkKB.createInMemoContextCoordinates(aliceJavaTag, 
                aliceOwnerTag, null, aliceOwnerTag, null, null, 
                SharkCS.DIRECTION_OUT);

        ContextPoint aliceCP = aliceKB.createContextPoint(aliceCC);
        aliceCP.addInformation(testText);
        
        Interest aliceInterest = aliceKB.createInterest(aliceCC);
        
        StandardKP aliceKP = new StandardKP(alice, aliceInterest, aliceKB);
        
        alice.startTCP(alicePort);
        
        // init Bob
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
        SharkKB bobKB = new InMemoSharkKB();
        
        PeerSemanticTag bobOwnerTag = bobKB.createPeerSemanticTag("Bob", bobURL, bobAddress);
        Interest bobInterest = bobKB.createInterest(
                InMemoSharkKB.createInMemoContextCoordinates(null, bobOwnerTag, 
                null, null, null, null, SharkCS.DIRECTION_IN));
        StandardKP bobKP = new StandardKP(bob, bobInterest, bobKB);
        
        bob.startTCP(bobPort);
        
        // send knowledge directly from alice
        InMemoKnowledge k = new InMemoKnowledge();
        k.addContextPoint(aliceCP);

//        aliceKP.sendKnowledge(k, new String[] {bobAddress});
        aliceKP.sendKnowledge(k, bobOwnerTag);
        
        
        Thread.sleep(2000);
//        Thread.sleep(Integer.MAX_VALUE);
        
        ContextCoordinates bobCC = InMemoSharkKB.createInMemoContextCoordinates(
                aliceJavaTag, aliceOwnerTag, null, aliceOwnerTag, null, 
                null, SharkCS.DIRECTION_IN);

        ContextPoint cp2 = bobKB.getContextPoint(bobCC);
        
        Enumeration infoEnum = cp2.enumInformation();
        Information info = (Information) infoEnum.nextElement();
        
        byte[] contentBytes = info.getContentAsByte();
        String content = new String(contentBytes);
        
        Assert.assertEquals(testText, content);
    }
}