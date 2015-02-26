package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;

/**
 *
 * @author thsc
 * @param <ST>
 */
public class SQLGenericTagStorage<ST extends SemanticTag> extends 
        InMemoGenericTagStorage<ST> {
    
    private final SQLSharkKB sqlSharkKB;
    
    public SQLGenericTagStorage(SQLSharkKB sqlSharkKB) {
        this.sqlSharkKB = sqlSharkKB;
    }
    
    /**
     *
     * @param t
     * @throws SharkKBException
     */
    @Override
    protected void add(ST t) throws SharkKBException {
        if(t == null) return;
        
        super.add(t); // shouldn't be done - only use db!
        
        /* should we handle duplicate supression here ?
        guess it's already made by callee of this methode.
        */
        
        Statement statement = null;
        
        try {
            SemanticTag tag = t;
            String name = tag.getName();
            
            // tag type?
            int tagType = SQLSharkKB.SEMANTIC_TAG_TYPE; // assume it's a basic st
            
            if(tag instanceof PeerSemanticTag) {
                tagType = SQLSharkKB.PEER_SEMANTIC_TAG_TYPE;
            } else 
            if(tag instanceof TimeSemanticTag) {
                tagType = SQLSharkKB.TIME_SEMANTIC_TAG_TYPE;
            } else 
            if(tag instanceof SpatialSemanticTag) {
                tagType = SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE;
            }
            
            statement  = this.sqlSharkKB.getConnection().createStatement();
            
            if(name != null) {
                statement.execute("INSERT INTO " + SQLSharkKB.ST_TABLE + "(name, st_type) VALUES ('" + name + "', " + tagType + ")");
            } else {
                statement.execute("INSERT INTO " + SQLSharkKB.ST_TABLE + "(st_type) VALUES (" + tagType + ")");
            }
            
            int stKey = 0; // primary key newly created semantic tag row
            
            ResultSet result = statement.executeQuery("select currval('stid');");
            if(result.next()) {
                // there is a result
                stKey = result.getInt(1);
            } else {
                // strange - there should be an result
                throw new SharkKBException("cannot get current semantic tag primary key");
            }
            
            // insert subject identifier
            String[] sis = tag.getSI();
            if(sis != null && sis.length > 0) {
                for (String si : sis) {
                    // insert each si into si table - duplicates are not allowed
                    String sqlString = "INSERT INTO " + SQLSharkKB.SI_TABLE + "(si, stid) VALUES ('" + si + "', '" + stKey + "')";
                    try {
                        statement.execute(sqlString);
                    }
                    catch(SQLException e) {
                        // tried to insert si twice
                        // TODO: can that happen here or do we check on merge etc. in callee?
                    }
                }
            }
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
    
    // TODO
    @Override
    public void siAdded(String addSI, ST tag) {
        super.siAdded(addSI, tag);
    }

    // TODO
    @Override
    public void siRemoved(String deleteSI, ST tag) {
        super.siRemoved(deleteSI, tag);
    }
    
    
    /**
     * TODO must be overwritten
     * @param si
     * @return
     * @throws SharkKBException
     */
    @Override
    public ST getSemanticTag(String si) throws SharkKBException {    
        // TODO
        return null;
    }
    
    /**
     *
     * @param tag
     */
    @Override
    public void removeSemanticTag(ST tag) {
        super.removeSemanticTag(tag);
        
        // TODO: remove in db
    }
    
    /**
     *
     * @return
     */
//    @Override
//    public Enumeration<ST> tags() { 
//        // TODO
//        return null;
//    }
}
