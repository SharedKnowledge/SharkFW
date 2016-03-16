package net.sharkfw.knowledgeBase.sql;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import net.sharkfw.system.Iterator2Enumeration;
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
            
            StringBuilder sqlString = new StringBuilder("INSERT INTO ");
            sqlString.append(SQLSharkKB.CP_TABLE);
            sqlString.append(" (id, topicid, originatorid, peerid, remotepeerid, locationid, "); 
            sqlString.append("timeid, direction) VALUES (");
            sqlString.append(this.id).append(", ");
            StringBuilder append = sqlString.append(topicID).append(", ");
            sqlString.append(originatorID).append(", ");
            sqlString.append(peerID).append(", ");
            sqlString.append(remotePeerID).append(", ");
            sqlString.append(locationID).append(", ");
            sqlString.append(timeID).append(", ");
            sqlString.append(cc.getDirection());
            sqlString.append(")");
            
            statement.execute(sqlString.toString());

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
        return new SQLInformation(this.kb, this.id, null);
    }

    @Override
    public void addInformation(Information source) {
        // merge info into this system
        Information newInfo = this.addInformation();
        
        try {
            newInfo.setName(source.getName());
            newInfo.setContentType(source.getContentType());
            newInfo.streamContent(source.getOutputStream());
        } catch (SharkKBException ex) {
            L.e("cannot copy information: " + ex.getLocalizedMessage(), this);
        }
    }

    @Override
    public Information addInformation(InputStream is, long len) {
        Information newInfo = this.addInformation();
        newInfo.setContent(is, len);
        
        return newInfo;
    }

    @Override
    public Information addInformation(byte[] content) {
        Information newInfo = this.addInformation();
        newInfo.setContent(content);
        
        return newInfo;
    }

    @Override
    public Information addInformation(String content) {
        Information newInfo = this.addInformation();
        newInfo.setContent(content);
        
        return newInfo;
    }

    @Override
    public Enumeration<Information> enumInformation() {
        return new Iterator2Enumeration(this.getInformation());
    }

    @Override
    public Iterator<Information> getInformation(String name) {
        Statement statement = null;
        ArrayList<Information> infoList = new ArrayList<>();
        try {
            statement  = this.kb.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("SELECT id from "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" WHERE cpid = ");
            sqlString.append(this.id);
            
            if(name != null && name.length() > 0) {
                sqlString.append(" AND name = '");
                sqlString.append(name);
                sqlString.append("'");
            }
            
            ResultSet result = statement.executeQuery(sqlString.toString());
            while(result.next()) {
                int iid = result.getInt(1);
                SQLInformation sqlInformation = new SQLInformation(this.kb, iid);
                infoList.add(sqlInformation);
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
        
        return infoList.iterator();
    }

    @Override
    public Iterator<Information> getInformation() {
        return this.getInformation(null);
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
