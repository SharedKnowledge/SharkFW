package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQL_SN_TX_SemanticTag extends SQLSemanticTag implements SNSemanticTag {
    public SQL_SN_TX_SemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
    }

    @Override
    public Enumeration<String> predicateNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> targetPredicateNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<SNSemanticTag> targetTags(String predicateName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removePredicate(String type, SNSemanticTag target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void merge(SNSemanticTag toMerge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
