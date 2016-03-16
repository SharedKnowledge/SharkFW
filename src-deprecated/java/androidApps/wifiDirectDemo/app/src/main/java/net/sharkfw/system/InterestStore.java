package net.sharkfw.system;

import java.util.Iterator;
import net.sharkfw.knowledgeBase.SharkCS;

/**
 *
 * @author thsc
 */
public class InterestStore extends MessageStore<SharkCS> {

    public InterestStore() {
        super();
    }
    
    public InterestStore(long valid) {
        super(valid);
    }
    
    public Iterator<SharkCS> getInterests(long since) {
        return super.getMessages(since);
    }

    public Iterator<SharkCS> getInterests() {
        return this.getInterests(0);
    }
    
    public void addInterest(SharkCS interest) {
        super.addMessage(interest);
    }

    /**
     * Drops all message and recreates status from string
     * @param frozenStatus 
     */
    @Override
    public void restore(String frozenStatus) {
        // TODO
    }

    @Override
    public String serialize() {
        // TODO
        return "noYetImplemented";
    }
}
