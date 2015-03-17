package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQLSemanticNet extends SQLSTSet implements SemanticNet {

    SQLSemanticNet(SQLSharkKB kb) {
        super(kb);
    }

    @Override
    public STSet asSTSet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        SQLSemanticTagStorage sqlST = this.getSQLSemanticTagStorage(sis);
        
        if(sqlST == null) {
            sqlST = this.createSQLSemanticTag(
                this.getSSQLSharkKB(), name, null, 0, 0, false, 
                SQLSharkKB.SEMANTIC_TAG, sis, null);
        }
        
        return new SQL_SN_TX_SemanticTag(this.getSSQLSharkKB(), sqlST);
    }

    @Override
    public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si});
    }

    @Override
    public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException {
        SemanticTag st = tag;
        super.removeSemanticTag(st);
    }

    @Override
    public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        SQLSemanticTagStorage sqlST = this.getSQLSemanticTagStorage(sis);
        
        if(sqlST == null) {
            return null;
        }
        
        return new SQL_SN_TX_SemanticTag(this.getSSQLSharkKB(), sqlST);
    }

    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.getSemanticTag(new String[]{si});
    }

    @Override
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(SemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
