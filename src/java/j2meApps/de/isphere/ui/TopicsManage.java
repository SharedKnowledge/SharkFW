/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import java.util.Enumeration;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.ROAssociatedSTSet;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.wrapper.Vector;

/**
 *
 * @author Romy Gerlach
 */
public class TopicsManage extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;

    public TopicsManage(FormHandler handler, J2MEISpherePeer peer) throws SharkKBException, SharkNotSupportedException {
        super("Topics manage");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandMenu);
        this.setCommandListener(this);

        Enumeration topicse = peer.getISphereKB().getSTSet(ContextSpace.DIM_TOPIC).tags();
        Vector topicsV = new Vector(topicse);

        for (int i = 0; i < topicsV.size(); i++) {
            AssociatedSemanticTag topicsPAST = (AssociatedSemanticTag) topicsV.elementAt(i);

            Enumeration subE = topicsPAST.getAssociatedTags(ROAssociatedSTSet.SUBASSOC);
            Vector subV = new Vector(subE);
            Enumeration superE = topicsPAST.getAssociatedTags(ROAssociatedSTSet.SUPERASSOC);
            Vector superV = new Vector(superE);
            String topicnameS = "Topic: " + topicsPAST.getName() + "\n";
            this.append(topicnameS);
            String subS = "SubTopic: ";
            for (int j = 0; j < subV.size(); j++) {
                AssociatedSemanticTag subROAST = (AssociatedSemanticTag) subV.elementAt(i);
                subS += subROAST.getName() + ", ";
            }
            if (subV.size() > 0) {
                subS += "\n";
                this.append(subS);
            }

            String superS = "SuperTopic: ";
            for (int j = 0; j < superV.size(); j++) {
                AssociatedSemanticTag superROAST = (AssociatedSemanticTag) superV.elementAt(i);
                superS += superROAST.getName() + ", ";
            }
            superS += "\n\n";
            if (superV.size() > 0) {
                this.append(superS);
            }

        }
    }

    public int append(String str) {
        System.out.println(str);
        return super.append(str);
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
