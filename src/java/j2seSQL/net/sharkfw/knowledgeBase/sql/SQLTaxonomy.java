package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TaxonomyWrapper;

/**
 *
 * @author thsc
 */
public class SQLTaxonomy extends TaxonomyWrapper implements Taxonomy {
    private final SQLSharkKB kb;
    
    SQLTaxonomy(SQLSharkKB kb, SemanticNet sn) {
        super(sn);
        this.kb = kb;
    }

    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        if(tag instanceof SNSemanticTag) {
            this.sn.removeSemanticTag((SNSemanticTag) tag);
        } else {
            this.sn.removeSemanticTag(tag);
        }
    }

    @Override
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        return this.cast2TX(this.sn.createSemanticTag(name, sis));
    }

    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.cast2TX(this.sn.getSemanticTag(sis));
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        this.sn.removeSemanticTag(tag);
    }
    
    private TXSemanticTag cast2TX(SNSemanticTag snST) throws SharkKBException {
        if(snST == null) {
            return null;
        }
        
        if(snST instanceof TXSemanticTag) {
            return (TXSemanticTag)snST;
        } // else
        
        throw new SharkKBException("internal error: SQLTaxonomy not based on SemanticNet");
    }
}
