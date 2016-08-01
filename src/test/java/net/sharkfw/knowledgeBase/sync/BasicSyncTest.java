package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by thsc on 28.07.16.
 */
public class BasicSyncTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void SyncKB_Basics_success() throws Exception {

        SharkKB target1 = new InMemoSharkKB();
        SyncKB syncKB1 = new SyncKB(target1);

        SemanticTag eiscreme = syncKB1.getTopicSTSet().createSemanticTag("Eiscreme",
                "https://en.wikipedia.org/wiki/Ice_cream");

        PeerSemanticTag alice = syncKB1.getPeerSTSet().createPeerSemanticTag
                ("Alice", "http://www.sharksystem.net/alice.html", (String)null);
        
        syncKB1.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html");
        
        alice.addAddress("mail://alice@wonderland.net");

        ASIPSpace asipSpace = syncKB1.createASIPSpace(eiscreme, null, alice, null, null, null, null,
                ASIPSpace.DIRECTION_INOUT);

        syncKB1.addInformation("Test data", asipSpace);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d(L.kb2String(target1, true));
        
        Long t1 = System.currentTimeMillis();

        L.setLogLevel(L.LOGLEVEL_SILENT);
        Thread.sleep(10);

        // get changes - should be empty
        SharkKB changes = syncKB1.getChanges(t1);
        
        int infoNumber = changes.getNumberInformation();
        Assert.assertEquals(0, infoNumber);
        
        // add something new
        PeerSemanticTag bob = syncKB1.getPeerSTSet().createPeerSemanticTag
                ("Bob", "http://www.sharksystem.net/bob.html", (String)null);
        
        
        syncKB1.createASIPSpace(eiscreme, null, bob, null, null, null, null,
                ASIPSpace.DIRECTION_INOUT);

        syncKB1.addInformation("Bob data", asipSpace);
        
        syncKB1.addInformation("like ice cream too", asipSpace);
        
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        L.d("KB: After adding something to kb\n");
        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        L.d(L.kb2String(target1, true));

        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        L.d("CHANGES KB: After adding something to kb\n");
        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        
        Thread.sleep(10);
        changes = syncKB1.getChanges(t1);
        L.d(L.kb2String(changes, true));
        L.setLogLevel(L.LOGLEVEL_SILENT);
        
        Assert.assertNotNull(changes.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/bob.html"));
        Assert.assertNull(changes.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html"));
        
        infoNumber = changes.getNumberInformation();
        Assert.assertNotEquals(0, infoNumber);


        // no import changes into another KB
        SharkKB target2 = new InMemoSharkKB();
        SyncKB syncKB2 = new SyncKB(target2);

        syncKB2.putChanges(changes);
        L.setLogLevel(L.LOGLEVEL_ALL);
        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        L.d("TARGET KB: After putChanges()\n");
        L.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

        L.d(L.kb2String(target2, true));
        L.setLogLevel(L.LOGLEVEL_SILENT);

        Assert.assertNotNull(target2.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/bob.html"));
        Assert.assertNull(target2.getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html"));



        // test if both kb are identically now!!

        // stop here .. work todo
        /*
        Assert.assertNotNull(spaces2);
        Assert.assertNotNull(spaces2.getVocabulary().getTopicSTSet().getSemanticTag("https://en.wikipedia.org/wiki/Ice_cream"));
        Assert.assertNotNull(spaces2.getVocabulary().getPeerSTSet().getSemanticTag("http://www.sharksystem.net/alice.html"));
        ASIPInformationSpace space2 = spaces2.informationSpaces().next();
        String stringContent2 = space2.informations().next().getContentAsString();
        Assert.assertEquals("Test data", stringContent2);
         */
    }
}
