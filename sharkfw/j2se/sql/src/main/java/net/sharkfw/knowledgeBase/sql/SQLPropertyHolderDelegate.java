package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

/**
 *
 * @author thsc
 */
class SQLPropertyHolderDelegate implements SystemPropertyHolder {
    private SQLPropertyHolder propertyHolder;

    SQLPropertyHolderDelegate(SQLSharkKB kb, PropertyOwner pOwner) {
        this.propertyHolder = new SQLPropertyHolder(kb, pOwner);
    }
    
    SQLPropertyHolderDelegate() {
    }
    
    protected void initPropertyHolderDelegate(SQLSharkKB kb, PropertyOwner pOwner) {
        this.propertyHolder = new SQLPropertyHolder(kb, pOwner);
    }
    
    @Override
    public void setSystemProperty(String name, String value) { 
        // no implemented and used here
    }

    @Override
    public String getSystemProperty(String name) { 
        // no implemented and used here
        return null; 
    }
    
    private void refreshPropertys() throws SharkKBException {
        this.propertyHolder.refresh();
    }
        
    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.refreshPropertys();
        this.propertyHolder.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        this.refreshPropertys();
        return this.propertyHolder.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.refreshPropertys();
        this.propertyHolder.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.refreshPropertys();
        this.propertyHolder.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        this.refreshPropertys();
        return this.propertyHolder.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        this.refreshPropertys();
        return this.propertyHolder.propertyNames(all);
    }
    
    void remove() throws SharkKBException {
        this.propertyHolder.removeAllProperties();
    }
}
