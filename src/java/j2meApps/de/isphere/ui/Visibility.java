/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/**
 *
 * @author Romy Gerlach
 */
public class Visibility extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;
    private ChoiceGroup visibleCG = new ChoiceGroup("You are visible?", ChoiceGroup.MULTIPLE);

    public Visibility(FormHandler handler, J2MEISpherePeer peer) {
        super("Visibility");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);

        if (peer.isVisible()) {
            visibleCG.append("visible for all", null);
            visibleCG.setSelectedIndex(0, true);
        } else {
            visibleCG.append("invisible for all, turning-on?", null);
        }
        this.append(visibleCG);


    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_OPTIONS, peer);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;

            case Command.OK:

                if(visibleCG.isSelected(0)){
                    if(peer.isVisible()==false){
                        peer.setVisibility(true, 5566);
                    }
                }
                
                if(!visibleCG.isSelected(0)){
                    if(peer.isVisible()==true){
                        peer.setVisibility(false);
                    }
                }

                handler.switchForm(MEISphere.FORM_VISIBILITY, peer);
                break;
        }
    }
}
