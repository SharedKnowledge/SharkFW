package ApiRev1;

import java.lang.reflect.Method;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;
import org.junit.*;

/**
 * Test class for checking the work of the new API's knowledge base.
 * 
 * @author mfi
 */
public class SimpleKBTest {
    
  @BeforeClass
  public static void setUpClass() throws Exception { }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

    @Before
    public void setUp() {
        this.kb = new InMemoSharkKB();
    }

    @After
    public void tearDown() {
    }
    
    protected SharkKB kb = null;

    @Test
    public void testGetSTSet() throws SharkKBException, SharkNotSupportedException, SharkKBException, SharkKBException, SharkKBException {
      SemanticTag topic = kb.getTopicSTSet().createSemanticTag("Test", "http://test.de");
      PeerSemanticTag peer = kb.getPeerSTSet().createPeerSemanticTag("Testpeer", "http://testpeer.de", "tcp://localhost:1234");
      TimeSemanticTag time = kb.getTimeSTSet().createTimeSemanticTag(1000, 2000);
      
      Enumeration<SemanticTag> tags = kb.tags();
      Assert.assertNotNull(tags);

      int counter = 0;
      while(tags.hasMoreElements()) {
        SemanticTag tag = tags.nextElement();
        counter++;
      }

      Assert.assertEquals(counter, 3);
    }

    /**
     * Print all SI to the console, which can be retrieved using getAllSI().

     * @param stset The stset on which to call getAllSI()
     * @param dimension Placeholder for the string to identify the dimension in the output
     */
    private void printAllSiFromDim(STSet stset, String dimension) throws SharkKBException {
      System.out.println("All SI found on " + dimension + " dimension:");
      Enumeration<SemanticTag> siEnum = stset.tags();
      while(siEnum.hasMoreElements()) {
          String[] sis = siEnum.nextElement().getSI();
          for(int i = 0; i < sis.length; i++) {
              System.out.println(sis[i]);
          }
      }
    }

    /**
     * Create a tag on each stset.
     *
     * Try to retrieve the tag using its SI
     *
     * Compare if the same objects are returned
     */
    @Test
    public void testSTManagement() throws SharkKBException {
        kb = new InMemoSharkKB(); // that test is specific for in memo implementation!
      PeerSemanticTag peer = kb.createPeerSemanticTag("Peer", new String[]{"http://peer.de"}, new String[]{"tcp://peer.de:1234"});
      SemanticTag topic = kb.createSemanticTag("Topic", new String[]{"http://Topic.de"});
      TimeSemanticTag time = kb.createTimeSemanticTag(1000, 2000);
//      SpatialSemanticTag geo = kb.createSpatialSemanticTag("test1", new String[]{"http://test.de"}, new Double[]{12.34, 45.67}, 5000);

      PeerSemanticTag peerResult = kb.getPeerSemanticTag(peer.getSI());
      SemanticTag topicResult = kb.getSemanticTag(topic.getSI());
//      TimeSemanticTag timeResult = kb.getTimeSTSet().getTimeSemanticTag(time.getSI());
//      SpatialSemanticTag geoResult = kb.getSpatialSTSet().getSpatialSemanticTag(geo.getSI());

      Assert.assertEquals(peer, peerResult);
      Assert.assertEquals(topic, topicResult);
//      Assert.assertEquals(time, timeResult);
//      Assert.assertEquals(geo, geoResult);

    }


    /**
     * Create a tag.
     *
     * Create a new STSet using the KB
     *
     * Check if the newly created STSet is intially empty
     *
     * Add the created tag to the stset
     *
     * Check if the stset is no longer empty
     */
    @Test
    public void testCreateSTSet() throws SharkKBException {
//      SharkKB kb = new InMemoSharkKB();

      // Create a tag
      SemanticTag topic = kb.createSemanticTag("Topic", new String[]{"http://topic.de"});

      STSet stset = InMemoSharkKB.createInMemoSTSet();
      Enumeration tagEnum = stset.tags();
      Assert.assertTrue(!tagEnum.hasMoreElements()); // No tags must be present

      // Add a tag to the stset
      stset.merge(topic);

      Enumeration tagEnumNew = stset.tags();
      Assert.assertTrue(tagEnumNew.hasMoreElements()); // Must have at least one element
    }

    /**
     * Create a tag.
     *
     * Create a new STSet using the KB
     *
     * Check if the newly created STSet is intially empty
     *
     * Add the created tag to the stset
     *
     * Check if the stset is no longer empty
     */
    @Test
    public void testCreatePeerSTSet() throws SharkKBException {
//      SharkKB kb = new InMemoSharkKB();

      // Create a tag
      PeerSemanticTag peer = kb.createPeerSemanticTag("Peer", new String[]{"http://peer.de"}, new String[]{"tcp://peer.de:1234"});

      PeerSTSet stset = InMemoSharkKB.createInMemoPeerTaxonomy().asPeerSTSet();
      Enumeration tagEnum = stset.tags();
      Assert.assertTrue(!tagEnum.hasMoreElements()); // No tags must be present

      // Add a tag to the stset
      stset.merge(peer);

      Enumeration tagEnumNew = stset.tags();
      Assert.assertTrue(tagEnumNew.hasMoreElements()); // Must have at least one element
    }

    /**
     * Create a tag.
     *
     * Create a new STSet using the KB
     *
     * Check if the newly created STSet is intially empty
     *
     * Add the created tag to the stset
     *
     * Check if the stset is no longer empty
     */
    @Test
    public void testCreateTimeSTSet() throws SharkKBException {
//      SharkKB kb = new InMemoSharkKB();

      // Create a tag
      TimeSemanticTag time = kb.createTimeSemanticTag(1000, 2000);

      TimeSTSet stset = InMemoSharkKB.createInMemoTimeSTSet();
      Enumeration tagEnum = stset.tags();
      Assert.assertTrue(!tagEnum.hasMoreElements()); // No tags must be present

      // Add a tag to the stset
      stset.merge(time);

      Enumeration tagEnumNew = stset.tags();
      Assert.assertTrue(tagEnumNew.hasMoreElements()); // Must have at least one element
    }

