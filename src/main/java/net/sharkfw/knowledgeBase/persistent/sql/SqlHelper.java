package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

/**
 * Created by Dustin Feurich on 03.04.2017.
 */
public class SqlHelper {
    // TABLE
    public final static String TABLE_INFORMATION = " information";
    public final static String TABLE_TAG_SET = " tag_set";
    public final static String TABLE_SEMANTIC_TAG = " semantic_tag";
    public final static String TABLE_ADDRESS = " address";
    public final static String TABLE_SUBJECT_IDENTIFIER = " subject_identifier";
    public final static String TABLE_RELATION = " relation";
    public final static String TABLE_KNOWLEDGE_BASE = " knowledge_base";

    // FIELDS
    public final static String FIELD_ID = " id";
    public final static String FIELD_SUBJECT_IDENTIFIER_IDENTIFIER = " identifier";
    public final static String FIELD_TAG_ID = " tag_id";
    public final static String FIELD_INFO_ID = " info_id";
    public final static String FIELD_SET_KIND = " set_kind";
    public final static String FIELD_DIRECTION = " direction";
    public final static String FIELD_NAME = " name";
    public final static String FIELD_SYSTEM_PROPERTY = " system_property";
    public final static String FIELD_PROPERTY = " property";
    public final static String FIELD_TAG_KIND = " tag_kind";
    public final static String FIELD_WKT = " wkt";
    public final static String FIELD_TIME_DURATION = " t_duration";
    public final static String FIELD_TIME_START = " t_start";
    public final static String FIELD_ADDRESS_NAME = " address_name";
    public final static String FIELD_SOURCE_TAG_ID = " source_tag_id";
    public final static String FIELD_TARGET_TAG_ID = " target_tag_id";
    public final static String FIELD_CONTENT_STREAM = " content_stream";
    public final static String FIELD_CONTENT_TYPE = " content_type";
    public final static String FIELD_CONTENT_LENGTH = " content_length";
    public final static String FIELD_OWNER_TAG = " owner_tag";
    public final static String FIELD_FOREIGN_KEYS = " foreign_keys";

    // METHODS
    public final static String INSERTINTO = " INSERT INTO";
    public final static String DELETE = " DELETE";
    public final static String UPDATE = " UPDATE";
    public final static String PRAGMA = " PRAGMA";
    public final static String JOIN = " JOIN";
    public final static String SELECT = " SELECT";
    public final static String FROM = " FROM";
    public final static String WHERE = " WHERE";
    public final static String ON = " ON";
    public final static String EQ = " =";
    public final static String BO = "(";
    public final static String BC = ")";
    public final static String ALL = " *";
    public final static String OR = " OR";
    public final static String AND = " AND";
    public final static String VALUES = " VALUES";

    private SqlHelper() {
        //static usage only
    }

    public static void importSQL(Connection conn, InputStream in) throws SQLException {
        Scanner s = new Scanner(in);
        s.useDelimiter("(;(\r)?\n)|(--\n)");
        Statement st = null;
        conn.setAutoCommit(true);
        try {
            st = conn.createStatement();
            while (s.hasNext()) {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/")) {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0) {
                    st.executeUpdate(line);
                }
            }
        } finally {
            if (st != null) st.close();

        }
    }

    public static void executeSQLCommand(Connection conn, String sql) throws SQLException {
        L.d(sql, sql);
        Statement st = null;
        conn.setAutoCommit(true);
        try {
            st = conn.createStatement();
            st.executeUpdate(sql);
        } finally {
            if (st != null) st.close();
        }
    }

    public static ResultSet executeSQLCommandWithResult(Connection conn, String sql) throws SQLException {
        L.d(sql, sql);
        conn.setAutoCommit(true);
        Statement st = conn.createStatement();
        return st.executeQuery(sql);
    }

    public static int getLastCreatedEntry(Connection conn, String tableName) throws SQLException {
        int id = -1;
        String sql = "SELECT id FROM " + tableName + " ORDER BY id DESC LIMIT 1";
        Statement st = conn.createStatement();
        try {
            ResultSet resultSet = st.executeQuery(sql);
            id = resultSet.getInt("id");
        } finally {
            st.close();
        }
        if (id >= 0) {
            return id;
        } else {
            throw new SQLException();
        }
    }

    private static Connection getConnection(SqlSharkKB sharkKB) throws SharkKBException {
        try {
            Class.forName(sharkKB.getDialect());
            return DriverManager.getConnection(sharkKB.getDbAddress());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SharkKBException();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }

    }

    public static Map<String, String> extractProperties(String propertyString) {
        Map<String, String> map = new HashMap<>();
        String[] keyValues = propertyString.split(">");
        String[] keyValue;
        for (int i = 0; i < keyValues.length; i++) {
            keyValue = keyValues[i].split("<");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

    public static void persistProperties(Map<String, String> properties, int id, String table, SqlSharkKB sharkKB) throws SharkKBException {
        Connection connection = getConnection(sharkKB);
        StringBuilder sb = new StringBuilder();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sb.append(pair.getKey() + "<" + pair.getValue() + ">");
        }
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String update = create.update(table(table)).set(field("property"), inline(sb.toString())).where(field("id").eq(inline(Integer.toString(id)))).getSQL();
        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
    }

    public static void persistProperties(Map<String, String> properties, SqlSharkKB sharkKB) throws SharkKBException {
        persistProperties(properties, 1, "knowledge_base", sharkKB);
    }


}
