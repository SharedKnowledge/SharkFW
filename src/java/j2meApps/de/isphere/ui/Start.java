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
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author Romy Gerlach
 */
public class Start extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("save Status", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    private Command commandPublish = new Command("Publish all", Command.HELP, 1);
    private Command commandStatus = new Command("set new Status", Command.ITEM, 1);
    private Command commandSynchronize = new Command("synchronize", Command.SCREEN, 1);
    J2MEISpherePeer peer = null;
    TextField statusTF = new TextField("Statusmessage: ", "", 500, TextField.ANY);
    boolean newstatus= false;


     public Start(FormHandler handler, J2MEISpherePeer peer) {
        super("Start");
        this.handler = handler;
        this.peer = peer;
                try {
            Image avatar = Image.createImage("/de/isphere/ui/female.png");
            ImageItem avatarItem = new ImageItem("Avatar\n", avatar, ImageItem.LAYOUT_DEFAULT, "<Missing Image>");
            this.insert(0, avatarItem);
            String space = "\n\n";
            this.append(space);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.addCommand(commandExit);
        
        this.addCommand(commandMenu);
        this.addCommand(commandPublish);
        this.addCommand(commandSynchronize);
        
        this.setCommandListener(this);
         if(peer.getSharkKB().getOwner()!=null)
        {
        if(peer.getStatus() != null){
            String statusS = "current status: " + peer.getStatus();
            this.append(statusS);
            this.addCommand(commandStatus);
        }else{
            this.append(statusTF);
            this.addCommand(commandOkay);
            this.newstatus = true;
        }
        }else{
            String statusS = "Please create at first the owner : Menu/profile create";
            this.append(statusS);
        }
        
    }

    public Start(FormHandler handler, J2MEISpherePeer peer, boolean newstatus) {
        super("Start");
        this.handler = handler;
        this.peer = peer;
        this.newstatus = newstatus;
        try {
            Image avatar = Image.createImage("/de/isphere/ui/weiblich.png");
            ImageItem avatarItem = new ImageItem("Avatar\n", avatar, ImageItem.LAYOUT_DEFAULT, "<Missing Image>");
            this.insert(0, avatarItem);
            String space = "\n\n";
            this.append(space);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.addCommand(commandExit);
        this.addCommand(commandMenu);
        this.addCommand(commandSynchronize);
        this.addCommand(commandPublish);
        
        this.setCommandListener(this);

        if(peer.getSharkKB().getOwner()!=null)
        {
        if(peer.getStatus() != null){
            String statusS = "current status: " + peer.getStatus();
            this.append(statusS);
           
        }
        
        if(newstatus){
            this.append(statusTF);
            this.addCommand(commandOkay);
        }
        }else{
            String statusS = "Please create at first the owner : Menu/profile create";
            this.append(statusS);
        }

        


    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_MENU, peer);
                break;
            case Command.SCREEN:
                handler.switchForm(MEISphere.FORM_SYNCHRONIZE, peer);
                break;
              case Command.HELP:
                peer.publishAllKp();
                handler.switchForm(MEISphere.FORM_START, peer);
                break;
            case Command.OK:
                if(newstatus){
                    peer.setStatus(statusTF.getString());
                    newstatus = false;
                }
                handler.switchForm(MEISphere.FORM_START, peer);
                break;
            case Command.ITEM:
                this.newstatus = true;
                handler.switchtoStart(MEISphere.FORM_START, peer, newstatus);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;


        }


    }
}
