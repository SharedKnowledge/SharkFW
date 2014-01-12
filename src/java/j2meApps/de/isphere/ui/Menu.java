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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 *
 * @author Romy Gerlach
 */
public class Menu extends List implements CommandListener {

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
    
    //private Command commandMenu = new Command("Menu", Command.BACK, 1);

    public Menu(FormHandler handler, J2MEISpherePeer peer) throws IOException {
        super("MenU", IMPLICIT);
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.setCommandListener(this);
        Image startseiteI = Image.createImage("/de/isphere/ui/start.png");
        this.append("Start", startseiteI);
         Image profilhinzuI = Image.createImage("/de/isphere/ui/profileadd.png");
        this.append("Profile create/change", profilhinzuI);
        Image profilI = Image.createImage("/de/isphere/ui/profile.png");
        this.append("Profile view", profilI);
        Image kontakteI = Image.createImage("/de/isphere/ui/contacts.png");
        this.append("Contacts manage", kontakteI);
        Image kontaktehinzuI = Image.createImage("/de/isphere/ui/contactsadd.png");
        this.append("Contacts add", kontaktehinzuI);
        Image interestshinzuI = Image.createImage("/de/isphere/ui/interestsadd.png");
        this.append("Interests add", interestshinzuI);
        Image interestsI = Image.createImage("/de/isphere/ui/interests.png");
        this.append("Interests manage", interestsI);
        //Image interestsI = Image.createImage("/de/isphere/ui/interests.png");
        this.append("Private Message view", null);
        this.append("Topics view", null);
        this.append("Options", null);
    }




    public void commandAction(Command c, Displayable d) {
        switch(this.getSelectedIndex()){
            case 0: handler.switchForm(MEISphere.FORM_START, peer); break;
            case 1: handler.switchForm(MEISphere.FORM_PROFILECREATE, peer); break;
            case 2: handler.switchForm(MEISphere.FORM_PROFILEVIEW, peer); break;
            case 3: handler.switchForm(MEISphere.FORM_CONTACTSMANAGE, peer); break;
            case 4: handler.switchForm(MEISphere.FORM_CONTACTSADD, peer); break;
            case 5: handler.switchForm(MEISphere.FORM_INTERESTADD, peer);break;
            case 6: handler.switchForm(MEISphere.FORM_INTERESTSMANAGE, peer);break;
            case 7: handler.switchForm(MEISphere.FORM_PNVIEW, peer);break;
            case 8: handler.switchForm(MEISphere.FORM_TOPICSMANAGE, peer);break;
            case 9: handler.switchForm(MEISphere.FORM_OPTIONS, peer);break;


        }
        switch (c.getCommandType()) {
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;

        }
    }
}
