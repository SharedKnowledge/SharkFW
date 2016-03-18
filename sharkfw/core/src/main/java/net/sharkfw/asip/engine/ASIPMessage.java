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
 * @author msc, thsc
 */
public abstract class ASIPMessage {
    public static final int ASIP_EXPOSE = 0;
    public static final int ASIP_INSERT = 1;
    public static final int ASIP_RAW = 2;

    public static final String VERSION = "VERSION";
    public static final String FORMAT = "FORMAT";
    public static final String ENCRYPTED = "ENCRYPTED";
    public static final String ENCRYPTEDSESSIONKEY = "ENCRYPTEDSESSIONKEY";
    public static final String SIGNED = "SIGNED";
    public static final String TTL = "TTL";
    public static final String COMMAND = "COMMAND";
    public static final String SENDER = "SENDER";
    public static final String RECEIVERS = "RECEIVERS";
    public static final String SIGNATURE = "SIGNATURE";

    private SharkEngine engine;
    private StreamConnection connection;

    private final String version = "ASIP 1.0";
    private final String format = "JSON";
    private boolean encrypted = false;
    private String encryptedSessionKey = "";
    private boolean signed = false;
    private String signature = "";
    private long ttl = -1;
    private int command;
    private PeerSemanticTag sender;
    private STSet receivers;
    private PeerSemanticTag receiverPeer;
    private SpatialSemanticTag receiverSpatial;
    private TimeSemanticTag receiverTime;

    public ASIPMessage(SharkEngine engine, StreamConnection connection) {
        this.engine = engine;
        this.connection = connection;
    }

    public ASIPMessage(SharkEngine engine,
                       StreamConnection connection,
                       long ttl,
                       PeerSemanticTag sender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime) throws SharkKBException {
        this.engine = engine;
        this.connection = connection;
        this.ttl = ttl;

        this.sender = sender;
        if (receiverPeer != null) {
            this.receiverPeer = receiverPeer;
            this.receivers.merge(receiverPeer);
        }
        if (receiverSpatial != null) {
            this.receiverSpatial = receiverSpatial;
            this.receivers.merge(receiverSpatial);
        }
        if (receiverTime != null) {
            this.receiverTime = receiverTime;
            this.receivers.merge(receiverTime);
        }
    }

    public ASIPMessage(SharkEngine engine,
                       StreamConnection connection,
                       boolean encrypted,
                       String encryptedSessionKey,
                       boolean signed,
                       String signature,
                       long ttl,
                       PeerSemanticTag sender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime) throws SharkKBException {
        this.engine = engine;
        this.connection = connection;

        this.encrypted = encrypted;
        this.encryptedSessionKey = encryptedSessionKey;
        this.signed = signed;
        this.signature = signature;
        this.ttl = ttl;

        this.sender = sender;
        if (receiverPeer != null) {
            this.receiverPeer = receiverPeer;
            this.receivers.merge(receiverPeer);
        }
        if (receiverSpatial != null) {
            this.receiverSpatial = receiverSpatial;
            this.receivers.merge(receiverSpatial);
        }
        if (receiverTime != null) {
            this.receiverTime = receiverTime;
            this.receivers.merge(receiverTime);
        }
    }

    public String getVersion() {
        return version;
    }

    public String getFormat() {
        return format;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public String getEncryptedSessionKey() {
        return encryptedSessionKey;
    }

    public boolean isSigned() {
        return signed;
    }

    public String getSignature() {
        return signature;
    }

    public long getTtl() {
        return ttl;
    }

    public int getCommand() {
        return command;
    }

    public PeerSemanticTag getSender() {
        return sender;
    }

    public STSet getReceivers() {
        return receivers;
    }

    public PeerSemanticTag getReceiverPeer() {
        return receiverPeer;
    }

    public SpatialSemanticTag getReceiverSpatial() {
        return receiverSpatial;
    }

    public TimeSemanticTag getReceiverTime() {
        return receiverTime;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public void setSignature(String signature) {
        if(!signature.isEmpty()){
            this.signature = signature;
            this.signed = true;
        }
    }

    public void setEncryptedSessionKey(String encryptedSessionKey) {
        if(!encryptedSessionKey.isEmpty()){
            this.encryptedSessionKey = encryptedSessionKey;
            this.encrypted = true;
        }

    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public void setSender(PeerSemanticTag sender) {
        this.sender = sender;
    }

    public void setReceivers(STSet receivers) {
        this.receivers = receivers;
    }

    public void setReceiverPeer(PeerSemanticTag receiverPeer) {
        this.receiverPeer = receiverPeer;
    }

    public void setReceiverSpatial(SpatialSemanticTag receiverSpatial) {
        this.receiverSpatial = receiverSpatial;
    }

    public void setReceiverTime(TimeSemanticTag receiverTime) {
        this.receiverTime = receiverTime;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }
}