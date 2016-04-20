package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
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
    private ASIPInterest interest = null;
    private ASIPKnowledge knowledge = null;
    private InputStream raw = null;
    private OutputStream os = null;
    private boolean responseSent = false;
    private String recipientAddress = "";
    private ByteArrayOutputStream baos;
    private MessageStub outStub;

    public ASIPOutMessage(SharkEngine engine,
                          StreamConnection connection,
                          long ttl,
                          PeerSemanticTag sender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverLocation,
                          TimeSemanticTag receiverTime) throws SharkKBException {

        super(engine, connection, ttl, sender, receiverPeer, receiverLocation, receiverTime);
        this.recipientAddress = connection.getReceiverAddressString();
        this.os = connection.getOutputStream();
    }

    public ASIPOutMessage(SharkEngine engine, StreamConnection connection, ASIPInMessage in) throws SharkKBException {
        super(engine, connection, (in.getTtl()-1), engine.getOwner(), in.getSender(), in.getReceiverSpatial(), in.getReceiverTime());
//        osw = new OutputStreamWriter(connection.getSharkOutputStream().getOutputStream(), StandardCharsets.UTF_8);
        this.recipientAddress = connection.getReceiverAddressString();
        this.os = connection.getOutputStream();
    }

    public ASIPOutMessage(SharkEngine engine,
                          MessageStub stub,
                          long ttl,
                          PeerSemanticTag sender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverLocation,
                          TimeSemanticTag receiverTime,
                          String address) throws SharkKBException {

        super(engine, stub, ttl, sender, receiverPeer, receiverLocation, receiverTime);
        this.outStub = stub;
        this.recipientAddress = address;
        this.os = new ByteArrayOutputStream();
    }


    public boolean responseSent(){
        return this.responseSent;
    }

    private void sent(){

        try {
            if(outStub != null) {
                this.outStub.sendMessage(( (ByteArrayOutputStream) this.os).toByteArray(), this.recipientAddress);
            } else {
                this.osw.flush();
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
            String parse = ASIPSerializer.serializeExpose(this, interest).toString();
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
            this.osw.write(ASIPSerializer.serializeInsert(this, knowledge).toString());
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
            this.osw.write(ASIPSerializer.serializeRaw(this, raw).toString());
        } catch (SharkKBException e) {
            L.d("Serialize failed");
            e.printStackTrace();
        } catch (IOException e) {
            L.d("Write failed");
            e.printStackTrace();
        }
        this.sent();
    }

    public  void raw(InputStream inputStream){
        this.setCommand(ASIPMessage.ASIP_RAW);

//        this.initSecurity();

        this.osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPSerializer.serializeRaw(this, inputStream).toString());
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
