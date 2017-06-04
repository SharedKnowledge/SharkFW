package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.system.L;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteWhereStep;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

import static org.jooq.impl.DSL.*;

/**
 * Created by Dustin Feurich on 31.03.2017.
 */
@SuppressWarnings("Duplicates")
public class SqlSharkKB implements SharkKB {

    private SharkKB sharkKB;
    private InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();

    public Connection getConnection() {
        return connection;
    }

    private Connection connection;
    private String dbAddress;
    private String password;
    private Map<String, String> properties;

    private String dialect;

    private SQLDialect jooqDialect = SQLDialect.SQLITE;
    public final String JDBC_SQLITE = "org.sqlite.JDBC";
//    public final String scriptFile = ".\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\sharkNet.sql";
    public final String scriptFile = "./src/main/java/net/sharkfw/knowledgeBase/persistent/sql/sharkNet.sql";

    /**
     * Constructor for a new database with no initial data and default SQL dialect
     * @param dbAddress
     */
    public SqlSharkKB(String dbAddress)
    {
        new SqlSharkKB(dbAddress, JDBC_SQLITE);
    }

    /**
     * Constructor for a new database with no initial data
     * @param dbAddress
     */
    public SqlSharkKB(String dbAddress, String dialect)
    {
        this.dialect = dialect;
        this.dbAddress = dbAddress;
        try {
            Class.forName(this.dialect);
            connection = DriverManager.getConnection(this.dbAddress);
            System.out.println("Opened database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            buildDatabase();
            System.out.println("Built database successfully");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Constructor for a new database with initial data from a InMemoSharkKB
     * @param sharkKB
     * @param dbAddress
     * @param dialect
     */
    public SqlSharkKB(String dbAddress, String dialect, InMemoSharkKB sharkKB)
    {
        this(dbAddress, dialect);
        initDatabase(sharkKB);
    }

    /**
     * Create tables and constraints for the new database
     * @throws FileNotFoundException
     * @throws SQLException
     */
    private void buildDatabase() throws FileNotFoundException, SQLException
    {
        File initialFile = new File(scriptFile);
        InputStream targetStream = new FileInputStream(initialFile);
        SqlHelper.importSQL(connection, targetStream);
    }

    @Override
    public void setOwner(PeerSemanticTag owner) {
        //TODO: via properties
        try {
            new SqlPeerSemanticTag(owner.getSI(), owner.getName(), this, owner.getAddresses());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PeerSemanticTag getOwner() {
//TODO: via properties
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this.informationSpaces();
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        return null; //TODO: ID Problem
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return inMemoSharkKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return inMemoSharkKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
    }

    /**
     *
     * @param sharkKB
     */
    private void initDatabase(InMemoSharkKB sharkKB)
    {

    }

    public SQLDialect getJooqDialect() {
        return jooqDialect;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public String getDialect() {
        return dialect;
    }

    @Override
    public void setSystemProperty(String name, String value) {
        // TODO Use Property table
    }

    @Override
    public String getSystemProperty(String name) {
        // TODO Use Property table
        return null;
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        //TODO:???
    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {
        //TODO:???
    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {
        //TODO:???
    }

    @Override
    public ASIPSpace asASIPSpace() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest asASIPInterest() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        // TODO get all Tags with kind topic
        return null;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        // TODO get all Tags with kind topic
        // TODO Add relations
        return null;
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SemanticNet getTypesAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public Taxonomy getTypesAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSTSet getPeerSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException {
        return null;
    }

    @Override
    public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public TimeSTSet getTimeSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInterest contextualize(ASIPSpace as, FPSet fps) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {

        String propertyString = null;
        Map<String, String> properties;

        DSLContext getEntry = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getEntry.selectFrom(table("knowledge_base")).getSQL();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null && propertyString != "") {
            properties = SqlHelper.extractProperties(propertyString);
        }
        else {
            properties = new HashMap<String, String>();
        }
        properties.put(name, value);
        SqlHelper.persistProperties(properties, this);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        String propertyString = null;
        Map<String, String> properties;

        DSLContext getEntry = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getEntry.selectFrom(table("knowledge_base")).getSQL();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null && propertyString != "") {
            properties = SqlHelper.extractProperties(propertyString);
        }
        else {
            properties = new HashMap<String, String>();
        }
        return properties.get(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        setProperty(name, value);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        String propertyString = null;
        Map<String, String> properties;

        DSLContext getEntry = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getEntry.selectFrom(table("knowledge_base")).getSQL();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null && propertyString != "") {
            properties = SqlHelper.extractProperties(propertyString);
        }
        else {
            properties = new HashMap<String, String>();
        }
        properties.remove(name);
        SqlHelper.persistProperties(properties, this);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        String propertyString = null;
        Map<String, String> properties;

        DSLContext getEntry = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getEntry.selectFrom(table("knowledge_base")).getSQL();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null && propertyString != "") {
            properties = SqlHelper.extractProperties(propertyString);
        }
        else {
            properties = new HashMap<String, String>();
        }
        return Collections.enumeration(properties.keySet());
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {

        // TODO
        // GET ALL INFOS for this space
        // Check if sqlInfoName.eq infoName
        // merge
        // else
        // add

        return null;

    }

    private ASIPInformation addInformation(ASIPInformation information, ASIPSpace space) throws SharkKBException {
        try {
            return SqlSharkHelper.addInformation(this, space, information);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation(content);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation();
        inMemoInformation.setContent(contentIS, numberOfBytes);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation();
        inMemoInformation.setContent(content);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation();
        inMemoInformation.setContent(content);
        inMemoInformation.setName(name);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation();
        inMemoInformation.setContent(content);
        inMemoInformation.setName(name);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        InMemoInformation inMemoInformation = new InMemoInformation();
        inMemoInformation.setContent(contentIS, numberOfBytes);
        inMemoInformation.setName(name);
        return this.addInformation(inMemoInformation, semanticAnnotations);
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {
        SqlSharkHelper.r
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException{
        try {
            List<SqlAsipInformation> information = SqlSharkHelper.getInformation(this, infoSpace);
            return ((List<ASIPInformation>) (List<?>) information).iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        try {
            return SqlSharkHelper.getInfoSpaces(this, null).iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {

    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        try {
            Connection connection = SqlSharkHelper.createConnection(this);
            DSLContext sql = DSL.using(connection, SQLDialect.SQLITE);
            DSLContext sql1 = DSL.using(connection, SQLDialect.SQLITE);
            DeleteWhereStep<Record> deleteInformation = sql.deleteFrom(table("information"));
            DeleteWhereStep<Record> deleteTagSet = sql1.deleteFrom(table("tag_set"));

            Condition chainedTagIds = null;
            Condition chainedIds = null;

            List<SqlAsipInformation> information = SqlSharkHelper.getInformation(this, space);
            for (SqlAsipInformation sqlAsipInformation : information) {
                Condition infoId = field("info_id").eq(inline(sqlAsipInformation.getId()));
                Condition id = field("id").eq(inline(sqlAsipInformation.getId()));

                if(chainedTagIds==null) chainedTagIds=infoId;
                else chainedTagIds = chainedTagIds.or(infoId);
                if(chainedIds==null) chainedIds=id;
                else chainedIds = chainedIds.or(id);
            }

            if(chainedTagIds!=null){
                String sqlTagSet = deleteTagSet.where(chainedTagIds).getSQL();
                L.d(sqlTagSet, sqlTagSet);
                try{
                    SqlHelper.executeSQLCommand(connection, sqlTagSet);
                } catch (SQLException e){}
            }
            if (chainedIds!=null){
                String sqlIds = deleteInformation.where(chainedIds).getSQL();
                L.d(sqlIds, sqlIds);
                try{
                    SqlHelper.executeSQLCommand(connection, sqlIds);
                } catch (SQLException e){}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SharkVocabulary getVocabulary() {
        return null;
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        return 0;
    }

    @Override
    public ArrayList<ASIPSpace> assimilate(SharkKB target, ASIPSpace interest, FragmentationParameter[] backgroundFP, Knowledge knowledge, boolean learnTags, boolean deleteAssimilated) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] fp) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, PeerSemanticTag recipient) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups) throws SharkKBException {
        return null;
    }

    @Override
    public Knowledge extract(SharkKB target, ASIPSpace context, FragmentationParameter[] backgroundFP, boolean cutGroups, PeerSemanticTag recipient) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces(ASIPSpace as, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {

    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return new FragmentationParameter[0];
    }

}
