package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticNet;

/**
 *
 * @author thsc
 */
public class SQLSemanticNet extends SQLSTSet implements SemanticNet {

    SQLSemanticNet(SQLSharkKB kb, int type) {
        super(kb, SQLSharkKB.SEMANTIC_TAG_TYPE);
    }

    @Override
    public STSet asSTSet() {
        return this;
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
        
        SQLSemanticTagStorage sqlTag = null;
        // remove predicates
        if(tag instanceof SQLSemanticTag) {
            sqlTag = ((SQLSemanticTag)tag).getSQLSemanticTagStorage();
        } else {
            sqlTag = this.getSQLSemanticTagStorage(tag.getSI());
        }
        
        sqlTag.removeAllPredicates();
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
        source.setPredicate(type, target);    
    }

    @Override
    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException {
        source.removePredicate(type, target);
    }

    @Override
    public SemanticNet fragment(SemanticTag anchor, 
        FragmentationParameter fp) throws SharkKBException {
        
        SemanticNet fragment = new InMemoSemanticNet();
        return SharkCSAlgebra.fragment(fragment, anchor, this, 
                fp.getAllowedPredicates(), 
                fp.getForbiddenPredicates(), fp.getDepth());
    }
    
    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException {
        return this.fragment(anchor, this.getDefaultFP());
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, 
        FragmentationParameter fp) throws SharkKBException {
        
            SemanticNet fragment = new InMemoSemanticNet();
            
            if(fp != null) {
                SharkCSAlgebra.contextualize(fragment, anchorSet, this, 
                        fp.getAllowedPredicates(), fp.getForbiddenPredicates(), 
                        fp.getDepth());
            } else {
                SharkCSAlgebra.contextualize(fragment, this, anchorSet);
            }
            
            return fragment;
    }

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) 
            throws SharkKBException {
        
        return this.contextualize(anchorSet, this.getDefaultFP());
    }

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) 
            throws SharkKBException {
        
        if(context == null) return null;
        
        return this.contextualize(context.tags(), fp);
    }
    
    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException {
        return this.contextualize(context, this.getDefaultFP());
    }

    @Override
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException {
        SharkCSAlgebra.merge(this, remoteSemanticNet);
    }

    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException {
        return (SNSemanticTag) super.merge(source);
    }

    @Override
    public void add(SemanticTag tag) throws SharkKBException {
        throw new SharkKBException("adding a tag is not supported in SQL implementation");
    }
}