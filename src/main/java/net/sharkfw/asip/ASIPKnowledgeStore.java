package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.system.MessageStore;

import java.util.Iterator;

/**
 * Created by j4rvis on 24.03.16.
 */
public class ASIPKnowledgeStore extends MessageStore<ASIPKnowledge> {

    public ASIPKnowledgeStore() {
        super();
    }

    public ASIPKnowledgeStore(long valid) {
        super(valid);
    }

    public Iterator<ASIPKnowledge> getKnowledge(long since) {
        return super.getMessages(since);
    }

    public Iterator<ASIPKnowledge> getKnowledge() {
        return this.getKnowledge(0);
    }

    public void addKnowledge(ASIPKnowledge k) {
        super.addMessage(k);
    }

    @Override
    protected void restore(String frozenStatus) {

    }

    @Override
    protected String serialize() {
        return null;
    }
}
