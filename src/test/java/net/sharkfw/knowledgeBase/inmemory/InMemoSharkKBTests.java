package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static net.sharkfw.asip.ASIPSpace.DIRECTION_INOUT;

/**
 * Created by j4rvis on 15.08.16.
 */
public class InMemoSharkKBTests {

    private String name =  "Alice";
    private String si = "www.sharksystem.net/alice";

    private PeerSemanticTag peerSemanticTag;

    @Test
    public void createASIPSpace_justTags_mergesTags(){
        InMemoSharkKB sharkKB = new InMemoSharkKB();
        this.peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(name, si, null);

        try {
            ASIPSpace space = sharkKB.createASIPSpace((SemanticTag) null, null, null, this.peerSemanticTag, null, null, null, ASIPSpace.DIRECTION_NOTHING);
            PeerSemanticTag sender = space.getSender();
            Assert.assertTrue(SharkCSAlgebra.identical(sender, this.peerSemanticTag));
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextualize_nullPointerException() throws SharkKBException {

        SharkKB kb = new InMemoSharkKB();

        SemanticTag topic1 = kb.getTopicSTSet().createSemanticTag("Shark", "http://sharksystem.net/");
        SemanticTag topic2 = kb.getTopicSTSet().createSemanticTag("HTW", "http://www.htw-berlin.de/");

        PeerSemanticTag author1 = kb.getPeerSTSet().createPeerSemanticTag("Tim", "tim@mail.com", "");
        PeerSemanticTag author2 = kb.getPeerSTSet().createPeerSemanticTag("Harald", "harald@mail.com", "");

        ASIPSpace space1 = kb.createASIPSpace(topic1, null, author1, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space2 = kb.createASIPSpace(topic2, null, author2, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space3 = kb.createASIPSpace(topic1, null, author2, null, null, null, null, DIRECTION_INOUT);
        ASIPSpace space4 = kb.createASIPSpace(topic2, null, author1, null, null, null, null, DIRECTION_INOUT);


        kb.addInformation("Ein schöner Sharktext...", space1);

        kb.addInformation("HTW-Krimskrams", space2);
        kb.addInformation("Shark Something", space3);
        kb.addInformation("anderer HTW-Krimskrams", space4);



        STSet topicSet = InMemoSharkKB.createInMemoSTSet();
        topicSet.merge(topic2);
        ASIPInterest inter = InMemoSharkKB.createInMemoASIPInterest(topicSet,null,(PeerSTSet) null,null,null,null,null,DIRECTION_INOUT);

        //NullPointerException in AbstractSharkKB.getDefaultFPSet (Zeile 1141)
        // Die Zeiler unter mir muss genutzt werden, damit der Test laeuft.
        // Wurde auskommentiert, damit shark trotzdem gebuat werden kann.
        ASIPInterest ctx = kb.contextualize(inter);
        L.d(L.asipSpace2String(ctx), this);
    }
}
