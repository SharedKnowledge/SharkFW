/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.isphere.ui;

import de.isphere.knowledgeBase.impl.ISphereKBListener;
import de.isphere.peer.J2MEISpherePeer;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.ROSemanticTag;

/**
 *
 * @author Romy Gerlach
 */
public class J2MEISphereKBListener implements ISphereKBListener{
    J2MEISpherePeer peer = null;
    private FormHandler handler;

    public J2MEISphereKBListener(FormHandler handler, J2MEISpherePeer peer) {
        this.handler = handler;
        this.peer = peer;
    }

    public void topicAdded(ROSemanticTag topic) {
        Alert newtopic = new Alert("New Topic","New Topic learned", null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, peer, newtopic);
    }

    public void peerAdded(ROSemanticTag peer) {
         Alert newpeer = new Alert("New Peer added","New Peer added", null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newpeer);
    }

    public void privateMessageReceived(ROSemanticTag sender, String message) {
        Alert newpm = new Alert("New Private Message","New Private Message", null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newpm);
    }

    public void statusUpdated(ROSemanticTag peer, String status) {
        Alert newstatus = new Alert("New Status","New Status"  + " by " + peer.getName()+ " " + status, null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newstatus);
    }

    public void birthdateUpdated(ROSemanticTag peer, String birthdate) {
        Alert newbirth = new Alert("New Profil Info","Added Birthdate" + " by " + peer.getName()+ " " + birthdate , null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newbirth);
    }

    public void genderUpdated(ROSemanticTag peer, String gender) {
        Alert newgender = new Alert("New Profil Info","Added Gender"  + " by " + peer.getName()+ " " + gender, null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newgender);
    }

    public void emailAddressUpdated(ROSemanticTag peer, String emailAddress) {
        Alert newemail = new Alert("New Profil Info","Added Birthdate"  + " by " + peer.getName() + " " + emailAddress, null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newemail);
    }

    public void profilePictureUpdated(ROSemanticTag peer) {
        Alert newinfo = new Alert("New Picture","New Picture by " + peer.getName() + " please checked on pc", null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newinfo);
    }

    public void infoAddedForTopic(Information info, ROSemanticTag peer, ROSemanticTag topic) {
        Alert newinfo = new Alert("New Info by one topic","New Info by one topic, please checked on pc", null, AlertType.ERROR);
        handler.switchFormWithAlert(MEISphere.FORM_MENU, this.peer, newinfo);
    }



}
