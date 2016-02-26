package net.sharkfw.kep;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPInMessage;

/**
 *
 * @author thsc
 */
public interface KEPStub extends SharkStub {
    public boolean handleMessage(KEPInMessage msg);
    
    boolean callListener(KEPInMessage msg);
    
    Iterator<SharkCS> getSentInterests(long since);
            
    Iterator<Knowledge> getSentKnowledge(long since);
    
    Iterator<SharkCS> getUnhandledInterests(long since);
            
    Iterator<Knowledge> getUnhandledKnowledge(long since);
    
    void removeSentHistory();
}
