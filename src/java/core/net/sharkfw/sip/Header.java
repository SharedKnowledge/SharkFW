/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.sip;

/**
 *
 * @author micha
 */
public class Header {
    
    private boolean encrypted;
    private String encyptedSessionKey;
    private String version;
    private String format;
    private String command;
    private String senderInfo;
    private String signature;
    
    public Header(){ }

    public Header(boolean encrypted, String encyptedSessionKey, String version, String format, String command, String senderInfo, String signature) {
        this.encrypted = encrypted;
        this.encyptedSessionKey = encyptedSessionKey;
        this.version = version;
        this.format = format;
        this.command = command;
        this.senderInfo = senderInfo;
        this.signature = signature;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
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
