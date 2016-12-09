package net.sharkfw.knowledgeBase.sync.manager;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by j4rvis on 22.09.16.
 */
public class SyncMergeInfoSerializerTest {

    @Test
    public void addNewProperty_success(){
        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        SyncMergeInfoSerializer syncMergeInfoSerializer = new SyncMergeInfoSerializer(inMemoSharkKB);

        PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("BOB", "bob.de", "tcp://localhost:7070");
        SemanticTag semanticTag = InMemoSharkKB.createInMemoSemanticTag("KnowledgeBase", "knowledgeBase.de");
        long millis = System.currentTimeMillis();

        SyncMergeInfo mergeProperty = new SyncMergeInfo(peerSemanticTag, semanticTag, millis);
        syncMergeInfoSerializer.add(mergeProperty);
        SyncMergeInfo retrievedProperty = syncMergeInfoSerializer.get(peerSemanticTag, semanticTag);
        Assert.assertTrue(retrievedProperty.asString().equals(mergeProperty.asString()));
    }

    @Test
    public void getProperty_success(){
        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        SyncMergeInfoSerializer syncMergeInfoSerializer = new SyncMergeInfoSerializer(inMemoSharkKB);

        PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("BOB", "bob.de", "tcp://localhost:7070");
        SemanticTag semanticTag = InMemoSharkKB.createInMemoSemanticTag("KnowledgeBase", "knowledgeBase.de");
        SemanticTag anotherSemanticTag = InMemoSharkKB.createInMemoSemanticTag("anotherKnowledgeBase", "another-knowledgeBase.de");

        long millis = System.currentTimeMillis();
        long anotherMillis = millis - 5000;

        SyncMergeInfo mergeProperty = new SyncMergeInfo(peerSemanticTag, semanticTag, millis);
        SyncMergeInfo anotherMergeProperty = new SyncMergeInfo(peerSemanticTag, anotherSemanticTag, anotherMillis);
        syncMergeInfoSerializer.add(mergeProperty);
        syncMergeInfoSerializer.add(anotherMergeProperty);
        SyncMergeInfo retrievedProperty = syncMergeInfoSerializer.get(peerSemanticTag, semanticTag);
        Assert.assertTrue(retrievedProperty.asString().equals(mergeProperty.asString()));
    }

    @Test
    public void getProptertyByPeer_success(){
        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
        SyncMergeInfoSerializer syncMergeInfoSerializer = new SyncMergeInfoSerializer(inMemoSharkKB);
        PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag("BOB", "bob.de", "tcp://localhost:7070");
        SemanticTag semanticTag = InMemoSharkKB.createInMemoSemanticTag("KnowledgeBase", "knowledgeBase.de");
        SemanticTag anotherSemanticTag = InMemoSharkKB.createInMemoSemanticTag("anotherKnowledgeBase", "another-knowledgeBase.de");
        long millis = System.currentTimeMillis();
        long anotherMillis = millis - 5000;
        SyncMergeInfo mergeProperty = new SyncMergeInfo(peerSemanticTag, semanticTag, millis);
        SyncMergeInfo anotherMergeProperty = new SyncMergeInfo(peerSemanticTag, anotherSemanticTag, anotherMillis);
        syncMergeInfoSerializer.add(mergeProperty);
        syncMergeInfoSerializer.add(anotherMergeProperty);
        SyncMergeInfo retrievedProperty = syncMergeInfoSerializer.get(peerSemanticTag, semanticTag);
        Assert.assertTrue(retrievedProperty.asString().equals(mergeProperty.asString()));
    }
}
