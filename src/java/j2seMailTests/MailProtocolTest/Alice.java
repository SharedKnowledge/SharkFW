package MailProtocolTest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.SharkSecurityException;

/**
 *
 * @author Jacob Zschunke
 */
public class Alice {
    private static final int MESSAGE_LENGTH = 5*1024;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SharkKBException, SharkSecurityException {
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("After the Engine is started, you can type:\n"
                + "\tpublish - publishes the KP to Bob\n"
                + "\tquit - exit the programm");
        
        System.out.println("##################################################");
        System.out.println("##################################################");
        System.out.println("##################################################");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("init...");
        J2SEAndroidSharkEngine alice = new J2SEAndroidSharkEngine();
        SharkKB kb = new InMemoSharkKB();
        
        FragmentationParameter[] fps = kb.getStandardFPSet();
        fps[SharkCS.DIM_TOPIC] = new FragmentationParameter(true, true, 1);
        kb.setStandardFPSet(fps);
        
//        alice.setMailConfiguration("smtp.sharksystem.net", "thsc_test1@sharksystem.net", "thsc_test1", "pop3.sharksystem.net", "thsc_test1@sharksystem.net", "thsc_test1@sharksystem.net", "thsc_test1", 1);
        boolean ssl = true;
        alice.setMailConfiguration("smtp.sharksystem.net", 
                "thsc_test1@sharksystem.net", "thsc_test1", 
                ssl, "pop3.sharksystem.net", 
                "thsc_test1@sharksystem.net", 
                "thsc_test1@sharksystem.net", 
                "thsc_test1", 1, ssl);
        
        PeerSemanticTag alicePeer = kb.createPeerSemanticTag("Alice", "http://www.sharksystem.net/thsc_test1.html", Settings.ALICE_ADDRESS);
        PeerSemanticTag bobPeer = kb.createPeerSemanticTag("Bob", "http://www.sharksystem.net/thsc_test2.html", Settings.BOB_ADDRESS);
        TXSemanticTag p2p = kb.getTopicsAsTaxonomy().createTXSemanticTag("P2P", "http://www.p2p.de");
        TXSemanticTag shark = kb.getTopicsAsTaxonomy().createTXSemanticTag("Shark", "http://www.sharksystem.net");
        shark.move(p2p);
        
        ContextCoordinates cc = kb.createContextCoordinates(shark, alicePeer, null, null, null, null, SharkCS.DIRECTION_OUT);
        
        ContextPoint cp = kb.createContextPoint(cc);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int max = MESSAGE_LENGTH;
        for(int i = 0; i < max; i++) {
            baos.write(42);
        }
        
        cp.addInformation(baos.toByteArray());
        
        Interest interest = kb.createInterest(cc);
        
        StandardKP kp = new StandardKP(alice, interest, kb);
        
        
        System.out.println("start...");
        alice.startMail();
        System.out.println("running...");
                
        String s = "";
        while((s = reader.readLine()) != null) {
            System.out.println("You typed: " + s);            
            if(s.equalsIgnoreCase("quit")) {
                System.exit(0);
            }
            if(s.equalsIgnoreCase("publish")) {
                alice.publishKP(kp, bobPeer);
                System.out.println("published...");
            }
        }
        
    }
}