    /**
     * <ul>
     * <li>Create two topic tags inside the kb</li>
     * <li>Associated them with each other</li>
     * <li>Use one of the tags as an anchor for fragmentattion</li>
     * <li>Fragment</li>
     * <li>Check the resulting stset for *both* the original anchor and the associated tag</li>
     * <li>Assert, that neither of the returned tags is null.</li>
     * </ul>
     * 
     * @throws SharkKBException
     */
    @Test
    public void testAssociationsInTopics() throws SharkKBException {
//      SharkKB kb = new InMemoSharkKB();

      SNSemanticTag progLang = kb.getTopicsAsSemanticNet().createSemanticTag("Programming languages", new String[]{"http://en.wikipedia.org/wiki/programming_languages"});
      SNSemanticTag java = kb.getTopicsAsSemanticNet().createSemanticTag("Java", "http://en.wikipedia.org/wiki/java");

      java.setPredicate(SemanticNet.SUPERTAG, progLang);

      SemanticNet fragment = SharkCSAlgebra.fragment(java, kb.getTopicsAsSemanticNet(), 1);

      SNSemanticTag javaResult = fragment.getSemanticTag(java.getSI());
      SNSemanticTag langResult = fragment.getSemanticTag(progLang.getSI());

      Assert.assertNotNull(javaResult);
      Assert.assertNotNull(langResult);

    }

    /**
     * Creates tag vocabulary for two contextpoints.
     *
     * Creates two ContextPoints with distinct coordinates.
     *
     * Create two interests (which are ContextSpaces) to include either the one or the other ContextPoint.
     *
     * Uses the <code>getContextPoints(ContextSpace cs);</code> method on SharkKB to find the ContextPoint which is covered by either interest.
     *
     * Checks returnvalue for the correct containing ContextPoint and makes sure that the other ContextPoint is not part of either result.
     *
     * @throws SharkKBException If any Exception occurs while working on the kb it is simply thrown
     */
    @Test
    public void testGetContextPointsViaCS() throws SharkKBException {
//      SharkKB kb = new InMemoSharkKB();

      // Create Vocabulary
      SemanticTag java = kb.getTopicSTSet().createSemanticTag("Java", "http://java.net");
      SemanticTag coffee = kb.getTopicSTSet().createSemanticTag("Coffee", "http://coffee.org");

      PeerSemanticTag peerA = kb.getPeerSTSet().createPeerSemanticTag("PeerA", "http://peerA.de", "tcp://peerA.de:1234");
      PeerSemanticTag peerB = kb.getPeerSTSet().createPeerSemanticTag("PeerB", "http://peerB.de", "tcp://peerB.de:1234");

      TimeSemanticTag ttag1 = kb.getTimeSTSet().createTimeSemanticTag(1, 4);
      TimeSemanticTag ttag2 = kb.getTimeSTSet().createTimeSemanticTag(100, 400);

      // create ContextPoints
      ContextCoordinates co1 = kb.createContextCoordinates(java, null, peerA, null, ttag1, null, SharkCS.DIRECTION_IN);
      ContextPoint cp1 = kb.createContextPoint(co1);
      // Leaving away information, as they are not needed for testing the method

      ContextCoordinates co2 = kb.createContextCoordinates(coffee, null, peerB, null, ttag2, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp2 = kb.createContextPoint(co2);
      // Leaving away information, as they are not needed for testing the method

      // create "ContextSpace" (using an interest as implementation of ContextSpace)
      Interest interest = InMemoSharkKB.createInMemoInterest();

      STSet directions = InMemoSharkKB.createInMemoSTSet();
      STSet topics = InMemoSharkKB.createInMemoSTSet();
      PeerSTSet peers = InMemoSharkKB.createInMemoPeerSTSet();
      TimeSTSet times = InMemoSharkKB.createInMemoTimeSTSet();
      SpatialSTSet geo = InMemoSharkKB.createInMemoSpatialSTSet();

      topics.merge(java);
      peers.merge(peerA);
      times.merge(ttag1);
//      geo.merge(location1);

      interest.setDirection(SharkCS.DIRECTION_INOUT);
      interest.setTopics(topics);
      interest.setPeers(peers);
      interest.setTimes(times);
      interest.setLocations(geo);

      // Find all contextpoints covered by the ContextSpace
      Enumeration result1 = kb.getContextPoints(interest);
      Assert.assertNotNull(result1);
      Assert.assertTrue(result1.hasMoreElements());
      Assert.assertTrue(this.cpInResult(result1, co1)); // this one must be in
      Assert.assertFalse(this.cpInResult(result1, co2)); // this one must not be in (not the correct context)


      // Next test w/ different CS
      // Reconfiguring the ContextSpace
      interest = InMemoSharkKB.createInMemoInterest();

      // Reset the values
      topics = InMemoSharkKB.createInMemoSTSet();
      peers = InMemoSharkKB.createInMemoPeerSTSet();
      times = InMemoSharkKB.createInMemoTimeSTSet();
      geo = InMemoSharkKB.createInMemoSpatialSTSet();
      directions = InMemoSharkKB.createInMemoSTSet();

      topics.merge(coffee);
      peers.merge(peerB);
      times.merge(ttag2);
//      geo.merge(location2);
      

      // same code as above
      interest.setDirection(SharkCS.DIRECTION_INOUT);
      interest.setTopics(topics);
      interest.setPeers(peers);
      interest.setTimes(times);
      interest.setLocations(geo);

      // Find all contextpoints covered by this ContextSpace
      Enumeration result2 = kb.getContextPoints(interest);
      Assert.assertNotNull(result2);
      Assert.assertTrue(result2.hasMoreElements());
      Assert.assertTrue(this.cpInResult(result2, co2)); // this one must be in
      Assert.assertFalse(this.cpInResult(result2, co1)); // this one must not be in (not the correct context)

    }

    /**
     * Check if a ContextPoint is contained in the Enumeration which matches the
     * given coordinates.
     *
     * @param result An Enumeration (probably a result of some sort) to be checked
     * @param co ContextCoordinates to match against
     * @return true if a cp with the given coordinates has been found. false otherwise.
     */
    private boolean cpInResult(Enumeration result, ContextCoordinates co) {
      while(result.hasMoreElements()) {
        ContextPoint cp = (ContextPoint) result.nextElement();
        
        if (SharkCSAlgebra.identical(co, cp.getContextCoordinates())) 
            return true;
        }
      
      return false;
    }

    /**
     * Check if all tags from the stset are represented as Strings correctly
     *
     * @param sis An array of Strings as returned from <code>as.getSi(int dim)</code>
     * @param stset An STSet that has been used in the constructor of the AnchorSet
     * @return true if all tags are represented through all their SIs in the String[], false otherwise.
     */
    private boolean sisEqualSTSet(String[] sis, STSet stset) throws SharkNotSupportedException, SharkKBException {

      Enumeration<SemanticTag> tagEnum = stset.tags();
      boolean completeSTset = true;

      while(tagEnum.hasMoreElements()) {
        boolean completeTag = true;
        SemanticTag tag = (SemanticTag) tagEnum.nextElement();
        String[] tagSis = tag.getSI();

        for(int i = 0; i < tagSis.length; i++) {
          String tagString = tagSis[i];
          boolean completeString = false;

          for(int j = 0; j < sis.length; j++) {
            String siString = sis[j];
            if(siString.equals(tagString)) {
              // Found. Try next String from Tag
              completeString = true;
              break;
            } else {
              // No match, try next String from sis
              continue;
            }
          }
          completeTag = completeTag && completeString;
        }
        completeSTset = completeSTset && completeTag;
      }
      return completeSTset;
    }


    /**
     * Check if all sis from the String[] can be found in the stset
     *
     * @param sis An array of Strings as returned from <code>as.getSi(int dim)</code>
     * @param stset An STSet that has been used in the constructor of the AnchorSet
     * @return true if all SIs are represented through all their Tag in the stset, false otherwise.
     */
    private boolean stSetEqualSis(String[] sis, STSet stset) throws SharkNotSupportedException, SharkKBException {

      Enumeration tagEnum = stset.tags();
      boolean stringComplete = true;

      for(int i = 0; i < sis.length; i++) {
        String currentString = sis[i];
        boolean currentStringComplete = true;

        while(tagEnum.hasMoreElements()) {
          boolean tagComplete = true;
          SemanticTag currentTag = (SemanticTag) tagEnum.nextElement();
          String[] tagSis = currentTag.getSI();

          for(int j = 0; j < tagSis.length; j++) {
            String currentSI = tagSis[j];

            if(currentSI.equals(currentString)) {
              // match. currentString is used in a tag
              tagComplete = true;
              break;
            } else {
              // No luck here. Try next si.
              continue;
            }
          }
          currentStringComplete = currentStringComplete && tagComplete;
        }
        stringComplete = stringComplete && currentStringComplete;
      }
      
      return stringComplete;
    }

    /**
     * Check if the any tag is present on the given stset
     *
     * @param stset The stset to check
     * @return true if the ANY tag is present, false otherwise
     */
    public boolean checkForAnyTagOnStSet(STSet stset) {
      try {
        SemanticTag any = stset.getSemanticTag(SharkCS.ANYURL);
        return true;
      } catch (SharkKBException ex) {
        return false;
      }
    }

    /**
     * Create a new in memo kb
     * Create two tags
     * Get the topic dim as SemanticNet and check for both tags
     * Get the topic dim as STSet and check for both tags
     * Assume the tags are not null, and equal the ones we created before
     */
    @Test
    public void testTypingOfTopicSTSet() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticTag test = kb.getTopicSTSet().createSemanticTag("Test", "http://test.de");
      SemanticTag tag = kb.getTopicSTSet().createSemanticTag("Tag", "http://tag.de");

      // Check SemanticNet result
      SemanticNet topicNet = kb.getTopicsAsSemanticNet();

      SNSemanticTag testResultNet = topicNet.getSemanticTag(test.getSI());
      SNSemanticTag tagResultNet = topicNet.getSemanticTag(tag.getSI());

      testResultNet.setPredicate(SemanticNet.SUPERTAG, tagResultNet);
      
      Assert.assertNotNull(testResultNet);
      Assert.assertNotNull(tagResultNet);

      // check PlainSTSet result
      STSet topicSet = kb.getTopicSTSet();
      SemanticTag testResultSet = topicSet.getSemanticTag(test.getSI());
      SemanticTag tagResultSet = topicSet.getSemanticTag(tag.getSI());

      Assert.assertNotNull(testResultSet);
      Assert.assertNotNull(tagResultSet);
    }

