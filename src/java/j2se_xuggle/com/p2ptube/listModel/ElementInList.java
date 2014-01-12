/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.p2ptube.listModel;

import java.util.Hashtable;

/**
 *
 * @author RW
 */
public class ElementInList {

    private String name;
    private String size;
    private String originator;
    private String remotePeers;

    public ElementInList(String name, String size, String originator, String remotePeers) {
        this.name = name;
        this.size = size;
        this.originator = originator;
        this.remotePeers = remotePeers;
    }

    public ElementInList(Hashtable info) {
        this.name = (String) info.get("name");
        this.size = (String) info.get("size");
        this.originator = (String) info.get("originator");
        this.remotePeers = (String) info.get("peers");
    }

    public String getName() {
        return name;
    }

    public String getOriginator() {
        return originator;
    }

    public String getRemotePeers() {
        return remotePeers;
    }

    public String getSize() {
        return size;
    }

    public String toString() {
        return name + " (" + (Integer.parseInt(size)/1024) + "KB) [" + originator.substring(originator.lastIndexOf("/")) + "]";
    }
}
