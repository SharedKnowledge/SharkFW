package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author thsc
 */
public class SyncKPTest {
    
    public SyncKPTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void syncKPTest() throws SharkKBException {
        SharkKB target1 = new InMemoSharkKB();
        SyncKB syncKB1 = new SyncKB(target1);

        // fill kb1
        SemanticTag eiscreme = syncKB1.getTopicSTSet().createSemanticTag("Eiscreme",
                "https://en.wikipedia.org/wiki/Ice_cream");

        PeerSemanticTag alice = syncKB1.getPeerSTSet().createPeerSemanticTag
                ("Alice", "http://www.sharksystem.net/alice.html", (String)null);
        
        syncKB1.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html");
        
        alice.addAddress("mail://alice@wonderland.net");

        ASIPSpace asipSpace = syncKB1.createASIPSpace(eiscreme, null, alice, null, null, null, null,
                ASIPSpace.DIRECTION_INOUT);

        syncKB1.addInformation("Test data", asipSpace);
        
//        L.setLogLevel(L.LOGLEVEL_ALL);
//        L.d(L.kb2String(target1, true));
        
//        L.setLogLevel(L.LOGLEVEL_SILENT);
        
        // setup engine 1
        SharkEngine se1 = new J2SEAndroidSharkEngine();
        SemanticTag kbTitel1 = InMemoSharkKB.createInMemoSemanticTag("kb1", "http://kb1.somewhere");
//        SyncMergeKP kp1 = new SyncMergeKP(se1, syncKB1, kbTitel1);

//        L.setLogLevel(L.LOGLEVEL_ALL);
        
        // setup engine 1
        SharkEngine se2 = new J2SEAndroidSharkEngine();
        SemanticTag kbTitel2 = InMemoSharkKB.createInMemoSemanticTag("kb2", "http://kb2.somewhere");
        SharkKB target2 = new InMemoSharkKB();
        SyncKB syncKB2 = new SyncKB(target2);
//        SyncMergeKP kp2 = new SyncMergeKP(se2, syncKB2, kbTitel2);

        // do the exchange
        // no import changes into another KB

//        L.setLogLevel(L.LOGLEVEL_ALL);
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
//        L.d("TARGET KB: After putChanges()\n");
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

//        L.d(L.kb2String(target2, true));
//        L.setLogLevel(L.LOGLEVEL_SILENT);

//        Assert.assertNotNull(target2.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/bob.html"));
//        Assert.assertNull(target2.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html"));
        int infoNumber = target2.getNumberInformation();
//        Assert.assertEquals(1, infoNumber);
         
        // add something new
//        PeerSemanticTag bob = syncKB1.getPeerSTSet().createPeerSemanticTag
//                ("Bob", "http://www.sharksystem.net/bob.html", (String)null);
//        
//        
//        syncKB1.createASIPSpace(eiscreme, null, bob, null, null, null, null,
//                ASIPSpace.DIRECTION_INOUT);
//
//        syncKB1.addInformation("Bob data", asipSpace);
//        
//        syncKB1.addInformation("like ice cream too", asipSpace);
//        
//        L.setLogLevel(L.LOGLEVEL_ALL);
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
//        L.d("KB: After adding something to kb\n");
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
//        L.d(L.kb2String(target1, true));
//
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
//        L.d("CHANGES KB: After adding something to kb\n");
//        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
//        
//        Thread.sleep(10);
//        changes = syncKB1.getChanges(t1);
//        L.d(L.kb2String(changes, true));
//        L.setLogLevel(L.LOGLEVEL_SILENT);
//        
//        Assert.assertNotNull(changes.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/bob.html"));
//        Assert.assertNull(changes.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html"));
//        
//        infoNumber = changes.getNumberInformation();
//        Assert.assertNotEquals(0, infoNumber);


     }
}
