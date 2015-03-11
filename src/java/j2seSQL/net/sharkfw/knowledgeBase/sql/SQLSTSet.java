package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.AbstractSTSet;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQLSTSet extends AbstractSTSet implements STSet {
    private final SQLSharkKB kb;

    SQLSTSet(SQLSharkKB kb) {
        this.kb = kb;
    }
    
    SQLSharkKB getSSQLSharkKB() {
        return this.kb;
    }
    
    /**
     * Creates a new entry in tags table.
     * @param kb
     * @param name
     * @param ewkt
     * @param startTime
     * @param durationTime
     * @param hidden
     * @param type use SQLSharkKB.SemanticTag_Type etc.
     * @param sis
     * @return
     * @throws SharkKBException
     */
    protected SQLSemanticTag createSQLSemanticTag(
            SQLSharkKB kb,
            String name, 
            String ewkt, // if spatial semantic tag
            long startTime, // if time semantic tag
            long durationTime, // if time semantic tag
            boolean hidden, 
            int type,
            String[] sis) throws SharkKBException {
/*        
                        " (id integer PRIMARY KEY default nextval('stid'), "
                        + "name character varying("+ SQLSharkKB.MAX_ST_NAME_SIZE + "), "
                        + "ewkt character varying("+ SQLSharkKB.MAX_EWKT_NAME_SIZE + "), "
                        + "startTime bigint, "
                        + "durationTime bigint, "
                        + "hidden boolean default false, "
                        + "st_type smallint"
                        + ");");
        */
        
        if(type != SQLSharkKB.SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.PEER_SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.TIME_SEMANTIC_TAG_TYPE) {
            
            throw new SharkKBException("unknown semantic tag type: " + type);
        }
        
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlHead = "INSERT INTO " + SQLSharkKB.ST_TABLE + " (";
            String sqlValues = "VALUES ("; 
            
            if(name != null) { 
                sqlHead += "name, ";
                sqlValues += "'" + name + "', "; 
            }
            
            if(ewkt != null) { 
                sqlHead += "ewkt, ";
                sqlValues += "'" + ewkt + "', "; 
            }
            
            // rest
            sqlHead += "starttime, durationtime, hidden, st_type) ";
            sqlValues += startTime + ", "
                    + durationTime + ", "
                    + "'" + String.valueOf(hidden) + "', "
                    + type
                    + ")";
            
            String hiddenString;
            hiddenString = hidden ? "'true'" : "'false'";
            
            String sqlStatement = sqlHead + sqlValues;
            
            statement.execute(sqlStatement);
            
            // add subject identifier - get new id first
            int semanticTagID = 0;
            
            ResultSet result = statement.executeQuery("select currval('stid');");
            if(result.next()) {
                // there must be a result
                semanticTagID = result.getInt(1);
            } else {
                // TODO: remove semantic tag entry!!
                throw new SharkKBException("cannot get current semantic tag primary key");
            }
            
            // insert subject identifier
            if(sis != null && sis.length > 0) {
                for (String si : sis) {
                    // insert each si into si table - duplicates are not allowed
                    String sqlString = "INSERT INTO " + SQLSharkKB.SI_TABLE + "(si, stid) VALUES ('" + si + "', '" + semanticTagID + "')";
                    try {
                        statement.execute(sqlString);
                    }
                    catch(SQLException e) {
                        // tried to insert si twice
                        // TODO: can that happen here or do we check on merge etc. in callee?
                    }
                }
            }
            
            return new SQLSemanticTag(kb, semanticTagID);
            
        } catch (SQLException ex) {
            throw new SharkKBException("cannot create semantic tag in SQL DB: " + ex.getLocalizedMessage());
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
    
    /**
     *
     * @param si
     * @return
     * @throws SharkKBException
     */
    protected SQLSemanticTag getSQLSemanticTag(String si) throws SharkKBException {    
        Statement statement = null;
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
            String sqlString = "select * from " + SQLSharkKB.ST_TABLE + 
                    " where id = (select stid from " + 
                    SQLSharkKB.SI_TABLE + " where si = '" + si + "');";
            
            ResultSet result = statement.executeQuery(sqlString);
            
            if(!result.next()) {
                // nothing found - leave
                throw new SharkKBException("tag not found with si:  " + si);
            }
            
            int stID = result.getInt("id");
            
            return new SQLSemanticTag(kb, stID);
            
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
    
    @Override
    public SemanticTag merge(SemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEnumerateHiddenTags(boolean hide) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet fragment(SemanticTag anchor) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public STSet contextualize(STSet context) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SemanticTag> stTags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
