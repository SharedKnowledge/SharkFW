    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import java.util.Enumeration;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;
import net.sharkfw.wrapper.Vector;

/**
 *
 * @author Romy Gerlach
 */
public class ContactsManage extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save group", Command.OK, 1);
    private Command commandPN = new Command("write private message", Command.ITEM, 1);
    private Command commandProfil = new Command("see profil details", Command.SCREEN, 1);
    private Command commandPeerDelete = new Command("delete person", Command.HELP, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    private ChoiceGroup cgMember = new ChoiceGroup("Member", ChoiceGroup.MULTIPLE);
    Vector memberListeV = new Vector();
    private TextField newGroup;
    Vector groups = new Vector();
    Vector membersfromgroup = new Vector();
    J2MEISpherePeer peer = null;

    public ContactsManage(FormHandler handler, J2MEISpherePeer peer) throws SharkKBException, SharkNotSupportedException {
        super("Contacts manage");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.addCommand(commandPN);
        this.addCommand(commandProfil);
        this.addCommand(commandPeerDelete);
        this.setCommandListener(this);
        //peer.publishAllKp();
        
            
        //String nameAnzeige = "Name: \n" + peer.getSharkKB().getOwner().getID();

        Enumeration membere = peer.getISphereKB().getSTSet(ContextSpace.DIM_REMOTEPEER).tags();
        while (membere != null && membere.hasMoreElements()) {
            ROSemanticTag memberrst = (ROSemanticTag) membere.nextElement();
            
            PeerAssociatedSemanticTag memberpasts = (PeerAssociatedSemanticTag) memberrst;
            Enumeration groupsE = memberpasts.getAssociatedTags(ISphereKB.PRED_GROUP_MEMBER);
            String groupS = " is in group/s: ";
            while (groupsE != null && groupsE.hasMoreElements()) {
                ROSemanticTag gruppenrst = (ROSemanticTag) groupsE.nextElement();
                groupS += gruppenrst.getName() + ", ";
            }

            if(memberpasts.getProperty(ISphereKB.GROUP)== null && !Util.sameEntity(memberpasts.getSI(), peer.getOwner().getSI()))
            {
            cgMember.append(memberrst.getName() + groupS, null);
            memberListeV.add(memberrst);
            }
        }
        this.append(cgMember);

        newGroup = new TextField("create new group", "groupname", 500, TextField.ANY);
        this.append(newGroup);

    }

    public int append(String str) {
        return super.append(str);
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.HELP://peer delete
                Vector memberVpl = new Vector();

                for (int i = 0; i < cgMember.size(); i++) {
                    if (cgMember.isSelected(i)) {
                        memberVpl.add(memberListeV.elementAt(i));
                    }
                }
                if (memberVpl.size() > 0) {
                    for (int i = 0; i < memberVpl.size(); i++) {
                    PeerAssociatedSemanticTag cur = (PeerAssociatedSemanticTag) memberVpl.elementAt(i);
                    peer.getISphereKB().deletePeerTag(cur);
                    }
                }else{
                    Alert nocontactselect = new Alert("No contact select","No contact select", null, AlertType.ERROR);
                    handler.switchFormWithAlert(MEISphere.FORM_CONTACTSMANAGE, peer, nocontactselect);
                }
                handler.switchForm(MEISphere.FORM_CONTACTSMANAGE, peer);
                break;
            case Command.OK:

                peer.createGroup(newGroup.getString(), new String[]{"http://www." + newGroup + ".de"});

                handler.switchForm(MEISphere.FORM_CONTACTSADD, peer);
                break;
            case Command.ITEM://PN
                Vector memberV = new Vector();

                for (int i = 0; i < cgMember.size(); i++) {
                    if (cgMember.isSelected(i)) {
                        memberV.add(memberListeV.elementAt(i));
                    }
                }
                if (memberV.size() > 0) {
                    handler.switchtoPNOrProfilPeers(MEISphere.FORM_PNWRITE, peer, memberV);
                    break;
                }
            case Command.SCREEN: //Profil
                Vector memberVe = new Vector();

                for (int i = 0; i < cgMember.size(); i++) {
                    if (cgMember.isSelected(i)) {
                        memberVe.add(memberListeV.elementAt(i));

                    }
                }
                if (memberVe.size() > 0) {
                    handler.switchtoPNOrProfilPeers(MEISphere.FORM_PROFILPEERS, peer, memberVe);
                }
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;
        }
    }
}
