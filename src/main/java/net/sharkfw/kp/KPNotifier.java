package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by msc on 25.05.16.
 */
public interface KPNotifier {

    public void notifyInterestReceived(ASIPInterest interest, ASIPConnection connection);

    public void notifyKnowledgeReceived(ASIPKnowledge knowledge, ASIPConnection connection);
}
