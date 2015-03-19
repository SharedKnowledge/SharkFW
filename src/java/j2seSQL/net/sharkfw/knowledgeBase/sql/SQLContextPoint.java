package net.sharkfw.knowledgeBase.sql;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class SQLContextPoint extends SQLPropertyHolderDelegate implements ContextPoint, PropertyOwner {
    private final SQLSharkKB kb;
    private int id;
    private ContextCoordinates coordinates = null;
    
    SQLContextPoint(SQLSharkKB kb, int id) {
        this.initPropertyHolderDelegate(kb, this);
        this.kb = kb;
        this.id = id;
    }
    
    SQLContextPoint(SQLSharkKB kb, ContextCoordinates cc) throws SharkKBException {
        this.initPropertyHolderDelegate(kb, this);
        
        this.kb = kb;
        
        int topicID = this.kb.getOrMergeTagID(cc.getTopic());
        int originatorID = this.kb.getOrMergeTagID(cc.getOriginator());
        int peerID = this.kb.getOrMergeTagID(cc.getPeer());
        int remotePeerID = this.kb.getOrMergeTagID(cc.getRemotePeer());
        int locationID = this.kb.getOrMergeTagID(cc.getLocation());
        int timeID = this.kb.getOrMergeTagID(cc.getTime());
        
        Statement statement = null;
        try {
            statement  = kb.getConnection().createStatement();
            
            ResultSet result = statement.executeQuery("select nextval('cpid');");
            if(result.next()) {
                // there must be a result
                this.id = result.getInt(1);
            } else {
                // TODO: remove cp !
                throw new SharkKBException("cannot get next semantic tag primary key");
            }
            
            String sqlString = "INSERT INTO " + SQLSharkKB.CP_TABLE
                    + " (topicid, originatorid, peerid, remotepeerid, locationid, " 
                    + "timeid, direction) VALUES ("
                    + topicID + ", "
                    + originatorID + ", "
                    + peerID + ", "
                    + remotePeerID + ", "
                    + locationID + ", "
                    + timeID + ", "
                    + cc.getDirection()
                    + ")";
            
            statement.execute(sqlString);

        } catch (SQLException e) {
            L.w("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
            throw new SharkKBException("error while creating SQL-statement: " + e.getLocalizedMessage());
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
    public Information addInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addInformation(Information source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Information addInformation(InputStream is, long len) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Information addInformation(byte[] content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Information addInformation(String content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<Information> enumInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Information> getInformation(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<Information> getInformation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInformation(Information toDelete) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextCoordinates getContextCoordinates() {
        if(this.coordinates == null) {

            Statement statement = null;
            try {
                statement  = kb.getConnection().createStatement();

                String sqlString = "SELECT topicid, originatorid, peerid, remotepeerid, locationid, " 
                        + "timeid, direction FROM " + SQLSharkKB.CP_TABLE
                        + " WHERE id = " + this.id;

                ResultSet result = statement.executeQuery(sqlString);
                
                SemanticTag topic = null;
                PeerSemanticTag originator = null;
                PeerSemanticTag peer = null;
                PeerSemanticTag remotePeer = null;
                SpatialSemanticTag location = null;
                TimeSemanticTag time = null;
                
                if(result.next()) {
                    int stid = result.getInt("topicid");
                    SQLSemanticTagStorage sqlST = null;
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        topic = SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.SEMANTIC_TAG_TYPE);
                    }
                    
                    stid = result.getInt("originatorid");
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        originator = (PeerSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
                    }
                    
                    stid = result.getInt("peerid");
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        peer = (PeerSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
                    }
                    
                    stid = result.getInt("remotepeerid");
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        remotePeer = (PeerSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
                    }
                    
                    stid = result.getInt("locationid");
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        location = (SpatialSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE);
                    }
                    
                    stid = result.getInt("timeid");
                    if(stid != SQLSharkKB.DEFAULT_ANY_TAG_ID) {
                        sqlST = new SQLSemanticTagStorage(this.kb, stid);
                        time = (TimeSemanticTag) SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.TIME_SEMANTIC_TAG_TYPE);
                    }
                    
                    int direction = result.getInt("direction");
                    
                    this.coordinates = InMemoSharkKB.createInMemoContextCoordinates(topic, originator, peer, remotePeer, time, location, direction);
                }

            } catch (SQLException | SharkKBException e) {
                L.w("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
                return null;
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
        
        return this.coordinates;
    }
    
    @Override
    public void setContextCoordinates(ContextCoordinates cc) {
        if(cc == null) { return; }
        
        Statement statement = null;
        try {
            int topicID = this.kb.getOrMergeTagID(cc.getTopic());
            int originatorID = this.kb.getOrMergeTagID(cc.getOriginator());
            int peerID = this.kb.getOrMergeTagID(cc.getPeer());
            int remotePeerID = this.kb.getOrMergeTagID(cc.getRemotePeer());
            int locationID = this.kb.getOrMergeTagID(cc.getLocation());
            int timeID = this.kb.getOrMergeTagID(cc.getTime());

            statement  = this.kb.getConnection().createStatement();

            String sqlString = "UPDATE " + SQLSharkKB.CP_TABLE
                    + " SET topicid = " + topicID
                    + ", SET originatorid = " + originatorID
                    + ", SET peerid = " + peerID
                    + ", SET remotepeerid = " + remotePeerID
                    + ", SET locationid = " + locationID
                    + ", SET timeid = " + timeID
                    + " WHERE id = " + this.id;

            statement.execute(sqlString);
            this.coordinates = InMemoSharkKB.createInMemoCopy(cc);
            
        } catch (SQLException | SharkKBException e) {
            L.w("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
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
    public int getNumberInformation() {
        Statement statement = null;
        try {
            statement  = kb.getConnection().createStatement();

            String sqlString = "SELECT COUNT(id) FROM " + SQLSharkKB.INFORMATION_TABLE
                    + " WHERE cpid = " + this.id;

            ResultSet result = statement.executeQuery(sqlString);
            
            if(result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            L.w("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
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
        
        return 0;
    }

    @Override
    public void setListener(ContextPointListener cpl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getType() {
        return SQLSharkKB.CONTEXT_POINT;
    }
}
