package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.ASIPSpace;

/**
 *
 * @author thsc
 */
public interface InformationCoordinates extends ASIPSpace {
    SemanticTag getTopic();
    SemanticTag getType();
    PeerSemanticTag getApprover();
    @Override
    PeerSemanticTag getSender();
    PeerSemanticTag getReceiver();
    TimeSemanticTag getTime();
    SpatialSemanticTag getLocation();
    @Override
    int getDirection();
}
