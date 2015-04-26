package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.inmemory.InMemoPropertyHolder;

/**
 *
 * @author thsc
 */
public class PropertyHolderDelegate implements SystemPropertyHolder {
    private SystemPropertyHolder propertyHolder = null;

    protected PropertyHolderDelegate(SystemPropertyHolder persistentHolder) {
        this.propertyHolder = persistentHolder;
    }
    
    protected PropertyHolderDelegate() {
        this.propertyHolder = new InMemoPropertyHolder();
    }
    
    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.propertyHolder.getProperty(name);
    }
    
    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.propertyHolder.setProperty(name, value);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.propertyHolder.setProperty(name, value, transfer);
    }

    @Override
    public Enumeration propertyNames() throws SharkKBException {
        return this.propertyHolder.propertyNames();
    }

    @Override
    public Enumeration propertyNames(boolean all) throws SharkKBException {
        return this.propertyHolder.propertyNames(all);
    }
    
    public SystemPropertyHolder getPropertyHolder() {
        return this.propertyHolder;
    }
    
    public void setPropertyHolder(SystemPropertyHolder ph) {
        this.propertyHolder = ph;
    }
    
    /**
     * Save system status with system properties - handle with care.
     * Application developers should consult advanced programmers guide before
     * using that feature.
     */
    public void persist() {}

    /**
     * Refresh system status from status properties.
     */
    public void refreshStatus() {}

    @Override
    public void setSystemProperty(String name, String value) {
        this.propertyHolder.setSystemProperty(name, value);
    }

    @Override
    public String getSystemProperty(String name) {
        return this.propertyHolder.getSystemProperty(name);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.setProperty(name, null);
    }
}
