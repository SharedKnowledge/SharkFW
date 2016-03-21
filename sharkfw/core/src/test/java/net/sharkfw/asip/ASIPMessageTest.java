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
import org.apache.commons.compress.utils.IOUtils;
import org.junit.*;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    @Before
    public void setUp() throws Exception {
        engine = new J2SEAndroidSharkEngine();
        connection = new TestConnection();

        sis = new String[]{"www.test.de", "www.test1.de"};
        addresses = new String[]{"tcp://test.de", "tcp://test1.de"};

        peers = InMemoSharkKB.createInMemoPeerSTSet();

        sender = peers.createPeerSemanticTag("SENDER", "www.si1.de", "tcp://addr1.de");
        receiverPeer = peers.createPeerSemanticTag("RECEIEVER", "www.si2.de", "tcp://addr2.de");



    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void ASIPMessage_CompareInToOutMessage_success() throws Exception {

        L.setLogLevel(L.LOGLEVEL_ALL);

        L.d("");

        String rawInput = "Hello ASIP.";
        L.d(rawInput);
        InputStream is = new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8));

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.raw(is);
        this.connection.createInputStream();

//        is.close();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);

        L.d("InMessage created");

        inMessage.parse();

        L.d("");
        Assert.assertEquals(inMessage, outMessage);

    }
}
