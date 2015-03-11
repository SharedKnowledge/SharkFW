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
    protected final SQLSemanticTagStorage sqlST;
    
    public SQLSemanticTag(SQLSemanticTagStorage sqlST) throws SharkKBException {
        this.sqlST = sqlST;
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
        this.sqlST.setName(name);
    }

    @Override
    public void merge(SemanticTag st) {
        SharkCSAlgebra.merge(this, st);
    }

    @Override
    public void setHidden(boolean isHidden) {
        this.sqlST.setHidden(isHidden);
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

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.sqlST.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.sqlST.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.sqlST.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.sqlST.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.sqlST.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.sqlST.propertyNames(all);
    }
}