    /**
     * Create a new in memo kb
     * Create two tags
     * Get the topic dim as SemanticNet and check for both tags
     * Get the topic dim as STSet and check for both tags
     * Assume the tags are not null, and equal the ones we created before
     */
    @Test
    public void testTypingOfPeerSTSet() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticTag test = kb.getPeerSTSet().createPeerSemanticTag("Test", "http://test.de", "tcp://test.de:1234");
      SemanticTag tag = kb.getPeerSTSet().createPeerSemanticTag("Tag", "http://tag.de", "tcp://tag.de:1234");

      // Check SemanticNet result
      PeerSemanticNet peerNet = kb.getPeersAsSemanticNet();

      PeerSNSemanticTag testResultNet = peerNet.getSemanticTag(test.getSI());
      PeerSNSemanticTag tagResultNet = peerNet.getSemanticTag(tag.getSI());

      testResultNet.setPredicate(SemanticNet.SUPERTAG, tagResultNet);

      Assert.assertNotNull(testResultNet);
      Assert.assertNotNull(tagResultNet);

      // check PlainSTSet result
      PeerSTSet peerSet = kb.getPeerSTSet();
      
      PeerSemanticTag testResultSet = peerSet.getSemanticTag(test.getSI());
      PeerSemanticTag tagResultSet = peerSet.getSemanticTag(tag.getSI());

      Assert.assertNotNull(testResultSet);
      Assert.assertNotNull(tagResultSet);
    }


    /**
     * Create a new in memo kb
     * Create two tags
     * Get the topic dim as SemanticNet and check for both tags
     * Get the topic dim as STSet and check for both tags
     * Assume the tags are not null, and equal the ones we created before
     */
    @Test
    public void testTypingOfTopicSTSetTaxonomy() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticTag test = kb.createSemanticTag("Test", "http://test.de");
      SemanticTag tag = kb.createSemanticTag("Tag", "http://tag.de");

      // Check Taxonomy result
      Taxonomy topicNet = kb.getTopicsAsTaxonomy();

      TXSemanticTag testResultTax = topicNet.getSemanticTag(test.getSI());
      TXSemanticTag tagResultTax = topicNet.getSemanticTag(tag.getSI());

      testResultTax.move(tagResultTax); // Setting tagResultTax as new super-tag.

      // Check relation
      Enumeration tagSubTags = tagResultTax.subTags();
      Assert.assertNotNull(tagSubTags);
      Assert.assertTrue(tagSubTags.hasMoreElements());
      TXSemanticTag subtag = (TXSemanticTag) tagSubTags.nextElement(); // Must be one
