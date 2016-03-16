package net.sharkfw.system;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;

/**
 *
 * @author thsc
 */
public class KnowledgeStore extends MessageStore<Knowledge> {
    public KnowledgeStore() {
        super();
    }
    
    public KnowledgeStore(long valid) {
        super(valid);
    }

    public Iterator<Knowledge> getKnowledge(long since) {
        return super.getMessages(since);
    }

    public Iterator<Knowledge> getKnowledge() {
        return this.getKnowledge(0);
    }
    
    public void addKnowledge(Knowledge k) {
        super.addMessage(k);
    }

    @Override
    protected void restore(String frozenStatus) {
        // TODO
    }

    @Override
    protected String serialize() {
        // TODO
        return "noYetImplemented";
    }
    
}
