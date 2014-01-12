/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 *
 * @author Romy Gerlach
 */
public class Options extends List implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Okay", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    public static final int FORM_START = 0;
    public static final int FORM_MENU = 1;
    public static final int FORM_PROFILECREATE = 2;
    public static final int FORM_PROFILEVIEW = 3;
    public static final int FORM_SYNCHRONIZE = 4;
    public static final int FORM_CONTACTSADD = 5;
    public static final int FORM_CONTACTSMANAGE = 6;
    public static final int FORM_INTERESTADD = 7;
    public static final int FORM_INTERESTSMANAGE = 8;
    public static final int FORM_TOPICADD = 9;
    public static final int FORM_TOPICSMANAGE = 10;
    public static final int FORM_PNWRITE = 11;
    public static final int FORM_PNVIEW = 12;
    public static final int FORM_PROFILPEERS = 13;
    public static final int FORM_RELAIS = 14;
    public static final int FORM_IPADDRESS = 15;
    public static final int FORM_OPTIONS = 16;
    public static final int FORM_VISIBILITY = 17;
    public static final int EXIT = -42;
    J2MEISpherePeer peer = null;
    int menu = 50;
    private Command commandMenu = new Command("Menu", Command.BACK, 1);

    public Options(FormHandler handler, J2MEISpherePeer peer) throws IOException {
        super("Options Menu", IMPLICIT);
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandMenu);
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.setCommandListener(this);
        //Image startseiteI = Image.createImage("/de/isphere/ui/startseite.png");
        this.append("Relaisaddress", null);
        //Image startseiteI = Image.createImage("/de/isphere/ui/startseite.png");
        this.append("IP-Address", null);
        this.append("Visibility", null);
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;
            case Command.OK:
                switch (this.getSelectedIndex()) {
                    case 0:
                        handler.switchForm(MEISphere.FORM_RELAIS, peer);
                        break;
                    case 1:
                        handler.switchForm(MEISphere.FORM_IPADDRESS, peer);
                        break;
                    case 2:
                        handler.switchForm(MEISphere.FORM_VISIBILITY, peer);
                        break;
                    
                }

        }

    }
}