//      Assert.assertEquals(subtag, testResultTax); // Must be the same tags in InMemoimplementation


      Assert.assertNotNull(testResultTax);
      Assert.assertNotNull(tagResultTax);

//      Assert.assertEquals(testResultTax, test);
//      Assert.assertEquals(tagResultTax, tag);

      // check PlainSTSet result
      STSet topicSet = kb.getTopicSTSet();
      SemanticTag testResultSet = topicSet.getSemanticTag(test.getSI());
      SemanticTag tagResultSet = topicSet.getSemanticTag(tag.getSI());

      Assert.assertNotNull(testResultSet);
      Assert.assertNotNull(tagResultSet);

//      Assert.assertEquals(testResultSet, test);
//      Assert.assertEquals(tagResultSet, tag);
    }


    /**
     * Create a new in memo kb
     * Create two tags
     * Get the topic dim as SemanticNet and check for both tags
     * Get the topic dim as STSet and check for both tags
     * Assume the tags are not null, and equal the ones we created before
     */
    @Test
    public void testTypingOfPeerSTSetTaxonomy() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      PeerSemanticTag test = kb.createPeerSemanticTag("Test", "http://test.de", "tcp://test.de:1234");
      PeerSemanticTag tag = kb.createPeerSemanticTag("Tag", "http://tag.de", "tcp://tag.de:1234");

      // Check SemanticNet result
      PeerTaxonomy peerTax = kb.getPeersAsTaxonomy();

      PeerTXSemanticTag testResultTax = peerTax.getSemanticTag(test.getSI());
      PeerTXSemanticTag tagResultTax = peerTax.getSemanticTag(tag.getSI());

      testResultTax.move(tagResultTax); // Setting tagResultTax as new super-tag.

      // Check relation
      Enumeration tagSubTags = tagResultTax.subTags();
      Assert.assertNotNull(tagSubTags);
      Assert.assertTrue(tagSubTags.hasMoreElements());
      PeerTXSemanticTag subtag = (PeerTXSemanticTag) tagSubTags.nextElement(); // Must be one
//      Assert.assertEquals(subtag, testResultTax); // Must be the same tags in in moemo implementation


      Assert.assertNotNull(testResultTax);
      Assert.assertNotNull(tagResultTax);

//      Assert.assertEquals(testResultTax, test);
//      Assert.assertEquals(tagResultTax, tag);


      // check PlainSTSet result
      PeerSTSet peerSet = kb.getPeerSTSet();
      PeerSemanticTag testResultSet = peerSet.getSemanticTag(test.getSI());
      PeerSemanticTag tagResultSet = peerSet.getSemanticTag(tag.getSI());

      Assert.assertNotNull(testResultSet);
      Assert.assertNotNull(tagResultSet);

//      Assert.assertEquals(testResultSet, test);
//      Assert.assertEquals(tagResultSet, tag);

      this.testClassMethods(PeerTaxonomy.class);

    }

    public void testClassMethods(Class toTest) {
      Method[] methods = toTest.getMethods();

      System.out.println("\nClass:" + toTest.getName() + " has methods:");
      for(int i = 0; i < methods.length; i++) {
        String name = methods[i].getName();

        System.out.println(name);
      }
    }

    /**
     * Get different STSets and check the proper typing of each
     */
    @Test
    public void testSTSetTyping() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();

