/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;

/**
 *
 * @author Romy Gerlach
 */
public class ProfileDetailsByPeer extends Form implements CommandListener {

    private FormHandler handler;
    private Command commandExit = new Command("Bye", Command.EXIT, 1);
    private Command commandMenu = new Command("Menu", Command.BACK, 1);
    
    J2MEISpherePeer peer = null;
    
  Vector members = new Vector();

     public ProfileDetailsByPeer(FormHandler handler, J2MEISpherePeer peer, Vector members) {
        super("Profil");
        this.handler = handler;
        this.peer = peer;
        this.members = members;
               
        this.addCommand(commandExit);
        this.addCommand(commandMenu);
       
        this.setCommandListener(this);

        PeerAssociatedSemanticTag peerRT = (PeerAssociatedSemanticTag) members.elementAt(0);
        ContextCoordinates cco = new ContextCoordinates();
        cco.setSI(ContextSpace.DIM_TOPIC, new String[]{ISphereKB.STATUS_URL});
        cco.setSI(ContextSpace.DIM_PEER, peerRT.getSI());
        String statusS = "dont get status";
        try {
            ContextPoint cp = peer.getISphereKB().getContextPoint(cco);
            Enumeration infoE = cp.getInformation();
            Vector infoV = new Vector(infoE);
            Information iniI = (Information)infoV.elementAt(infoV.size()-1);
            statusS = new String(iniI.getContentAsByte());
        } catch (SharkKBException ex) {
            ex.printStackTrace();
        }

         

        String nameView = "Name: \n" + peerRT.getName() + "\n";
        String statusView = "Status: \n" + statusS;
        String siView = "Personal Homepage: \n" + peerRT.getSI()[0]  + "\n";
        String dateView = "Birthdate: \n" + peer.getProfileDetailFromRemotepeer(ISphereKB.BIRTHDATE_URL, peerRT) + "\n";
        String emailView = "Email: \n" + peer.getProfileDetailFromRemotepeer(ISphereKB.EMAILADDRESS_URL, peerRT) + "\n" ;
        String genderView = "Gender: \n" + peer.getProfileDetailFromRemotepeer(ISphereKB.GENDER_URL, peerRT)  + "\n";
        this.append(nameView);
        this.append(statusView);
        this.append(dateView);
        this.append(emailView);
        this.append(genderView);
        this.append(siView);
        
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
