/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;

/**
 *
 * @author Romy Gerlach
 *
 * In this form can the user add a contact to a group. At first it will be
 * pumped from the knowledgebase of all existing members and groups and than
 * it will be displayed.
 * The user can hook one ore more member to one ore more groups. Then he can
 * save the entry with the menu button and hook save.
 */
public class ContactsAdd extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    Vector groups;
    private Vector memberListeV = new Vector();
    private Vector groupsListeV = new Vector();
    private ChoiceGroup cgGroups = new ChoiceGroup("Group", ChoiceGroup.MULTIPLE);
    private ChoiceGroup cgMember = new ChoiceGroup("Member", ChoiceGroup.MULTIPLE);

    public ContactsAdd(FormHandler handler, J2MEISpherePeer peer) throws SharkKBException, SharkNotSupportedException {
        super("Contact add in group");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandOkay);
        this.setCommandListener(this);
        this.addCommand(commandExit);
        this.addCommand(commandMenu);


        groups = peer.getAllGroups();
        
        for (int k = 0; k < groups.size(); k++) {
            PeerAssociatedSemanticTag tag2 = (PeerAssociatedSemanticTag) groups.elementAt(k);
            String gr = tag2.getName();
            groupsListeV.add(tag2);
            cgGroups.append(gr, null);
        }

        Enumeration membere = peer.getISphereKB().getSTSet(ContextSpace.DIM_REMOTEPEER).tags();
        while (membere != null && membere.hasMoreElements()) {
            ROSemanticTag memberrst = (ROSemanticTag) membere.nextElement();

            if (memberrst.getProperty(ISphereKB.GROUP) == null && !Util.sameEntity(memberrst.getSI(), peer.getOwner().getSI())) {
                memberListeV.add(memberrst);
                cgMember.append(memberrst.getName(), null);
            }
        }
        this.append(cgMember);
        this.append(cgGroups);

        for (int i = 0; i < groups.size(); i++) {
            String groupsS = "Group: ";
            String member = "Member:\n";
            PeerAssociatedSemanticTag tag = (PeerAssociatedSemanticTag) groups.elementAt(i);
            groupsS += tag.getName() + "\n";
            this.append(groupsS);
            Vector membersfromgroup = peer.getGroupMembers(tag);
            for (int j = 0; j < membersfromgroup.size(); j++) {
                PeerAssociatedSemanticTag tagm = (PeerAssociatedSemanticTag) membersfromgroup.elementAt(j);

                if (tagm.getProperty(ISphereKB.GROUP) == null) {
                    member += " - " + tagm.getName() + "\n";

                }
            }
            this.append(member);
        }
    }

    public int append(String str) {
        return super.append(str);
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.OK:
                Vector memberV = new Vector();
                Vector groupsV = new Vector();

                for (int i = 0; i < cgMember.size(); i++) {
                    if (cgMember.isSelected(i)) {
                        memberV.add(memberListeV.elementAt(i));
                    }
                }
                for (int i = 0; i < cgGroups.size(); i++) {
                    if (cgGroups.isSelected(i)) {
                        groupsV.add(groupsListeV.elementAt(i));
                    }
                }
                peer.addMembersToGroup(memberV, groupsV);
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;
        }


    }
}