//      SharkKB kb = new InMemoSharkKB();

      STSet topics = kb.getTopicSTSet();
      Assert.assertNotNull(topics);
      Assert.assertTrue(topics instanceof STSet);

      Taxonomy topicsTax = kb.getTopicsAsTaxonomy();
      Assert.assertNotNull(topicsTax);
      Assert.assertTrue(topicsTax instanceof Taxonomy);

      SemanticNet topicsSn = kb.getTopicsAsSemanticNet();
      Assert.assertNotNull(topicsSn);
      Assert.assertTrue(topicsSn instanceof SemanticNet);

      PeerSTSet peers = kb.getPeerSTSet();
      Assert.assertNotNull(peers);
      Assert.assertTrue(peers instanceof PeerSTSet);

      PeerSemanticNet peerSn = kb.getPeersAsSemanticNet();
      Assert.assertNotNull(peerSn);
      Assert.assertTrue(peerSn instanceof PeerSemanticNet);

      PeerTaxonomy peerTax = kb.getPeersAsTaxonomy();
      Assert.assertNotNull(peerTax);
      Assert.assertTrue(peerTax instanceof PeerTaxonomy);


    }

    /**
     * Set one property on a semantic tag to transferable,
     * set another property to be not-transferable.
     *
     * Try to get both values using the keys originally provided.
     *
     * This method tests, if the user is still able to get a value from
     * properties using the original key he/she provided. Marking a property
     * to be not-transferable involves annotating the key. This must however
     * never show to the user.
     */
    @Test
    public void testSerializableProperties() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      String value1 = "value1";
      String value2 = "value2";

      SemanticTag test = kb.createSemanticTag("Test", "http://test.de");
      test.setProperty("key1", value1, false);
      test.setProperty("key2", value2, true);

      String v1 = test.getProperty("key1");
      String v2 = test.getProperty("key2");
      
      Assert.assertEquals(v1, value1);
      Assert.assertEquals(v2, value2);
    }

    /**
     * Create 3 tags in the topics stset and build associations between them.
     * Next run a fragment on this stset and check if the tags covered
     * by depth = 1 are complete, and if only the tags covered by depth = 1
     * have been retrieved.
     */
    @Test
    public void semanticNetFragmentTestDepth1() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticNet topics = kb.getTopicsAsSemanticNet();
      SNSemanticTag top = kb.getTopicsAsSemanticNet().createSemanticTag("Top", "http://top.de");
      SNSemanticTag bottom = kb.getTopicsAsSemanticNet().createSemanticTag("Bottom", "http://bottom.de");
      SNSemanticTag supertop = kb.getTopicsAsSemanticNet().createSemanticTag("Supertop", "http://supertop.de");

      bottom.setPredicate(SemanticNet.SUPERTAG, top);
      top.setPredicate(SemanticNet.SUPERTAG, supertop);

      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      SemanticNet fragment = topics.fragment(bottom, fp);//ContextSpaceUtil.fragment(topics, bottom, fp);

      SNSemanticTag topResult = fragment.getSemanticTag(top.getSI());
      SNSemanticTag bottomResult = fragment.getSemanticTag(bottom.getSI());
      SNSemanticTag supertopResult = fragment.getSemanticTag(supertop.getSI());

      // Not covered by depth = 1
      Assert.assertNull(supertopResult);

      // Covered by depth = 1
      Assert.assertNotNull(topResult);
      
      // Anchorpoint, hence automatically covered
      Assert.assertNotNull(bottomResult);

      // Check for one associated tag, following super association from bottom
      Enumeration bottomsSuper = bottomResult.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(bottomsSuper);
      Assert.assertTrue(bottomsSuper.hasMoreElements());

      // Later:
      Enumeration topsSub = topResult.sourceTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(topsSub);
      Assert.assertTrue(topsSub.hasMoreElements());

    }


    /**
     * Create 3 tags in the topics stset and build associations between them.
     * Next run a fragment on this stset and check if the tags covered
     * by depth = 2 are complete, and if only the tags covered by depth = 2
     * have been retrieved.
     */
    @Test
    public void semanticNetFragmentTestDepth2() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticNet topics = kb.getTopicsAsSemanticNet();
      SNSemanticTag top = kb.getTopicsAsSemanticNet().createSemanticTag("Top", "http://top.de");
      SNSemanticTag bottom = kb.getTopicsAsSemanticNet().createSemanticTag("Bottom", "http://bottom.de");
      SNSemanticTag supertop = kb.getTopicsAsSemanticNet().createSemanticTag("Supertop", "http://supertop.de");

      bottom.setPredicate(SemanticNet.SUPERTAG, top);
      top.setPredicate(SemanticNet.SUPERTAG, supertop);

      FragmentationParameter fp = new FragmentationParameter(true, true, 2);
      SemanticNet fragment = topics.fragment(bottom, fp);//ContextSpaceUtil.fragment(topics, bottom, fp);

      SNSemanticTag topResult = fragment.getSemanticTag(top.getSI());
      SNSemanticTag bottomResult = fragment.getSemanticTag(bottom.getSI());
      SNSemanticTag supertopResult = fragment.getSemanticTag(supertop.getSI());

      // Depth = 2 covers supertop tag
      Assert.assertNotNull(supertopResult);
      Assert.assertTrue(Util.sameEntity(supertopResult.getSI(), supertop.getSI()));

      // covers also top tag
      Assert.assertNotNull(topResult);
      Assert.assertTrue(Util.sameEntity(topResult.getSI(), top.getSI()));

      // and of course bottom (being the anchor)
      Assert.assertNotNull(bottomResult);
      Assert.assertTrue(Util.sameEntity(bottomResult.getSI(), bottom.getSI()));

      // Check bottoms association to top
      Enumeration bottomsSuper = bottomResult.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(bottomsSuper);
      Assert.assertTrue(bottomsSuper.hasMoreElements());

      // Check tops association to supertop
      Enumeration topsSuper = topResult.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(topsSuper);
      Assert.assertTrue(topsSuper.hasMoreElements());

      // Later:
      Enumeration topsSub = topResult.sourceTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(topsSub);
      Assert.assertTrue(topsSub.hasMoreElements());

    }

    /**
     * Create 3 tags in the topics stset and build associations between them.
     * Next run a fragment on this stset and check if the tags covered
     * by depth = 0 are complete, and if only the tags covered by depth = 0
     * have been retrieved.
     */
    @Test
    public void semanticNetFragmentTestDepth0() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticNet topics = kb.getTopicsAsSemanticNet();
      SNSemanticTag top = kb.getTopicsAsSemanticNet().createSemanticTag("Top", "http://top.de");
      SNSemanticTag bottom = kb.getTopicsAsSemanticNet().createSemanticTag("Bottom", "http://bottom.de");
      SNSemanticTag supertop = kb.getTopicsAsSemanticNet().createSemanticTag("Supertop", "http://supertop.de");

      bottom.setPredicate(SemanticNet.SUPERTAG, top);
      top.setPredicate(SemanticNet.SUPERTAG, supertop);

      FragmentationParameter fp = new FragmentationParameter(true, true, 0);
      SemanticNet fragment = topics.fragment(bottom, fp);// ContextSpaceUtil.fragment(topics, bottom, fp);

      SNSemanticTag topResult = fragment.getSemanticTag(top.getSI());
      SNSemanticTag bottomResult = fragment.getSemanticTag(bottom.getSI());
      SNSemanticTag supertopResult = fragment.getSemanticTag(supertop.getSI());

      // Not covered by depth = 0
      Assert.assertNull(supertopResult);

      // Not covered by depth = 0
      Assert.assertNull(topResult);
      
      // Anchorpoint, hence covered by depth 0
      Assert.assertNotNull(bottomResult);

      // Must have no associations
      Enumeration bottomsSuper = bottomResult.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNull(bottomsSuper);
    }


    /**
     * Create 3 tags in the topics stset (Taxonomy) and build associations between them.
     * Next run a fragment on this stset and check if the tags covered
     * by depth = 1 are complete, and if only the tags covered by depth = 1
     * have been retrieved.
     */
    @Test
    public void semanticTaxFragmentTestDepth1() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      Taxonomy topics = kb.getTopicsAsTaxonomy();
      TXSemanticTag top = kb.getTopicsAsTaxonomy().createTXSemanticTag("Top", "http://top.de");
      TXSemanticTag bottom = kb.getTopicsAsTaxonomy().createTXSemanticTag("Bottom", "http://bottom.de");
      TXSemanticTag supertop = kb.getTopicsAsTaxonomy().createTXSemanticTag("Supertop", "http://supertop.de");

      // Move bottom below top
      bottom.move(top);

      // Move top below supertop
      top.move(supertop);


      FragmentationParameter fp = new FragmentationParameter(true, true, 1);
      
      Taxonomy fragment = topics.fragmentTaxonomy(bottom, fp);//ContextSpaceUtil.fragment(topics, bottom, fp);

      TXSemanticTag topResult = fragment.getSemanticTag(top.getSI());
      TXSemanticTag bottomResult = fragment.getSemanticTag(bottom.getSI());
      TXSemanticTag supertopResult = fragment.getSemanticTag(supertop.getSI());

      // Not covered by depth = 1
      Assert.assertNull(supertopResult);

      // Covered by depth = 1
      Assert.assertNotNull(topResult);

      // Anchorpoint, hence automatically covered
      Assert.assertNotNull(bottomResult);

      // Check for one associated tag, following super association from bottom
      Enumeration topsSub = topResult.subTags();
      Assert.assertNotNull(topsSub);
      Assert.assertTrue(topsSub.hasMoreElements());

    }


    /**
     * The simplest kind of fragmentation is tested.
     *
     * Create two tags.
     *
     * Run fragment on topics using one of the tags as anchor.
     *
     * Result must contain anchor, but not the second tag.
     */
    @Test
    public void testSTSetFragment() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();

