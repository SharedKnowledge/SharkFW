package net.sharkfw.knowledgeBase.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.KnowledgeBaseListener;
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
import net.sharkfw.knowledgeBase.geom.SharkGeometry;
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
public class SQLSharkKB implements SharkKB {
    private Connection connection;
    private String connectionString;
    private String user;
    private String pwd;
    
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
    }

    public static final String SHARKKB_TABLE = "knowledgebase";
    public static final String ST_TABLE = "semantictags";
    public static final String PROERTIES_TABLE = "properties";
    public static final String SI_TABLE = "subjectidentifier";
    public static final String ADDRESS_TABLE = "addresses";
    public static final String CP_TABLE = "contextpoints";
    public static final String MAX_SI_SIZE = "200";
    
    
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
    private void setupKB() {
        Statement statement;
        
        try {
        
        // CREATE TABLE distributors (
  //   did    integer PRIMARY KEY DEFAULT nextval('serial'),
  //   name   varchar(40) NOT NULL CHECK (name <> '')
        
        // create knowledge base table
        
        // already exists?
            statement = connection.createStatement();
            
            try {
                statement.execute("SELECT * from " + SQLSharkKB.SHARKKB_TABLE);
                
                L.d(SQLSharkKB.SHARKKB_TABLE + " already exists", this);
            }
            catch(SQLException e) {
                // does not exist: create
//                statement.execute("CREATE TABLE " + SQLSharkKB.SHARKKB_TABLE + 
//                        " id ,"
//                        + "owner varchar(" + SQLSharkKB.MAX_SI_SIZE + ")");
                L.d(SQLSharkKB.SHARKKB_TABLE + "does not exists - create", this);
                try {
                    statement.execute("drop sequence kbid;");
                }
                catch(SQLException ee) {
                    // ignore
                }
                
                statement.execute("create sequence kbid;");
                statement.execute("CREATE TABLE knowledgebase (id integer PRIMARY KEY default nextval('kbid'), owner character varying(200));");
            }
//            statement.execute("INSERT INTO test(name) VALUES ('t');");
//            
//            ResultSet results = statement.executeQuery("SELECT * FROM test;");
            // do someting with those results
        } catch (SQLException e) {
            System.err.println("Excuting SQL statment failed");
            System.err.println(e.getMessage());
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
    

    @Override
    public void setOwner(PeerSemanticTag owner) {
    }

    @Override
    public PeerSemanticTag getOwner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextCoordinates createContextCoordinates(SemanticTag topic, PeerSemanticTag originator, PeerSemanticTag peer, PeerSemanticTag remotepeer, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContextPoint createContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Knowledge createKnowledge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest createInterest() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSemanticTag(SemanticTag st) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void persist() {
        // nothing todo - it's a database we are working with
    }

    @Override
    public SharkCS asSharkCS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest asInterest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /////////////////////////////////////////////////////////////////////////
    //                           Vocabulary                                //
    /////////////////////////////////////////////////////////////////////////
    
    @Override
    public SemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag getSemanticTag(String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest contextualize(SharkCS as) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Interest contextualize(SharkCS as, FragmentationParameter[] fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<SemanticTag> tags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SemanticTag> getTags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //////////////////////////////////////////////////////////////////////////
    //                        Property Management                           //
    //////////////////////////////////////////////////////////////////////////
    
    @Override
    public void setSystemProperty(String name, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSystemProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeProperty(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //////////////////////////////////////////////////////////////////////////
    //                        Change Notifications                          //
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //////////////////////////////////////////////////////////////////////////
    //                        Interest Management                           //
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void addInterest(SharkCS interest) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeInterest(SharkCS interest) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<SharkCS> interests() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //////////////////////////////////////////////////////////////////////////
    //   deprecated in SharkKB interface - dont implement that stuff        //
    //////////////////////////////////////////////////////////////////////////
    
    @Override
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
