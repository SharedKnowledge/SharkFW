package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import org.jooq.SQLDialect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by Dustin Feurich on 31.03.2017.
 */
public class SqlSharkKB implements SharkKB {

    private SharkKB sharkKB;

    public Connection getConnection() {
        return connection;
    }

    private Connection connection;
    private String dbAddress;
    private String password;

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
        try {
            new SqlPeerSemanticTag(owner.getSI(), owner.getName(), -1, this, owner.getAddresses());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PeerSemanticTag getOwner() {
        return null;
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

    }

    @Override
    public String getSystemProperty(String name) {
        return null;
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {

    }

    @Override
    public void semanticTagRemoved(SemanticTag tag, STSet stset) {

    }

    @Override
    public void semanticTagChanged(SemanticTag tag, STSet stset) {

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
        return null;
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
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

    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return null;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {

    }

    @Override
    public void removeProperty(String name) throws SharkKBException {

    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return null;
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, String content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, byte[] content, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPInformation addInformation(String name, InputStream contentIS, int numberOfBytes, ASIPSpace semanticAnnotations) throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformation(ASIPInformation info, ASIPSpace infoSpace) throws SharkKBException {

    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public void removeInformationSpace(ASIPSpace space) throws SharkKBException {

    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {

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
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return null;
    }

    @Override
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return null;
    }

    @Override
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
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
