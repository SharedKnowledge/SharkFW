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
import net.sharkfw.knowledgeBase.ASIPInterest;
import net.sharkfw.knowledgeBase.ASIPSpace;
import net.sharkfw.system.SharkNotSupportedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author micha
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
        t5 = kb.getTopicSTSet().createSemanticTag("Topic1", sis );

        topics = InMemoSharkKB.createInMemoSTSet();
        topics.merge(t1);
        topics.merge(t2);
        
        p1 = kb.getPeerSTSet().createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
        p2 = kb.getPeerSTSet().createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");
        p3 = kb.getPeerSTSet().createPeerSemanticTag("Peer3", "http://peer3.de", "tcp://peer3.de:1234");
        p4 = kb.getPeerSTSet().createPeerSemanticTag("Peer4", "http://peer4.de", "tcp://peer4.de:1234");
        p4 = kb.getPeerSTSet().createPeerSemanticTag("Peer5", "http://peer4.de", addresses);

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
    
    @Test
    public void semanticTagTest() throws SharkKBException {
        
        String serializedT1 = ASIPSerializer.serializeTag(t1);
        SemanticTag deserializedT1 = ASIPSerializer.deserializeTag(serializedT1);
        
        Assert.assertEquals(deserializedT1, t1);
        
        String serializedT5 = ASIPSerializer.serializeTag(t5);
        SemanticTag deserializedT5 = ASIPSerializer.deserializeTag(serializedT5);
        
        Assert.assertEquals(deserializedT5, t5);
    }
    
    @Test
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
