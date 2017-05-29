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
    private int stSetID;
    protected Connection connection;
    private String property;
    private String tagKind;
    private Map<String, String> properties;

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

        properties = new HashMap<>();
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
        ID = Integer.toString(id);

        String sqlSIs = getSqlForSIs();
        if (sqlSIs != null) SqlHelper.executeSQLCommand(connection, getSqlForSIs());

        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table("semantic_tag")).set(field("system_property"), inline(Integer.toString(this.getId()))).where(field("id").eq(inline(Integer.toString(this.getId())))).getSQL();

        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get SemanticTag from database
     * @param si
     * @param stSetID
     */
    public SqlSemanticTag(int id, String si, int stSetID, SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            connection = DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch ( SQLException e) {
            throw new SharkKBException(e.toString());
        }

        DSLContext getEntry = DSL.using(connection, SQLDialect.SQLITE);
        String sql = null;
        if (id == -1 && si != null) {
            sql = getEntry.selectFrom(table("semantic_tag").join("subject_identifier")
                    .on(field("identifier").eq(inline(si)))).where((field("tag_set")
                    .eq(inline(stSetID)))).and(field("semantic_tag.id").eq(field("tag_id"))).getSQL();
        }
        else if (id >= 0 && si == null) {
            sql = getEntry.selectFrom(table("semantic_tag")).where(field("id").eq(inline(id))).getSQL();
        }
        else if (id == -1 && si == null) {
            sql = getEntry.selectFrom(table("semantic_tag")).where(field("tag_set").eq(inline(stSetID))).getSQL();
        }
        String propertyString = null;
        try {
            ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            if (rs != null) {
                this.name = rs.getString("name");
                this.sis = getSisFromDB();
                this.stSetID = stSetID;
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
        String[] keyValues = propertyString.split(";");
        String[] keyValue;
        for (int i = 0; i < keyValues.length; i++) {
            keyValue = keyValues[i].split(":");
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
        DSLContext getSis = DSL.using(this.getConnection(), SQLDialect.SQLITE);
        String tags = getSis.selectFrom(table("subject_identifier")).where(field("tag_id")
                .eq(inline(this.getSystemProperty("id")))).getSQL();
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
        //TODO: transfer?
    }

    private void persistProperties() throws SharkKBException {
        StringBuilder sb = new StringBuilder();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sb.append(pair.getKey() + ":" + pair.getValue() + ";");
        }

        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table("semantic_tag")).set(field("property"), inline(sb.toString())).where(field("id").eq(inline(Integer.toString(this.getId())))).getSQL();
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
        return null;
        //TODO: ???
    }

    @Override
    public void removeSI(String si) throws SharkKBException {

    }

    @Override
    public void addSI(String si) throws SharkKBException {

    }

    @Override
    public void setSystemProperty(String name, String value) {
        //TODO: column SystemProperty used as ID
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
