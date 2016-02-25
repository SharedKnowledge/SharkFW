package net.sharkfw.kep;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KEPInMessage;

/**
 *
 * @author thsc
 */
public abstract class KEPStub extends SharkStub {
    public abstract boolean handleMessage(KEPInMessage msg);
    
    protected abstract boolean callListener(KEPInMessage msg);
    
    public abstract Iterator<SharkCS> getSentInterests(long since);
            
    public abstract Iterator<Knowledge> getSentKnowledge(long since);
    
    public abstract  Iterator<SharkCS> getUnhandledInterests(long since);
            
    public abstract Iterator<Knowledge> getUnhandledKnowledge(long since);
    
    public abstract void removeSentHistory();
}
