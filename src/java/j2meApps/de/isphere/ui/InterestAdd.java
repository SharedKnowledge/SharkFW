/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
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
public class InterestAdd extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Interest create", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    private Vector memberListeV = new Vector();
    private Vector groupsListeV = new Vector();
    private ChoiceGroup inout = new ChoiceGroup("send/receive", ChoiceGroup.MULTIPLE);
    boolean in = false;
    boolean out = false;
    private ChoiceGroup cgTopic = new ChoiceGroup("Topic:", ChoiceGroup.POPUP);
    private ChoiceGroup cgGroups = new ChoiceGroup("Group", ChoiceGroup.MULTIPLE);
    private ChoiceGroup cgMember = new ChoiceGroup("Member", ChoiceGroup.MULTIPLE);
    Vector groups = new Vector();
    Vector topicsV = new Vector();

    public InterestAdd(FormHandler handler, J2MEISpherePeer peer) throws SharkNotSupportedException, SharkKBException {
        super("Interest add");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);
        try {
            //topic CG füllen mit allen vorhandenen Topics in WB
            Enumeration topicse = peer.getISphereKB().getSTSet(ContextSpace.DIM_TOPIC).tags();
            while (topicse != null && topicse.hasMoreElements()) {
                ROSemanticTag topicsrst = (ROSemanticTag) topicse.nextElement();
                topicsV.add(topicsrst);
                cgTopic.append(topicsrst.getName(), null);

            }
        } catch (SharkKBException ex) {
            ex.printStackTrace();
        }
        this.append(cgTopic);


        inout.append("send", null);
        inout.append("receive", null);
        this.append(inout);

        String text = "share with: \n";
        this.append(text);

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
    }

    public int append(String str) {
        return super.append(str);
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.OK:
                if (inout.isSelected(0)) {
                    //send
                    out = true;
                }
                if (inout.isSelected(1)) {
                    //receive
                    in = true;
                }

                Vector memberV = new Vector();
                Vector groupsV = new Vector();
                Vector atopicsV = new Vector();

                for (int i = 0; i < cgTopic.size(); i++) {
                    if (cgTopic.isSelected(i)) {
                        atopicsV.add(topicsV.elementAt(i));
                    }
                }
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
                if (memberV.size() > 0 && groupsV.size() == 0) {
                    peer.createInterest(out, in, atopicsV, memberV, false);
                }
                if (memberV.size() == 0 && groupsV.size() > 0) {
                    peer.createInterest(out, in, atopicsV, groupsV, true);
                }
                if (memberV.size() > 0 && groupsV.size() > 0) {
                    Vector memberswrV = new Vector();
                    for (int i = 0; i < groupsV.size(); i++) {
                        PeerAssociatedSemanticTag groupscur = (PeerAssociatedSemanticTag)groupsV.elementAt(i);
                        Vector membersfromgroupsV = peer.getGroupMembers(groupscur);
                        for (int j = 0; j < membersfromgroupsV.size(); j++) {
                        if(!memberswrV.contains(membersfromgroupsV.elementAt(j))){
                            membersfromgroupsV.elementAt(j);
                        }
                        }
                    }
                    for (int i = 0; i < memberV.size(); i++) {
                        if (!memberswrV.contains(memberV.elementAt(i))){
                            memberswrV.add(memberV.elementAt(i));
                        }
                    }
                    peer.createInterest(out, in, atopicsV, memberswrV, false);
                }
                //peer.publishAllKp();
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
