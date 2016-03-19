package net.sharkfw.kep;

import net.sharkfw.asip.SharkStub;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPInMessage;
import net.sharkfw.protocols.RequestHandler;

/**
 *
 * @author thsc
 */
public interface KEPStub extends SharkStub, RequestHandler, KEPMessageAccounting {
    public boolean handleMessage(KEPInMessage msg);
    
    boolean callListener(KEPInMessage msg);
    
    Iterator<SharkCS> getSentInterests(long since);
            
    Iterator<Knowledge> getSentKnowledge(long since);
    
    Iterator<SharkCS> getUnhandledInterests(long since);
            
    Iterator<Knowledge> getUnhandledKnowledge(long since);
    
    void removeSentHistory();
}
