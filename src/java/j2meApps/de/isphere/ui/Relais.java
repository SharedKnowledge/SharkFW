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

/**
 *
 * @author Romy Gerlach
 */
public class Relais extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Options", Command.BACK, 1);
    private Command commandAddrChange = new Command("Relaisaddress change", Command.SCREEN, 1);
    private Command commandRelDelete = new Command("Relaisaddress delete", Command.ITEM, 1);
    J2MEISpherePeer peer = null;
    TextField addressesTF = null;
    String addresseA = null;
    String relaisaddress = "Relaisadresse: ";
    boolean ischangedAd = false;
    TextField newaddressesTF = null;
    boolean syncAddr = false;

    public Relais(FormHandler handler, J2MEISpherePeer peer) {
        super("Relaisaddress");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.addCommand(commandRelDelete);
        this.addCommand(commandAddrChange);
        this.setCommandListener(this);
        //PeerAssociatedSemanticTag owner = (PeerAssociatedSemanticTag) peer.getISphereKB().getOwner();

        addresseA = peer.getRelaisAddress();//owner.getAddresses();

        if (addresseA == null) {
            addressesTF = new TextField("Address", "please enter address", 500, TextField.ANY);
            ischangedAd = true;
            this.append(addressesTF);
        } else {
            this.append(relaisaddress + addresseA);

        }
    }

    public Relais(FormHandler handler, J2MEISpherePeer peer, boolean syncAddr) {
        super("Relaisaddress");
        this.handler = handler;
        this.peer = peer;
        this.syncAddr = syncAddr;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.addCommand(commandRelDelete);
        
        this.setCommandListener(this);
        //PeerAssociatedSemanticTag owner = (PeerAssociatedSemanticTag) peer.getISphereKB().getOwner();

        addresseA = peer.getRelaisAddress();//addressesA = owner.getAddresses();



        if (addresseA == null) {
            addressesTF = new TextField("Address", "please enter name", 500, TextField.ANY);
            ischangedAd = true;
            this.append(addressesTF);
        } else {

            this.append(relaisaddress + addresseA);
            this.addCommand(commandAddrChange);

        }
        this.append(relaisaddress);
        if (syncAddr) {
            newaddressesTF = new TextField("overwrite Relaisaddress: ", "", 500, TextField.ANY);
            this.append(newaddressesTF);




        }
    }

    public void commandAction(Command c, Displayable d) {
        switch (c.getCommandType()) {
            case Command.BACK:
                handler.switchForm(MEISphere.FORM_OPTIONS, peer);
                break;
            case Command.EXIT:
                handler.switchForm(MEISphere.EXIT, peer);
                break;
                case Command.ITEM://Addr. delete
                peer.setRelaisAddress(null);
                handler.switchForm(MEISphere.FORM_RELAIS, peer);
                break;
            case Command.SCREEN://Addr. set new
                this.syncAddr = true;
                handler.switchtoIPAdrOrRelais(MEISphere.FORM_RELAIS, peer, syncAddr);
                break;
            case Command.OK:

                //PeerAssociatedSTSet localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ContextSpace.DIM_PEER);

                if (ischangedAd == true) {
                    addresseA = new String(addressesTF.getString());
                }
                //PeerAssociatedSemanticTag ownertag = localpeers.createPeerAssociatedSemanticTag(peer.getOwner().getName(), peer.getOwner().getSI(), adressesA);

                if (ischangedAd) {
                    peer.setRelaisAddress(new String(addressesTF.getString()));
                }

                if (syncAddr) {
                    peer.setRelaisAddress(new String(newaddressesTF.getString()));
                    syncAddr = false;
                }
//                peer.publishAllKp();

                handler.switchForm(MEISphere.FORM_RELAIS, peer);
                break;
        }
    }
}
