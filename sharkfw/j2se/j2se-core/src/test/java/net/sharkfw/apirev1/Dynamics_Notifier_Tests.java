package net.sharkfw.apirev1;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import org.junit.*;

/**
 *
 * @author thsc
 */
public class Dynamics_Notifier_Tests {
    
    public Dynamics_Notifier_Tests() {
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
     * Create a default kp, using AnchorSet and FPs.
     *
     * Switch auto-update on for this kp
     *
     * Change the KB in a way that must change the kepInterest as well
     *
     * Check if the changes in the KB have also  been updated inside the kepInterest
     *
     * BUG: The notifications fires right after the new tag has been created,
     * at that time, it does not have an associations, thus the update
     * can't find the new tag when re-creating the kepInterest.
     *
     */
    //FIXME
    // @Test
    public void testDynamicInterest() throws SharkKBException {
      J2SEAndroidSharkEngine aliceSe = new J2SEAndroidSharkEngine();
      SharkKB kb = new InMemoSharkKB();

      SNSemanticTag topic1 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic1", "http://topic1.org");
      SNSemanticTag topic2 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic2", "http://topic2.org");
      SNSemanticTag topic3 = kb.getTopicsAsSemanticNet().createSemanticTag("Topic3", "http://topic3.org");

      // Create association between the two topics
      topic1.setPredicate(SemanticNet.SUBTAG, topic2);

      // Create AnchorSet
      STSet topics = kb.getTopicSTSet();
      
      ContextCoordinates initialInterest = kb.createContextCoordinates(topic1, null, null, null, null, null, SharkCS.DIRECTION_OUT);
      
      // Create 0-FP on all dim except topics
      FragmentationParameter fp = new FragmentationParameter(true, true, 2);
      FragmentationParameter fps[] = KnowledgePort.getZeroFP();
      fps[SharkCS.DIM_TOPIC] = fp;

      StandardKP kp = null;
//      DynamicInterest dynamicInterest = new InMemoDynamicInterest(kb, initialInterest, fps);
//      StandardKP kp = new StandardKP(aliceSe, dynamicInterest, fps, kb);
      kp.keepInterestInSyncWithKB(true);

      // Check if the kepInterest has been created properly
      SharkCS original = kp.getKEPInterest();
      STSet originalTopicDim = original.getTopics();

      SemanticTag originalTopic1 = originalTopicDim.getSemanticTag(topic1.getSI());
      SemanticTag originalTopic2 = originalTopicDim.getSemanticTag(topic2.getSI());
      SemanticTag originalTopic3 = originalTopicDim.getSemanticTag(topic3.getSI());

      Assert.assertNotNull(originalTopic1);
      Assert.assertNotNull(originalTopic2);
      Assert.assertNotNull(originalTopic3);


      // Now update the KB to see if the changes take effect in the kepInterest
      SNSemanticTag topic4 = kb.getTopicsAsSemanticNet().createSemanticTag("topic4", "http://topic4.org");
      topic4.setPredicate(SemanticNet.SUPERTAG, topic2);

      SharkCS updatedInterest = kp.getKEPInterest();
      STSet updatedTopics = updatedInterest.getTopics();

      SemanticTag updatedTopic1 = updatedTopics.getSemanticTag(topic1.getSI());
      SemanticTag updatedTopic2 = updatedTopics.getSemanticTag(topic2.getSI());
      SemanticTag updatedTopic3 = updatedTopics.getSemanticTag(topic3.getSI());
      SemanticTag updatedTopic4 = updatedTopics.getSemanticTag(topic4.getSI());

      Assert.assertNotNull(updatedTopic1);
      Assert.assertNotNull(updatedTopic2);
      Assert.assertNotNull(updatedTopic4);
      Assert.assertNotNull(updatedTopic3);
    }

    @Test
    public void dummyTest(){

    }
}
