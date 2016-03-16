/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.asip.engine;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;

import java.util.List;

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
    
    private boolean encrypted;
    private String encyptedSessionKey;
    private String version;
    private String format;
    private int command;
    private STSet senders;
    private STSet receivers;
    private String signature;

    protected ASIPMessage(/*alle params*/){

    }

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

    public STSet getSenders() {
        return senders;
    }

    public void setSenders(STSet senders) {
        this.senders = senders;
    }

    public void addSender(SemanticTag sender){

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
