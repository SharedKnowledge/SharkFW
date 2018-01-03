package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;

import java.util.ArrayList;
import java.util.List;

public class CompositeFilter implements SemanticFilter {

    private List<SemanticFilter> childFilters = new ArrayList<SemanticFilter>();

    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest entryProfile) {
        boolean isInteresing = true;
        for (SemanticFilter filter: childFilters) {
            if (isInteresing) {
               isInteresing = filter.filter(message, newKnowledge, entryProfile);
            }
        }
        return isInteresing;
    }

    public void add(SemanticFilter filter) {
        childFilters.add(filter);
    }

    public void remove(SemanticFilter filter) {
        childFilters.remove(filter);
    }

}
