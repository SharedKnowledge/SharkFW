/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;

/**
 *
 * @author micha, thsc
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
    
    private boolean encrypted;
    private String encyptedSessionKey;
    private String version;
    private String format;
    private int command;
    private PeerSemanticTag sender;
    private STSet receivers;
    private String signature;
    
    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getEncyptedSessionKey() {
        return encyptedSessionKey;
    }

    public void setEncyptedSessionKey(String encyptedSessionKey) {
        this.encyptedSessionKey = encyptedSessionKey;
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

    public void setSender(PeerSemanticTag sender) {
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
