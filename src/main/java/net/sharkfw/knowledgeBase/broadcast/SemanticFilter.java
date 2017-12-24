package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;

public interface SemanticFilter {

    public boolean filter(ASIPInMessage message, ASIPKnowledge asipKnowledge);
}
