package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;

public class PeerFilter implements SemanticFilter {

    private Dimension dimension;

    public PeerFilter(Dimension dimension) {
        if (dimension == Dimension.SENDER || dimension == Dimension.APPROVERS || dimension == Dimension.RECEIVERS) {
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
            case SENDER:
                isInteresting = checkPeerSemanticTag(newKnowledge, activeEntryProfile);
            case RECEIVERS:
                if (activeEntryProfile.getReceivers() instanceof SemanticNet) {
                    isInteresting = checkPeerSemanticNet(activeEntryProfile.getTopics(), newKnowledge);
                }
                else {
                    isInteresting = checkPeerSemanticTag(newKnowledge, activeEntryProfile);
                }
                break;
            case APPROVERS:
                if (activeEntryProfile.getApprovers() instanceof SemanticNet) {
                    isInteresting = checkPeerSemanticNet(activeEntryProfile.getTypes(), newKnowledge);
                }
                else {
                    isInteresting = checkPeerSemanticTag(newKnowledge, activeEntryProfile);
                }
                break;
            default:
                isInteresting = false;
                break;
        }
        return isInteresting;
    }

    private boolean checkPeerSemanticTag(SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        boolean isInteresting = false;
        String profileSI = null;
        try {
            if (dimension == Dimension.SENDER) {
                profileSI = activeEntryProfile.getSender().getSI()[0];
            }
            else if (dimension == Dimension.RECEIVERS) {
                profileSI = activeEntryProfile.getReceivers().tags().nextElement().getSI()[0];
            }
            else {
                profileSI = activeEntryProfile.getApprovers().tags().nextElement().getSI()[0];
            }
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
        Enumeration<SemanticTag> dimensionTags = null;
        try {
            dimensionTags = newKnowledge.getPeerSTSet().tags();
        }
        catch (SharkKBException e) {
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

    private boolean checkPeerSemanticNet(STSet profileSet, SharkKB newKnowledge) {
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
