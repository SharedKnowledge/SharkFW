package ApiRev1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.kep.KnowledgeSerializer;
import net.sharkfw.kep.format.XMLSerializer;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.protocols.SharkInputStream;
import net.sharkfw.protocols.SharkOutputStream;
import net.sharkfw.protocols.StandardSharkInputStream;
import net.sharkfw.protocols.UTF8SharkOutputStream;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;
import org.junit.*;

/**
 *
 * @author mfi
 */
public class SerializationTest {

    public SerializationTest() {
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
     * Create two SimpleContextCoordinates. One with all coords set to tags, and
     * one with all coords (except for direction) set to ANY.
     *
     * Check the proper setting of all values after the creation of the
     * Coordinates.
     * 
     * @throws SharkKBException
     * @throws SharkNotSupportedException
     */
    @Test
    public void coordinateSerializationTest() throws SharkKBException, SharkNotSupportedException {

      J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
      SharkKB kb = new InMemoSharkKB();

      // Build vocabulary
      SemanticTag t1 = kb.createSemanticTag("Topic1", "http://topci1.de");
      SemanticTag t2 = kb.createSemanticTag("Topic2", "http://topci2.de");
      SemanticTag t3 = kb.createSemanticTag("Topic3", "http://topci3.de");
      SemanticTag t4 = kb.createSemanticTag("Topic4", "http://topci4.de");

      PeerSemanticTag p1 = kb.createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
      PeerSemanticTag p2 = kb.createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");
      PeerSemanticTag p3 = kb.createPeerSemanticTag("Peer3", "http://peer3.de", "tcp://peer3.de:1234");
      PeerSemanticTag p4 = kb.createPeerSemanticTag("Peer4", "http://peer4.de", "tcp://peer4.de:1234");

      TimeSemanticTag ti1 = kb.createTimeSemanticTag(100, 200);
      TimeSemanticTag ti2 = kb.createTimeSemanticTag(200, 300);
      TimeSemanticTag ti3 = kb.createTimeSemanticTag(300, 400);
      TimeSemanticTag ti4 = kb.createTimeSemanticTag(400, 500);

//      SpatialSemanticTag g1 = kb.createSpatialSemanticTag("test1", new String[]{"http://test.de"}, new Double[]{10.0, 20.0}, 1.0);
//      SpatialSemanticTag g2 = kb.createSpatialSemanticTag("test2", new String[]{"http://test2.de"}, new Double[]{20.0, 30.0}, 1.0);
//      SpatialSemanticTag g3 = kb.createSpatialSemanticTag("test3", new String[]{"http://test3.de"}, new Double[]{30.0, 40.0}, 1.0);
//      SpatialSemanticTag g4 = kb.createSpatialSemanticTag("test4", new String[]{"http://test4.de"}, new Double[]{40.0, 50.0}, 1.0);

      // First CP
      ContextCoordinates co1 = InMemoSharkKB.createInMemoContextCoordinates(t1, p1, p2, p1, ti1, null, SharkCS.DIRECTION_OUT);
      ContextPoint cp1 = kb.createContextPoint(co1);
      cp1.addInformation("ContextPoint1");

      XMLSerializer xml = new XMLSerializer();
      String serialized = xml.serializeSharkCS(co1);

      System.out.println("Serialized ContextCoordinates:");
      System.out.println(serialized);

      System.out.println("\n\nDeserialization .....");
      SharkCS cs = xml.deserializeSharkCS(serialized);
      System.out.println(".. completed\n\n");

      ContextCoordinates co2 = InMemoSharkKB.createInMemoContextCoordinates(
              cs.getTopics().tags().nextElement(),
              cs.getOriginator(),
              cs.getPeers().peerTags().nextElement(),
              cs.getRemotePeers().peerTags().nextElement(),
              cs.getTimes().timeTags().nextElement(),
//              cs.getLocations().spatialTags().nextElement(),
              null,
              cs.getDirection()
              );

      boolean same = SharkCSAlgebra.identical(co1, co2);
      Assert.assertTrue(same);
    }

        /**
         * Create a tag and set two properties to it.
         * Serialitze and deserialize the STSet to check if the resulting
         * (deserialized) stset contains the tag with both properties.
         * Thus proving, that the [de]serialization works.
         * 
         * @throws SharkNotSupportedException
         * @throws SharkKBException
         */
        @Test
        public void testTagPropertySerialization() throws SharkNotSupportedException, SharkKBException {
          J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
          SharkKB kb = new InMemoSharkKB();

          SemanticTag test = kb.createSemanticTag("Test", "http://test.de");
          test.setProperty("key1", "Value1");
          test.setProperty("key2", "value2");

          STSet topics = kb.getTopicSTSet();

          KnowledgeSerializer xml = new XMLSerializer();
          String serialized = xml.serializeSTSet(topics);
          System.out.println(serialized);

          STSet target = InMemoSharkKB.createInMemoSTSet();
          xml.deserializeSTSet(target, serialized);
          SemanticTag testResult = target.getSemanticTag(test.getSI());
          
          String value1 = testResult.getProperty("key1");
          String value2 = testResult.getProperty("key2");

          Assert.assertEquals(value1, test.getProperty("key1"));
          Assert.assertEquals(value2, test.getProperty("key2"));

        }

        /**
         * Create a tag and add a property w/o transer-constraints (just call setProperty(name, value))
         * Add a second property with transferable switch set to "false" to stop
         * the system from serializing this second property.
         *
         * Deserialize the result of the first operation, and check if property 1
         * is existing, and check if property 2 is *not* existing.
         * 
         * @throws SharkNotSupportedException
         * @throws SharkKBException
         */
        @Test
        public void testTagPropertySerializationWithTransferable() throws SharkNotSupportedException, SharkKBException {
          J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
          SharkKB kb = new InMemoSharkKB();

          SemanticTag test = kb.createSemanticTag("Test", "http://test.de");
          test.setProperty("key1", "Value1");
          test.setProperty("key2", "value2", false);

          STSet topics = kb.getTopicSTSet();

          KnowledgeSerializer xml = new XMLSerializer();
          String serialized = xml.serializeSTSet(topics);
          System.out.println(serialized);

          STSet target = InMemoSharkKB.createInMemoSTSet();
          xml.deserializeSTSet(target, serialized);
          SemanticTag testResult = target.getSemanticTag(test.getSI());
          
          String value1 = testResult.getProperty("key1");
          String value2 = testResult.getProperty("key2");

          Assert.assertEquals(value1, test.getProperty("key1"));
          Assert.assertNull(value2);

        }


        /**
         * Create a GeoSemanticTag and set two properties.
         * Serialize and Deserialize the GeoSTSet and check if the resulting
         * GeoSTSet contains the given tag with both properties intact.
         * 
         * @throws SharkNotSupportedException
         * @throws SharkKBException
         */
//        @Test
//        public void testGeoTagPropertySerialization() throws SharkNotSupportedException, SharkKBException {
//          J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
//          SharkKB kb = new InMemoSharkKB();
//
//          SpatialSemanticTag test = kb.createSpatialSemanticTag("test1", new String[]{"http://test.de"}, new Double[]{10.0, 20.0}, 5.0);
//
//          SpatialSTSet locations = kb.getSpatialSTSet();
//
//          KnowledgeSerializer xml = new XMLSerializer();
//          String serialized = xml.serializeSTSet(locations);
//          System.out.println("Serialized GeoTag: \n" + serialized);
//
//          SpatialSTSet target = InMemoSharkKB.createInMemoSpatialSTSet();
//          xml.deserializeSTSet(target, serialized);
//          SemanticTag testResult = target.getSemanticTag(test.getSI());
//          
//          SpatialSemanticTag geoResult = (SpatialSemanticTag) testResult;
//          String value1 = geoResult.getProperty("key1");
//          String value2 = geoResult.getProperty("key2");
//
//          Assert.assertEquals(value1, test.getProperty("key1"));
//          Assert.assertEquals(value2, test.getProperty("key2"));
//
//        }
        
//        @Test - converting string to byte isn't same as in real communication - test seems to be wrong - check it when time
        public void testKnowledgeSerialization() throws SharkNotSupportedException, SharkKBException, IOException {
            SharkKB kb = TestData.createKB1();
            
            KnowledgeSerializer ks = new XMLSerializer();
            
            Knowledge k = kb.createKnowledge();
            
            Enumeration<ContextPoint> cpEnum = kb.getAllContextPoints();
            Assert.assertNotNull(cpEnum);
            
            while(cpEnum.hasMoreElements()) {
                k.addContextPoint(cpEnum.nextElement());
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SharkOutputStream sos = new UTF8SharkOutputStream(baos);
            
            ks.write(k, sos);
            
            String serializedKnowledge = baos.toString();
            byte[] byteK = serializedKnowledge.getBytes("UTF-8"); // charset??
            
            ByteArrayInputStream bais = new ByteArrayInputStream(byteK);
            SharkInputStream sis = new StandardSharkInputStream(bais);
            
            Knowledge rK = ks.parseKnowledge(sis);
            
            // tests
            
            // context
            SharkVocabulary context = rK.getVocabulary();

            // topic
            SemanticTag topic1 = context.getSemanticTag(TestData.TOPIC1_SI);
            Assert.assertNotNull(topic1);
            
            // peer
            PeerSemanticTag peer1 = context.getPeerSemanticTag(TestData.PEER1_SI);
            Assert.assertNotNull(peer1);
            
            cpEnum = rK.contextPoints();
            Assert.assertNotNull(cpEnum);
            
            ContextPoint cp = cpEnum.nextElement();
            
            ContextCoordinates cc = kb.createContextCoordinates(topic1, peer1, null, null, null, null, SharkCS.DIRECTION_INOUT);
            
            Assert.assertTrue(SharkCSAlgebra.identical(cp.getContextCoordinates(), cc));
            
            Information i = cp.enumInformation().nextElement();
            
            String content = new String(i.getContentAsByte());
            Assert.assertTrue(content.equalsIgnoreCase(TestData.INFO_1_CONTENT));
        }
        
}