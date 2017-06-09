package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoInformation;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;

/**
 * Created by Dustin Feurich on 31.03.2017.
 */
@SuppressWarnings("Duplicates")
public class SqlSharkKB extends SqlSharkPropertyHolder implements SharkKB {

    public final String JDBC_SQLITE = "org.sqlite.JDBC";
    //    public final String scriptFile = ".\\src\\main\\java\\net\\sharkfw\\knowledgeBase\\persistent\\sql\\sharkNet.sql";
    public String scriptFile = "./src/main/java/net/sharkfw/knowledgeBase/persistent/sql/sharkNet.sql";
    private SharkKB sharkKB;
    private InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
    private Connection connection;
    private String dbAddress;
    private String password;
    private String dialect;

    /**
     * Constructor for a new database with no initial data and default SQL dialect
     *
     * @param dbAddress
     */
    public SqlSharkKB(String dbAddress) {
        super(TABLE_KNOWLEDGE_BASE);
        new SqlSharkKB(dbAddress, JDBC_SQLITE);
    }

    public SqlSharkKB(String dbAddress, String dialect, InputStream stream) {
        super(TABLE_KNOWLEDGE_BASE);
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
            buildDatabase(stream);
            System.out.println("Built database successfully");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for a new database with no initial data
     *
     * @param dbAddress
     */
    public SqlSharkKB(String dbAddress, String dialect) {
        this(dbAddress, dialect, (InputStream) null);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public int getId() {
        return 1;
    }

    /**
     * Create tables and constraints for the new database
     *
     * @throws FileNotFoundException
     * @throws SQLException
     */
    private void buildDatabase(InputStream stream) throws FileNotFoundException, SQLException {
        if (stream == null) {
            File initialFile = new File(scriptFile);
            stream = new FileInputStream(initialFile);
        }
        SqlHelper.importSQL(connection, stream);
    }

    /**
     * @param sharkKB
     */
    private void initDatabase(InMemoSharkKB sharkKB) {

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
    public PeerSemanticTag getOwner() {
        //TODO: via properties
        return null;
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
    public String getSystemProperty(String name) {
        // TODO Use Property table
        return null;
    }

    @Override
    public void semanticTagCreated(SemanticTag tag, STSet stset) {
        //TODO:???
    }

    @Override
    public Iterator<ASIPInformationSpace> getAllInformationSpaces() throws SharkKBException {
        return this.informationSpaces();
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
    public Iterator<ASIPInformationSpace> getInformationSpaces(ASIPSpace space) throws SharkKBException {
        try {
            return SqlSharkHelper.getInfoSpaces(this, space).iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
    public ASIPSpace createASIPSpace(STSet topics, STSet types, PeerSTSet approvers, PeerSemanticTag sender, PeerSTSet receiver, TimeSTSet times, SpatialSTSet locations, int direction) throws SharkKBException {
        return inMemoSharkKB.createASIPSpace(topics, types, approvers, sender, receiver, times, locations, direction);
    }

    @Override
    public STSet getTopicSTSet() throws SharkKBException {
        // TODO get all Tags with kind topic
        // TODO GANZ SCHLIMMER WORKARAOUND!!!!!!!
        return InMemoSharkKB.createInMemoSTSet();
    }

    @Override
    public SemanticNet getTopicsAsSemanticNet() throws SharkKBException {
        // TODO get all Tags with kind topic
        // TODO Add relations
        return null;
    }

    @Override
    public ASIPSpace createASIPSpace(SemanticTag topic, SemanticTag type, PeerSemanticTag approver, PeerSemanticTag sender, PeerSemanticTag receiver, TimeSemanticTag time, SpatialSemanticTag location, int direction) throws SharkKBException {
        return inMemoSharkKB.createASIPSpace(topic, type, approver, sender, receiver, time, location, direction);
    }

    @Override
    public Taxonomy getTopicsAsTaxonomy() throws SharkKBException {
        return null;
    }

    @Override
    public STSet getTypeSTSet() throws SharkKBException {
        // TODO GANZ SCHLIMMER WORKARAOUND!!!!!!!
        return InMemoSharkKB.createInMemoSTSet();
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
        // TODO GANZ SCHLIMMER WORKARAOUND!!!!!!!
        return InMemoSharkKB.createInMemoPeerSTSet();
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
        // TODO GANZ SCHLIMMER WORKARAOUND!!!!!!!
        return InMemoSharkKB.createInMemoTimeSTSet();
    }

    @Override
    public SpatialSTSet getSpatialSTSet() throws SharkKBException {
        // TODO GANZ SCHLIMMER WORKARAOUND!!!!!!!
        return InMemoSharkKB.createInMemoSpatialSTSet();
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
    public ASIPInformationSpace mergeInformation(Iterator<ASIPInformation> information, ASIPSpace space) throws SharkKBException {
        try {
            List<SqlAsipInformation> informationList = SqlSharkHelper.getInformation(this, space, true);
            if (informationList.isEmpty()) {
                while (information.hasNext()) {
                    ASIPInformation next = information.next();
                    new SqlAsipInformation(next, space, this);
                }
            } else {
                for (SqlAsipInformation sqlAsipInformation : informationList) {
                    while (information.hasNext()) {
                        ASIPInformation next = information.next();
                        boolean merged = false;
                        if (sqlAsipInformation.getName().equals(next.getName())) {
                            sqlAsipInformation.setContent(next.getContentAsByte());
                            sqlAsipInformation.setContentType(next.getContentType());
                            merged = true;
                        }
                        if (!merged) {
                            new SqlAsipInformation(next, space, this);
                        }
                    }
                }
            }
            List<ASIPInformationSpace> infoSpaces = SqlSharkHelper.getInfoSpaces(this, space);
            if (infoSpaces != null && !infoSpaces.isEmpty()) {
                return infoSpaces.get(0);
            }
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
        SqlSharkHelper.removeInformation(this, infoSpace, info);
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace) throws SharkKBException {
        try {
            List<SqlAsipInformation> information = SqlSharkHelper.getInformation(this, infoSpace, false);
            return ((List<ASIPInformation>) (List<?>) information).iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterator<ASIPInformation> getInformation(ASIPSpace infoSpace, boolean fullyInside, boolean matchAny) throws SharkKBException {
        try {
            List<SqlAsipInformation> information = SqlSharkHelper.getInformation(this, infoSpace, false);
            return ((List<ASIPInformation>) (List<?>) information).iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        SqlSharkHelper.removeInformation(this, space, null);
    }

    @Override
    public void removeInformation(ASIPSpace space) throws SharkKBException {
        SqlSharkHelper.removeInformation(this, space, null);
    }

    @Override
    public SharkVocabulary getVocabulary() {
        // TODO
        return null;
    }

    @Override
    public int getNumberInformation() throws SharkKBException {
        try {
            return SqlSharkHelper.getNumberOfInformation(SqlSharkHelper.createConnection(this));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
        try {
            List<ASIPInformationSpace> infoSpaces = SqlSharkHelper.getInfoSpaces(this, as);
            return infoSpaces.iterator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public void removeListener(KnowledgeBaseListener kbl) {

    }

    @Override
    public FragmentationParameter[] getStandardFPSet() {
        return new FragmentationParameter[0];
    }

    @Override
    public void setStandardFPSet(FragmentationParameter[] fps) {

    }

}
