/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import java.util.Date;
import java.util.Hashtable;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;

/**
 *
 * @author Romy Gerlach
 */
public class PrivateMessageView extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    

    public PrivateMessageView(FormHandler handler, J2MEISpherePeer peer) throws SharkKBException, SharkNotSupportedException {
        super("PN anzeigen");
        this.handler = handler;
        this.peer = peer;

        this.addCommand(commandExit);
        this.addCommand(commandMenu);
        this.setCommandListener(this);
        //peer.publishAllKp();
        Vector messagesV = peer.getPrivateMessages(false); //all received
        String posteingang = "In-Box: \n";
        this.append(posteingang);

        Vector sortmessages = this.sorttodate(messagesV);
        this.showmessages(sortmessages/*messagesV */, false);

        Vector messagesV2 = peer.getPrivateMessages(true); //all sended
        String postausgang = "Out-Box: \n";
        this.append(postausgang);

        Vector sortmessages2 = this.sorttodate(messagesV2);
        this.showmessages( sortmessages2/*messagesV2*/, true);


    }

    private void showmessages(Vector sortmessages, boolean send) {

        for (int i = 0; i < sortmessages.size(); i++) {
            String onemessagerec = "Receiver: ";
            String onemessagesend = "Sender: ";
            String onemessage = "Message: \n";
            Hashtable omht = (Hashtable) sortmessages.elementAt(i);
            String rmsi = null;
            if (send == false) {
                rmsi = (String) omht.get("remotePeer");
            } else {
                rmsi = (String) omht.get("peer");
            }

            PeerAssociatedSTSet localpeers = null;
            try {
                if (send == false) {
                    localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ISphereKB.DIM_PEER);
                } else {
                    localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ISphereKB.DIM_REMOTEPEER);
                }
            } catch (SharkKBException ex) {
                ex.printStackTrace();
            }
            ROSemanticTag peerROST = null;
            try {
                peerROST = localpeers.getSemanticTag(rmsi);
            } catch (SharkKBException ex) {
                ex.printStackTrace();
            }

            Information messageI = (Information) omht.get("msg");
            String s = new String(messageI.getContentAsByte());
            String tt = "";
            try {
                tt = messageI.getProperty(ISphereKB.TIMESTAMP);
            } catch (SharkKBException ex) {
                ex.printStackTrace();
            }
            long ltt = Long.parseLong(tt);
            Date date = new Date(ltt);
            if (send == false) {
                onemessagerec += peerROST.getName();
                String datum = "Received: " + date.toString();
                this.append(onemessagerec + "\n");
                this.append(datum + "\n");
                this.append(onemessage + s + "\n\n");
            } else {
                onemessagesend += peerROST.getName();
                String dateS = "Sended: " + date.toString();
                this.append(onemessagesend +"\n");
                this.append(dateS + "\n");
                this.append(onemessage + s + "\n\n");
            }
        }
    }

    private Vector sorttodate(Vector messagesV) {
        Vector sortmessages = new Vector();
        Hashtable current = null;
        int c = 0;
        for (int j = 0; j < messagesV.size(); j++) {
            long biggest = 0;
            for (int i = 0; i < messagesV.size(); i++) {
                Hashtable omht = (Hashtable) messagesV.elementAt(i);
                Information messageI = (Information) omht.get("msg");
                String s = new String(messageI.getContentAsByte());
                String tt = "";
                try {
                    tt = messageI.getProperty(ISphereKB.TIMESTAMP);
                } catch (SharkKBException ex) {
                    L.e("TIMESTAMP nicht vorhanden", this);
                }
                long ltt = Long.parseLong(tt);
                if (ltt >= biggest) {
                    biggest = ltt ;
                    current = omht;
                    c = i;
                }
            }
            sortmessages.add(current);
            
        }
        return sortmessages;

    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;
        }

    }
}
