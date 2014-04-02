package ApiRev1;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkException;
import net.sharkfw.system.Util;
import org.junit.*;

/**
 *
 * @author mfi
 */
public class NYI_SharkKB {

    public NYI_SharkKB() {
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
     * Test the contextualization of PlainSTSets on ContextSpaceUtil. Create two STSets. One of them holding three tags. The other holding only a subset of those tags (1 or 2 or 0).
     *
     * After contextualization only the tags, that were present in both STSets must be part of the result. All other tags from the source must not be in the result.
     *
     * In this test the received semantic net does not contains any tags, hence the implementation reads it as "ANY" and returns the constraining part. Is this a bug or a feature?!
     */
    @Test
    public void testSemanticNetContextualizationWithEmptyContext() throws SharkException {

        J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();

        SemanticNet source = InMemoSharkKB.createInMemoSemanticNet();
        SemanticNet context = InMemoSharkKB.createInMemoSemanticNet();

        SemanticTag tag1 = source.createSemanticTag("tag1", new String[]{"http://test.de"});
        SemanticTag tag2 = source.createSemanticTag("tag2", new String[]{"http://test2.de"});
        SemanticTag tag3 = source.createSemanticTag("tag3", new String[]{"http://test3.de"});

        FragmentationParameter fp = new FragmentationParameter(false, false, 0);
        STSet result = SharkCSAlgebra.contextualize(context, source, fp);
        SemanticTag tagResult1 = result.getSemanticTag(tag1.getSI());
        SemanticTag tagResult2 = result.getSemanticTag(tag2.getSI());
        SemanticTag tagResult3 = result.getSemanticTag(tag3.getSI());

        // This one is not in context and this must not be in the result
        Assert.assertNull(tagResult3);
        Assert.assertNull(tagResult2);
        Assert.assertNull(tagResult1);
    }

    /**
     * Create KB and some vocabulary.
     *
     * Create three CPs.
     *
     * Create an interest covering all three of the CPs using "ANY" on the topic dim.
     *
     * Use the interest to retrieve the CPs.
     *
     * Check if a result has been returned.
     *
     * Check if the correct CPs has been returned.
     *
     * Check that no more than the correct CPs has been returned.
     *
     * This method interprets unset values by the letter and thus only returns contextpoints which have an unset value on the according dimension. If a number of ContextPoints shall be retrieved,
     * where unset values are handled as "ANY", the method getAllCPs(ContextCoordinates co) should be used.
     *
     * @throws SharkKBException
     */
    @Test
    public void testContextPointsViaContextSpace() throws SharkKBException {
        J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic1 = kb.createSemanticTag("Topic1", "http://topic1.de");
        SemanticTag topic2 = kb.createSemanticTag("Topic2", "http://topic2.de");
        SemanticTag topic3 = kb.createSemanticTag("Topic3", "http://topic3.de");

        PeerSemanticTag peer1 = kb.createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
        PeerSemanticTag peer2 = kb.createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");

        ContextCoordinates co1 = kb.createContextCoordinates(topic1, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint cp1 = kb.createContextPoint(co1);
        cp1.addInformation("Information1");

        ContextCoordinates co2 = kb.createContextCoordinates(topic2, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint cp2 = kb.createContextPoint(co2);
        cp2.addInformation("Information2");

        ContextCoordinates co3 = kb.createContextCoordinates(topic3, null, peer1, peer2, null, null, SharkCS.DIRECTION_OUT);
        ContextPoint cp3 = kb.createContextPoint(co3);
        cp3.addInformation("Information3");

        Interest interest = InMemoSharkKB.createInMemoInterest();

        // Leave topics unset, as unset should mean ANY thus covering all topics!
//      STSet interestTopic = kb.createSTSet();
//      interestTopic.addSemanticTag(topic1);
        PeerSTSet interestPeer = InMemoSharkKB.createInMemoPeerSTSet();
        interestPeer.merge(peer1);

        PeerSTSet interestRemotepeer = InMemoSharkKB.createInMemoPeerSTSet();
        interestRemotepeer.merge(peer2);

        // Set the dimensions of the interest
        //interest.setTopic(interestTopic);
        interest.setPeers(interestPeer);
        interest.setRemotePeers(interestRemotepeer);
        interest.setDirection(SharkCS.DIRECTION_OUT);

        // Retrieve ContextPoints covered by the interest
        Enumeration cps = kb.getContextPoints(interest);
        // The above method does not take "ANY" into account properly

        boolean foundCp1 = false;
        boolean foundCp2 = false;
        boolean foundCp3 = false;
        int numCp = 0;

        Assert.assertNotNull(cps);
        while (cps.hasMoreElements()) {
            ContextPoint resultCp = (ContextPoint) cps.nextElement();
            Enumeration infoEnum = resultCp.enumInformation();
            Assert.assertNotNull(infoEnum);
            Information info = (Information) infoEnum.nextElement();
            byte[] content = info.getContentAsByte();
            String text = new String(content);
            if (text.equals("Information1")) {
                foundCp1 = true;
            } else if (text.equals("Information2")) {
                foundCp2 = true;
            } else if (text.equals("Information3")) {
                foundCp3 = true;
            }

            numCp++;
        }

        Assert.assertTrue(foundCp1);
        Assert.assertTrue(foundCp2);
        Assert.assertTrue(foundCp3);
        Assert.assertEquals(3, numCp);
    }

    /**
     * Return max date of Java's time value. Seems to be of no concers as it is faaaar in the future.
     */
    public void maxTime() {
        long max = Long.MAX_VALUE;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(max));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("Java-Time will overflow on:" + day + "." + month + "." + year + " at: " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
    }

    @Test
    public void testLongToByteArray() {
        long l1 = 1024;
        byte[] l1Byte = Util.longToByteArray(l1);
        Assert.assertEquals(l1Byte.length, 8);

        long l2 = Util.byteArrayToLong(l1Byte);
        Assert.assertEquals(l1, l2);
    }

    /**
     * Create a topic in an empty SharkKB.
     *
     * Create a stand alone STSet.
     *
     * Add topic to created STSet.
     *
     * Add an SI to the topic.
     *
     * Try to retrieve the topic from the created STSet using the newly added SI.
     */
    @Test
    public void testAddSiInStandAloneSTSet() throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic = kb.createSemanticTag("Topic", "http://topic.de");

        // New STSet, empty
        STSet topics = InMemoSharkKB.createInMemoSTSet();
        topics.merge(topic);
        SemanticTag result1 = topics.getSemanticTag(topic.getSI());
        Assert.assertNotNull(result1);

        topic.addSI("http://topic.com"); //when the topic is merged with the STSet, the STSet creates a copy of the topic.
        topics.merge(topic); //when we want to add a SI, the topic must be merged again to update the topic in the STSet.
        SemanticTag result2 = topics.getSemanticTag(new String[]{"http://topic.com"});
        Assert.assertNotNull(result2);
        Assert.assertTrue(SharkCSAlgebra.identical(topic, result2));
    }
}
