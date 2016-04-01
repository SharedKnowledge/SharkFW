package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
import net.sharkfw.knowledgeBase.geom.SpatialAlgebra;
import net.sharkfw.knowledgeBase.inmemory.InMemoASIPKnowledge;
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
public class ASIPMessageTest extends ASIPBaseTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
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
    public void ASIPMessage_CompareInToOutMessageRawByteArray_success() throws Exception {

        String rawInput = "Hello ASIP.";

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.raw(rawInput.getBytes(StandardCharsets.UTF_8));
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertEquals(rawInput, IOUtils.toString(inMessage.getRaw(), "UTF-8"));
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageRawInputStream_success() throws Exception {

        String rawInput = "Hello ASIP.";
        ByteArrayInputStream stream = new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8));

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.raw(stream);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertEquals(rawInput, IOUtils.toString(inMessage.getRaw(), "UTF-8"));
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageExpose_success() throws Exception {

        ASIPInterest space = InMemoSharkKB.createInMemoASIPInterest(topics, types, sender, peers, peers, null, null, ASIPSpace.DIRECTION_INOUT);

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        outMessage.expose(space);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertTrue(SharkAlgebra.identical(space, inMessage.getInterest()));
    }

    @Ignore
    @Test
    public void ASIPMessage_CompareInToOutMessageInsert_success() throws Exception{

        String rawInput = "Hello ASIP.";

        L.d("before k");
        SharkKB kb = new InMemoSharkKB();
        ASIPSpace space = kb.createASIPSpace(topics, types, peers, sender, peers, null, null, ASIPSpace.DIRECTION_INOUT);
        ASIPKnowledge knowledge = new InMemoASIPKnowledge(kb.getVocabulary());
        L.d("after creation");
        knowledge.addInformation(rawInput, space);
        L.d("created");

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null);
        L.d("insert");
        outMessage.insert(knowledge);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        L.d("parsed;");

    }
}
