package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by msc on 25.05.16.
 */
public interface KPNotifier {

    public void notifyInterestReceived(ASIPInterest interest);

    public void notifyKnowledgeReceived(ASIPKnowledge knowledge);

    public void notifyPeerReceived(PeerSemanticTag peer);
}
