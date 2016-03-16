package net.sharkfw.apirev1;

import java.io.IOException;
import junit.framework.Assert;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkSecurityException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class DifferentAddresses {
    public static final int alicePort = 2222;
    public static final int bobPort = 3333;
    private String bobAddress;
    
    public DifferentAddresses() {
        this.bobAddress = "tcp://localhost:" + DifferentAddresses.bobPort;
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Two peers are created. Both open a TCP port. Neither access a mail box.
     * Sending peer dafines two recipient addresses: valid TCP port and wrong
     * mail address. System shall - of course - use the worling TCP port and
     * figure out that mail is not wirking.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     * @throws java.io.IOException
     * @throws net.sharkfw.system.SharkSecurityException
     */
    @Test
    public void wrongMailAddress() throws SharkKBException, IOException, SharkSecurityException, InterruptedException {
        // Alice
        J2SEAndroidSharkEngine aliceSE = new J2SEAndroidSharkEngine();
        
        SharkKB aliceKB = new InMemoSharkKB();
        Interest aliceInterest = InMemoSharkKB.createInMemoInterest();
        
        DummyKP aliceKP = new DummyKP(aliceSE);
        PeerSemanticTag aliceBob = aliceKB.getPeerSTSet().createPeerSemanticTag("Bob", "http://www.sharksystem.net/bob.html", this.bobAddress);
        
        // Bob
        J2SEAndroidSharkEngine bobSE = new J2SEAndroidSharkEngine();
        SharkKB bobKB = new InMemoSharkKB();
        
        DummyKP bobKP = new DummyKP(bobSE);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        bobSE.startTCP(bobPort);
        
        // send interest
        aliceKP.sendInterest(aliceInterest, aliceBob);
        
        // give receiving thread a while
        Thread.sleep(100);
        
        // bob must have received something
        Assert.assertNotNull("Bob must have received something", bobKP.lastInterest);
        
        ///////////////// test with wrong mail address but in second line
        String wrongMailAddress = "mail://bob@htw-berlin.de";
        
        String[] bobAddresses = new String[2];
        
        bobAddresses[0] = this.bobAddress;
        bobAddresses[1] = wrongMailAddress;
        
        aliceBob.setAddresses(bobAddresses);
        L.d(L.semanticTag2String(aliceBob));

        // reset bob port
        bobKP.reset();
        
        // send again
        aliceKP.sendInterest(aliceInterest, aliceBob);
        
        // give receiving thread a while
        Thread.sleep(100);
        
        // bob must have received something
        Assert.assertNotNull("Bob must have received something (2)", bobKP.lastInterest);

        ///////////////// test with wrong mail address in first place
        bobAddresses[0] = wrongMailAddress;
        bobAddresses[1] = this.bobAddress;
        
        aliceBob.setAddresses(bobAddresses);
        L.d(L.semanticTag2String(aliceBob));

        // reset bob port
        bobKP.reset();
        
        // send again
        aliceKP.sendInterest(aliceInterest, aliceBob);
        
        // give receiving thread a while
        Thread.sleep(100);
        
        // bob must have received something
        Assert.assertNotNull("Bob must have received something (3)", bobKP.lastInterest);

    }
}
