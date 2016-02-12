package net.sharkfw.knowledgeBase;

/**
 *
 * @author thsc
 */
public interface InformationCoordinates {
    SemanticTag getTopic();
    SemanticTag getType();
    PeerSemanticTag getApprover();
    PeerSemanticTag getSender();
    PeerSemanticTag getReceiver();
    TimeSemanticTag getTime();
    SpatialSemanticTag getLocation();
    int getDirection();
}
