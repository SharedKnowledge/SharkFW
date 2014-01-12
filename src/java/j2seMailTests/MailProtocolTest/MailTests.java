package MailProtocolTest;

import java.io.IOException;
import junit.framework.Assert;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.StandardKP;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class MailTests implements KPListener {
    private static final String ALICE_HOMEPAGE = "http://www.sharksystem.net/alice.html";
    private static final String BOB_HOMEPAGE = "http://www.sharksystem.net/bob.html";
    private static final String CARMEN_HOMEPAGE = "http://www.sharksystem.net/peers/carmen";
    private static final String DOUGLAS_HOMEPAGE = "http://www.sharksystem.net/peers/douglas";
    private boolean success;
    
    public static final String JAVA_SI = "http://oracle.com/j2se";

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        // ignore
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        // ignore
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        // success - something has been assimilated
        this.success = true;
    }
    
//     @Test
     public void aliceBobMailExchange() throws SharkKBException, InterruptedException, IOException, SharkSecurityException {
         L.setLogLevel(L.LOGLEVEL_ALL);
         
        // Create a new SharkEngine
        J2SEAndroidSharkEngine aliceSE = new J2SEAndroidSharkEngine();

        // Create a new knowledgebase for this peer
        SharkKB aliceKB = new InMemoSharkKB();

        // Create a peer to describe the topic "Java"
        SemanticTag javaST = aliceKB.createSemanticTag("Java", JAVA_SI);

        PeerSemanticTag alicePST = aliceKB.createPeerSemanticTag("Alice", 
                MailTests.ALICE_HOMEPAGE, Settings.ALICE_ADDRESS);

         aliceKB.setOwner(alicePST);

        // Create new coordinates before creating a ContextPoint
        ContextCoordinates javaAliceContext = aliceKB.createContextCoordinates(javaST, 
                alicePST, /*remote peer*/ null, /*originator*/ null, /*time*/ null, 
                /*place*/ null, SharkCS.DIRECTION_OUT);

        // Create a ContextPoint to add information to
        ContextPoint javaAliceCP = aliceKB.createContextPoint(javaAliceContext);

        // Add a string to the ContextPoint at the given coordinates
        javaAliceCP.addInformation("Java ist toll");

        // Use the coordinates to create a new interest from them
        Interest javaAliceInterest = aliceKB.createInterest(javaAliceContext);

        // Create a KnowledgePort to handle the interest
        KnowledgePort kp = aliceSE.createKP(javaAliceInterest, aliceKB);

        // configure e-mail
        aliceSE.setMailConfiguration("smtp.sharksystem.net", 
                "pandu@sharksystem.net", "pandupandu", false, 
                "pop3.sharksystem.net", "pandu@sharksystem.net", 
                "pandu@sharksystem.net", "pandupandu", 1, false);

        // mail is now active, mailbox is checked any 5 minutes
        aliceSE.startMail();

        // Create a new peer that will act as out partner for communications
        PeerSemanticTag bobPST = aliceKB.createPeerSemanticTag("Bob", 
                BOB_HOMEPAGE, Settings.BOB_ADDRESS);

        // publish the KnowledgePort to our partner
        aliceSE.publishKP(kp, bobPST);

//        Thread.sleep(Integer.MAX_VALUE);

        // now, Bob enters the scene
         
        // Create a new shark engine
        J2SEAndroidSharkEngine bobSE = new J2SEAndroidSharkEngine();

        // Create an in memory knowledgebase to store your information in
        SharkKB bobKB = new InMemoSharkKB();

        // Create a tap representing the subject "Java"
        SemanticTag bobJavaST = bobKB.createSemanticTag("Java", JAVA_SI);

         // Create a peer to describe ourselves (Bob)
        String[] bobAddr = new String[1];
        bobAddr[0] = Settings.BOB_ADDRESS;

        PeerSemanticTag bobbobPST = bobKB.createPeerSemanticTag("Bob", BOB_HOMEPAGE, bobAddr);

        // Create new ContextCoordinates
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(bobJavaST, null, 
                bobbobPST, null, null, null, SharkCS.DIRECTION_IN);

        // Use these coordinates to create a new interest from them
        Interest javaInterest = bobKB.createInterest(cc);

        // Activate a KnowledgePort using the interest to handle incoming events
        StandardKP bobKP = bobSE.createKP(javaInterest, bobKB);

        MailTests bobPeer = new MailTests();
        bobKP.addListener(bobPeer);

        // configure e-mail
        bobSE.setMailConfiguration("smtp.sharksystem.net", 
                     "olga@sharksystem.net", "olgaolga", false /* ssl smtp */,
                     "pop3.sharksystem.net", "olga@sharksystem.net", 
                     "olga@sharksystem.net", "olgaolga", 1, false /* ssl pop */);
        
        // mail is now active, mailbox is checked any minute
        bobSE.startMail();

        // wait to finish mail exchange between both peers...
        Thread.sleep(180000);
//        Thread.sleep(Integer.MAX_VALUE);

        aliceSE.stopMail();

        bobSE.stop();
        
        if(!bobPeer.success) {
            Assert.fail();
        }
     }

     @Test
     public void largeMailExchange() throws SharkKBException, InterruptedException, IOException, SharkSecurityException {
         L.setLogLevel(L.LOGLEVEL_ALL);
         
        // create Bob first in this test (cut's coommunication a while)
         
        // Create a new shark engine
        J2SEAndroidSharkEngine bobSE = new J2SEAndroidSharkEngine();

        // Create an in memory knowledgebase to store your information in
        SharkKB bobKB = new InMemoSharkKB();

        // Create a tap representing the subject "Java"
        SemanticTag bobJavaST = bobKB.createSemanticTag("Java", JAVA_SI);

         // Create a peer to describe ourselves (Bob)
        String[] bobAddr = new String[1];
        bobAddr[0] = Settings.BOB_ADDRESS;

        PeerSemanticTag bobbobPST = bobKB.createPeerSemanticTag("Bob", BOB_HOMEPAGE, bobAddr);

        // Create new ContextCoordinates
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(bobJavaST, null, 
                bobbobPST, null, null, null, SharkCS.DIRECTION_IN);

        // Use these coordinates to create a new interest from them
        Interest javaInterest = bobKB.createInterest(cc);

        // Activate a KnowledgePort using the interest to handle incoming events
        StandardKP bobKP = bobSE.createKP(javaInterest, bobKB);

        MailTests bobPeer = new MailTests();
        bobKP.addListener(bobPeer);

        // configure e-mail
        bobSE.setMailConfiguration("smtp.sharksystem.net", 
                     "olga@sharksystem.net", "olgaolga", false /* ssl smtp */,
                     "pop3.sharksystem.net", "olga@sharksystem.net", 
                     "olga@sharksystem.net", "olgaolga", 1, false /* ssl pop */, 
                     Settings.MAX_LEN_IN_KBYTES);
        
        // mail is now active, mailbox is checked any minute
        bobSE.startMail();
        
        PeerSemanticTag alicePST = InMemoSharkKB.createInMemoPeerSemanticTag(
                "Alice", MailTests.ALICE_HOMEPAGE, Settings.ALICE_ADDRESS);
        
        bobSE.publishKP(bobKP, alicePST);
        
        Thread.sleep(5000);

        // Create a new SharkEngine
        J2SEAndroidSharkEngine aliceSE = new J2SEAndroidSharkEngine();

        // Create a new knowledgebase for this peer
        SharkKB aliceKB = new InMemoSharkKB();

        // Create a peer to describe the topic "Java"
        SemanticTag javaST = aliceKB.createSemanticTag("Java", JAVA_SI);

        alicePST = aliceKB.createPeerSemanticTag("Alice", 
                MailTests.ALICE_HOMEPAGE, Settings.ALICE_ADDRESS);

         aliceKB.setOwner(alicePST);

        // Create new coordinates before creating a ContextPoint
        ContextCoordinates javaAliceContext = aliceKB.createContextCoordinates(javaST, 
                alicePST, /*remote peer*/ null, /*originator*/ null, /*time*/ null, 
                /*place*/ null, SharkCS.DIRECTION_OUT);

        // Create a ContextPoint to add information to
        ContextPoint javaAliceCP = aliceKB.createContextPoint(javaAliceContext);

        // Add a string to the ContextPoint at the given coordinates
        
        // create long content 
//        int contentLen = Settings.MAX_LEN_IN_KBYTES/2*1024;
        int contentLen = 2500;
        
        byte[] longContent = new byte[contentLen];
        for(int i = 0; i < contentLen; i++) {
            longContent[i] = 4;
        }
        javaAliceCP.addInformation(longContent);

        // Use the coordinates to create a new interest from them
        Interest javaAliceInterest = aliceKB.createInterest(javaAliceContext);

        // Create a KnowledgePort to handle the interest
        KnowledgePort kp = aliceSE.createKP(javaAliceInterest, aliceKB);

        // configure e-mail
        aliceSE.setMailConfiguration("smtp.sharksystem.net", 
                "pandu@sharksystem.net", "pandupandu", false, 
                "pop3.sharksystem.net", "pandu@sharksystem.net", 
                "pandu@sharksystem.net", "pandupandu", 1, false, 
                Settings.MAX_LEN_IN_KBYTES);

        // mail is now active, mailbox is checked any 5 minutes
        aliceSE.startMail();

//        Thread.sleep(Integer.MAX_VALUE);

        // wait to finish mail exchange between both peers...
        Thread.sleep(180000);
//        Thread.sleep(Integer.MAX_VALUE);

        aliceSE.stopMail();

        bobSE.stop();
        
        if(!bobPeer.success) {
            Assert.fail();
        }
        
        // take cp
        ContextPoint cp = bobKB.getAllContextPoints().nextElement();
        byte[] c2 = cp.enumInformation().nextElement().getContentAsByte();
        for(int i = 0; i < contentLen; i++) {
            Assert.assertEquals(c2[i], 4);
        }
     }
}
