package net.sharkfw.knowledgeBase.inmemory;

import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
}
