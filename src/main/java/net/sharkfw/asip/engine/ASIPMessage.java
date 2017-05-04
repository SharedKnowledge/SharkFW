/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.StreamConnection;

import java.security.PrivateKey;

/**
 * @author j4rvis, thsc
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
    public static final String PHYSICALSENDER = "PHYSICALSENDER";
    public static final String LOGICALSENDER = "LOGICALSENDER";
    public static final String SIGNATURE = "SIGNATURE";
    public static final String RECEIVERPEER = "RECEIVERPEER";
    public static final String RECEIVERLOCATION = "RECEIVERLOCATION";
    public static final String RECEIVERTIME = "RECEIVERTIME";
    public static final String TOPIC = "TOPIC";
    public static final String TYPE = "TYPE";

    private SharkEngine engine;
    private StreamConnection connection;

    private final String version = "ASIP1.0";
    private final String format = "JSON";

    private boolean encrypted = false;
    private String encryptedSessionKey = "";
    private boolean signed = false;
    private String signature = "";
    private long ttl = 1;
    private MessageStub stub;
    private int command = -1;
    private PeerSemanticTag physicalSender;
    private PeerSemanticTag logicalSender;
    private PeerSemanticTag receiverPeer = null;
    private SpatialSemanticTag receiverSpatial = null;
    private TimeSemanticTag receiverTime = null;
    private SemanticTag topic;
    private SemanticTag type;

    private PrivateKey privateKey;
    //    private SharkPkiStorage sharkPkiStorage;
    private SharkEngine.SecurityLevel signatureLevel;
    private SharkEngine.SecurityLevel encryptionLevel;
    private SharkEngine.SecurityReplyPolicy replyPolicy;
    private boolean refuseUnverifiably;

    public ASIPMessage(SharkEngine engine, StreamConnection connection) {
        this.engine = engine;
        this.connection = connection;
    }

    public ASIPMessage(SharkEngine engine,
                       StreamConnection connection,
                       long ttl,
                       PeerSemanticTag physicalSender,
                       PeerSemanticTag logicalSender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime,
                       SemanticTag topic,
                       SemanticTag type) throws SharkKBException {

        this.engine = engine;
        this.connection = connection;
        this.ttl = ttl;


        this.physicalSender = physicalSender;
        this.logicalSender = logicalSender;

        if (receiverPeer != null) {
            this.receiverPeer = receiverPeer;
        }
        if (receiverSpatial != null) {
            this.receiverSpatial = receiverSpatial;
        }
        if (receiverTime != null) {
            this.receiverTime = receiverTime;
        }

        this.topic = topic;
        this.type = type;
    }

    public ASIPMessage(SharkEngine engine,
                       MessageStub stub,
                       long ttl,
                       PeerSemanticTag physicalSender,
                       PeerSemanticTag logicalSender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime,
                       SemanticTag topic,
                       SemanticTag type) throws SharkKBException {

        this.engine = engine;
        this.stub = stub;
        this.ttl = ttl;

        this.physicalSender = physicalSender;
        this.logicalSender = logicalSender;

        if (receiverPeer != null) {
            this.receiverPeer = receiverPeer;
        }
        if (receiverSpatial != null) {
            this.receiverSpatial = receiverSpatial;
        }
        if (receiverTime != null) {
            this.receiverTime = receiverTime;
        }
        this.topic = topic;
        this.type = type;
    }

    public void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ /*SharkPkiStorage sharkPkiStorage,*/
                             SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel,
                             SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably) {

        this.privateKey = privateKey;
        //this.publicKeyStorage = publicKeyStorage;
//        this.sharkPkiStorage = sharkPkiStorage;
        this.signatureLevel = signatureLevel;
        this.encryptionLevel = encryptionLevel;
        this.replyPolicy = replyPolicy;
        this.refuseUnverifiably = refuseUnverifiably;
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

    public PeerSemanticTag getPhysicalSender() {
        return physicalSender;
    }

    public void setPhysicalSender(PeerSemanticTag physicalSender) {
        this.physicalSender = physicalSender;
    }

    public PeerSemanticTag getLogicalSender() {
        return logicalSender;
    }

    public void setLogicalSender(PeerSemanticTag logicalSender) {
        this.logicalSender = logicalSender;
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

    public SemanticTag getTopic() {
        return this.topic;
    }

    public SemanticTag getType() {
        return this.type;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public void setSignature(String signature) {
        if (!signature.isEmpty()) {
            this.signature = signature;
            this.signed = true;
        }
    }

    public void setEncryptedSessionKey(String encryptedSessionKey) {
        if (!encryptedSessionKey.isEmpty()) {
            this.encryptedSessionKey = encryptedSessionKey;
            this.encrypted = true;
        }

    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
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

    public void setTopic(SemanticTag topic) {
        this.topic = topic;
    }

    public void setType(SemanticTag type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ASIPMessage)) return false;

        ASIPMessage that = (ASIPMessage) o;

        if (encrypted != that.encrypted) return false;
        if (signed != that.signed) return false;
        if (ttl != that.ttl) return false;
        if (command != that.command) return false;
        if (!version.equals(that.version)) return false;
        if (!format.equals(that.format)) return false;
        if (encryptedSessionKey != null ? !encryptedSessionKey.equals(that.encryptedSessionKey) : that.encryptedSessionKey != null)
            return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;
        if (receiverPeer != null ? !receiverPeer.equals(that.receiverPeer) : that.receiverPeer != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (receiverSpatial != null ? !receiverSpatial.equals(that.receiverSpatial) : that.receiverSpatial != null)
            return false;
        return receiverTime != null ? receiverTime.equals(that.receiverTime) : that.receiverTime == null;

    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + format.hashCode();
        result = 31 * result + (encrypted ? 1 : 0);
        result = 31 * result + (encryptedSessionKey != null ? encryptedSessionKey.hashCode() : 0);
        result = 31 * result + (signed ? 1 : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + (int) (ttl ^ (ttl >>> 32));
        result = 31 * result + command;
//        resultSet = 31 * resultSet + (receivers != null ? receivers.hashCode() : 0);
        result = 31 * result + (receiverPeer != null ? receiverPeer.hashCode() : 0);
        result = 31 * result + (receiverSpatial != null ? receiverSpatial.hashCode() : 0);
        result = 31 * result + (receiverTime != null ? receiverTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ASIPMessage{" +
                "version='" + version + '\'' +
                ", format='" + format + '\'' +
                ", encrypted=" + encrypted +
                ", encryptedSessionKey='" + encryptedSessionKey + '\'' +
                ", signed=" + signed +
                ", signature='" + signature + '\'' +
                ", ttl=" + ttl +
                ", command=" + command +
                ", receiverPeer=" + receiverPeer +
                ", receiverSpatial=" + receiverSpatial +
                ", receiverTime=" + receiverTime +
                ", topic=" + topic +
                ", type=" + type +
                '}';
    }
}