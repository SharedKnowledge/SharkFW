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
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.ROPeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerAssociatedSTSet;

/**
 *
 * @author Romy Gerlach
 */
public class Synchronize extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandOkay = new Command("Synchronisieze with master", Command.OK, 1);
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    J2MEISpherePeer peer = null;    
    String[] addressesA = null;
    private ChoiceGroup masterCG = new ChoiceGroup("MASTER: ", ChoiceGroup.POPUP);
    private Vector masteraddressesV = new Vector();
    

    public Synchronize(FormHandler handler, J2MEISpherePeer peer) {
        super("Start");
        this.handler = handler;
        this.peer = peer;
        this.addCommand(commandExit);
        this.addCommand(commandOkay);
        this.addCommand(commandMenu);
        
        this.setCommandListener(this);
        PeerAssociatedSemanticTag owner = (PeerAssociatedSemanticTag) peer.getISphereKB().getOwner();
        
        addressesA = owner.getAddresses();
        if (addressesA == null) {
            String noIP = "no IP Addresse exist, please set one in the Menu Options.";
            this.append(noIP);
        } else {
            for (int i = 0; i < addressesA.length; i++) {
                masterCG.append(addressesA[i], null);
                masteraddressesV.add(addressesA[i]);
            }
            this.append(masterCG);

        }
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
                PeerSTSet pst = new InMemoPeerAssociatedSTSet();
                String[] adSA = null;
                for (int i = 0; i < masterCG.size(); i++) {
                    if (masterCG.isSelected(i)) {
                        adSA = new String[]{masteraddressesV.elementAt(i).toString()};
                    }
                }
                               ROPeerSemanticTag relay = pst.createPeerSemanticTag("relay", new String[]{""}, adSA);
                               peer.publishKP(peer.getSyncKP(), relay);
//                               peer.publishAllKp();
                handler.switchForm(MEISphere.FORM_SYNCHRONIZE, peer);
                break;
        }
    }
}
