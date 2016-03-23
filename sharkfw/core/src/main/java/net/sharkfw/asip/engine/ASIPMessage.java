/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import java.security.PrivateKey;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;

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
    public static final String RECEIVERPEER = "RECEIVERPEER";
    public static final String RECEIVERLOCATION = "RECEIVERLOCATION";
    public static final String RECEIVERTIME = "RECEIVERTIME";

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
    private PeerSemanticTag receiverPeer = null;
    private SpatialSemanticTag receiverSpatial = null;
    private TimeSemanticTag receiverTime = null;
    private PrivateKey privateKey;
    private SharkPkiStorage sharkPkiStorage;
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
                       PeerSemanticTag sender,
                       PeerSemanticTag receiverPeer,
                       SpatialSemanticTag receiverSpatial,
                       TimeSemanticTag receiverTime) throws SharkKBException {

        this.engine = engine;
        this.connection = connection;
        this.ttl = ttl;

        this.receivers = InMemoSharkKB.createInMemoSTSet();

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
    
    public void initSecurity(PrivateKey privateKey, /*SharkPublicKeyStorage publicKeyStorage,*/ SharkPkiStorage sharkPkiStorage,
            SharkEngine.SecurityLevel encryptionLevel, SharkEngine.SecurityLevel signatureLevel, 
            SharkEngine.SecurityReplyPolicy replyPolicy, boolean refuseUnverifiably) {
        
        this.privateKey = privateKey;
        //this.publicKeyStorage = publicKeyStorage;
        this.sharkPkiStorage = sharkPkiStorage;
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
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;
//        if (receivers != null ? !receivers.equals(that.receivers) : that.receivers != null) return false;
        if (receiverPeer != null ? !receiverPeer.equals(that.receiverPeer) : that.receiverPeer != null) return false;
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
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
//        result = 31 * result + (receivers != null ? receivers.hashCode() : 0);
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
                ", sender=" + sender +
//                ", receivers=" + receivers +
                ", receiverPeer=" + receiverPeer +
                ", receiverSpatial=" + receiverSpatial +
                ", receiverTime=" + receiverTime +
                '}';
    }
}