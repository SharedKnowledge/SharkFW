package net.sharkfw.asip;

import net.sharkfw.system.MessageStore;

import java.util.Iterator;

/**
 * Created by j4rvis on 24.03.16.
 */
public class ASIPInterestStore extends MessageStore<ASIPInterest> {

    public ASIPInterestStore() {
        super();
    }

    public ASIPInterestStore(long valid) {
        super(valid);
    }

    public Iterator<ASIPInterest> getInterests(long since) {
        return super.getMessages(since);
    }

    public Iterator<ASIPInterest> getInterests() {
        return this.getInterests(0);
    }

    public void addInterest(ASIPInterest interest) {
        super.addMessage(interest);
    }

    @Override
    protected void restore(String frozenStatus) {

    }

    @Override
    protected String serialize() {
        return null;
    }
}
