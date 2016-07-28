package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.util.Enumeration;

/**
 * Created by thsc on 28.07.16.
 */
class SyncPropertyHolder implements SystemPropertyHolder {
    private final SystemPropertyHolder target;

    SyncPropertyHolder(SystemPropertyHolder target) {
        this.target = target;
    }

    protected PropertyHolder getTarget() {
        return this.target;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.target.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.target.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.target.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.target.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.target.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.target.propertyNames(all);
    }

    @Override
    public void setSystemProperty(String name, String value) {
        this.target.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return this.target.getSystemProperty(name);
    }
    
    protected void setTimeStamp() {
        String timeString = Long.toString(System.currentTimeMillis());
        this.target.setSystemProperty(SyncKB.TIME_PROPERTY_NAME, timeString);
    }
    
    protected long getTimeStamp() {
        String timeString = this.target.getSystemProperty(SyncKB.TIME_PROPERTY_NAME);
        if(timeString == null) {
            return Long.MIN_VALUE;
        }
        
        return Long.parseLong(timeString);
    }
}
