/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import java.security.Key;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

/**
 *
 * @author Romy Gerlach
 */
public class ProfilView extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;

    public ProfilView(FormHandler handler, J2MEISpherePeer peer) {
        super("Profil");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandMenu);
        this.setCommandListener(this);
        String nameView = "Name: \n" + peer.getSharkKB().getOwner().getName() + "\n";
        String siView = "personal Homepage: \n" + peer.getSharkKB().getOwner().getSI()[0] + "\n";
        String dateView = "Birthdate: \n" + peer.getBirthdate() + "\n";
        String emailView = "Email: \n" + peer.getEmailAddress()  + "\n";
        String genderView = "Gender: \n" + peer.getGender() + "\n";
        this.append(nameView);
        this.append(dateView);
        this.append(emailView);
        this.append(genderView);
        this.append(siView);
    }

    public int append(String str) {
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