//      SharkKB kb = new InMemoSharkKB();

      SemanticTag test1 = kb.createSemanticTag("Test1", "http://test1.de");
      SemanticTag test2 = kb.createSemanticTag("Test2", "http://test2.de");

      STSet topics = kb.getTopicSTSet();
      STSet fragment = topics.fragment(test1);

      SemanticTag resultTest1 = fragment.getSemanticTag(test1.getSI());
      Assert.assertNotNull(resultTest1);

      SemanticTag resultTest2 = fragment.getSemanticTag(test2.getSI());
      Assert.assertNull(resultTest2);
    }

    /**
     * Create three PeerAssociatedSemanticTags in the kb
     * 
     * create an assoc between two of them
     * 
     * Fragment with one of the associated tags as anchor, sub/super allowed and depth = 1
     * 
     * result must contain both tags
     * 
     * result must not contain the third tag
     */
    @Test
    public void testPeerSemanticNetFragmentDepth1() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      PeerSNSemanticTag alice = kb.getPeersAsSemanticNet().createSemanticTag("Alice", "http://alice.org", "tcp://alice.org:1234");
      PeerSNSemanticTag aliceCorp = kb.getPeersAsSemanticNet().createSemanticTag("Alice Corp.", "http://alicecorp.org", "tcp://alicecorp.org:1234");
      PeerSemanticTag bob = kb.createPeerSemanticTag("Bob", "http://bob.org", "tcp://bob.org:1234");
      
      alice.setPredicate(SemanticNet.SUPERTAG, aliceCorp);

      PeerSemanticNet psn = kb.getPeersAsSemanticNet();
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);

      PeerSemanticNet fragment = psn.fragment(alice, fp);

      PeerSNSemanticTag resultAlice = fragment.getSemanticTag(alice.getSI());
      Assert.assertNotNull(resultAlice);

      PeerSNSemanticTag resultAliceCorp = fragment.getSemanticTag(aliceCorp.getSI());
      Assert.assertNotNull(resultAliceCorp);

      PeerSNSemanticTag resultBob = fragment.getSemanticTag(bob.getSI());
      Assert.assertNull(resultBob);
    }
    
    
    /**
     * Create three PeerHierarchicalSemanticTags in the kb
     * 
     * create an assoc between two of them
     * 
     * Fragment with one of the associated tags as anchor, sub/super allowed and depth = 1
     * 
     * result must contain both tags
     * 
     * result must not contain the third tag
     */
    @Test
    public void testPeerTaxFragmentDepth1() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();


      PeerTXSemanticTag alice = kb.getPeersAsTaxonomy().createPeerTXSemanticTag("Alice", "http://alice.org", "tcp://alice.org:1234");
      PeerTXSemanticTag aliceCorp = kb.getPeersAsTaxonomy().createPeerTXSemanticTag("Alice Corp.", "http://alicecorp.org", "tcp://alicecorp.org:1234");
      PeerTXSemanticTag bob = kb.getPeersAsTaxonomy().createPeerTXSemanticTag("Bob", "http://bob.org", "tcp://bob.org:1234");
      
      alice.move(aliceCorp);
      
      PeerTaxonomy ptx = kb.getPeersAsTaxonomy();
      
      FragmentationParameter fp = new FragmentationParameter(true, true, 1);

      PeerTaxonomy fragment = (PeerTaxonomy) ptx.fragment(alice, fp);

      PeerTXSemanticTag resultAlice = fragment.getSemanticTag(alice.getSI());
      Assert.assertNotNull(resultAlice);

      PeerTXSemanticTag resultAliceCorp = fragment.getSemanticTag(aliceCorp.getSI());
      Assert.assertNotNull(resultAliceCorp);

      PeerTXSemanticTag resultBob = fragment.getSemanticTag(bob.getSI());
      Assert.assertNull(resultBob);
    }


     /**
     * The simplest kind of fragmentation is tested.
     *
     * Create two peer-tags.
     *
     * Run fragment on peers using one of the tags as anchor.
     *
     * Result must contain anchor, but not the second tag.
     */
    @Test
    public void testPeerSTSetFragment() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();

