package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;

public class SpatialFilter implements SemanticFilter {

    @Override
    public boolean filter(ASIPInMessage message, ASIPKnowledge asipKnowledge) {
        //TODO: Hier die spatiale Auswertung vornehmen
        return false;
    }
}
