package net.sharkfw.knowledgeBase.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.AbstractSharkKB;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.InformationCoordinates;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.SpatialSemanticTag;
import net.sharkfw.knowledgeBase.Taxonomy;
import net.sharkfw.knowledgeBase.TimeSTSet;
import net.sharkfw.knowledgeBase.TimeSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
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
    
    private int ownerID;
    
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
        SemanticNet topics = new SQLSemanticNet(this, SQLSharkKB.SEMANTIC_TAG_TYPE);
        PeerTaxonomy peers = new SQLPeerTaxonomy(this, new SQLPeerSemanticNet(this));
        SpatialSTSet locations = new SQLSpatialSTSet(this);
        TimeSTSet times = new SQLTimeSTSet(this);
        
        this.setTopics(topics);
        this.setPeers(peers);
        this.setLocations(locations);
        this.setTimes(times);
        
        this.refreshStatus();
        
        // TODO attach knowledge
    }
    
    @Override
    public void refreshStatus() {
        // don't call super method
        
        Statement statement = null;
        ArrayList<Information> infoList = new ArrayList<>();
        try {
            statement  = this.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("SELECT (ownerid) from "); 
            sqlString.append(SQLSharkKB.KNOWLEDGEBASE);
            
            // we assume there is just a single knowledge base per database..
            
            ResultSet result = statement.executeQuery(sqlString.toString());
            while(result.next()) {
                int ownerid = result.getInt("ownerid");
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
    
    public static final int MAX_BUFFER_SIZE = 10000;
    
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
                        + "contentlength bigint, "
                        + "name character varying("+ SQLSharkKB.MAX_ST_NAME_SIZE + "), "
                        + "contentType character varying("+ SQLSharkKB.MAX_ST_NAME_SIZE + "), "
                        + "creationtime bigint, "
                        + "lastmodifiedtime bigint"
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
    
    public static final int DEFAULT_ANY_TAG_ID = -1;
    
    int getAnyTagID() throws SharkKBException {
        SemanticTag any = this.getTopicSTSet().getSemanticTag((String)null);
        if(any == null) {
            return SQLSharkKB.DEFAULT_ANY_TAG_ID;
        }
        
        return ((SQLSemanticTag)any).getSQLSemanticTagStorage().getID();
    }
    
    int getOrMergeTagID(SemanticTag tag) throws SharkKBException {
        if(tag == null) { 
            // it's the ANY tag
            return this.getAnyTagID();
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
    public void setOwner(PeerSemanticTag owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getOwner() {
        try {
            return (PeerSemanticTag) SQLSharkKB.wrapSQLTagStorage(this,
                    new SQLSemanticTagStorage(this, this.ownerID),
                    SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
        } catch (SharkKBException ex) {
            // TODO
            return null;
        }
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
        
    private void addWhereClause(StringBuffer sqlStatement, String idString, STSet stset, boolean matchAny) throws SharkKBException {
        
        /* matchAny: As soon as stset is any - any tag is allowed in this dimension.
        which means: no contraints to define in where clause
        */
        if(matchAny) {
            if(SharkCSAlgebra.isAny(stset)) return;
        }
        
        HashSet idSet = new HashSet();
        if(stset != null && !stset.isEmpty()) {
            Iterator<SemanticTag> stTags = stset.stTags();
            while(stTags != null && stTags.hasNext()) {
                SemanticTag tag = stTags.next();
                idSet.add(this.getOrMergeTagID(tag));
            }
            
            Iterator iterator = idSet.iterator();
            boolean first = true;
            while(iterator.hasNext()) {
                if(first) {
                    sqlStatement.append(" ( ");
                    first = false;
                } else {
                    sqlStatement.append(" OR ");
                }
                sqlStatement.append(" ").append(idString).append(" = ").append(iterator.next());
            }

            if(!first) {
                    sqlStatement.append(" ) AND ");
            }
        } else {
            // stset empty or null
            if(!matchAny) {
                // any tag is not a joker - it's a must!!
                sqlStatement.append("( ");
                sqlStatement.append(idString);
                sqlStatement.append(" = ");
                sqlStatement.append(this.getAnyTagID());
                sqlStatement.append(" ) AND ");
            }
        }
    }
    
    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        ArrayList cpList = new ArrayList();
        Statement statement = null;
        try {
            
            statement  = connection.createStatement();
            
            StringBuffer sqlStatement = new StringBuffer();
            sqlStatement.append("SELECT id FROM " + SQLSharkKB.CP_TABLE + " WHERE ");
                    
            this.addWhereClause(sqlStatement, "topicid", cs.getTopics(), matchAny);
            this.addWhereClause(sqlStatement, "peerid", cs.getPeers(), matchAny);
            this.addWhereClause(sqlStatement, "remotepeerid", cs.getRemotePeers(), matchAny);
            this.addWhereClause(sqlStatement, "locationid", cs.getLocations(), matchAny);
            this.addWhereClause(sqlStatement, "timeid", cs.getTimes(), matchAny);
            
            int originatorID = this.getOrMergeTagID(cs.getOriginator());
            if(originatorID != this.getAnyTagID() || !matchAny ) {
                sqlStatement.append(" originatorid = " + originatorID);
                sqlStatement.append(" AND ");
            }
            
            if(cs.getDirection() == SharkCS.DIRECTION_INOUT) {
                sqlStatement.append(" ( direction = ").append(SharkCS.DIRECTION_IN);
                sqlStatement.append(" OR direction = ").append(SharkCS.DIRECTION_OUT);
                sqlStatement.append(" OR direction = ").append(SharkCS.DIRECTION_INOUT);
                sqlStatement.append(" ) ");
            } else {
                sqlStatement.append(" direction = ").append(cs.getDirection());
            }
            
            ResultSet result = statement.executeQuery(sqlStatement.toString());
            cpList = this.cpList(result);
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
        
        return cpList.iterator();
    }
    
    private ArrayList<SQLContextPoint> cpList(ResultSet result) throws SQLException {
        ArrayList<SQLContextPoint> cpList = new ArrayList();
        while(result.next()) {
            int cpid = result.getInt(1);
            SQLContextPoint sqlCP = new SQLContextPoint(this, cpid);
            cpList.add(sqlCP);
        }
        
        return cpList;
    }

    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        ArrayList<SQLContextPoint> cpList = null;
        Statement statement = null;
        try {
            
            statement  = connection.createStatement();
            
            String sqlStatement = "SELECT id FROM " + SQLSharkKB.CP_TABLE;
            
            ResultSet result = statement.executeQuery(sqlStatement);
            cpList = this.cpList(result);
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
            
        return null;
    }

    @Override
    public InformationCoordinates createInformationCoordinates(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInterest asASIPInterest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInformation(Information info, ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSTSet sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
