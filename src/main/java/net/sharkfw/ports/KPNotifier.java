package net.sharkfw.ports;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.engine.ASIPConnection;

import java.io.InputStream;

/**
 * Created by j4rvis on 25.05.16.
 */
public interface KPNotifier {

    void notifyInterestReceived(ASIPInterest interest, ASIPConnection connection);

    void notifyKnowledgeReceived(ASIPKnowledge knowledge, ASIPConnection connection);

    void notifyRawReceived(InputStream inputStream, ASIPConnection connection);
}
