/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import de.isphere.peer.impl.ISphereKP;
import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.LocalInterest;
import net.sharkfw.knowledgeBase.ROSTSet;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;
import net.sharkfw.wrapper.Vector;

/**
 *
 * @author Romy Gerlach
 */
public class InterestsManage extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    Vector allInterestsVLI = new Vector();
    private boolean active = false;
    boolean interestActiveB = false;
    Enumeration allInterestsE = null;
    Vector allpeer = null;
    Vector cgsforinterests = new Vector();

    public InterestsManage(FormHandler handler, J2MEISpherePeer peer) throws SharkKBException, SharkNotSupportedException {
        super("Interests manage");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);
        allInterestsE = peer.getISphereKB().interests();
        allpeer = new Vector(peer.getKPs());
        //peer.publishAllKp();

        while (allInterestsE != null && allInterestsE.hasMoreElements()) {
            LocalInterest li = (LocalInterest) allInterestsE.nextElement();
            allInterestsVLI.add(li);
        }
        if (allInterestsVLI != null) {
            for (int i = 0; i < allInterestsVLI.size(); i++) {
                Vector allTopicsNamesFromOneInterestV = new Vector();

                Vector inOutFromOneInterestV = new Vector();
                Vector allInterestinWBV = new Vector();

                LocalInterest lii = (LocalInterest) allInterestsVLI.elementAt(i);
                ChoiceGroup interestActiveCG = new ChoiceGroup("Interest active?", ChoiceGroup.MULTIPLE);
                cgsforinterests.add(interestActiveCG);

                for (int m = 0; m < allpeer.size(); m++) {
                    KnowledgePort kp = (KnowledgePort) allpeer.elementAt(m);
                    if (lii != null && kp.getInterest().getID() != null) {
                        if (lii.getID().equals(kp.getInterest().getID())) {
                            interestActiveB = true;
                        }
                    }
                }

                STSet topicssts = (STSet) lii.getSTSet(ContextSpace.DIM_TOPIC);
                Enumeration topicsE = topicssts.tags();
                while (topicsE != null && topicsE.hasMoreElements()) {
                    ROSemanticTag topicsrst = (ROSemanticTag) topicsE.nextElement();
                    allTopicsNamesFromOneInterestV.add(topicsrst.getName());
                }
                ROSTSet remotepeersts = lii.getSTSet(ContextSpace.DIM_REMOTEPEER);
                Enumeration rpE = remotepeersts.tags();
                String remotePeerS = "Receiver: ";
                while (rpE != null && rpE.hasMoreElements()) {
                    ROSemanticTag rprst = (ROSemanticTag) rpE.nextElement();
                    remotePeerS += rprst.getName() + ", ";
                }
                remotePeerS += "\n";
                ROSTSet inout = lii.getSTSet(ContextSpace.DIM_DIRECTION);
                Enumeration inoutE = inout.tags();
                while (inoutE != null && inoutE.hasMoreElements()) {
                    ROSemanticTag inoutrst = (ROSemanticTag) inoutE.nextElement();

                    if (Util.sameEntity(inoutrst.getSI(), ContextSpace.OUTSI)) {
                        inOutFromOneInterestV.add("Out");
                    }
                    if (Util.sameEntity(inoutrst.getSI(), ContextSpace.INSI)) {
                        inOutFromOneInterestV.add("In");
                    }
                }
                Enumeration interestsInWBE = peer.getKPs();
                while (interestsInWBE != null && interestsInWBE.hasMoreElements()) {
                    KnowledgePort interestsInWBrst = (KnowledgePort) interestsInWBE.nextElement();

                    allInterestinWBV.add(interestsInWBrst.getInterest().getID());
                }
                for (int m = 0; m < allInterestinWBV.size(); m++) {
                    if (allInterestinWBV.equals(lii.getID())) {
                        active = true;
                    }
                }
                String topicsS = "Topics: ";
                for (int k = 0; k < allTopicsNamesFromOneInterestV.size(); k++) {
                    topicsS += (String) allTopicsNamesFromOneInterestV.elementAt(k) + ", ";
                }
                this.append(topicsS + "\n");

                this.append(remotePeerS);
                String inoutS = "send and/or receive: ";
                if (inOutFromOneInterestV.size() < 2) {
                    inoutS += inOutFromOneInterestV.elementAt(0);
                    inoutS += "\n";
                } else {
                    inoutS += inOutFromOneInterestV.elementAt(0) + " and ";
                    inoutS += inOutFromOneInterestV.elementAt(1) + "\n";
                }
                this.append(inoutS);
                ChoiceGroup agiia = (ChoiceGroup) cgsforinterests.elementAt(i);
                if (interestActiveB) {

                    agiia.append("yes", null);
                    agiia.setSelectedIndex(0, interestActiveB);

                } else {
                    agiia.append("no, want activate?", null);
                }
                this.append(agiia);

                String s = "\n\n";
                this.append(s);
                
            }
        }
        //Enumeration allpeer = peer.getKPs();
    }

    public int append(String str) {
        return super.append(str);
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.OK:
                for (int i = 0; i < allInterestsVLI.size(); i++) {
                    LocalInterest lii = (LocalInterest) allInterestsVLI.elementAt(i);
                    boolean b = false;
                    ChoiceGroup cgiia = (ChoiceGroup) cgsforinterests.elementAt(i);
                    for (int j = 0; j < allpeer.size(); j++) {
                        KnowledgePort kp = (KnowledgePort) allpeer.elementAt(j);

                        if (lii != null && kp.getInterest().getID() != null) {
                            if (lii.getID().equals(kp.getInterest().getID())) {


                                if (!cgiia.isSelected(0)) {
                                    //KP delete
                                    peer.deleteKP(kp);
                                    b = true;
                                }
                            }

                        }

                    }
                    if (b == false) {
                        if (cgiia.isSelected(0)) {
                            //KP create
                            ISphereKP interesttokp = new ISphereKP(peer, lii);
                        }
                    }
                }


                //peer.publishAllKp();
                handler.switchForm(MEISphere.FORM_INTERESTSMANAGE, peer);
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
