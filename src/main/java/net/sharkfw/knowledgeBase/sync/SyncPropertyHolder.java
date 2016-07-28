package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.util.Enumeration;

/**
 * Created by thsc on 28.07.16.
 */
class SyncPropertyHolder implements SystemPropertyHolder {

    private final PropertyHolder target;

    SyncPropertyHolder(PropertyHolder target) {
        this.target = target;
    }

    protected PropertyHolder getTarget() {
        return this.target;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {

    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {

    }

    @Override
    public void removeProperty(String name) throws SharkKBException {

    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return null;
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return null;
    }

    @Override
    public void setSystemProperty(String name, String value) {

    }

    @Override
    public String getSystemProperty(String name) {
        return null;
    }
}
