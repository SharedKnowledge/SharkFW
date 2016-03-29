package net.sharkfw.asip.engine;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.peer.SharkEngine;
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

    public ASIPOutMessage(SharkEngine engine,
                          StreamConnection connection,
                          long ttl,
                          PeerSemanticTag sender,
                          PeerSemanticTag receiverPeer,
                          SpatialSemanticTag receiverSpatial,
                          TimeSemanticTag receiverTime) throws SharkKBException {

        super(engine, connection, ttl, sender, receiverPeer, receiverSpatial, receiverTime);

        this.os = connection.getOutputStream();

        osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);
    }

    public ASIPOutMessage(SharkEngine engine, StreamConnection connection, ASIPInMessage in) throws SharkKBException {
        super(engine, connection, in.getTtl(), engine.getOwner(), in.getSender(), in.getReceiverSpatial(), in.getReceiverTime());
        osw = new OutputStreamWriter(connection.getSharkOutputStream().getOutputStream(), StandardCharsets.UTF_8);

        // TODO set kepInterest, knowledge or raw
    }

    public void expose(ASIPInterest interest) {
        this.setCommand(ASIPMessage.ASIP_EXPOSE);

//        this.initSecurity();

        if(this.osw == null)
            osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPSerializer.serializeExpose(this, interest).toString());
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.osw.close();
                this.osw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void insert(ASIPKnowledge knowledge) {

        this.setCommand(ASIPMessage.ASIP_INSERT);

//        this.initSecurity();

        if(this.osw == null)
            osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPSerializer.serializeInsert(this, knowledge).toString());
        } catch (SharkKBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void raw(byte[] raw) {

        this.setCommand(ASIPMessage.ASIP_RAW);

//        this.initSecurity();

        if(this.osw == null)
            osw = new OutputStreamWriter(this.os, StandardCharsets.UTF_8);

        try {
            this.osw.write(ASIPSerializer.serializeRaw(this, raw).toString());
        } catch (SharkKBException e) {
            L.d("Serialize failed");
            e.printStackTrace();
        } catch (IOException e) {
            L.d("Write failed");
            e.printStackTrace();
        } finally {
            try {
                this.osw.close();
                this.osw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  void raw(InputStream inputStream){
        this.setCommand(ASIPMessage.ASIP_RAW);

//        this.initSecurity();

        try {
            this.osw.write(ASIPSerializer.serializeRaw(this, inputStream).toString());
        } catch (SharkKBException e) {
            L.d("Serialize failed");
            e.printStackTrace();
        } catch (IOException e) {
            L.d("Write failed");
            e.printStackTrace();
        } finally {
            try {
                this.osw.close();
                this.osw = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
