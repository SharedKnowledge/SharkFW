package com.shark.demo.kbs;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.knowledgeBase.sync.SyncKB;
import net.sharkfw.system.L;

import java.util.UUID;
/**
 * Created by simon on 18.03.15.
 * This is a helper class that creates KnowledgeBases with random data.
 */
public class KnowledgeBaseCreator {

    private PeerSemanticTag _alice = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "aliceId", "tcp://localhost:5555");
    private PeerSemanticTag _bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob", "bobId", "tcp://localhost:5555");
    private PeerSemanticTag _clara = InMemoSharkKB.createInMemoPeerSemanticTag("Clara", "claraId", "tcp://localhost:5555");

    public SyncKB getKb(String owner) throws SharkKBException {
        switch (owner) {
            case "Alice":
                return prepareKb(_alice);
            case "Bob":
                return prepareKb(_bob);
            case "Clara":
                return prepareKb(_clara);
            default: throw new IllegalArgumentException("Alice, Clara or Bob have to be the ID.");
        }
    }

    private SyncKB prepareKb(PeerSemanticTag owner) throws SharkKBException {
        SharkKB kb = new InMemoSharkKB();
        try {
            SemanticTag tag1 = kb.createSemanticTag(owner.getName() + " Semantic Tag 1", owner.getName() + " Subject Identifier 1");
            SemanticTag tag2 = kb.createSemanticTag(owner.getName() + " Semantic Tag 2", owner.getName() + " Subject Identifier 2");
            kb.createContextPoint(
                    kb.createContextCoordinates(tag1, owner, null, null, null, null, SharkCS.DIRECTION_INOUT)
            ).addInformation(
                    UUID.randomUUID().toString()
            );
            kb.createContextPoint(
                    kb.createContextCoordinates(tag2, owner, null, null, null, null, SharkCS.DIRECTION_INOUT)
            ).addInformation(
                    UUID.randomUUID().toString()
            );

        } catch (SharkKBException e) {
            L.e("Knowledge Base Factory", "Could not create semantic tags for " + owner.getName() + " knowledge base");
            throw e;
        }

        kb.setOwner(owner);

        try {
            return new SyncKB(kb);
        } catch (SharkKBException e) {
            L.e("Knowledge Base Factory", "Could not create a sync KB from " + owner.getName() + "s InMemoKb.");
            throw e;
        }
    }
}
