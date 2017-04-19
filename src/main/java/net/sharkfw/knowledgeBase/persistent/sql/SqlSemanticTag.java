package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Dustin Feurich
 */
public class SqlSemanticTag implements SemanticTag
{
    private int id;
    private String[] sis;
    private String name;
    private int stSetID;
    protected Connection connection;
    private String property;
    private String tagKind;

    public SqlSemanticTag(String[] sis, String name, String tagKind, int stSetID) {
        this.sis = sis;
        this.name = name;
        this.tagKind = tagKind;
        this.stSetID = stSetID;
    }

    /**
     * Write SemanticTag to database
     * @param sis
     * @param name
     * @param stSetID
     */
    public SqlSemanticTag(String[] sis, String name, int stSetID, SqlSharkKB sharkKB) throws SQLException {
        this(sis, name, "normal", stSetID);
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO semantic_tag (name, tag_set) VALUES " + "(\'" + this.name + "\'," + this.stSetID + ");");
        SqlHelper.executeSQLCommand(connection, sql.toString());
        id = SqlHelper.getLastCreatedEntry(connection, "semantic_tag");
        SqlHelper.executeSQLCommand(connection, getSqlForSIs());
    }

    /**
     * Get SemanticTag from database
     * @param si
     * @param stSetID
     */
    public SqlSemanticTag(String si, int stSetID, SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch ( SQLException e) {
            throw new SharkKBException(e.toString());
        }

        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("SELECT FROM semantic_tag, subject_identifier WHERE semantic_tag.id = tag_id AND tag_set = " + stSetID + " AND identifier = \"" + si + "\";");
        try {
            ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql.toString());
            if (rs != null) {
                this.name = rs.getString("name");
                this.sis = new String[]{si};
                this.stSetID = stSetID;
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
    }

    protected String getSqlForSIs()
    {
        StringBuilder sql = new StringBuilder();
        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO subject_identifier (identifier, tag_id) VALUES ");
        for (int i = 0; i < sis.length; i++)
        {
            if (i != sis.length - 1)
            {
                sql.append("(\'" + sis[i] + "\'," + id + ")" + ',');
            }
            else
            {
                sql.append("(\'" + sis[i] + "\'," + id + ")" + "; ");
            }
        }
        return sql.toString();
    }

    public int getId() {
        return id;
    }

    public String[] getSis() {
        return sis;
    }

    public int getStSetID() {
        return stSetID;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getProperty() {
        return property;
    }

    public String getTagKind() {
        return tagKind;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getSI() {
        return new String[0];
    }

    @Override
    public void setSystemProperty(String name, String value) {

    }

    @Override
    public String getSystemProperty(String name) {
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
    public void removeSI(String si) throws SharkKBException {

    }

    @Override
    public void addSI(String si) throws SharkKBException {

    }

    @Override
    public void setName(String newName) {

    }

    @Override
    public void merge(SemanticTag st) {

    }

    @Override
    public void setHidden(boolean isHidden) {

    }

    @Override
    public boolean hidden() {
        return false;
    }

    @Override
    public boolean isAny() {
        return false;
    }

    @Override
    public boolean identical(SemanticTag other) {
        return false;
    }

    public void setId(int id) {
        this.id = id;
    }
}
