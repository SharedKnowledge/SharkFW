/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.p2ptube.listModel;

import com.p2ptube.peer.P2PTubePeer;
import com.p2ptube.kb.P2PTubeKB;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 *
 * @author RW
 */
public class ConnectedPeers extends DefaultListModel {

    P2PTubePeer peer;

    public ConnectedPeers(P2PTubePeer peer) {
        if (peer != null) {
            this.peer = peer;
            updateInfo();
        }
    }

    private void updateInfo() {
        this.removeAllElements();
        if (peer == null) {
            return;
        }
        PeerSTSet pst = null;
        try {
            pst = (PeerSTSet) peer.getKB().getSTSet(ContextSpace.DIM_ORIGINATOR);
        } catch (SharkKBException ex) {
            L.e(ex.getMessage(), this);
        }
        int i = 0;
        Enumeration enumer = pst.getAllSI();
        while (enumer.hasMoreElements()) {
            try {
                PeerSemanticTag pstag = pst.getPeerSemanticTag((String[]) enumer.nextElement());
                String name = pstag.getName();
                PeerInList pil = new PeerInList(name, pstag.getSI()[0]);
                this.addElement(pil);
                //createKP(pstag.getSI()[0]);
            } catch (SharkKBException ex) {
                L.e(ex.getMessage(), this);
            }
            i++;
        }
    }

    public void setPeer(P2PTubePeer peer) {
        this.peer = peer;
        this.updateInfo();
    }

    public void addNewPeer(String[] peerInfo) {
        if (peer == null) {
            return;
        }
        PeerSTSet pst = null;
        try {
            pst = (PeerSTSet) peer.getKB().getSTSet(ContextSpace.DIM_REMOTEPEER);
            pst.createPeerSemanticTag(peerInfo[1], new String[]{P2PTubeKB.PEER_URL + peerInfo[1]}, new String[]{"socket://" + peerInfo[0]});
            //Is it needed?
            pst = (PeerSTSet) peer.getKB().getSTSet(ContextSpace.DIM_ORIGINATOR);
            pst.createPeerSemanticTag(peerInfo[1], new String[]{P2PTubeKB.PEER_URL + peerInfo[1]}, new String[]{"socket://" + peerInfo[0]});
            System.out.println("Added: " + peerInfo[0]);
        } catch (SharkKBException ex) {
        }
        this.peer.getEngine().getAllMediaFromRemotePeer(P2PTubeKB.PEER_URL + peerInfo[1]);
        PeerInList pil = new PeerInList(peerInfo[1], P2PTubeKB.PEER_URL + peerInfo[1]);
        this.addElement(pil);
    }
}
