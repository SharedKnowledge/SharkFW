package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQLSemanticTag extends SQLPropertyHolderDelegate implements SemanticTag {
    protected final SQLSemanticTagStorage sqlST;
    protected final SQLSharkKB kb;
    
    SQLSemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
        this.kb = kb;
        this.sqlST = sqlST;
    }
    
    SQLSemanticTagStorage getSQLSemanticTagStorage() {
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
    void remove() throws SharkKBException {
        super.remove();
        this.sqlST.remove();
    }
}
