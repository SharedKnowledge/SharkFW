package net.sharkfw.knowledgeBase.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.AbstractSharkKB;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.EnumerationChain;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;

/**
 * This shall become a SQL implementation of the SharkKB.
 * 
 * Scatch:
 * properties:
 * <ul>
 * <li>Properties</li>
 * <li>Owner</li>
 * <li>Knowledge</li>
 * <li>Vocabulary: STSet, PeerSTSet, SpatialSTSet, TimeSTSet</li>
 * </ul>
 * 
 * @author thsc
 */
public class SQLSharkKB extends AbstractSharkKB implements SharkKB {

    private Connection connection;
    private String connectionString;
    private String user;
    private String pwd;
    
    static final int UNKNOWN_SEMANTIC_TAG_TYPE = -1;
    static final int SEMANTIC_TAG_TYPE = 0;
    static final int PEER_SEMANTIC_TAG_TYPE = 1;
    static final int SPATIAL_SEMANTIC_TAG_TYPE = 2;
    static final int TIME_SEMANTIC_TAG_TYPE = 3;
    
    static final int SEMANTIC_TAG = 0;
    static final int CONTEXT_POINT = 1;
    static final int KNOWLEDGEBASE = 2;
    static final int INFORMATION = 3;
    
    public SQLSharkKB(String connectionString, String user, String pwd) throws SharkKBException {
	try {
            this.connectionString = connectionString;
            this.user = user;
            this.pwd = pwd;
            connection = DriverManager.getConnection(connectionString, user, pwd);
 	} catch (SQLException e) {
            throw new SharkKBException("cannot connect to database: " + e.getLocalizedMessage());
 	}
        
 	if (connection == null) {
            throw new SharkKBException("cannot connect to database: reason unknown");
	}
        
        // check if tables already created - if not - do it
        this.setupKB();
        
        /************     setup vocabulary       **************/
        SemanticNet topics = new SQLSemanticNet(this);
        PeerTaxonomy peers = new SQLPeerTaxonomy(this, new SQLPeerSemanticNet(this));
        SpatialSTSet locations = new SQLSpatialSTSet(this);
        TimeSTSet times = new SQLTimeSTSet(this);
        
        this.setTopics(topics);
        this.setPeers(peers);
        this.setLocations(locations);
        this.setTimes(times);
        
        // TODO attach knowledge
    }
    
    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        if(this.topics instanceof Taxonomy) {
            return (Taxonomy) this.topics;
        } else {
            if(this.topics instanceof SemanticNet) {
                return new SQLTaxonomy(this, (SemanticNet)this.topics);
            } else {
                throw new SharkKBException("topic semantic tag set is not a taxonomy and cannot be used as taxonomy");
            }
        }
    }
    
    Connection getConnection() {
        return this.connection;
    }

    public static final String SHARKKB_TABLE = "knowledgebase";
    public static final String ST_TABLE = "semantictags";
    public static final String PROPERTY_TABLE = "properties";
    public static final String SI_TABLE = "subjectidentifier";
    public static final String ADDRESS_TABLE = "addresses";
    public static final String CP_TABLE = "contextpoints";
    public static final String PREDICATE_TABLE = "predicates";
    public static final String INFORMATION_TABLE = "information";
    
    public static final String MAX_SI_SIZE = "200";
    public static final String MAX_ST_NAME_SIZE = "200";
    public static final String MAX_EWKT_NAME_SIZE = "200";
    public static final String MAX_PROPERTY_NAME_SIZE = "200";
    public static final String MAX_PROPERTY_VALUE_SIZE = "200";
    public static final String MAX_ADDR_SIZE = "200";
    public static final String MAX_PREDICATE_SIZE = "200";
    
    /**
     * Tables: 
     * <ul>
     * <li>SemanticTags</li>
     * <li>Properties</li>
     * <li>SubjectIdentifier</li>
     * <li>addresses</li>
     * <li>knowledgebase</li>
     * <li>contextpoints</li>
     * <Iul>
     */
    private void setupKB() throws SharkKBException {
        Statement statement = null;
        try {
            statement  = connection.createStatement();
            
            /************** Knowledge base table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.SHARKKB_TABLE);
                L.d(SQLSharkKB.SHARKKB_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.SHARKKB_TABLE + "does not exists - create", this);
                try { statement.execute("drop sequence kbid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence kbid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.SHARKKB_TABLE + 
                        " (id integer PRIMARY KEY default nextval('kbid'), "
                        + "ownerID integer" // foreign key in st table
                        + ");");
            }

            /************** semantic tag table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.ST_TABLE);
                L.d(SQLSharkKB.ST_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.ST_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence stid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence stid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.ST_TABLE + 
                        " (id integer PRIMARY KEY default nextval('stid'), "
                        + "name character varying("+ SQLSharkKB.MAX_ST_NAME_SIZE + "), "
                        + "ewkt character varying("+ SQLSharkKB.MAX_EWKT_NAME_SIZE + "), "
                        + "startTime bigint, "
                        + "durationTime bigint, "
                        + "hidden boolean default false, "
                        + "st_type smallint"
                        + ");");
            }

            /************** properties table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.PROPERTY_TABLE);
                L.d(SQLSharkKB.PROPERTY_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.PROPERTY_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence propertyid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence propertyid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.PROPERTY_TABLE + 
                        " (id integer PRIMARY KEY default nextval('propertyid'), "
                        + "name character varying("+ SQLSharkKB.MAX_PROPERTY_NAME_SIZE + "), "
                        + "value character varying("+ SQLSharkKB.MAX_PROPERTY_VALUE_SIZE + "), "
                        + "ownerID integer, "
                        + "hidden boolean default false, "
                        + "entity_type smallint"
                        + ");");
            }
            
            /************** si table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.SI_TABLE);
                L.d(SQLSharkKB.SI_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.SI_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence siid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence siid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.SI_TABLE + 
                        " (id integer PRIMARY KEY default nextval('siid'), "
                        + "si character varying("+ SQLSharkKB.MAX_SI_SIZE + ") UNIQUE, "
                        + "stID integer"
                        + ");");
            }
            
            /************** addresses table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.ADDRESS_TABLE);
                L.d(SQLSharkKB.ADDRESS_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.ADDRESS_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence addrid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence addrid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.ADDRESS_TABLE + 
                        " (id integer PRIMARY KEY default nextval('addrid'), "
                        + "addr character varying("+ SQLSharkKB.MAX_ADDR_SIZE + "), "
                        + "stID integer"
                        + ");");
            }
            
            /************** predicate table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.PREDICATE_TABLE);
                L.d(SQLSharkKB.PREDICATE_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.PREDICATE_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence predicateid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence predicateid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.PREDICATE_TABLE + 
                        " (id integer PRIMARY KEY default nextval('predicateid'), "
                        + "predicate character varying("+ SQLSharkKB.MAX_PREDICATE_SIZE + "), "
                        + "sourceID integer, targetID integer"
                        + ");");
            }
            
            /************** contextpoints table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.CP_TABLE);
                L.d(SQLSharkKB.CP_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.CP_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence cpid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence cpid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.CP_TABLE + 
                        " (id integer PRIMARY KEY default nextval('cpid'), "
                        + "topicID integer, "
                        + "originatorID integer, "
                        + "peerID integer, "
                        + "remotePeerID integer, "
                        + "locationID integer, "
                        + "timeID integer, "
                        + "direction smallint"
                        + ");");
            }
            
            /************** information table *****************************/
            try {
                statement.execute("SELECT * from " + SQLSharkKB.INFORMATION_TABLE);
                L.d(SQLSharkKB.INFORMATION_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
                L.d(SQLSharkKB.INFORMATION_TABLE + " does not exists - create", this);
                try { statement.execute("drop sequence infoid;"); }
                catch(SQLException ee) { /* ignore */ }
                statement.execute("create sequence infoid;");
                statement.execute("CREATE TABLE " + SQLSharkKB.INFORMATION_TABLE + 
                        " (id integer PRIMARY KEY default nextval('infoid'), "
                        + "cpID integer, "
                        + "content bytea, "
                        + "name character varying("+ SQLSharkKB.MAX_ST_NAME_SIZE + ")"
                        + ");");
            }
            
        } catch (SQLException e) {
            L.w("error while setting up tables: " + e.getLocalizedMessage(), this);
            throw new SharkKBException("error while setting up tables: " + e.getLocalizedMessage());
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
    
    SQLSemanticTagStorage getSQLSemanticTagStorage(String[] sis) throws SharkKBException {
        if(sis == null || sis.length == 0) {
            return null;
        }
        
        Statement statement = null;
        
        try {
            statement  = this.getConnection().createStatement();
            
            String sqlString = "select * from " + SQLSharkKB.ST_TABLE + 
                    " where id = (select stid from " + 
                    SQLSharkKB.SI_TABLE + " where si = '" + sis[0] + "'";
            
            for(int i = 1; i < sis.length; i++) {
                sqlString += " OR si = '" + sis[i] + "'";
            }
            
            sqlString += ");";
            
            ResultSet result = statement.executeQuery(sqlString);
            
            if(!result.next()) {
                // nothing found - leave
                return null;
            }
            
            int stID = result.getInt("id");
            
            return new SQLSemanticTagStorage(this, stID);
            
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
    
    /**
     * Removes all tables in SQL database which store Shark data
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void drop() throws SharkKBException {
        Statement statement = null;
        try {
            statement  = connection.createStatement();
            
            /************** Knowledge base table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.SHARKKB_TABLE);
            }
            catch(SQLException e) {
                // go ahead
            }

            /************** semantic tag table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.ST_TABLE);
            }
            catch(SQLException e) {
            }

            /************** properties table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.PROPERTY_TABLE);
            }
            catch(SQLException e) {
            }
            
            /************** properties table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.PREDICATE_TABLE);
            }
            catch(SQLException e) {
            }
            
            /************** si table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.SI_TABLE);
            }
            catch(SQLException e) {
            }
            
            /************** addresses table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.ADDRESS_TABLE);
            }
            catch(SQLException e) {
            }
            
            /************** contextpoints table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.CP_TABLE);
            }
            catch(SQLException e) {
            }
            
            /************** information table *****************************/
            try {
                statement.execute("DROP TABLE " + SQLSharkKB.INFORMATION_TABLE);
            }
            catch(SQLException e) {
            }
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
    
    /**
     * close database
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void close() throws SharkKBException {
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException ex) {
            throw new SharkKBException(ex.getLocalizedMessage());
        }
    }
    
    /**
     * Reconnect after prior close. Note: An open connection would be
     * closed an re-opened.
     * 
     * Note also: A jdbc connection is already opened when constructor is
     * called.
     * 
     * @throws SharkKBException 
     */
    public void reconnect() throws SharkKBException {
        if(this.connection != null) {
            this.close();
        }

        try {
            this.connection = DriverManager.getConnection(connectionString, user, pwd);
        } catch (SQLException ex) {
            throw new SharkKBException(ex.getLocalizedMessage());
        }
    }
    
    /**
     * JDBC connection is open or not
     * @return 
     */
    public boolean connected() {
        return this.connection != null;
    }
    
    String[] getSIs(int id) {
        String[] sis = null;
        
        Statement statement = null;
        try {
            statement  = connection.createStatement();
            
            ArrayList<String> sisList = new ArrayList(); 
            ResultSet result = statement.executeQuery("SELECT si from " + SQLSharkKB.SI_TABLE + " where stid = " + id + ";");
            while(result.next()) {
                sisList.add(result.getString(1));
            }

            if(!sisList.isEmpty()) {
                sis = new String[sisList.size()];
                Iterator<String> sisIter = sisList.iterator();
                for(int i = 0; i < sis.length; i++) {
                    sis[i] = sisIter.next();
                }
            }
        } catch (SQLException e) {
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
        
        return sis;
    }
    
    /**
     *
     * @param result result set of semantic tags table - must constist of id
     * @return
     * @throws SQLException
     * @throws SharkKBException
     */
    static List<SemanticTag> createSTListBySTTableEntries(SQLSharkKB kb, ResultSet result) throws SQLException, SharkKBException {
        List<SemanticTag> tagList = new ArrayList<>();
        while (result.next()) {
            int id = result.getInt("id");
            SQLSemanticTagStorage sqlST = new SQLSemanticTagStorage(kb, id);
            
            SemanticTag newTag = SQLSharkKB.wrapSQLTagStorage(kb, sqlST, SQLSharkKB.UNKNOWN_SEMANTIC_TAG_TYPE);
            
            tagList.add(newTag);
        }
        return tagList;
    }
    
    static SQLSemanticTag wrapSQLTagStorage(SQLSharkKB kb, SQLSemanticTagStorage sqlST, int wishedType) throws SharkKBException {
        if(sqlST == null) return null;
        
        SQLSemanticTag newTag = null;

        int type = sqlST.getType();
        
        if(wishedType != SQLSharkKB.UNKNOWN_SEMANTIC_TAG_TYPE && wishedType != type) {
            throw new SharkKBException("type mismatch: semantic tag type in database differs from wrapper type");
        }
       
        switch (type) {
            case SQLSharkKB.PEER_SEMANTIC_TAG_TYPE:
                newTag = new SQL_SN_TX_PeerSemanticTag(kb, sqlST);
                break;
            case SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE:
                newTag = new SQLSpatialSemanticTag(kb, sqlST);
                break;
            case SQLSharkKB.TIME_SEMANTIC_TAG_TYPE:
                newTag = new SQLTimeSemanticTag(kb, sqlST);
                break;
            default:
                newTag = new SQL_SN_TX_SemanticTag(kb, sqlST);
        }
        
        return newTag;
    }
    
    SQLSemanticTag getOrCreateAnyTag() throws SharkKBException {
        return (SQLSemanticTag) this.getTopicSTSet().createSemanticTag((String) null, (String) null);
    }
    
    int getOrMergeTagID(SemanticTag tag) throws SharkKBException {
        if(tag == null) { 
            // it's the ANY tag
            SQLSemanticTag anyTag = this.getOrCreateAnyTag();
            return anyTag.getSQLSemanticTagStorage().getID();
        }
        
        SQLSemanticTagStorage sqlTag = null;
        
        if(tag instanceof SQLSemanticTag) {
            sqlTag = ((SQLSemanticTag) tag).getSQLSemanticTagStorage();
        } else{
            sqlTag = ((SQLSemanticTag) this.getTopicSTSet().merge(tag)).getSQLSemanticTagStorage();
        }
        
        return sqlTag.getID();
    }

    SQLSemanticNet getTopicsAsSQLSemanticNet() {
        return (SQLSemanticNet)this.topics;
    }
    
    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return new SQLPeerSTSet(this);
    }
    
    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return new SQLPeerSemanticNet(this);
    }
    
    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        return this.getTopicSTSet().tags();
    }
    
    @Override
    public Interest createInterest(STSet topics, PeerSemanticTag originator, PeerSTSet peers, PeerSTSet remotePeers, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getOwner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextPoint getContextPoint(ContextCoordinates cc) throws SharkKBException {
        int topicID = this.getOrMergeTagID(cc.getTopic());
        int originatorID = this.getOrMergeTagID(cc.getOriginator());
        int peerID = this.getOrMergeTagID(cc.getPeer());
        int remotePeerID = this.getOrMergeTagID(cc.getRemotePeer());
        int locationID = this.getOrMergeTagID(cc.getLocation());
        int timeID = this.getOrMergeTagID(cc.getTime());
        
        Statement statement = null;
        try {
            statement  = this.getConnection().createStatement();
            
            String sqlString = "SELECT id FROM " + SQLSharkKB.CP_TABLE
                    + " WHERE " 
                    + "topicid = " + topicID + " AND "
                    + "originatorid = " + originatorID + " AND "
                    + "peerid = " + peerID + " AND "
                    + "remotepeerid = " + remotePeerID + " AND "
                    + "locationid = " + locationID + " AND "
                    + "timeid = " + timeID + " AND "
                    + "direction = " + cc.getDirection();
            
            ResultSet result = statement.executeQuery(sqlString);
            if(result.next()) {
                int cpID = result.getInt(1);
                return new SQLContextPoint(this, cpID);
            }

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
        
        return null;
    }

    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return InMemoSharkKB.createInMemoContextCoordinates(topic, originator, peer, remotepeer, time, location, direction);
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        if(coordinates == null) { return null; }
        
        return new SQLContextPoint(this, coordinates);   
    }

    @Override
    public Knowledge createKnowledge() {
        return InMemoSharkKB.createInMemoKnowledge(this);
    }

    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        Statement statement = null;
        ArrayList<SQLContextPoint> cpList = new ArrayList();
        try {
            
            statement  = connection.createStatement();
            
            String sqlStatement = "SELECT id FROM " + SQLSharkKB.CP_TABLE;
            
            ResultSet result = statement.executeQuery(sqlStatement);
            while(result.next()) {
                int cpid = result.getInt(1);
                SQLContextPoint sqlCP = new SQLContextPoint(this, cpid);
                cpList.add(sqlCP);
            }
        }
        catch(SQLException e) {
            // go ahead
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
            
        return new Iterator2Enumeration(cpList.iterator());
    }

    @Override
    public Interest createInterest() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SemanticTag> getTags() throws SharkKBException {
        EnumerationChain eChain = new EnumerationChain();
        eChain.addEnumeration(this.tags());
        
        return eChain;
    }
}
