package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;
import static org.jooq.impl.DSL.*;

/**
 * Created by Dustin Feurich
 */
public class SqlSemanticTag implements SemanticTag
{
    private int id;
    public  String ID;
    private String[] sis;
    private String name;
    protected Connection connection;
    private String property;
    private String tagKind;
    private Map<String, String> properties;

    public SqlSemanticTag(String[] sis, String name, String tagKind) {
        this.sis = sis;
        this.name = name;
        this.tagKind = tagKind;
    }

    public SqlSemanticTag(int id, String[] sis, String name, String property, String tagKind) {
        this.id = id;
        this.sis = sis;
        this.name = name;
        this.property = property;
        this.tagKind = tagKind;
    }

    /**
     * Write SemanticTag to database
     * @param sis
     * @param name
     */
    public SqlSemanticTag(String[] sis, String name, SqlSharkKB sharkKB) throws SQLException {
        this(sis, name, "normal");

        properties = new HashMap<>();
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sql = INSERTINTO + TABLE_SEMANTIC_TAG + BO + FIELD_NAME + BC + VALUES + BO + QU + this.name + QU + BC;
        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "semantic_tag");
        ID = Integer.toString(id);

        String sqlSIs = getSqlForSIs();
        if (sqlSIs != null) SqlHelper.executeSQLCommand(connection, getSqlForSIs());
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_SYSTEM_PROPERTY + EQ + Integer.toString(this.getId())
                + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get SemanticTag from database with SI
     * @param si
     */
    public SqlSemanticTag(String si, SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch ( SQLException e) {
            throw new SharkKBException(e.toString());
        }
        String sql;
        if (si != null) {
             sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + JOIN + TABLE_SUBJECT_IDENTIFIER + ON +
             FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU + WHERE + TABLE_SEMANTIC_TAG + DOT + FIELD_ID + EQ + FIELD_TAG_ID;
        }
        else {
            throw new SharkKBException();
        }
        String propertyString = null;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                this.name = rs.getString("name");
                this.id = Integer.parseInt(rs.getString("system_property"));
                this.sis = getSisFromDB();
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null) {
            properties = extractProperties(propertyString);
        }
    }

    /**
     * Get SemanticTag from database with id
     * @param id
     */
    public SqlSemanticTag(int id, SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch ( SQLException e) {
            throw new SharkKBException(e.toString());
        }
        String sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + WHERE + FIELD_ID + EQ + id;
        String propertyString = null;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {

            if (rs != null) {
                this.name = rs.getString("name");
                this.id = Integer.parseInt(rs.getString("system_property"));
                this.sis = getSisFromDB();
                propertyString = rs.getString("property");
            }
        }
        catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null) {
            properties = extractProperties(propertyString);
        }
    }


    private Map<String, String> extractProperties(String propertyString) {
        Map<String, String> map = new HashMap<>();
        String[] keyValues = propertyString.split(">");
        String[] keyValue;
        for (int i = 0; i < keyValues.length; i++) {
            keyValue = keyValues[i].split("<");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

    protected String getSqlForSIs()
    {
        if (sis == null) return null;
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

    private String[] getSisFromDB() {
        String tags = SELECT + ALL + FROM + TABLE_SUBJECT_IDENTIFIER + WHERE + FIELD_TAG_ID + EQ + this.getSystemProperty("id");
        ResultSet rs = null;
        List<String> list = new ArrayList<>();
        try {
            rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags);
            while (rs.next()) {
                list.add(rs.getString("identifier"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
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
        return sis;
    }

    @Override
    public String getSystemProperty(String name) {
        return Integer.toString(id);
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        properties.put(name,value);
        persistProperties();
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        properties.put(name,value);
        persistProperties();
    }

    private void persistProperties() throws SharkKBException {
        StringBuilder sb = new StringBuilder();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sb.append(pair.getKey() + "<" + pair.getValue() + ">");
        }
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_PROPERTY + EQ + QU + sb.toString() + QU
                + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }


    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return properties.get(name);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        properties.remove(name);
        persistProperties();
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        Set<String> set = properties.keySet();
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        if (properties != null) {
            Set<String> set = properties.keySet();
            return Collections.enumeration(set);
        }
        else
            return null;
    }

    @Override
    public void removeSI(String si) throws SharkKBException {
        String sql = DELETE + FROM + TABLE_SUBJECT_IDENTIFIER + WHERE + FIELD_TAG_ID + EQ + this.getSystemProperty("id") + AND +
                FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU;
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSI(String si) throws SharkKBException {
        addSIsToDB(new String[]{si});
    }

    private void addSIsToDB(String[] sis) {
        StringBuilder sqlAddresses = new StringBuilder();
        this.sis = sis;
        sqlAddresses.append("INSERT INTO subject_identifier (identifier, tag_id) VALUES ");
        for (int i = 0; i < this.sis.length; i++)
        {
            if (i != this.sis.length - 1)
            {
                sqlAddresses.append("(\'" + this.sis[i] + "\'," + this.getId() + ")" + ',');
            }
            else
            {
                sqlAddresses.append("(\'" + this.sis[i] + "\'," + this.getId() + ")" + "; ");
            }
        }
        try {
            SqlHelper.executeSQLCommand(connection, sqlAddresses.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSystemProperty(String name, String value) {
        //TODO: column SystemProperty used as ID
    }

    @Override
    public void setName(String newName) {
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_NAME + EQ + QU + newName + QU
                + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(connection, update);
            name = newName;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void merge(SemanticTag st) {
        String[] paramSI = st.getSI();
        String[] thisSI = this.getSI();
        List<String> newSIs = new ArrayList<>();
        for (String i : paramSI) {
            for (String j : thisSI) {
                if (!(j.equals(i))) {
                    newSIs.add(i);
                }
            }
        }
        String[] arr = new String[newSIs.size()];
        arr = newSIs.toArray(arr);
        addSIsToDB(arr);
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
