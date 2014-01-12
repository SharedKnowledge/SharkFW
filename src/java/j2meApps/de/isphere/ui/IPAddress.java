/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;


import de.isphere.peer.J2MEISpherePeer;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.Util;

/**
 *
 * @author Romy Gerlach
 */
public class IPAddress extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Save", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    
    private Command commandAddrAdd = new Command("ident. IP-Address add", Command.SCREEN, 1);
    J2MEISpherePeer peer = null;
    
    
    TextField addressesTF = null;
    
    String[] addressesA = null;
    private ChoiceGroup masterCG = new ChoiceGroup("IP-Addresses: ", ChoiceGroup.POPUP);
    
    boolean ischangedAd = false;
    
    TextField newaddressesTF = null;
    private Vector masteraddressesV = new Vector();
    boolean syncAddr = false;
    

    public IPAddress(FormHandler handler, J2MEISpherePeer peer) {
        super("IPAddress");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        
        this.addCommand(commandAddrAdd);
        this.setCommandListener(this);
        PeerAssociatedSemanticTag owner = (PeerAssociatedSemanticTag) peer.getISphereKB().getOwner();
        
        addressesA = owner.getAddresses();
        if (addressesA == null) {
            addressesTF = new TextField("Adresse", "bitte Name eintragen", 500, TextField.ANY);
            ischangedAd = true;
            this.append(addressesTF);
        } else {
            for (int i = 0; i < addressesA.length; i++) {
                masterCG.append(addressesA[i], null);
                masteraddressesV.add(addressesA[i]);
            }
            this.append(masterCG);

        }
    }

    public IPAddress(FormHandler handler, J2MEISpherePeer peer,  boolean syncAddr) {
        super("IPAddress");
        this.handler = handler;
        this.peer = peer;
        this.syncAddr = syncAddr;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        this.setCommandListener(this);
        PeerAssociatedSemanticTag owner = (PeerAssociatedSemanticTag) peer.getISphereKB().getOwner();
        
        addressesA = owner.getAddresses();
        if (addressesA == null) {
            addressesTF = new TextField("Address: ", "please enter your address", 500, TextField.ANY);
            ischangedAd = true;
            this.append(addressesTF);
        } else {
            for (int i = 0; i < addressesA.length; i++) {
                masterCG.append(addressesA[i], null);
                masteraddressesV.add(addressesA[i]);
            }
            this.append(masterCG);
            if (syncAddr) {
                newaddressesTF = new TextField("more addresses add: ", "", 500, TextField.ANY);
                this.append(newaddressesTF);
                
            }

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
            
            case Command.SCREEN://Addr. add
                this.syncAddr = true;
                handler.switchtoIPAdrOrRelais(MEISphere.FORM_IPADDRESS, peer, syncAddr);
                break;
            case Command.OK:
                try {
                    PeerAssociatedSTSet localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ContextSpace.DIM_PEER);
                    
                    if (ischangedAd == true) {
                        addressesA = new String[]{addressesTF.getString()};
                    }
                    PeerAssociatedSemanticTag ownertag = localpeers.createPeerAssociatedSemanticTag(peer.getISphereKB().getOwner().getName(), peer.getISphereKB().getOwner().getSI(), addressesA);
                    
                    if (ischangedAd) {
                        ownertag.setAddresses(new String[]{addressesTF.getString()});
                    }
                    
                    if(syncAddr){
                        String[] addressesS = Util.addString(ownertag.getAddresses(), newaddressesTF.getString());
                        ownertag.setAddresses(addressesS);
                        syncAddr = false;
                    }
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }
                
                              
                handler.switchForm(MEISphere.FORM_IPADDRESS, peer);
                break;
        }
    }
}
