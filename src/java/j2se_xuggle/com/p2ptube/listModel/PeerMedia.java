/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.p2ptube.listModel;

import com.p2ptube.kb.P2PTubeKB;
import com.p2ptube.peer.P2PTubePeer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.DefaultListModel;

/**
 *
 * @author RW
 */
public class PeerMedia extends DefaultListModel {

    private P2PTubePeer peer;
    private String remotePeerSI;
    private Hashtable mediaInfo;

    private String type = P2PTubeKB.MEDIA_TYPE;

    public PeerMedia() {
    }

    public void setPeer(P2PTubePeer peer) {
        this.peer = peer;
    }

    public void setRemotePeerSI(String remotePeerSI) {
        this.remotePeerSI = remotePeerSI;
        this.peer.getEngine().getAllMediaFromRemotePeer(remotePeerSI);
        System.out.println(this.remotePeerSI);
    }

    public PeerMedia(P2PTubePeer peer) {
        if (peer != null) {
            this.peer = peer;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public void updateInfo() {
        this.clear();

        System.out.println("Searching...");
        this.mediaInfo = peer.getEngine().getAllMediaFromSpecificPeer(remotePeerSI, type);
        Enumeration mediaKeys = this.mediaInfo.keys();
        //for (int i = 0; i < mediaInfo.size(); i++) {
        while(mediaKeys.hasMoreElements()) {
            Hashtable mediaInfoTable = (Hashtable) mediaInfo.get(mediaKeys.nextElement());
            /*String name = (String) mediaInfoTable.get("name");
            String size = (String) mediaInfoTable.get("size");
            String toAdd = name + " - " + (Integer.parseInt(size) / 1024) + "Kb";*/
            ElementInList toAdd = new ElementInList(mediaInfoTable);
            this.addElement(toAdd);
        }
        /*if (mediaInfo.size() == 0) {
            System.out.println("Could not find any media from the given peer...");
        }*/
    }
}
