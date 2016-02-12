/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASIPTests;

import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.asip.ASIPSerializer;
import net.sharkfw.knowledgeBase.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Naming Convention as When/Given/Then
 * Taken from http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html
 * When - MethodName, or many methods names if needed, or action without associated with specific method
 * Given - state at test moment
 * Then - excepted result.
 * @author msc
 */
public class ASIPSerializerTest {
    
    SharkKB kb;
    String[] sis;
    String[] addresses;
    
    SemanticTag t1;
    SemanticTag t2;
    SemanticTag t3;
    SemanticTag t4;
    SemanticTag t5;
    
    STSet topics;
    
    PeerSemanticTag p1;
    PeerSemanticTag p2;
    PeerSemanticTag p3;
    PeerSemanticTag p4;
    PeerSemanticTag p5;
    
    PeerSTSet approvers;
    PeerSTSet receivers;
    
    TimeSemanticTag ti1;
    TimeSemanticTag ti2;
    TimeSemanticTag ti3;
    TimeSemanticTag ti4;
    
    TimeSTSet times;
    SpatialSTSet locations;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SharkKBException {
        kb = new InMemoSharkKB();
        sis = new String[] { "http://si1.de", "http://si2.de" };
        addresses = new String[] { "tcp://address1.de:1234", "tcp://address2.de:1234" };
        
        t1 = kb.getTopicSTSet().createSemanticTag("Topic1", "http://topci1.de");
        t2 = kb.getTopicSTSet().createSemanticTag("Topic2", "http://topci2.de");
        t3 = kb.getTopicSTSet().createSemanticTag("Topic3", "http://topci3.de");
        t4 = kb.getTopicSTSet().createSemanticTag("Topic4", "http://topci4.de");
        t5 = kb.getTopicSTSet().createSemanticTag("Topic5", sis );

        topics = InMemoSharkKB.createInMemoSTSet();
        topics.merge(t1);
        topics.merge(t2);
        
        p1 = kb.getPeerSTSet().createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
        p2 = kb.getPeerSTSet().createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");
        p3 = kb.getPeerSTSet().createPeerSemanticTag("Peer3", "http://peer3.de", "tcp://peer3.de:1234");
        p4 = kb.getPeerSTSet().createPeerSemanticTag("Peer4", "http://peer4.de", "tcp://peer4.de:1234");
        p5 = kb.getPeerSTSet().createPeerSemanticTag("Peer5", "http://peer5.de", addresses);

        approvers = InMemoSharkKB.createInMemoPeerSTSet();
        approvers.merge(p1);
        approvers.merge(p2);
        
        receivers = InMemoSharkKB.createInMemoPeerSTSet();
        receivers.merge(p3);
        receivers.merge(p4);
        
        ti1 = kb.getTimeSTSet().createTimeSemanticTag(100, 200);
        ti2 = kb.getTimeSTSet().createTimeSemanticTag(200, 300);
        ti3 = kb.getTimeSTSet().createTimeSemanticTag(300, 400);
        ti4 = kb.getTimeSTSet().createTimeSemanticTag(400, 500);
        
        times = InMemoSharkKB.createInMemoTimeSTSet();
        times.merge(ti1);
        times.merge(ti2);
        
        //TODO Add SpatialSemanticTags
        
        locations = InMemoSharkKB.createInMemoSpatialSTSet();
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Serializes and deserializes a SemanticTag to check if both tags still equals
     * Uses a String as si
     * @throws SharkKBException 
     */
    @Test
    public void SemanticTagSerialization_SToneSI_success() throws SharkKBException {
        String serializedTag = ASIPSerializer.serializeTag(t1);
        SemanticTag deserializedTag = ASIPSerializer.deserializeTag(serializedTag);
        
        Assert.assertTrue(SharkCSAlgebra.identical(deserializedTag, t1));
    }
    
    /**
     * Serializes and deserializes a SemanticTag to check if both tags still equals
     * Uses an array as si
     * @throws SharkKBException 
     */
    @Test
    public void SemanticTagSerialization_STmultipleSIS_success() throws SharkKBException{
        String serializedTag = ASIPSerializer.serializeTag(t5);
        SemanticTag deserializedTag = ASIPSerializer.deserializeTag(serializedTag);
        
        Assert.assertTrue(SharkCSAlgebra.identical(deserializedTag, t5));
    }
    
    /**
     * Serializes and deserializes a SemanticTag to check if both tags still equals
     * Uses a String as si
     * @throws SharkKBException 
     */
    @Test
    public void SemanticTagSerialization_PSToneAddress_success() throws SharkKBException {
        String serializedTag = ASIPSerializer.serializeTag(p1);
        SemanticTag deserializedTag = ASIPSerializer.deserializeTag(serializedTag);
        
        Assert.assertTrue(SharkCSAlgebra.identical(deserializedTag, p1));
    }
    
    /**
     * Serializes and deserializes a SemanticTag to check if both tags still equals
     * Uses an array as si
     * @throws SharkKBException 
     */
    @Test
    public void SemanticTagSerialization_PSTmultipleAddresses_success() throws SharkKBException{
        String serializedTag = ASIPSerializer.serializeTag(p5);
        SemanticTag deserializedTag = ASIPSerializer.deserializeTag(serializedTag);
        
        Assert.assertTrue(SharkCSAlgebra.identical(deserializedTag, p5));
    }
    
    /**
     * Serializes and deserializes a SemanticTag to check if both tags still equals
     * Uses a String as si
     * @throws SharkKBException 
     */
    @Test
    public void SemanticTagSerialization_TSToneTime_success() throws SharkKBException {
        String serializedTag = ASIPSerializer.serializeTag(ti1);
        SemanticTag deserializedTag = ASIPSerializer.deserializeTag(serializedTag);
        
        Assert.assertTrue(SharkCSAlgebra.identical(deserializedTag, ti1));
    }
    
    
    /**
     * TODO Workaround: Compare with Strings of Objects not with Objects
     * @throws SharkKBException 
     */
    @Test
    public void SemanticSTSetSerialization_singleST_success() throws SharkKBException{
        
        STSet stSetWithOneTag = InMemoSharkKB.createInMemoSTSet();
        stSetWithOneTag.merge(t1);
        
        String serializedTopics = ASIPSerializer.serializeSTSet(stSetWithOneTag);
        STSet deserialized = InMemoSharkKB.createInMemoSTSet();
        ASIPSerializer.deserializeSTSet(deserialized, serializedTopics);
        
        Assert.assertTrue(SharkCSAlgebra.identical(stSetWithOneTag, deserialized));
    }
    
    /**
     * TODO Workaround: Compare with Strings of Objects not with Objects
     * @throws SharkKBException 
     */
    @Test
    public void SemanticSTSetSerialization_singlePST_success() throws SharkKBException{
        
        STSet stSetWithOneTag = InMemoSharkKB.createInMemoSTSet();
        stSetWithOneTag.merge(p1);
        
        String serializedTopics = ASIPSerializer.serializeSTSet(stSetWithOneTag);
        STSet deserialized = InMemoSharkKB.createInMemoSTSet();
        ASIPSerializer.deserializeSTSet(deserialized, serializedTopics);
        
        Assert.assertTrue(SharkCSAlgebra.identical(stSetWithOneTag, deserialized));
    }
    
    /**
     * TODO Workaround: Compare with Strings of Objects not with Objects
     * @throws SharkKBException 
     */
    @Test
    public void SemanticSTSetSerialization_singleTST_success() throws SharkKBException{
        
        STSet stSetWithOneTag = InMemoSharkKB.createInMemoSTSet();
        stSetWithOneTag.merge(ti1);
        
        String serializedTopics = ASIPSerializer.serializeSTSet(stSetWithOneTag);
        STSet deserialized = InMemoSharkKB.createInMemoSTSet();
        ASIPSerializer.deserializeSTSet(deserialized, serializedTopics);
        
        Assert.assertTrue(SharkCSAlgebra.identical(stSetWithOneTag, deserialized));
    }
    
    /**
     * TODO Workaround: Compare with Strings of Objects not with Objects
     * @throws SharkKBException 
     */
    @Test
    public void SemanticSTSetSerialization_multipleTags_success() throws SharkKBException{
        
        STSet stSetWithMultipleTags = InMemoSharkKB.createInMemoSTSet();
        stSetWithMultipleTags.merge(t1);
        stSetWithMultipleTags.merge(t2);
        stSetWithMultipleTags.merge(t3);
        stSetWithMultipleTags.merge(t5);
        
        String serializedTopics = ASIPSerializer.serializeSTSet(stSetWithMultipleTags);
        STSet deserialized = InMemoSharkKB.createInMemoSTSet();
        ASIPSerializer.deserializeSTSet(deserialized, serializedTopics);
        
        Assert.assertTrue(SharkCSAlgebra.identical(stSetWithMultipleTags, deserialized));
    }
    
    
    /**
     * TODO Workaround: Compare with Strings of Objects not with Objects
     * @throws SharkKBException 
     */
    @Test
    public void SemanticSTSetSerialization_multipleMixedTags_success() throws SharkKBException{
        
        STSet stSetWithMultipleTags = InMemoSharkKB.createInMemoSTSet();
        stSetWithMultipleTags.merge(t1);
        stSetWithMultipleTags.merge(ti2);
        stSetWithMultipleTags.merge(p5);
        stSetWithMultipleTags.merge(t5);
        
        String serializedTopics = ASIPSerializer.serializeSTSet(stSetWithMultipleTags);
        STSet deserialized = InMemoSharkKB.createInMemoSTSet();
        ASIPSerializer.deserializeSTSet(deserialized, serializedTopics);
        
        Assert.assertTrue(SharkCSAlgebra.identical(stSetWithMultipleTags, deserialized));
    }
    
//    @Test
    public void coordinateSerializationTest() throws SharkKBException, SharkNotSupportedException {

        J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();
        
        Interest co1 = InMemoSharkKB.createInMemoInterest(topics, p4, approvers, receivers, times, locations, 0);
//        ContextPoint cp1 = kb.createContextPoint(co1);
//        cp1.addInformation("ContextPoint1");
        
        System.out.println(ASIPSerializer.serializeInterestJSON((ASIPSpace)co1).toString(2) );
        
        Assert.assertTrue(true);
    }
}
