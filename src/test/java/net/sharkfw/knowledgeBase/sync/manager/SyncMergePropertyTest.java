package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by j4rvis on 22.09.16.
 */
public class SyncMergePropertyTest {

    @Test
    public void addNewProperty_success(){
        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        SyncMergePropertyList syncMergePropertyList = new SyncMergePropertyList(inMemoSharkKB);

        PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("BOB", "bob.de", "tcp://localhost:7070");
        SemanticTag semanticTag = InMemoSharkKB.createInMemoSemanticTag("KnowledgeBase", "knowledgeBase.de");
        long millis = System.currentTimeMillis();

        SyncMergeProperty mergeProperty = new SyncMergeProperty(peerSemanticTag, semanticTag, millis);

        syncMergePropertyList.add(mergeProperty);

        SyncMergeProperty retrievedProperty = syncMergePropertyList.get(peerSemanticTag, semanticTag);

        Assert.assertTrue(retrievedProperty.asString().equals(mergeProperty.asString()));
    }

    @Test
    public void getProperty_success(){
        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        SyncMergePropertyList syncMergePropertyList = new SyncMergePropertyList(inMemoSharkKB);

        PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("BOB", "bob.de", "tcp://localhost:7070");
        SemanticTag semanticTag = InMemoSharkKB.createInMemoSemanticTag("KnowledgeBase", "knowledgeBase.de");
        SemanticTag anotherSemanticTag = InMemoSharkKB.createInMemoSemanticTag("anotherKnowledgeBase", "another-knowledgeBase.de");
        long millis = System.currentTimeMillis();
        long anotherMillis = millis - 5000;

        SyncMergeProperty mergeProperty = new SyncMergeProperty(peerSemanticTag, semanticTag, millis);
        SyncMergeProperty anotherMergeProperty = new SyncMergeProperty(peerSemanticTag, anotherSemanticTag, anotherMillis);

        syncMergePropertyList.add(mergeProperty);
        syncMergePropertyList.add(anotherMergeProperty);

        SyncMergeProperty retrievedProperty = syncMergePropertyList.get(peerSemanticTag, semanticTag);

        Assert.assertTrue(retrievedProperty.asString().equals(mergeProperty.asString()));
    }
}
