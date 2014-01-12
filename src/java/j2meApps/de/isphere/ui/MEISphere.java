/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.isphere.ui;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.knowledgeBase.impl.InMemoISphereKB;
import de.isphere.peer.J2MEISpherePeer;
import de.isphere.peer.impl.ISphereSyncKP;
import java.io.IOException;
import net.sharkfw.wrapper.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import net.sharkfw.knowledgeBase.AnchorSet;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.LocalInterest;
import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerAssociatedSTSet;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.SharkNotSupportedException;

/**
 *
 * @author Romy Gerlach
 */
public class MEISphere extends MIDlet implements FormHandler {

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
    private J2MEISphereKBListener listener = null;
    ISphereSyncKP sync = null;
    boolean status = false;
    boolean syncAdr = false;
    boolean syncLink = false;
    Vector members = new Vector();

    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {

        Display display = getDisplay();
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }

    }

    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    protected void startApp() {
        ISphereKB kb = new InMemoISphereKB("sender");
        this.peer = new J2MEISpherePeer(kb);
        this.listener = new J2MEISphereKBListener(this, peer);
        kb.addISphereListener(listener);
        try {
            PeerSTSet peers = (PeerSTSet) kb.getSTSet(ContextSpace.DIM_PEER);
            PeerSemanticTag owner = peers.createPeerSemanticTag("alicaa",
                    new String[]{"http://alica.de"}, new String[]{"socket://thinkblock:5566"});
            peer.setOwner(owner);
            peer.setRelaisAddress("socket://141.45.204.102:4444");
            peer.setConnectionTimeOut(15000);
            peer.setVisibility(true, 5566);

//            PeerSTSet remotepeers = (PeerSTSet) kb.getSTSet(ContextSpace.DIM_REMOTEPEER);
//            PeerSemanticTag receiver = remotepeers.createPeerSemanticTag("sender",
//                    new String[]{"http://sender.de"}, new String[]{"socket://141.45.207.147:5555"});
//            peer.setStatus("test");
//            peer.setConnectionTimeOut(15000);
            //           peer.setRelaisAddress("socket://shark.htw-berlin.de:5555");
            //           peer.setVisibility(true, 5555);


            //           peer.publishAllKp();
            PeerAssociatedSTSet localpeers = (PeerAssociatedSTSet) peer.getISphereKB().getSTSet(ContextSpace.DIM_PEER);
            PeerAssociatedSemanticTag tag = localpeers.createPeerAssociatedSemanticTag("Thomas", new String[]{"www.thomas.de"}, null);

            PeerAssociatedSemanticTag tag2 = localpeers.createPeerAssociatedSemanticTag("Haanelore", new String[]{"www.haan.de"}, null);
            PeerAssociatedSemanticTag tag3 = localpeers.createPeerAssociatedSemanticTag("Dummie", new String[]{"www.ddd.de"}, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        switchForm(0, peer);
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) {
    }

    public void switchForm(int i, J2MEISpherePeer neuesPeer) {
        switch (i) {
            case FORM_START:
                this.peer = neuesPeer;
                switchDisplayable(null, new Start(this, peer));
                break;
            case FORM_OPTIONS:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(null, new Options(this, peer));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_MENU:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(null, new Menu(this, peer));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_PROFILECREATE:
                this.peer = neuesPeer;
                switchDisplayable(null, new ProfileCreate(this, peer));
                break;
            case FORM_PROFILEVIEW:
                this.peer = neuesPeer;
                switchDisplayable(null, new ProfilView(this, peer));
                break;
            case FORM_CONTACTSADD:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(null, new ContactsAdd(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_CONTACTSMANAGE:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(null, new ContactsManage(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_INTERESTADD:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(null, new InterestAdd(this, peer));
                    } catch (SharkKBException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_INTERESTSMANAGE:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(null, new InterestsManage(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_SYNCHRONIZE:
                this.peer = neuesPeer;
                switchDisplayable(null, new Synchronize(this, peer));
                break;
            case FORM_PNVIEW:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(null, new PrivateMessageView(this, peer));
                    } catch (SharkNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_RELAIS:
                this.peer = neuesPeer;
                switchDisplayable(null, new Relais(this, peer));
                break;
            case FORM_IPADDRESS:
                this.peer = neuesPeer;
                switchDisplayable(null, new IPAddress(this, peer));
                break;
            case FORM_VISIBILITY:
                this.peer = neuesPeer;
                switchDisplayable(null, new Visibility(this, peer));
                break;
            case FORM_TOPICSMANAGE:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(null, new TopicsManage(this, peer));
                    } catch (SharkNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }
                break;
            case EXIT:
                exitMIDlet();
                break;
        }

    }

    public void switchtoStart(int i, J2MEISpherePeer neuesPeer, boolean status) {
        switch (i) {
            case FORM_START:
                this.peer = neuesPeer;
                this.status = status;
                switchDisplayable(null, new Start(this, peer, status));
                break;

        }
    }

    public void switchtoIPAdrOrRelais(int i, J2MEISpherePeer neuesPeer, boolean syncAdr) {
        switch (i) {
            case FORM_IPADDRESS:
                this.peer = neuesPeer;
                this.syncAdr = syncAdr;
                switchDisplayable(null, new IPAddress(this, peer, syncAdr));
                break;
            case FORM_RELAIS:
                this.peer = neuesPeer;
                this.syncAdr = syncAdr;
                switchDisplayable(null, new Relais(this, peer, syncAdr));
                break;

        }
    }

    public void switchtoPNOrProfilPeers(int i, J2MEISpherePeer neuesPeer, Vector members) {
        switch (i) {
            case FORM_PNWRITE:
                this.peer = neuesPeer;
                this.members = members;
                switchDisplayable(null, new PrivateMessageWrite(this, peer, members));
                break;
            case FORM_PROFILPEERS:
                this.peer = neuesPeer;
                this.members = members;
                switchDisplayable(null, new ProfileDetailsByPeer(this, peer, members));
                break;
        }
    }

    public void switchFormWithAlert(int i, J2MEISpherePeer neuesPeer, Alert alert) {
        switch (i) {
            case FORM_START:
                this.peer = neuesPeer;
                switchDisplayable(alert, new Start(this, peer));
                break;
            case FORM_OPTIONS:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(alert, new Options(this, peer));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_MENU:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(alert, new Menu(this, peer));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_PROFILECREATE:
                this.peer = neuesPeer;
                switchDisplayable(alert, new ProfileCreate(this, peer));
                break;
            case FORM_PROFILEVIEW:
                this.peer = neuesPeer;
                switchDisplayable(alert, new ProfilView(this, peer));
                break;
            case FORM_CONTACTSADD:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(alert, new ContactsAdd(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_CONTACTSMANAGE:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(alert, new ContactsManage(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_INTERESTADD:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(alert, new InterestAdd(this, peer));
                    } catch (SharkKBException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_INTERESTSMANAGE:
                this.peer = neuesPeer;
                try {
                    switchDisplayable(alert, new InterestsManage(this, peer));
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                } catch (SharkNotSupportedException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_SYNCHRONIZE:
                this.peer = neuesPeer;
                switchDisplayable(alert, new Synchronize(this, peer));
                break;
            case FORM_PNVIEW:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(alert, new PrivateMessageView(this, peer));
                    } catch (SharkNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }
                break;
            case FORM_RELAIS:
                this.peer = neuesPeer;
                switchDisplayable(alert, new Relais(this, peer));
                break;
            case FORM_IPADDRESS:
                this.peer = neuesPeer;
                switchDisplayable(alert, new IPAddress(this, peer));
                break;
            case FORM_VISIBILITY:
                this.peer = neuesPeer;
                switchDisplayable(alert, new Visibility(this, peer));
                break;
            case FORM_TOPICSMANAGE:
                this.peer = neuesPeer;
                try {
                    try {
                        switchDisplayable(alert, new TopicsManage(this, peer));
                    } catch (SharkNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                } catch (SharkKBException ex) {
                    ex.printStackTrace();
                }
                break;
            case EXIT:
                exitMIDlet();
                break;
        }
    }

    public void exitMIDlet() {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Create a LocalInterest with the Owner from the SharkEngine on both PEER and
     * REMOTEPEER dimension. Don't set any anchor points.
     *
     * @param se The <code>SharkEngine</code> from which the owner is read.
     * @return A <code>LocalInterest</code> as described above.
     */
    private LocalInterest createSyncInterest(SharkEngine se) throws SharkKBException {
        AnchorSet as = new AnchorSet();
        FragmentationParameter[] fps = new FragmentationParameter[ContextSpace.MAXDIMENSIONS];
        FragmentationParameter fp = new FragmentationParameter(true, true, 0);
        for (int i = 0; i < ContextSpace.MAXDIMENSIONS; i++) {
            fps[i] = fp;
        }

        LocalInterest interest = se.getSharkKB().createInterest(as, fps);

        PeerSTSet peers = new InMemoPeerAssociatedSTSet();
        peers.createPeerSemanticTag(se.getOwner().getName(), se.getOwner().getSI(), se.getOwner().getAddresses());

        PeerSTSet remotepeers = new InMemoPeerAssociatedSTSet();
        remotepeers.createPeerSemanticTag(se.getOwner().getName(), se.getOwner().getSI(), se.getOwner().getAddresses());

        interest.setDimension(ContextSpace.DIM_PEER, peers);
        interest.setDimension(ContextSpace.DIM_REMOTEPEER, remotepeers);

        return interest;

    }
}
