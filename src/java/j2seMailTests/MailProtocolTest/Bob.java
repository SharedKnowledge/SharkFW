package MailProtocolTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.StandardKP;

/**
 *
 * @author Jacob Zschunke
 */
public class Bob {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SharkKBException {
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("After the Engine is started, you can type:\n"
                + "\tstatus - prints CPs and all known Peers\n"
                + "\tquit - exit the programm");
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("##################################################");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("init...");
        J2SEAndroidSharkEngine bob = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();
        
        boolean ssl = true;
        bob.setMailConfiguration("smtp.sharksystem.net", 
                "thsc_test2@sharksystem.net", "thsc_test2", 
                ssl, "pop3.sharksystem.net", "thsc_test2@sharksystem.net", 
                "thsc_test2@sharksystem.net", "thsc_test2", 
                1, ssl);
        
        PeerSemanticTag bobPeer = kb.createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", Settings.BOB_ADDRESS);
        SemanticTag p2p = kb.createSemanticTag("P2P", "http://www.p2p.de");
        
        ContextCoordinates cc = kb.createContextCoordinates(p2p, bobPeer, null, null, null, null, SharkCS.DIRECTION_IN);
        Interest interest = kb.createInterest(cc);        
        StandardKP kp = new StandardKP(bob, interest, kb);        
        System.out.println("done!");
        
        System.out.println("Press Return to start the mail engine");
        reader.readLine();
        System.out.println("start...");
        bob.startMail();
        System.out.println("running...");
        
        String s = "";
        while((s = reader.readLine()) != null) {
            System.out.println("You typed: " + s);
            if(s.equalsIgnoreCase("status")) {
                printStatus(kb);
            }
            if(s.equalsIgnoreCase("quit")) {
                System.exit(0);
            }
        }

    }

    private static void printStatus(SharkKB kb) throws SharkKBException {
        Enumeration<ContextPoint> cpEnum = kb.getContextPoints(kb.createContextCoordinates(null, null, null, null, null, null, SharkCS.DIRECTION_INOUT));
        int cpCount = 0;
        System.out.println("\n\n\n\nContextPoints:");
        while (cpEnum != null && cpEnum.hasMoreElements()) {
            ContextPoint cp = cpEnum.nextElement();
            System.out.println("CP #" + Integer.toString(cpCount) + " has " + Integer.toString(cp.getNumberInformation()) + " Informations");
        }
        
        System.out.println("\n\nKnown Peers:");        
        Enumeration<PeerSemanticTag> peerEnum = kb.getPeerSTSet().peerTags();
        while (peerEnum != null && peerEnum.hasMoreElements()) {
            PeerSemanticTag peer = peerEnum.nextElement();
            System.out.println("Name: " + peer.getName());
            for(String si : peer.getSI()) System.out.println("\tSI: " + si);                
            for(String adr : peer.getAddresses()) System.out.println("\tAddress: " + adr);            
        }
        
    }


}
