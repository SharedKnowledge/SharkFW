package ApiRev1;

import java.io.IOException;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;

/**
 * Example code from the programmer's handbook.
 *
 * This is bob's simple shark peer.
 * Bob is interested in information about "Java" and creates an interest.
 * Bob activates his interest by starting a KnowledgePort and waits for
 * incoming connections on port 5556 using tcp.
 *
 * @author mfi
 */
public class Bob implements KPListener {
    public static J2SEAndroidSharkEngine bobSE;
    
  public static void main(String[] args) throws SharkKBException, IOException {

    // Create a new shark engine
    bobSE = new J2SEAndroidSharkEngine();

    // Create an in memory knowledgebase to store your information in
    SharkKB kb = new InMemoSharkKB();

    // Create a tap representing the subject "Java"
    SemanticTag javaST = kb.createSemanticTag("Java", "http://oracle.com/j2se");

     // Create a peer to describe ourselves (Bob)
    String[] bobAddr = new String[1];
    bobAddr[0] = "tcp://localhost:5556";

    PeerSemanticTag bobPST = kb.createPeerSemanticTag("Bob", "http://linkedIn.com/bob", bobAddr);
    //bobSE.setOwner(bobPST);

    // Create new ContextCoordinates
    ContextCoordinates interest = kb.createContextCoordinates(javaST, null, bobPST, null, null, null, SharkCS.DIRECTION_IN);

    // Activate a KnowledgePort using the interest to handle incoming events
    StandardKP kp = bobSE.createKP(interest, kb);

    Bob bobPeer = new Bob();
    kp.addListener(bobPeer);

    bobSE.setConnectionTimeOut(10000);
    
    // Start the tcp-networking module to be able to send and receive using tcp
    bobSE.startTCP(5556);
    
    System.out.println("Bob is up and running... Now start Alice.");
    
  }

  // Implementing KPListener to notfiy about actions 
    @Override
  public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
    System.out.println("Sent interest: " + sentMutualInterest);
  }

    @Override
  public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
    System.out.println("Sent knowledge: " + sentKnowledge);
  }

    @Override
  public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
    System.out.println("Assimilated ContextPoint with coordinates\n: " + L.contextSpace2String(newCP.getContextCoordinates()));
    
    // received something - stop engine
    bobSE.stop();
  }
}
