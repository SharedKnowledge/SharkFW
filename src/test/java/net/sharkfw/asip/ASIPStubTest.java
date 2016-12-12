package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by j4rvis on 21.03.16.
 */
public class ASIPStubTest extends ASIPBaseTest {

    PeerSemanticTag sender;
    PeerSemanticTag receiverPeer;

    PeerSTSet peers;

    String rawInput = "Hello ASIP.";
    TestKP testKPA;
    TestKP testKPB;
    J2SEAndroidSharkEngine engineA;
    J2SEAndroidSharkEngine engineB;
    PeerSemanticTag peerA;
    PeerSemanticTag peerB;

    @Before
    public void setUp() throws Exception {

        super.setUp();

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");

        engineA = new J2SEAndroidSharkEngine();
        testKPA = new TestKP(engineA, "Port A");
        testKPA.setText("Pong");

        engineB = new J2SEAndroidSharkEngine();
        testKPB = new TestKP(engineB, "Port B");
        testKPB.setText("Ping");

        peerA = InMemoSharkKB.createInMemoPeerSemanticTag("Peer A", "www.peer-a.de", "tcp://localhost:7070");
        peerB = InMemoSharkKB.createInMemoPeerSemanticTag("Peer B", "www.peer-b.de", "tcp://localhost:7071");

        engineA.setEngineOwnerPeer(peerA);
        engineB.setEngineOwnerPeer(peerB);

        engineB.startTCP(7071);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        engineB.stopTCP();
    }

    @Test
    public void KPCommunication_replyToIncomingData_success() throws Exception {
        ASIPInterest space = InMemoSharkKB.createInMemoASIPInterest(topics, types, peerA, peers, peers, null, null, ASIPSpace.DIRECTION_INOUT);
        Thread.sleep(2000);


//        String rawInput = "Hello ASIP.";
//        InputStream is = new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8));
//        engineA.sendRaw(is, peerB,  testKPA);

//        engineA.sendASIPInterest(space, peerB, testKPA);

        ASIPOutMessage outMessage = engineA.createASIPOutMessage(peerB.getAddresses(), peerA, peerB, null, null, null, null, 10);
        outMessage.expose(space);

        Thread.sleep(10000);
    }

    @Ignore
    @Test
    public void ASIPMessage_sendRawViaEngine_success() throws Exception {
        InputStream is = new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8));

        engineA.sendRaw(is, peerB, testKPA);

        Thread.sleep(1000);

        Assert.assertEquals(testKPB.getRawContentOnce(), rawInput);
        Assert.assertNotEquals(testKPB.getRawContentOnce(), rawInput);
    }
}
