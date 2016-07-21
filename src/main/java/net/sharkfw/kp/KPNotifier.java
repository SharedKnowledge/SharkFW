package net.sharkfw.kp;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.io.InputStream;

/**
 * Created by msc on 25.05.16.
 */
public interface KPNotifier {

    void notifyInterestReceived(ASIPInterest interest, ASIPConnection connection);

    void notifyKnowledgeReceived(ASIPKnowledge knowledge, ASIPConnection connection);

    void notifyRawReceived(InputStream inputStream, ASIPConnection connection);
}
