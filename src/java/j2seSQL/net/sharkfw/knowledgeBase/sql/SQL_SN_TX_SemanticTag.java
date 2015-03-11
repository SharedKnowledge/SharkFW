package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

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
        Statement statement = null;
        String sqlString = null;
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
            // HIER WEITERMACHEN!!
             sqlString = "SELECT * FROM " + SQLSharkKB.ST_TABLE
                    + " WHERE id = (SELECT (targetid) FROM " + SQLSharkKB.PREDICATE_TABLE + 
                    " WHERE sourceid = "
                    + this.sqlST.getID() + " AND predicate = '"
                    + predicateName + "')";
                    
            ResultSet result = statement.executeQuery(sqlString);
            
            while(result.next()) {
                result.getInt(1);
            }
        }
        catch(SQLException e) {
            L.l("couldn't execute: " + sqlString + " because: " + e.getLocalizedMessage(), this);
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
        
        return null; // TODO
    }

    @Override
    public Enumeration<SNSemanticTag> sourceTags(String predicateName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPredicate(String type, SNSemanticTag target) {
        SQLSemanticTagStorage targetSQLst = null;
        try {
            // target already in this storage?
            targetSQLst = this.kb.getSQLSemanticTagStorage(target.getSI());
            if(targetSQLst == null) {
                this.kb.getTopicSTSet().merge(target);
                targetSQLst = this.kb.getSQLSemanticTagStorage(target.getSI());
            }
        } catch (SharkKBException ex) {
            // TODO
        }
        
        Statement statement = null;
        String sqlString = null;
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
             sqlString = "INSERT INTO " + SQLSharkKB.PREDICATE_TABLE + 
                    " (predicate, sourceid, targetid) VALUES ('"
                    + type + "', "
                    + this.sqlST.getID() + ", "
                    + targetSQLst.getID()
                    + ")";
                    
            statement.execute(sqlString);
        }
        catch(SQLException e) {
            L.l("couldn't execute: " + sqlString + " because: " + e.getLocalizedMessage(), this);
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

    @Override
    public void removePredicate(String type, SNSemanticTag target) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void merge(SNSemanticTag toMerge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
