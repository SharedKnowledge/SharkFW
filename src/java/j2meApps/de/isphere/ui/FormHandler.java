/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.isphere.ui;

import de.isphere.peer.J2MEISpherePeer;
import javax.microedition.lcdui.Alert;
import net.sharkfw.wrapper.Vector;

/**
 *
 * @author Romy Gerlach
 */
public interface FormHandler {

    public void switchForm(int i, J2MEISpherePeer peer);
    public void switchtoStart(int i, J2MEISpherePeer neuesPeer, boolean status);
    public void switchtoPNOrProfilPeers(int i, J2MEISpherePeer neuesPeer, Vector members);
    public void switchtoIPAdrOrRelais(int i, J2MEISpherePeer neuesPeer, boolean syncAddr);
    public void switchFormWithAlert(int i, J2MEISpherePeer neuesPeer, Alert alert);
   

}
