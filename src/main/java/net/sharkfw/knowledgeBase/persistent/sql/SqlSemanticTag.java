package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.ALL;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.AND;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.BC;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.BO;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.DELETE;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.DOT;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_ID;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_NAME;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_PROPERTY;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_SUBJECT_IDENTIFIER_IDENTIFIER;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_SYSTEM_PROPERTY;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_TAG_ID;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FROM;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.INSERTINTO;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.JOIN;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.ON;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.QU;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.SELECT;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.SET;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.TABLE_SEMANTIC_TAG;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.TABLE_SUBJECT_IDENTIFIER;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.UPDATE;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.VALUES;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.WHERE;

/**
 * Created by Dustin Feurich
 */
public class SqlSemanticTag extends SqlSharkPropertyHolder implements SemanticTag {
    public String ID;
    protected int id;
    private String[] sis;
    private String name;
    private String property;
    private String tagKind;
    private Map<String, String> properties;
    private ConnectionHolder connectionHolder;

    public SqlSemanticTag(String[] sis, String name, String tagKind, ConnectionHolder connectionHolder) {
        super(TABLE_SEMANTIC_TAG);
        this.sis = sis;
        this.name = name;
        this.tagKind = tagKind;
        this.connectionHolder = connectionHolder;
    }

    /**
     * Write SemanticTag to database
     *
     * @param sis
     * @param name
     */
    public SqlSemanticTag(String[] sis, String name, SqlSharkKB sharkKB) throws SQLException {
        this(sis, name, "normal", sharkKB);

        String sql = INSERTINTO + TABLE_SEMANTIC_TAG + BO + FIELD_NAME + BC + VALUES + BO + QU + this.name + QU + BC;
        SqlHelper.executeSQLCommand(this.getConnection(), sql);
        id = SqlHelper.getLastCreatedEntry(this.getConnection(), "semantic_tag");
        ID = Integer.toString(id);

        String sqlSIs = getSqlForSIs();
        if (sqlSIs != null) SqlHelper.executeSQLCommand(this.getConnection(), getSqlForSIs());
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_SYSTEM_PROPERTY + EQ + Integer.toString(this.getId()) + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connectionHolder.getConnection();
    }

    /**
     * Get SemanticTag from database with SI
     *
     * @param si
     */
    public SqlSemanticTag(String si, SqlSharkKB sharkKB, String tagKind) throws SharkKBException {
        this(null, null, tagKind, sharkKB);
        String sql;
        if (si != null) {
            sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + JOIN + TABLE_SUBJECT_IDENTIFIER + ON + TABLE_SEMANTIC_TAG + DOT + "id" + EQ + FIELD_TAG_ID + WHERE + FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU;
        } else {
            throw new SharkKBException();
        }
        String propertyString = null;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {
            if (rs.next()) {
                this.name = rs.getString("name");
                this.id = Integer.parseInt(rs.getString("system_property"));
                this.sis = getSisFromDB();
                propertyString = rs.getString("property");
            } else {
                throw new SharkKBException("No results.");
            }
        } catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null) {
            properties = extractProperties(propertyString);
        }
    }

    /**
     * Get SemanticTag from database with id
     *
     * @param id
     */
    public SqlSemanticTag(int id, SqlSharkKB sharkKB) throws SharkKBException {
        this(null, null, null, sharkKB);
        String sql = SELECT + ALL + FROM + TABLE_SEMANTIC_TAG + WHERE + FIELD_ID + EQ + id;
        String propertyString = null;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {

            if (rs.next()) {
                this.name = rs.getString("name");
                this.id = Integer.parseInt(rs.getString("system_property"));
                this.sis = getSisFromDB();
                propertyString = rs.getString("property");
            } else throw new SharkKBException("no results");
        } catch (SQLException e) {
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

    protected String getSqlForSIs() {
        if (sis == null) return null;
        StringBuilder sql = new StringBuilder();
//        sql.append("PRAGMA foreign_keys = ON; ");
        sql.append("INSERT INTO subject_identifier (identifier, tag_id) VALUES ");
        for (int i = 0; i < sis.length; i++) {
            if (i != sis.length - 1) {
                sql.append("(\'" + sis[i] + "\'," + id + ")" + ',');
            } else {
                sql.append("(\'" + sis[i] + "\'," + id + ")" + "; ");
            }
        }
        return sql.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getSis() {
        return sis;
    }

    private String[] getSisFromDB() {
        String tags = SELECT + ALL + FROM + TABLE_SUBJECT_IDENTIFIER + WHERE + FIELD_TAG_ID + EQ + this.getSystemProperty("id");
        List<String> list = new ArrayList<>();
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), tags)){
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
    public void removeSI(String si) throws SharkKBException {
        String sql = DELETE + FROM + TABLE_SUBJECT_IDENTIFIER + WHERE + FIELD_TAG_ID + EQ + this.getSystemProperty("id") + AND + FIELD_SUBJECT_IDENTIFIER_IDENTIFIER + EQ + QU + si + QU;
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

    @Override
    public void setName(String newName) {
        String update = UPDATE + TABLE_SEMANTIC_TAG + SET + FIELD_NAME + EQ + QU + newName + QU + WHERE + FIELD_ID + EQ + Integer.toString(this.getId());
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), update);
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

    private void addSIsToDB(String[] sis) {
        StringBuilder sqlAddresses = new StringBuilder();
        this.sis = sis;
        sqlAddresses.append("INSERT INTO subject_identifier (identifier, tag_id) VALUES ");
        for (int i = 0; i < this.sis.length; i++) {
            if (i != this.sis.length - 1) {
                sqlAddresses.append("(\'" + this.sis[i] + "\'," + this.getId() + ")" + ',');
            } else {
                sqlAddresses.append("(\'" + this.sis[i] + "\'," + this.getId() + ")" + "; ");
            }
        }
        try {
            SqlHelper.executeSQLCommand(this.getConnection(), sqlAddresses.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSystemProperty(String name, String value) {
        //TODO: column SystemProperty used as ID
    }

    @Override
    public String getSystemProperty(String name) {
        return Integer.toString(id);
    }
}
