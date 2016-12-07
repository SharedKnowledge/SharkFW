package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.serialization.ASIPMessageSerializer;
import net.sharkfw.asip.serialization.ASIPMessageSerializerHelper;
import net.sharkfw.asip.serialization.ASIPSerializationHolder;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.system.L;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Objects of this class are produced by the framework in order
 * to be serialized and transmitted to another peer.
 *
 * @author thsc
 */
public class ASIPOutMessage extends ASIPMessage {

    private Writer osw = null;
    private OutputStream os = null;
    private boolean responseSent = false;
    private String recipientAddress = "";
    private MessageStub outStub;

    public ASIPOutMessage(SharkEngine engine,
                          StreamConnection connection,
                          long ttl,
                          PeerSemanticTag physicalSender,
                          PeerSemanticTag logicalSender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverLocation,
                          TimeSemanticTag receiverTime,
                          SemanticTag topic,
                          SemanticTag type) throws SharkKBException {

        super(engine, connection, ttl, physicalSender, logicalSender, receiverPeer, receiverLocation, receiverTime, topic, type);
        this.recipientAddress = connection.getReceiverAddressString();
        this.os = connection.getOutputStream();
    }

    public ASIPOutMessage(SharkEngine engine, StreamConnection connection, ASIPInMessage in, SemanticTag topic, SemanticTag type) throws SharkKBException {
        super(engine, connection, (in.getTtl() - 1), engine.getOwner(), in.getLogicalSender(), in.getSender(), in.getReceiverSpatial(), in.getReceiverTime(), topic, type);

        this.recipientAddress = connection.getReceiverAddressString();
        // TODO throws error!
//        PeerSemanticTag receiver = in.getSender();
//        receiver.addAddress(this.recipientAddress);
//        this.setReceiverPeer(receiver);
        this.os = connection.getOutputStream();
    }

    public ASIPOutMessage(SharkEngine engine,
                          MessageStub stub,
                          long ttl,
                          PeerSemanticTag physicalSender,
                          PeerSemanticTag logicalSender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverLocation,
                          TimeSemanticTag receiverTime,
                          SemanticTag topic,
                          SemanticTag type,
                          String address) throws SharkKBException {

        super(engine, stub, ttl, physicalSender, logicalSender, receiverPeer, receiverLocation, receiverTime, topic, type);
        this.outStub = stub;
        this.recipientAddress = address;
        this.os = new ByteArrayOutputStream();
    }


    public boolean responseSent() {
        return this.responseSent;
    }

    private void sent() {

        try {
            this.osw.flush();
            if (outStub != null) {
                final byte[] msg = ((ByteArrayOutputStream) this.os).toByteArray();
                this.outStub.sendMessage(msg, this.recipientAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.responseSent = true;
    }

    public void expose(ASIPInterest interest) {
        this.setCommand(ASIPMessage.ASIP_EXPOSE);

//        this.initSecurity();

        this.osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            String parse = ASIPMessageSerializer.serializeExpose(this, interest).toString();
            this.osw.write(parse);
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sent();
    }

    public void insert(ASIPKnowledge knowledge) {

        this.setCommand(ASIPMessage.ASIP_INSERT);

//        this.initSecurity();

        this.osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
                this.osw.write(ASIPMessageSerializer.serializeInsert(this, knowledge).toString());
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sent();
    }

    public void raw(byte[] raw) {

        this.setCommand(ASIPMessage.ASIP_RAW);

//        this.initSecurity();

        this.osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPMessageSerializer.serializeRaw(this, raw).toString());
        } catch (SharkKBException e) {
            L.d("Serialize failed");
            e.printStackTrace();
        } catch (IOException e) {
            L.d("Write failed");
            e.printStackTrace();
        }
        this.sent();
    }

    public void raw(InputStream inputStream) {
        this.setCommand(ASIPMessage.ASIP_RAW);

//        this.initSecurity();

        this.osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPMessageSerializer.serializeRaw(this, inputStream).toString());
        } catch (SharkKBException e) {
            L.d("Serialize failed");
            e.printStackTrace();
        } catch (IOException e) {
            L.d("Write failed");
            e.printStackTrace();
        }
        this.sent();
    }

}
