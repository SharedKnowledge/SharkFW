/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.wasp;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 *
 * @author micha, thsc
 */
public abstract class LASPMessage {
    public static final int LASP_EXPOSE = 0;
    public static final int LASP_INSERT = 1;
    public static final int LASP_RAW = 2;
    
    private boolean encrypted;
    private String encyptedSessionKey;
    private String version;
    private String format;
    private int command;
    private String senderInfo;
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

    public String getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
