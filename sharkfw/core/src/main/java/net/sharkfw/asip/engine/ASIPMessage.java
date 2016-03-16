/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;

/**
 *
 * @author msc, thsc
 */
public abstract class ASIPMessage {
    public static final int ASIP_EXPOSE = 0;
    public static final int ASIP_INSERT = 1;
    public static final int ASIP_RAW = 2;

    public static final String ENCRYPTED = "ENCRYPTED";
    public static final String ENCRYPTEDSESSIONKEY = "ENCRYPTEDSESSIONKEY";
    public static final String VERSION = "VERSION";
    public static final String FORMAT = "FORMAT";
    public static final String COMMAND = "COMMAND";
    public static final String SENDER = "SENDER";
    public static final String RECEIVERS = "RECEIVERS";
    public static final String SIGNATURE = "SIGNATURE";

    SharkEngine engine;
    StreamConnection connection;

    private boolean encrypted;
    private String encryptedSessionKey;
    private String version;
    private String format;
    private int command;
    private PeerSemanticTag sender;
    private STSet receivers;
    private PeerSemanticTag receiverPeer;
    private SpatialSemanticTag receiverSpatial;
    private TimeSemanticTag receiverTime;
    private String signature;

    public ASIPMessage(SharkEngine engine,
                       StreamConnection connection,
                       boolean encrypted,
                       String encryptedSessionKey,
                       String version,
                       String format,
                       int command,
                       PeerSemanticTag sender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime,
                       String signature) throws SharkKBException {
        this.engine = engine;
        this.connection = connection;
        this.encrypted = encrypted;
        this.encryptedSessionKey = encryptedSessionKey;
        this.version = version;
        this.format = format;
        this.command = command;
        this.sender = sender;
        // TODO all receiver as single STSet or separated?
        if(receiverPeer!=null) this.receivers.merge(receiverPeer);
        if(receiverSpatial!=null) this.receivers.merge(receiverSpatial);
        if(receiverTime!=null) this.receivers.merge(receiverTime);
        this.signature = signature;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public void setEncryptedSessionKey(String encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public PeerSemanticTag getSender() {
        return sender;
    }

    public void setSenders(PeerSemanticTag sender) {
        this.sender = sender;
    }

    public STSet getReceivers() {
        return receivers;
    }

    public void setReceivers(STSet receivers) {
        this.receivers = receivers;
    }
    
    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
