package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

import java.util.ArrayList;
import java.util.Enumeration;

public class TimeFilter implements SemanticFilter {
    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        TimeSTSet messageTimesSet = null;
        Enumeration<TimeSemanticTag> messageTimesTags = null;
        Enumeration<TimeSemanticTag> profileTimesTags = null;
        try {
            messageTimesSet = newKnowledge.getTimeSTSet();
            messageTimesTags = messageTimesSet.timeTags();
            profileTimesTags = activeEntryProfile.getTimes().timeTags();
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        if (activeEntryProfile == null || activeEntryProfile.getTimes() == null || messageTimesSet == null) {
            return true;
        }
        ArrayList<TimeSemanticTag> profileTimesTagsList = new ArrayList<>();
        while (profileTimesTags.hasMoreElements()) {
            profileTimesTagsList.add(profileTimesTags.nextElement());
        }
        if (profileTimesTagsList.size() == 0) return true;
        if (!messageTimesTags.hasMoreElements()) return true;

        TimeSemanticTag currentMessageTag = null;
        while (messageTimesTags.hasMoreElements()) {
            currentMessageTag = messageTimesTags.nextElement();
            for (TimeSemanticTag currentProfileTag: profileTimesTagsList) {
                if (currentMessageTag.getFrom() > currentProfileTag.getFrom() &&
                        currentMessageTag.getFrom() + currentMessageTag.getDuration() <
                        currentProfileTag.getFrom() + currentProfileTag.getDuration()) {
                    return true;
                }
            }
        }
        return false;
    }

}
