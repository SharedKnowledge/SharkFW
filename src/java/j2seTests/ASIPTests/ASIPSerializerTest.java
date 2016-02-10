/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ASIPTests;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.asip.ASIPSerializer;
import net.sharkfw.system.SharkNotSupportedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author micha
 */
public class ASIPSerializerTest {
    
    public ASIPSerializerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void coordinateSerializationTest() throws SharkKBException, SharkNotSupportedException {

        J2SEAndroidSharkEngine se = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();

        // Build vocabulary
        SemanticTag t1 = kb.getTopicSTSet().createSemanticTag("Topic1", "http://topci1.de");
        SemanticTag t2 = kb.getTopicSTSet().createSemanticTag("Topic2", "http://topci2.de");
        SemanticTag t3 = kb.getTopicSTSet().createSemanticTag("Topic3", "http://topci3.de");
        SemanticTag t4 = kb.getTopicSTSet().createSemanticTag("Topic4", "http://topci4.de");

        STSet topics = InMemoSharkKB.createInMemoSTSet();
        topics.merge(t1);
        topics.merge(t2);
        
        PeerSemanticTag p1 = kb.getPeerSTSet().createPeerSemanticTag("Peer1", "http://peer1.de", "tcp://peer1.de:1234");
        PeerSemanticTag p2 = kb.getPeerSTSet().createPeerSemanticTag("Peer2", "http://peer2.de", "tcp://peer2.de:1234");
        PeerSemanticTag p3 = kb.getPeerSTSet().createPeerSemanticTag("Peer3", "http://peer3.de", "tcp://peer3.de:1234");
        PeerSemanticTag p4 = kb.getPeerSTSet().createPeerSemanticTag("Peer4", "http://peer4.de", "tcp://peer4.de:1234");

        PeerSTSet peers = InMemoSharkKB.createInMemoPeerSTSet();
        peers.merge(p1);
        peers.merge(p2);
        PeerSTSet remotePeers = InMemoSharkKB.createInMemoPeerSTSet();
        remotePeers.merge(p3);
        remotePeers.merge(p4);
        
        TimeSemanticTag ti1 = kb.getTimeSTSet().createTimeSemanticTag(100, 200);
        TimeSemanticTag ti2 = kb.getTimeSTSet().createTimeSemanticTag(200, 300);
        TimeSemanticTag ti3 = kb.getTimeSTSet().createTimeSemanticTag(300, 400);
        TimeSemanticTag ti4 = kb.getTimeSTSet().createTimeSemanticTag(400, 500);
        
        TimeSTSet times = InMemoSharkKB.createInMemoTimeSTSet();
        times.merge(ti1);
        times.merge(ti2);
        SpatialSTSet locations = InMemoSharkKB.createInMemoSpatialSTSet();
        
        Interest co1 = InMemoSharkKB.createInMemoInterest(topics, p4, peers, remotePeers, times, locations, 0);
//        ContextPoint cp1 = kb.createContextPoint(co1);
//        cp1.addInformation("ContextPoint1");
        
        System.out.println(ASIPSerializer.serializeInterest(co1).toString(2) );
        
        Assert.assertTrue(true);
    }
}
