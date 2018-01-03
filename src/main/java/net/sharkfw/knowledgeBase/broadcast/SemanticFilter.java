package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;

public interface SemanticFilter {
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest entryProfile);
}
