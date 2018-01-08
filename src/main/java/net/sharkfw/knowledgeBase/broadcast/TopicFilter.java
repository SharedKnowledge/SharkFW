package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;

public class TopicFilter implements SemanticFilter {

    private Dimension dimension;

    public TopicFilter(Dimension dimension) {
        if (dimension == Dimension.TOPIC || dimension == Dimension.TYPE) {
            this.dimension = dimension;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        if (activeEntryProfile == null) return true;
        boolean isInteresting = false;
        switch (dimension){
            case TOPIC:
                if (activeEntryProfile.getTopics() instanceof SemanticNet) {
                    isInteresting = checkSemanticNet(activeEntryProfile.getTopics(), newKnowledge);
                }
                else {
                    isInteresting = checkSemanticTag(newKnowledge, activeEntryProfile);
                }
                break;
            case TYPE:
                if (activeEntryProfile.getTypes() instanceof SemanticNet) {
                    isInteresting = checkSemanticNet(activeEntryProfile.getTypes(), newKnowledge);
                }
                else {
                    isInteresting = checkSemanticTag(newKnowledge, activeEntryProfile);
                }
                break;
            default:
                isInteresting = false;
                break;

        }
        return isInteresting;
    }

    private boolean checkSemanticTag(SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        boolean isInteresting = false;
        String profileSI = null;
        try {
            if (dimension == Dimension.TOPIC) {
                profileSI = activeEntryProfile.getTopics().tags().nextElement().getSI()[0];
            }
            else {
                profileSI = activeEntryProfile.getTypes().tags().nextElement().getSI()[0];
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        Enumeration<SemanticTag> dimensionTags = null;
        try {
            if (dimension == Dimension.TOPIC) {
                dimensionTags = newKnowledge.getTopicSTSet().tags();
            }
            else {
                dimensionTags = newKnowledge.getTypeSTSet().tags();
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
        SemanticTag currentElement;
        while (dimensionTags.hasMoreElements()) {
            currentElement = dimensionTags.nextElement();
            if (profileSI.equals(currentElement.getSI()[0])) {
                isInteresting = true;
            }
        }
        return isInteresting;
    }

    private boolean checkSemanticNet(STSet profileSet, SharkKB newKnowledge) {
        SemanticNet inputNet = null;
        FragmentationParameter fp = new FragmentationParameter(true, true, 10); //TODO: use the user data for FP
        try {
            inputNet = (SemanticNet) newKnowledge.getTopicSTSet();
        } catch (SharkKBException e) {
            e.printStackTrace();
            return false;
        }
        SemanticNet resultNet = null;
        try {
            resultNet = SharkCSAlgebra.contextualize(inputNet, profileSet, fp);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        if (resultNet == null || resultNet.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    public Dimension getDimension() {
        return dimension;
    }
}
