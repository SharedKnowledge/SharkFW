package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by msc on 21.03.16.
 */
public class ASIPStubTest extends ASIPBaseTest {


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

        super.setUp();

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");

    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

    }

//    @Ignore
    @Test
    public void KPCommunication_replyToIncomingData_success() throws Exception {

        J2SEAndroidSharkEngine engineA = new J2SEAndroidSharkEngine();
        engineA.activateASIP();
        KnowledgePort testKPA = new TestKP(engineA, "Port A");

        J2SEAndroidSharkEngine engineB = new J2SEAndroidSharkEngine();
        engineB.activateASIP();
        KnowledgePort testKPB = new TestKP(engineB, "Port B");

//        engineA.startTCP(7070);
        engineB.startTCP(7071);

        String[] addressA = new String[] { "tcp://localhost:7070" };
        String[] addressB = new String[] { "tcp://localhost:7071" };

        PeerSemanticTag peerA = InMemoSharkKB.createInMemoPeerSemanticTag("Peer A", "www.peer-a.de", "tcp://localhost:7070");
        PeerSemanticTag peerB = InMemoSharkKB.createInMemoPeerSemanticTag("Peer B", "www.peer-b.de", "tcp://localhost:7071");

        engineA.setEngineOwnerPeer(peerA);
        engineB.setEngineOwnerPeer(peerB);

        ASIPInterest space = InMemoSharkKB.createInMemoASIPInterest(topics, types, peerA, peers, peers, null, null, ASIPSpace.DIRECTION_INOUT);

        Thread.sleep(2000);

        ASIPOutMessage outMessage = engineA.createASIPOutMessage(peerB.getAddresses(), peerA, peerB, null, null, 10);
        outMessage.expose(space);

        Thread.sleep(1000);

    }
}
