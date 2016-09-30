package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPOutMessage;
import net.sharkfw.knowledgeBase.SharkAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoASIPKnowledge;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import net.sharkfw.system.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;


/**
 * Created by j4rvis on 21.03.16.
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

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null, null, null);
        outMessage.raw(rawInput.getBytes(StandardCharsets.UTF_8));
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertEquals(inMessage, outMessage);
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageRawByteArray_success() throws Exception {

        String rawInput = "Hello ASIP.";

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null, null, null);
        outMessage.raw(rawInput.getBytes(StandardCharsets.UTF_8));
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        String text = null;
        try (Scanner scanner = new Scanner(inMessage.getRaw(), StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }

        Assert.assertEquals(rawInput, text);
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageRawInputStream_success() throws Exception {

        String rawInput = "Hello ASIP.";
        ByteArrayInputStream stream = new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8));

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null, null, null);
        outMessage.raw(stream);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        String text = null;
        try (Scanner scanner = new Scanner(inMessage.getRaw(), StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }

        Assert.assertEquals(rawInput, text);
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageExpose_success() throws Exception {

        ASIPInterest space = InMemoSharkKB.createInMemoASIPInterest(topics, types, sender, peers, peers, null, null, ASIPSpace.DIRECTION_INOUT);

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null, null, null);
        outMessage.expose(space);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        Assert.assertTrue(SharkAlgebra.identical(space, inMessage.getInterest()));
    }

    @Test
    public void ASIPMessage_getOutputInterest_success() throws Exception {

        ASIPInterest space = (ASIPInterest) TestUtils.createRandomASIPSpace();

        ASIPOutMessage outMessage = new ASIPOutMessage(
                this.engine,
                this.connection,
                10,
                TestUtils.createRandomPeerSemanticTag(),
                TestUtils.createRandomPeerSemanticTag(),
                null,
                null,
                TestUtils.createRandomSemanticTag(),
                TestUtils.createRandomSemanticTag());
        outMessage.expose(space);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();
    }

    @Test
    public void ASIPMessage_getOutputKnowledge_success() throws Exception {

        InMemoSharkKB sharkKB = new InMemoSharkKB(
                TestUtils.createRandomSemanticNet(1),
                TestUtils.createRandomSemanticNet(1),
                TestUtils.createRandomPeerTaxonomy(1),
                InMemoSharkKB.createInMemoSpatialSTSet(),
                InMemoSharkKB.createInMemoTimeSTSet()
        );

        ASIPSpace asipSpace = sharkKB.createASIPSpace(
                TestUtils.createRandomSTSet(1),
                TestUtils.createRandomSTSet(1),
                TestUtils.createRandomPeerSTSet(1),
                TestUtils.createRandomPeerSemanticTag(),
                TestUtils.createRandomPeerSTSet(1),
                null,
                null,
                TestUtils.createRandomDirection()
        );

        sharkKB.addInformation(TestUtils.createRandomString(20), asipSpace);

        ASIPOutMessage outMessage = new ASIPOutMessage(
                this.engine,
                this.connection,
                10,
                TestUtils.createRandomPeerSemanticTag(),
                TestUtils.createRandomPeerSemanticTag(),
                null,
                null,
                TestUtils.createRandomSemanticTag(),
                TestUtils.createRandomSemanticTag());
        outMessage.insert(sharkKB);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();
    }

    @Test
    public void ASIPMessage_CompareInToOutMessageInsert_success() throws Exception{

        String rawInput = "Hello ASIP.";

        SharkKB kb = new InMemoSharkKB();
        ASIPSpace space = kb.createASIPSpace(topics, types, peers, sender, peers, null, null, ASIPSpace.DIRECTION_INOUT);
        ASIPKnowledge knowledge = new InMemoASIPKnowledge(kb.getVocabulary());
        knowledge.addInformation(rawInput, space);

        ASIPOutMessage outMessage = new ASIPOutMessage(this.engine, this.connection, 10, sender, receiverPeer, null, null, null, null);
        outMessage.insert(knowledge);
        this.connection.createInputStream();

        ASIPInMessage inMessage = new ASIPInMessage(this.engine, this.connection);
        inMessage.parse();

        String receivedContent = "";
        Iterator<ASIPInformationSpace> informationSpaces = inMessage.getKnowledge().informationSpaces();
        while(informationSpaces.hasNext()){
            ASIPInformationSpace informationSpace = informationSpaces.next();
            Iterator<ASIPInformation> infos = informationSpace.informations();
            while (infos.hasNext()){
                ASIPInformation info = infos.next();
                receivedContent = new String(info.getContentAsByte(),StandardCharsets.UTF_8);
            }
        }

        Assert.assertTrue(rawInput.equals(receivedContent));

    }
}
