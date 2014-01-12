package ApiRev1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;

/**
 * This is the example code taken from the programmers handbook.
 *
 * Alice describes a subject called "Java" by creating a tag.
 * Alice then describes herself by creating a tag for herself.
 * Next Alice defines a context, in which she puts an information, thereby creating a ContextPoint.
 * To be able to exchange information with other peer, Alice now creates an interest,
 * describing the context in which she had put the information.
 *
 * To make the interest usable, Alice creates a new KnowledgePort for handling the interest.
 * To make the KnowledgePort reachable from the outside, Alice starts TCP-Networking next.
 * The interest and her information are now passively available.
 *
 * Next Alice describes another peer called "bob" living on the same machine (see Bob.java).
 * Alice publishes her interest in java to bob, to see if he is interested.
 * 
 * @author mfi
 */

public class Alice implements KPListener {
    public static J2SEAndroidSharkEngine aliceSE;
    
  public static void main(String[] args) throws SharkKBException, IOException, SharkSecurityException {

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    // Create a new SharkEngine
    aliceSE = new J2SEAndroidSharkEngine();

    // Create a new knowledgebase for this peer
    SharkKB kb = new InMemoSharkKB();

    // Create a peer to describe the topic "Java"
    SemanticTag javaST = kb.createSemanticTag("Java", "http://oracle.com/j2se");

    // Create a peer to describe ourselves
    String[] aliceAddr = new String[2];
    aliceAddr[0] = "mail://alice@wonderland.org";
    aliceAddr[1] = "tcp://141.45.204.112:5555";

     PeerSemanticTag alicePST = kb.createPeerSemanticTag("Alice", "http://linkedIn.com/alice", aliceAddr);

     //aliceSE.setOwner(alicePST);

    // Create new coordinates before creating a ContextPoint
    ContextCoordinates javaAliceContext = kb.createContextCoordinates(javaST, /*originator*/ alicePST, /* peer */ alicePST, /*remote peer*/ null, /*time*/ null, /*place*/ null, SharkCS.DIRECTION_OUT);

    // Create a ContextPoint to add information to
    ContextPoint javaAliceCP = kb.createContextPoint(javaAliceContext);
    
    // Add a string to the ContextPoint at the given coordinates
    javaAliceCP.addInformation("Java ist toll");

    // Create a KnowledgePort to handle the interest
    StandardKP kp = aliceSE.createKP(javaAliceContext, kb);
    
    aliceSE.setConnectionTimeOut(10000);
    
    Alice alice = new Alice();
    kp.addListener(alice);
    
    // Start the TCP-networking component on the SharkEngine to send and receive traffic
    aliceSE.startTCP(5555);   

    // Now the peer is passively waiting for incoming traffic

    // Create a new peer that will act as out partner for communications
    PeerSemanticTag bobPST = kb.createPeerSemanticTag("Bob", "http://linkedIn.com/bob", "tcp://localhost:5556");

    // publish the KnowledgePort to our partner
    aliceSE.publishKP(kp, bobPST);
    
    System.out.print("Bob has to run before starting Alice. If so, he "
            + "has already received something from Alice");
  }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        // ignore
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        // just to illustrate the idea.. Alice shuts down after sending knowledge
        System.out.println("Alice has sent something - enough for today...");
        
        System.out.println(L.knowledge2String(sentKnowledge.contextPoints()));
        
        aliceSE.stop();
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        // ignore
    }
}
