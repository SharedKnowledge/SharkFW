package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

import java.util.Enumeration;
import net.sharkfw.system.L;

/**
 * Created by thsc on 28.07.16.
 */
abstract class SyncPropertyHolder extends Sync implements SystemPropertyHolder {
    private final SystemPropertyHolder target;
    public static final Long UNKNOWN_TIME = Long.MIN_VALUE;

    SyncPropertyHolder(SystemPropertyHolder target) {
        this.target = target;
        try {
            if(this.getTimeStamp() == SyncPropertyHolder.UNKNOWN_TIME) {
                this.changed();
            }
        }
        catch(SharkKBException e) {
            L.e("cannot write time stampf to properties");
        }
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
    
    /**
     * Taht method is to be called whenever a change occurred on that entity.
     */
    protected final void changed() {
        String timeString = Long.toString(System.currentTimeMillis());
        
        try {
            this.target.setProperty(SyncKB.TIME_PROPERTY_NAME, timeString);
        } 
        catch(SharkKBException e) {
            L.e("cannot write time stamp - sync won't work accordingly");
        }
    }
    
    protected final long getTimeStamp() throws SharkKBException {
        return SyncKB.getTimeStamp(this.target);
    }
}
