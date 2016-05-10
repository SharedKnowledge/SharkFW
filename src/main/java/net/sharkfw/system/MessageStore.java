package net.sharkfw.system;

import java.util.ArrayList;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.PropertyHolder;

/**
 *
 * @author thsc
 */
public abstract class MessageStore<T> {
    private ArrayList<MessageSlot> messages;
    private final long valid;
    
    public MessageStore() {
        this(Long.MAX_VALUE);
    }

    /**
     * 
     * @param ph
     * @param valid in milliseconds
     */
    public MessageStore(long valid) {
        this.messages = new ArrayList();
        
        this.valid = valid;
    }
    
    protected abstract void restore(String frozenStatus);
    protected abstract String serialize();
    
    protected Iterator<T> getMessages(long since) {
        ArrayList<T> tempMsgList = new ArrayList();
        
        int size = this.messages.size();
        int index = 0;
        long now = System.currentTimeMillis();
        
        while(index < size) {
            MessageSlot entry = this.messages.get(index);
            
            // entry expire?
            if(this.valid != Long.MAX_VALUE && entry.getTime()+this.valid < now) {
                // to old
                this.messages.remove(index);
                size--;
            }
            else {
                T message = (T) entry.getMessage();
                tempMsgList.add(message);
                index++;
            }
        }
        
        return tempMsgList.iterator();
    }
    
    protected Iterator<T> getMessages() {
        return this.getMessages(0); // get all
    }
    
    protected void addMessage(T message) {
        if(message != null) {
            MessageSlot entry = new MessageSlot(message);
            this.messages.add(entry);
        }
    }
    
    private class MessageSlot<T> {
        private final T message;
        private final long time;
        
        MessageSlot(T message) {
            this.message = message;
            this.time = System.currentTimeMillis();
        }
        
        T getMessage() {
            return this.message;
        }
        
        long getTime() {
            return this.time;
        }
    }
}
