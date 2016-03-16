package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.SNSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
class SQL_SN_TX_SemanticTag extends SQLSemanticTag implements SNSemanticTag, TXSemanticTag {
    SQL_SN_TX_SemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
    }

    protected Iterator<String> predicates(boolean target) {
        Statement statement = null;
        String sqlString = null;
        
        List predicateList = new ArrayList();
        
        String idString = target ? "sourceid" : "targetid";
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
             sqlString = "SELECT predicate FROM " + SQLSharkKB.PREDICATE_TABLE + 
                    " WHERE " + idString + " = "
                    + this.sqlST.getID();
                    
            ResultSet result = statement.executeQuery(sqlString);
            while(result.next()) {
                predicateList.add(result.getString(1));
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
        
        return predicateList.iterator();
    }

    public Iterator<String> predicates() {
        return this.predicates(true);
    }
    
    @Override
    public Enumeration<String> predicateNames() {
        return new Iterator2Enumeration(this.predicates());
    }
    
    public Iterator<String> targetPredicates() {
        return this.predicates(false);
    }
    
    @Override
    public Enumeration<String> targetPredicateNames() {
        return new Iterator2Enumeration(this.targetPredicates());
    }
    
    protected Iterator sources_SQL_SN_TX_(int targetID, String predicateName) {
        return this.connected_SQL_SN_TX_(targetID, predicateName, false);
    }
    
    protected Iterator targets_SQL_SN_TX_(int sourceID, String predicateName) {
        return this.connected_SQL_SN_TX_(sourceID, predicateName, true);
    }
    
    private Iterator connected_SQL_SN_TX_(int tagID, String predicateName, boolean isSource) {
        Statement statement = null;
        String sqlString = null;
        
        List stList = new ArrayList();
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
            /* Example
            select distinct semantictags.* from semantictags, predicates where semantictags.id = predicates.targetid and predicates.predicate = 'p1'
            */
            
            String aim = null;
            String nonaim = null;
            if(isSource) {
                aim = "targetid";
                nonaim = "sourceid";
            } else {
                aim = "sourceid";
                nonaim = "targetid";
            }
            
             sqlString = "SELECT DISTINCT " + SQLSharkKB.ST_TABLE + ".* FROM "
                     + SQLSharkKB.ST_TABLE + ", " + SQLSharkKB.PREDICATE_TABLE 
                     + " WHERE "
                     + SQLSharkKB.ST_TABLE + ".id = " 
                     + SQLSharkKB.PREDICATE_TABLE + "." + aim 
                     + " AND "
                     + SQLSharkKB.PREDICATE_TABLE + "." + nonaim 
                     + " = " + tagID
                     + " AND "
                     + SQLSharkKB.PREDICATE_TABLE + ".predicate = '"
                     + predicateName + "'";
                     
            ResultSet result = statement.executeQuery(sqlString);
            
            stList = SQLSharkKB.createSTListBySTTableEntries(this.kb, result);
        }
        catch(SQLException | SharkKBException e) {
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
        // those are only SQL_SN_TX_SemanticTag - be sure!
        return stList.iterator();
    }
    
    public Iterator<SNSemanticTag> targets(String predicateName) {
        return (Iterator<SNSemanticTag>)this.targets_SQL_SN_TX_(this.sqlST.getID(), predicateName);
    }
    
    @Override
    public Enumeration<SNSemanticTag> targetTags(String predicateName) {
        return new Iterator2Enumeration(this.targets(predicateName));
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
        
        // TODO: duplicate supression!!!
        
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
        try {
            // get target tag from kb first
            SQLSemanticTag st = (SQLSemanticTag) this.kb.getTopicSTSet().getSemanticTag(target.getSI());
            
            if(st == null) { return; }
            
            int targetID = st.getSQLSemanticTagStorage().getID();
            
            Statement statement = null;
            String sqlString = null;

            try {
                statement  = this.kb.getConnection().createStatement();

                 sqlString = "DELETE FROM " + SQLSharkKB.PREDICATE_TABLE + 
                        " WHERE  predicate = '" + type + "' AND targetid = "
                        + targetID;

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
        } catch (SharkKBException ex) {
            L.d("cannot remove predicate: " + ex.getLocalizedMessage(), this);
        }
    }

    @Override
    public void merge(SNSemanticTag toMerge) {
        super.merge(toMerge);
        
        // TODO: merge predicates as well
    }

    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration enumTags = this.getSubTags();
        return enumTags;
    }

    @Override
    public TXSemanticTag getSuperTag() {
        Iterator iter = this.targets_SQL_SN_TX_(this.sqlST.getID(), SUPER_TAG);
        if(iter == null || !iter.hasNext()) {
            return null;
        }

        Iterator<SNSemanticTag> targets = iter;
        if(targets != null && targets.hasNext()) {
            return (TXSemanticTag) targets.next();
        }
        
        return null;
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Iterator iter = this.sources_SQL_SN_TX_(this.sqlST.getID(), SUPER_TAG);
        if(iter == null || !iter.hasNext()) {
            return null;
        }
        
        return new Iterator2Enumeration(iter);
    }

    private final static String SUPER_TAG = "super";
    
    @Override
    public void move(TXSemanticTag supertag) {
        this.setPredicate(SUPER_TAG, (SNSemanticTag) supertag);
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.merge((SNSemanticTag) toMerge);
    }
}
