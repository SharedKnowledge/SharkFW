package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
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
        SharkKB syncKB1 = new SyncKB(target1);

        SharkKB target2 = new InMemoSharkKB();
        SharkKB syncKB2 = new SyncKB(target2);

        SemanticTag eiscreme = syncKB1.getTopicSTSet().createSemanticTag("Eiscreme",
                "https://en.wikipedia.org/wiki/Ice_cream");

        PeerSemanticTag alice = syncKB1.getPeerSTSet().createPeerSemanticTag
                ("Alice", "http://www.sharksystem.net/alice.html", (String)null);

        ASIPSpace asipSpace = syncKB1.createASIPSpace(eiscreme, null, alice, null, null, null, null,
                ASIPSpace.DIRECTION_INOUT);

        syncKB1.addInformation("Test data", asipSpace);

        Knowledge spaces2 = syncKB2.extract(asipSpace);

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