//      SharkKB kb = new InMemoSharkKB();

      PeerSemanticTag test1 = kb.createPeerSemanticTag("Test1", "http://test1.de", "tcp://test1.de:1234");
      PeerSemanticTag test2 = kb.createPeerSemanticTag("Test2", "http://test2.de", "tcp://test2.de:1234");

      PeerSTSet peers = kb.getPeerSTSet();
      PeerSTSet fragment = peers.fragment(test1);

      PeerSemanticTag resultTest1 = fragment.getSemanticTag(test1.getSI());
      Assert.assertNotNull(resultTest1);

      PeerSemanticTag resultTest2 = fragment.getSemanticTag(test2.getSI());
      Assert.assertNull(resultTest2);
    }


    /**
     * Create two tags in the kb and associated them with each other.
     *
     * Next create an emtpy STSet and merge the existing topics into it.
     *
     * Check the result.
     */
    @Test
    public void testMergeTopicsNet() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SNSemanticTag topic1 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic1", "http://topic1.de");
      SNSemanticTag topic2 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic2", "http://topic2.de");

      topic1.setHidden(true);

      topic2.setPredicate(SemanticNet.SUPERTAG, topic1);

      SemanticNet external = InMemoSharkKB.createInMemoSemanticNet();
      external.merge(kb.getTopicsAsSemanticNet());

      SNSemanticTag resultTopic1 = external.getSemanticTag(topic1.getSI());
      Assert.assertNotNull(resultTopic1);
      Assert.assertTrue(resultTopic1.hidden());

      SNSemanticTag resultTopic2 = external.getSemanticTag(topic2.getSI());
      Assert.assertNotNull(resultTopic2);

      Enumeration assocEnum = resultTopic2.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(assocEnum);
      Assert.assertTrue(assocEnum.hasMoreElements());

      while(assocEnum.hasMoreElements()) {
        SNSemanticTag resultTopicAssociated = (SNSemanticTag) assocEnum.nextElement();
        Assert.assertEquals(resultTopicAssociated, resultTopic1);
      }
    }


    /**
     * Create two tags in the kb and associated them with each other.
     *
     * Next create an emtpy STSet and merge the existing topics into it.
     *
     * Check the result.
     */
    @Test
    public void testMergePeersNet() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      PeerSNSemanticTag peer1 = kb.getPeersAsSemanticNet().createSemanticTag("Topic1", "http://topic1.de", "tcp://topic1.de:1234");
      PeerSNSemanticTag peer2 = kb.getPeersAsSemanticNet().createSemanticTag("Topic2", "http://topic2.de", "tcp://topci2.de:1234");

      peer1.setHidden(true);

      peer2.setPredicate(SemanticNet.SUPERTAG, peer1);

      PeerSemanticNet external = InMemoSharkKB.createInMemoPeerSemanticNet();
      external.merge(kb.getPeersAsSemanticNet());

      PeerSNSemanticTag resultTopic1 = external.getSemanticTag(peer1.getSI());
      Assert.assertNotNull(resultTopic1);
      Assert.assertTrue(resultTopic1.hidden());

      PeerSNSemanticTag resultTopic2 = external.getSemanticTag(peer2.getSI());
      Assert.assertNotNull(resultTopic2);

      Enumeration assocEnum = resultTopic2.targetTags(SemanticNet.SUPERTAG);
      Assert.assertNotNull(assocEnum);
      Assert.assertTrue(assocEnum.hasMoreElements());

      while(assocEnum.hasMoreElements()) {
        PeerSNSemanticTag resultTopicAssociated = (PeerSNSemanticTag) assocEnum.nextElement();
        Assert.assertEquals(resultTopicAssociated, resultTopic1);
      }
    }

    /**
     * Test the contextualization of PlainSTSets on KnowledgePort.
     * Create two STSets. One of them holding three tags. The other holding only
     * a subset of those tags (1 or 2 or 0).
     *
     * After contextualization only the tags, that were present in both STSets
     * must be part of the result. All other tags from the source must not be
     * in the result.
     */
    @Test
    public void testSemanticNetContextualizationWith1Tag() throws SharkException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticNet source = InMemoSharkKB.createInMemoSemanticNet();
      SemanticNet context = InMemoSharkKB.createInMemoSemanticNet();

      SemanticTag tag1 = source.createSemanticTag("tag1", new String[]{"http://test.de"});
      SemanticTag tag2 = source.createSemanticTag("tag2", new String[]{"http://test2.de"});
      SemanticTag tag3 = source.createSemanticTag("tag3", new String[]{"http://test3.de"});

      context.merge(tag1);

      FragmentationParameter fp = new FragmentationParameter(false, false, 0);
      STSet result = SharkCSAlgebra.contextualize(source, context, fp);
      
      SemanticTag tagResult1 = result.getSemanticTag(tag1.getSI());
      SemanticTag tagResult2 = result.getSemanticTag(tag2.getSI());
      SemanticTag tagResult3 = result.getSemanticTag(tag3.getSI());

      // These two tags can be found in both STSets and thus must be present
      Assert.assertNotNull(tagResult1);


      // This one is not in context and this must not be in the result
      Assert.assertNull(tagResult3);
      Assert.assertNull(tagResult2);
    }

     /**
     * Create KB and some vocabulary.
     *
     * Create three CPs.
     *
     * Create an interest covering one of the CPs.
     *
     * Use the interest to retrieve that CP.
     *
     * Check if a result has been returned.
     *
     * Check if the correct CP has been returned.
     *
     * Check that no more than the correct CP has been returned.
     *
     * @throws SharkKBException
     */
    @Test
    public void testContextPointsViaContextSpace() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Topic1", "http://topic1.de");
      SemanticTag topic2 = kb.getTopicSTSet().createSemanticTag("Topic2", "http://topic2.de");
      SemanticTag topic3 = kb.getTopicSTSet().createSemanticTag("Topic3", "http://topic3.de");

      PeerSemanticTag peer1 = kb.getPeerSTSet().createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
      PeerSemanticTag peer2 = kb.getPeerSTSet().createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");

      ContextCoordinates co1 = InMemoSharkKB.createInMemoContextCoordinates(topic1, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp1 = kb.createContextPoint(co1);
      cp1.addInformation("Information1");

      ContextCoordinates co2 = InMemoSharkKB.createInMemoContextCoordinates(topic2, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp2 = kb.createContextPoint(co2);
      cp2.addInformation("Information2");

      ContextCoordinates co3 = InMemoSharkKB.createInMemoContextCoordinates(topic3, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp3 = kb.createContextPoint(co3);
      cp3.addInformation("Information3");

      Interest interest = InMemoSharkKB.createInMemoInterest();

      STSet interestTopic = InMemoSharkKB.createInMemoSTSet();
      interestTopic.merge(topic1);

      PeerSTSet interestPeer = InMemoSharkKB.createInMemoPeerSTSet();
      interestPeer.merge(peer1);

      PeerSTSet interestRemotepeer = InMemoSharkKB.createInMemoPeerSTSet();
      interestRemotepeer.merge(peer2);


      // Set the dimensions of the interest
      interest.setTopics(interestTopic);
      interest.setPeers(interestPeer);
      interest.setRemotePeers(interestRemotepeer);
      interest.setDirection(SharkCS.DIRECTION_OUT);

      // Retrieve ContextPoints covered by the interest
      Enumeration cps = kb.getContextPoints(interest);
      Assert.assertNotNull(cps);
      Assert.assertTrue(cps.hasMoreElements());
      ContextPoint cp = (ContextPoint) cps.nextElement();
      cp.getNumberInformation();
      Assert.assertFalse(cps.hasMoreElements()); // Only one CP must be found.

      // Check if the retrieved ContextPoint is the right one
      Enumeration infoEnum = cp.enumInformation();
      Assert.assertNotNull(infoEnum);
      Assert.assertTrue(infoEnum.hasMoreElements());
      Information info = (Information) infoEnum.nextElement();
      byte[] content = info.getContentAsByte();
      String text = new String(content);
      Assert.assertEquals("Information1", text);
    }


     /**
     * Locations and timespand are considered to be topics as well.
     *
     * Technically GeoSemanticTags and TimeSemanticTags however to do not subclass
     * AssociatedSemanticTag, and hence can't be treated as AssociatedSemanticTags.
     *
     * SemanticNets and Taxonomies however expect that all the tags they manage
     * are a form of AssociatedSemanticTags. So when trying to get the topics
     * as SemanticNet or Taxonomy and accessing a Time or GeoSemanticTag,
     * a ClassCastException is thrown.
      *
      * This has been fixed by introducing the TimeAssociatedSemanticTag interface
      * and extending InMemoTimeSemanticTag from InMemoAssociatedSemanticTag
      * (instead of extending InMemoSemanticTag).
     */
    @Test
    public void testTimeTagsInTopicsSemanticNet() throws SharkKBException {
       J2SEAndroidSharkEngine aliceSe = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SNSemanticTag topic1 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic1", "http://topic1.org");
      SNSemanticTag topic2 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic2", "http://topic2.org");
      SNSemanticTag topic3 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic3", "http://topic3.org");

      // Create association between the two topics
      topic1.setPredicate(SemanticNet.SUBTAG, topic2);

      // Create AnchorSet
      STSet topics = kb.getTopicSTSet();

      TimeSemanticTag time1 = kb.createTimeSemanticTag(5000, 6000);
      TimeSemanticTag time2 = kb.createTimeSemanticTag(9000, 12000);

      SemanticNet topicsNet = kb.getTopicsAsSemanticNet();
      SNSemanticTag time1Assoc = topicsNet.getSemanticTag(time1.getSI());

    }

    /**
     * Create a semantic tag.
     *
     * Check that it has been created.
     *
     * Remove the semantic tag.
     *
     * Try to get the SemanticTag from the kb -> Must return null.
     */
    @Test
    public void testRemoveTag() throws SharkKBException {
      J2SEAndroidSharkEngine aliceSe = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

      SemanticTag test = kb.getTopicSTSet().createSemanticTag("Test", "http://test.de");
      Assert.assertNotNull(test);

      Assert.assertNotNull(kb.getSemanticTag(test.getSI()));
      
      kb.getTopicSTSet().removeSemanticTag(test);

      Assert.assertNull(kb.getSemanticTag(test.getSI()));
    }


    /**
     * Create three contextpoints, of which two share the same topic.
     *
     * Create ContextCoordinates with everything left unset save for the topic.
     *
     * Use getAllCPs() to get the two contextpoints.
     *
     * Make sure the third one is not part of the result.
     */
    @Test
    public void testGetAllCps() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();

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

      // exact match
      Enumeration<ContextPoint> resultEnum = kb.getContextPoints(extractCo, false);
      
      Assert.assertTrue(resultEnum == null || !resultEnum.hasMoreElements());
      
      // sloppy match: allow any matching
      resultEnum = kb.getContextPoints(extractCo, true);
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

     /**
     * Create three tags.
     *
     * Associated them in a hierarchical order.
     *
     * Remove the middle tag.
     *
     * Check that the hierarchic structure is left intact.
     *
     * Check that the tag is really gone.
     */
    @Test
    public void removeTagFromTaxomomyTest() throws SharkKBException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();
      
      Taxonomy topicTX = kb.getTopicsAsTaxonomy();

      TXSemanticTag hTop = topicTX.createTXSemanticTag("Top", "http://top.de");
      TXSemanticTag hMiddle = topicTX.createTXSemanticTag("middle", "http://middle.de");
      TXSemanticTag hBottom = topicTX.createTXSemanticTag("bottom", "http://bottom.de");

      hMiddle.move(hTop);
      hBottom.move(hMiddle);
      
      String[] middleSIS = hMiddle.getSI();
      topicTX.removeSemanticTag(hMiddle);

      TXSemanticTag hMiddleResult = topicTX.getSemanticTag(middleSIS);
      Assert.assertNull(hMiddleResult);

      Enumeration<SemanticTag> subtags = hTop.subTags();
      // Subtag should now be hBottom
      Assert.assertTrue(subtags.hasMoreElements());
      SemanticTag sub = subtags.nextElement();
      Assert.assertTrue(SharkCSAlgebra.identical(sub, hBottom));
    }
    
    /**
     * Removing a topic which has already been removed throws a NPE.
     * This must not happen.
     */
    @Test
    public void testRemoveTopicFromTax() throws SharkKBException {
      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//      SharkKB kb = new InMemoSharkKB();
      
      TXSemanticTag top = kb.getTopicsAsTaxonomy().createTXSemanticTag("Top", "http://top.de");
      TXSemanticTag middle = kb.getTopicsAsTaxonomy().createTXSemanticTag("Middle", "http://middle.de");
      TXSemanticTag bottom = kb.getTopicsAsTaxonomy().createTXSemanticTag("Bottom", "http://bottom.de");
      
      Taxonomy topicTax = kb.getTopicsAsTaxonomy();
      topicTax.removeSemanticTag(middle);
      kb.removeSemanticTag(middle.getSI());
    }
    
    /**
     * Add a new SI to an existing tag after its creation.
     * 
     * Upon adding the new si, the 'container' of that tag (i.e. the SharkKB or
     * an STSet) are being notified of the new SI. They then update their internal
     * tables to map the new si to the existing topic, so that it can be retrieved
     * by the new si.
     */
    @Test
    public void addSITest() throws SharkKBException {
//        SharkKB kb = new InMemoSharkKB();
        SemanticTag tag = kb.getTopicSTSet().createSemanticTag("test1", "http://si-1.de");
        tag.addSI("http://si-2.de");
        
        SemanticTag sameTag = kb.getSemanticTag(new String[] {"http://si-2.de"});
        Assert.assertTrue(SharkCSAlgebra.identical(tag, sameTag));
    }
    
    @Test
    public void mergePureSTIntoTaxonomyOrSemanticNet() throws SharkKBException {
        Taxonomy tx = InMemoSharkKB.createInMemoTaxonomy();
        
        // create unnamed tag of type ST
        SemanticTag st = InMemoSharkKB.createInMemoSemanticTag("A tag", "http://www.something.de");
        
        // merge into taxonomy
        tx.merge(st);
        
        SemanticNet sn = InMemoSharkKB.createInMemoSemanticNet();
        
        // merge into semantic net
        sn.merge(st);
    }
}