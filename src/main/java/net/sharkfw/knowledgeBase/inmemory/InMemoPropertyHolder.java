package net.sharkfw.knowledgeBase.inmemory;

import java.util.Enumeration;
import java.util.HashMap;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
public class InMemoPropertyHolder implements SystemPropertyHolder {
    private HashMap<String,String> properties = null;
    private HashMap<String,String> hiddenProperties = null;
    
    public HashMap<String,String> getUnhiddenProperties() {
        return this.properties;
    }
    
    public HashMap<String,String> getHiddenProperties() {
        return this.hiddenProperties;
    }
    
    public InMemoPropertyHolder() {
        this.properties = new HashMap<String,String>();
        this.hiddenProperties = new HashMap<String,String>();
    }
    
    public static final boolean DEFAULT_TRANSFER_FLAG = true;

    /**
     * Stores a property that will be transfered
     * @param name property name to set
     * @param value property value to set
     */
    @Override
    public void setProperty(String name, String value) {
        this.setProperty(name, value, DEFAULT_TRANSFER_FLAG);
    }

    /**
     * Get a property
     * @param name property name to set
     * @return property value or null
     */
    @Override
    public String getProperty(String name) {
        String value = this.properties.get(name);
       
        if(value == null) {
            value = this.hiddenProperties.get(name);
        }
        
        return value;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) {
        if(transfer) {
            if(value == null) {
                // remove
                this.properties.remove(name);
            } else {
                this.properties.put(name, value);
            }
        } else {
            if(value == null) {
                this.hiddenProperties.remove(name);
            }
            else {
                this.hiddenProperties.put(name, value);
            }
        }
    }

    /**
     * 
     * @return list of non hidden property names
     */
    @Override
    public Enumeration propertyNames() {
        return this.propertyNames(true);
    }

    @Override
    public Enumeration propertyNames(boolean all) {
        if(all) {
            return new Iterator2Enumeration(
                    this.properties.keySet().iterator(),
                    this.hiddenProperties.keySet().iterator());
        }
        
        return new Iterator2Enumeration(this.properties.keySet().iterator());
    }

    // following messages only implemented in persistent storages, e.g. file system
    @Override
    public void setSystemProperty(String name, String value) {
    }

    @Override
    public String getSystemProperty(String name) {
        return null;
    }

    @Override
    public void removeProperty(String name) {
        this.setProperty(name, null);
    }
}
