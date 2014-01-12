/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ROSemanticTag;

/**
 *
 * @author Romy Gerlach
 */
public class PrivateMessageWrite extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Send", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    Vector members = new Vector();
    TextField messagefeld = new TextField("Message", "", 2000, TextField.ANY);

    public PrivateMessageWrite(FormHandler handler, J2MEISpherePeer peer, Vector members) {
        super("write PN");
        this.handler = handler;
        this.peer = peer;
        this.members = members;
        
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);

        String receiver = "Receiver: ";
        for (int i = 0; i < members.size(); i++) {
            ROSemanticTag name = (ROSemanticTag) members.elementAt(i);
            receiver += name.getName() + ", ";

        }
        this.append(receiver);
        String space = "\n\n";
        this.append(space);
        this.append(messagefeld);
        
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.OK:
                for (int i = 0; i < members.size(); i++) {
                    PeerAssociatedSemanticTag empf = (PeerAssociatedSemanticTag) members.elementAt(i);
                    peer.sendPrivateMessage(messagefeld.getString(), empf);
                }
                //peer.publishAllKp();
                handler.switchForm(MEISphere.FORM_CONTACTSMANAGE, peer);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;

        }

    }
}
