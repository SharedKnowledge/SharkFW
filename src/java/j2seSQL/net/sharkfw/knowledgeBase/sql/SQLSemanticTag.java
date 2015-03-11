package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQLSemanticTag implements SemanticTag {
    private SQLPropertyHolder propertyHolder;
    protected final SQLSemanticTagStorage sqlST;
    protected final SQLSharkKB kb;
    
    SQLSemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        this.kb = kb;
        this.sqlST = sqlST;
        this.propertyHolder = new SQLPropertyHolder(kb, sqlST);
    }
    
    protected SQLSemanticTagStorage getSQLSemanticTagStorage() {
        return this.sqlST;
    }
    
    @Override
    public String getName() {
        return this.sqlST.getName();
    }

    @Override
    public String[] getSI() {
        try {
            return this.sqlST.getSIS();
        } catch (SharkKBException ex) {
            // TODO
        }
        
        return null;
    }

    @Override
    public void removeSI(String si) throws SharkKBException {
        this.sqlST.removeSI(si);
    }

    @Override
    public void addSI(String si) throws SharkKBException {
        this.sqlST.addSI(si);
    }

    @Override
    public void setName(String name) {
        try {
            this.sqlST.setName(name);
        } catch (SharkKBException ex) {
            // todo
        }
    }

    @Override
    public void merge(SemanticTag st) {
        SharkCSAlgebra.merge(this, st);
    }

    @Override
    public void setHidden(boolean isHidden) {
        try {
            this.sqlST.setHidden(isHidden);
        } catch (SharkKBException ex) {
            // TODO
        }
    }

    @Override
    public boolean hidden() {
        return this.sqlST.isHidden();
    }

    @Override
    public boolean isAny() {
        return SharkCSAlgebra.isAny(this);
    }

    @Override
    public boolean identical(SemanticTag other) {
        return SharkCSAlgebra.identical(this, other);
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
        this.sqlST.remove();
    }
}
