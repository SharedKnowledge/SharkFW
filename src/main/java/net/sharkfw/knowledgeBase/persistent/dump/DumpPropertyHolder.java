package net.sharkfw.knowledgeBase.persistent.dump;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SystemPropertyHolder;

/**
 *
 * @author thsc
 */
class DumpPropertyHolder implements SystemPropertyHolder {
    private final DumpPersistentSharkKB dumpKB;
    private SystemPropertyHolder systemPropertyHolder;
    private final PropertyHolder propertyHolder;
    
    DumpPropertyHolder(DumpPersistentSharkKB aThis, SystemPropertyHolder ph) {
        this(aThis, (PropertyHolder) ph);
        this.systemPropertyHolder = ph;
    }

    DumpPropertyHolder(DumpPersistentSharkKB aThis, PropertyHolder ph) {
        this.dumpKB = aThis;
        this.propertyHolder = ph;
        this.systemPropertyHolder = null;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSystemProperty(String name, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSystemProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
