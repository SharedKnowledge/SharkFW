package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.protocols.RequestHandler;

import java.util.Iterator;

/**
 *
 * @author thsc
 */
public interface ASIPStub extends SharkStub, RequestHandler {
    public boolean callListener(ASIPInMessage msg);

    Iterator<ASIPInterest> getSentInterests(long since);

    Iterator<ASIPKnowledge> getSentKnowledge(long since);

    Iterator<ASIPInterest> getUnhandledInterests(long since);

    Iterator<ASIPKnowledge> getUnhandledKnowledge(long since);

    void removeSentHistory();
}
