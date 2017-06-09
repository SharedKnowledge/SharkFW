package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;

/**
 * Created by j4rvis on 6/9/17.
 */
public abstract class SqlSharkPropertyHolder implements PropertyHolder, ConnectionHolder{

    private HashMap<String, String> properties = null;
    private String tableName;

    public SqlSharkPropertyHolder(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        properties = getProperties();
        properties.put(name, value);
        this.persistProperties(properties, this.getId(), this.tableName, this.getConnection());
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return getProperties().get(name);
    }

    private HashMap<String, String> getProperties() throws SharkKBException {
        if (this.properties != null) return this.properties;
        String propertyString = null;
//        HashMap<String, String> properties;
        String sql = SELECT + ALL + FROM + TABLE_KNOWLEDGE_BASE;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {
            if (rs.next()) {
                propertyString = rs.getString("property");
            }
        } catch (SQLException e) {
            throw new SharkKBException(e.toString());
        }
        if (propertyString != null && propertyString != "") {
            properties = SqlHelper.extractProperties(propertyString);
        } else {
            properties = new HashMap<String, String>();
        }
        return properties;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        setProperty(name, value);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        properties = getProperties();
        properties.remove(name);
        this.persistProperties(properties, this.getId(), this.tableName, this.getConnection());
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        properties = getProperties();
        return Collections.enumeration(properties.keySet());
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.propertyNames();
    }

    private void persistProperties(HashMap<String, String> properties, int id, String table, Connection connection) throws SharkKBException {
        StringBuilder sb = new StringBuilder();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            sb.append(pair.getKey() + "<" + pair.getValue() + ">");
        }
        String update = UPDATE + table + SET + FIELD_PROPERTY + EQ + "'" + sb.toString() + "'"
                + WHERE + FIELD_ID + EQ + Integer.toString(id);
        try {
            SqlHelper.executeSQLCommand(connection, update);
            this.properties = properties;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SharkKBException();
        }
    }

    protected void setProperties(PropertyHolder propertyHolder) throws SharkKBException {
        if(propertyHolder==null) return;
        Enumeration<String> enumeration = propertyHolder.propertyNames();
        if(enumeration==null) return;
        HashMap<String, String> hashMap = new HashMap<>();
        while (enumeration.hasMoreElements()){
            String s = enumeration.nextElement();
            if(s==null || s.isEmpty()) continue;
            String property = propertyHolder.getProperty(s);
            hashMap.put(s, property);
        }

        if(!hashMap.isEmpty()){
            this.persistProperties(hashMap, this.getId(), this.tableName, this.getConnection());
        }
    }

}
