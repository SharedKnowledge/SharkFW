package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeFilter implements SemanticFilter {

    private List<SemanticFilter> childFilters = new ArrayList<SemanticFilter>();

    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest entryProfile) {
        boolean isInteresing = true;
        int i = 0;
        while (isInteresing && i < childFilters.size()) {
            isInteresing =  childFilters.get(i).filter(message, newKnowledge, entryProfile);
            i++;
        }
        return isInteresing;
    }

    public void add(SemanticFilter filter) {
        childFilters.add(filter);
    }

    public void remove(SemanticFilter filter) {
        childFilters.remove(filter);
    }

    public int getFilterCount() {
        return childFilters.size();
    }

    public void swapFilterPosition(int oldPosition, int newPosition) {
        if (newPosition < childFilters.size() - 1 && oldPosition < childFilters.size() - 1) {
            Collections.swap(childFilters, oldPosition, newPosition);
        }
    }

    public List<SemanticFilter> getAll() {
        return new ArrayList<>(childFilters);
    }

}
