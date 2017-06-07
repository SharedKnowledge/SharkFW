package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_ID;

/**
 * Created by Dustin Feurich on 03.04.2017.
 */
public class SqlHelper {

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

    public static void executeSQLCommand(Connection conn, String sql, byte[] blob) throws SQLException {
        PreparedStatement st = null;
        conn.setAutoCommit(true);
        try {
            st = conn.prepareStatement(sql);
            st.setBytes(1, blob);
            st.executeUpdate();
        } finally {
            if (st != null) st.close();
        }
    }
    public static void executeSQLCommand(Connection conn, String sql) throws SQLException {
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
            if(resultSet.next()){
                id = resultSet.getInt("id");
            } else throw new SQLException("No results");
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
        String update = UPDATE + table + SET + FIELD_PROPERTY + EQ + QU + sb.toString() + QU
                + WHERE + FIELD_ID + EQ + Integer.toString(id);
        try {
            SqlHelper.executeSQLCommand(connection, update);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
    }

    public static void persistProperties(Map<String, String> properties, SqlSharkKB sharkKB) throws SharkKBException {
        persistProperties(properties, 1, TABLE_KNOWLEDGE_BASE, sharkKB);
    }


}
