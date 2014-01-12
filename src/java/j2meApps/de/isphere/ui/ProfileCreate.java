/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;


import de.isphere.peer.J2MEISpherePeer;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author Romy Gerlach
 */
public class ProfileCreate extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    private TextField name = null;
    private TextField si = null;
    private TextField email;
    private TextField date;
    private TextField gender;
    J2MEISpherePeer peer = null;
    String[] si2 = new String[]{""};

    public ProfileCreate(FormHandler handler, J2MEISpherePeer peer) {
        super("Profile create/change");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);


        if (peer.getSharkKB().getOwner()== null || peer.getSharkKB().getOwner().getName()== null) {
            name = new TextField("Name", "Maxi Mustermann", 500, TextField.ANY);
        } else {
            name = new TextField("Name", peer.getSharkKB().getOwner().getName(), 500, TextField.ANY);
        }
        this.append(name);
        if (peer.getSharkKB().getOwner()== null || peer.getSharkKB().getOwner().getSI()[0] == null) {
            si = new TextField("personal Homepage", "www.mysite.de", 500, TextField.ANY);
        } else {
            si = new TextField("personal Homepage", peer.getSharkKB().getOwner().getSI()[0], 500, TextField.ANY);
        }
        this.append(si);




        if (peer.getSharkKB().getOwner()== null || peer.getBirthdate() == null) {
            date = new TextField("Birthdae", "please enter here", 500, TextField.ANY);
        } else {
            date = new TextField("Birthdate", peer.getBirthdate(), 500, TextField.ANY);
        }
        this.append(date);

        if (peer.getSharkKB().getOwner()== null || peer.getEmailAddress() == null) {
            email = new TextField("Email", "please@enter.de", 500, TextField.ANY);
        } else {
            email = new TextField("Email", peer.getEmailAddress(), 500, TextField.ANY);
        }
        this.append(email);

        if (peer.getSharkKB().getOwner()== null || peer.getGender() == null) {
            gender = new TextField("Gender", "female", 500, TextField.ANY);
        } else {
            gender = new TextField("Gender", peer.getGender(), 500, TextField.ANY);
        }
        this.append(gender);






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
            case Command.OK:

                PeerAssociatedSTSet localpeers;
                try {
                    si2[0] = si.getString();
                    localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ContextSpace.DIM_PEER);
                    PeerAssociatedSemanticTag tag = localpeers.createPeerAssociatedSemanticTag(name.getString(), si2, new String[]{""});
                    peer.setOwner(tag);
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }

                
                peer.setBirthdate(date.getString());
                peer.setEmailAddress(email.getString());
                peer.setGender(gender.getString());
                //peer.publishAllKp();
                handler.switchForm(MEISphere.FORM_PROFILEVIEW, peer);
                break;
        }
    }
}
