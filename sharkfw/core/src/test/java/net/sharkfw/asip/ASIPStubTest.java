package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by msc on 21.03.16.
 */
public class ASIPStubTest {


    PeerSemanticTag sender;
    PeerSemanticTag receiverPeer;
    SpatialSemanticTag receiverSpatial;
    TimeSemanticTag receiverTime;

    String[] sis;
    String[] addresses;

    PeerSTSet peers;
    SpatialSTSet locations;
    TimeSTSet times;


    @Before
    public void setUp() throws Exception {

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");

    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void KPCommunication_replyToIncomingData_success() throws Exception {

        SharkEngine engineA = new J2SEAndroidSharkEngine();
        KnowledgePort testKPA = new TestKP(engineA, "Port A");

        SharkEngine engineB = new J2SEAndroidSharkEngine();
        KnowledgePort testKPB = new TestKP(engineB, "Port B");

//        engineA.startTCP(7070);
//        engineB.startTCP(7071);

//        ASIPOutMessage outMessage = engineA.createASIPOutMessage("");
//        outMessage.expose();



//        Thread.sleep(Integer.MAX_VALUE);
    }
}