package net.sharkfw.knowledgeBase.broadcast;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.knowledgeBase.*;

import java.util.Enumeration;

public class PeerFilter implements SemanticFilter {

    @Override
    public boolean filter(ASIPInMessage message, SharkKB newKnowledge, ASIPInterest activeEntryProfile) {
        if (activeEntryProfile == null || activeEntryProfile.getApprovers() == null) return true;
        boolean isInteresting = false;
        String profileSI = null;
        if (activeEntryProfile.getApprovers() instanceof SemanticNet) {
            isInteresting = checkSemanticNet(activeEntryProfile.getApprovers(), "Peer", newKnowledge);
        }
        else {
            try {
                profileSI = activeEntryProfile.getApprovers().tags().nextElement().getSI()[0];
            } catch (SharkKBException e) {
                e.printStackTrace();
            }
            Enumeration<SemanticTag> peerTags = null;
            try {
                peerTags = newKnowledge.getPeerSTSet().tags();
            } catch (SharkKBException e) {
                e.printStackTrace();
                return false;
            }
            SemanticTag currentElement;
            while (peerTags.hasMoreElements()) {
                currentElement = peerTags.nextElement();
                if (profileSI.equals(currentElement.getSI()[0])) {
                    isInteresting = true;
                }
            }
        }
        return isInteresting;
    }

    private boolean checkSemanticNet(STSet profileSet, String netKind, SharkKB newKnowledge) {
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
}
