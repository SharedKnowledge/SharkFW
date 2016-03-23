package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.SpatialAlgebra;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.GeoSensor;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.PeerSensor;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.tcp.TCPConnection;
import net.sharkfw.system.L;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by msc on 21.03.16.
 */
public class ASIPMessageTest {

    SharkEngine engine;
    TestConnection connection;

    PeerSemanticTag sender;
    PeerSemanticTag receiverPeer;
    SpatialSemanticTag receiverSpatial;
    TimeSemanticTag receiverTime;

    String[] sis;
    String[] addresses;

    PeerSTSet peers;
    SpatialSTSet locations;
    TimeSTSet times;

    STSet topics;
    STSet types;

    @Before
    public void setUp() throws Exception {
        engine = new J2SEAndroidSharkEngine();
        connection = new TestConnection();

        L.setLogLevel(L.LOGLEVEL_ALL);

        sis = new String[]{"www.test.de", "www.test1.de"};
        addresses = new String[]{"tcp://test.de", "tcp://test1.de"};

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");

        topics = InMemoSharkKB.createInMemoSTSet();
        topics.createSemanticTag("Topcic1", "www.topic1.de");
        topics.createSemanticTag("Topcic2", "www.topic2.de");

        types = InMemoSharkKB.createInMemoSTSet();
        types.createSemanticTag("Types1", "www.types1.de");
        types.createSemanticTag("Types2", "www.types2.de");


    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void ASIPMessage_CompareInToOutMessage_success() throws Exception {

        String rawInput = "Hello ASIP.";

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.raw(rawInput.getBytes(StandardCharsets.UTF_8));
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertEquals(inMessage, outMessage);
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageRaw_success() throws Exception {

        String rawInput = "Hello ASIP.";

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.raw(rawInput.getBytes(StandardCharsets.UTF_8));
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertEquals(new String(inMessage.getRaw(), StandardCharsets.UTF_8), rawInput);
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageExpose_success() throws Exception {

        ASIPInterest space = InMemoSharkKB.createInMemoASIPInterest(topics, types, sender, peers, peers, null, null, ASIPSpace.DIRECTION_INOUT);

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.expose(space);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertTrue(true);

    }
}
