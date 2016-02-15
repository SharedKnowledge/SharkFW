package net.sharkfw.knowledgeBase.sql;

import java.sql.SQLException;
import java.sql.Statement;
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
        // take tag out of its hierarchy first
        
        SQLSemanticTagStorage sqlST = null;
        if(tag instanceof SQLSemanticTag) {
             sqlST = ((SQLSemanticTag)tag).getSQLSemanticTagStorage();
        } else {
            SQLSemanticTag s = (SQLSemanticTag)this.sn.getSemanticTag(tag.getSI());
            if(s == null) { return; }
            sqlST = s.getSQLSemanticTagStorage();
        }
        
        Statement statement = null;
        
        TXSemanticTag superTag = tag.getSuperTag();
        if(superTag != null) {
            
            SQLSemanticTagStorage superSqlST = null;
            if(superTag instanceof SQLSemanticTag) {
                 superSqlST = ((SQLSemanticTag)superTag).getSQLSemanticTagStorage();
            } else {
                SQLSemanticTag s = (SQLSemanticTag)this.sn.getSemanticTag(superTag.getSI());
                if(s == null) { return; }
                superSqlST = s.getSQLSemanticTagStorage();
            }
        
            try {
                statement  = this.kb.getConnection().createStatement();

                // HIER WEITERMACHEN
                String sqlString = "UPDATE  " + SQLSharkKB.PREDICATE_TABLE + 
                        " SET targetid = " + superSqlST.getID()
                        + " WHERE predicate = '" + SemanticNet.SUPERTAG + "'"
                        + " AND targetid = " + sqlST.getID();

                statement.execute(sqlString);

            }
            catch(SQLException e) {
                throw new SharkKBException(e.getLocalizedMessage());
            }
            finally {
                if(statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        // ignore
                    }
                }
            }
        }
            
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

    @Override
    public void removeSemanticTag(String si) throws SharkKBException {
        this.sn.removeSemanticTag(si);
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        this.sn.removeSemanticTag(sis);
    }
}
