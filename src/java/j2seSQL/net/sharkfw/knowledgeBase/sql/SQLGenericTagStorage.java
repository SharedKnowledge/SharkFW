package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.geom.inmemory.InMemoSharkGeometry;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.knowledgeBase.inmemory.InMemoSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

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
        Statement statement = null;
        
        ST st = null;
        InMemoSemanticTag semanticTag = null;
        
        // HIER WEITERMACHEN
        try {
            statement  = this.sqlSharkKB.getConnection().createStatement();
            
            String sqlString = "select * from " + SQLSharkKB.ST_TABLE + 
                    " where id = (select stid from " + 
                    SQLSharkKB.SI_TABLE + " where si = '" + si + "');";
            
            ResultSet result = statement.executeQuery(sqlString);
            
            if(!result.next()) {
                // nothing found - leave
                return null;
            }
            
            // each st has at least a name - and here an stid
            String name = result.getString("name");
            int stID = result.getInt("id");
            int stType = result.getInt("st_type");
            String[] sis = this.sqlSharkKB.getSIs(stID);
            
            SQLPropertyHolder sqlPropertyHolder = new SQLPropertyHolder(stID, SQLSharkKB.SEMANTIC_TAG);

            switch(stType) {
                case SQLSharkKB.SEMANTIC_TAG_TYPE: 
                    semanticTag = new SQL_SN_TX_SemanticTag(name, sis, stID, sqlPropertyHolder);
                    break;
                    
                case SQLSharkKB.PEER_SEMANTIC_TAG_TYPE: 
                    String[] addresses = this.getPeerAddresses(stID);
                    semanticTag = new SQL_SN_TX_PeerSemanticTag(name, sis, addresses, stID, sqlPropertyHolder);
                    break;
                    
                case SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE: 
                    String ewkt = result.getString("ewkt");
                    
                    // TODO: cast is never good
                    semanticTag = (InMemoSemanticTag) InMemoSharkGeometry.createGeomByEWKT(ewkt);
                    semanticTag.setPropertyHolder(sqlPropertyHolder);
                    break;
                    
                case SQLSharkKB.TIME_SEMANTIC_TAG_TYPE: 
                    long start = result.getLong("starttime");
                    long duration = result.getLong("duration");
                    
                    // TODO: cast is never good
                    semanticTag = (InMemoSemanticTag) InMemoSharkKB.createInMemoTimeSemanticTag(start, duration);
                    semanticTag.setPropertyHolder(sqlPropertyHolder);
                    break;
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
        
        st = (ST) semanticTag;
        return st;
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

    private String[] getPeerAddresses(int stID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
