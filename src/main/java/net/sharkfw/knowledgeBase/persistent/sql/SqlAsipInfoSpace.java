package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlAsipInfoSpace implements ASIPInformationSpace {

    public int getId() {
        return id;
    }

    private int id;
    private SqlSharkKB sharkKB;
    protected Connection connection;

    public SqlAsipInfoSpace(SqlAsipSpace space, SqlKnowledge knowledge, SqlSharkKB sharkKB) throws SharkKBException, SQLException {

        connection = getConnection(sharkKB);
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("asip_information_space"),
                field("asip_space"),field("knowledge"))
                .values(inline(space.getId()),inline(knowledge.getId())).getSQL();
        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "asip_information_space");

        String update = create.update(table("semantic_tag")).set(field("system_property"), inline(Integer.toString(id))).where(field("id").eq(inline(Integer.toString(id)))).getSQL();
        SqlHelper.executeSQLCommand(connection, update);
    }

    SqlAsipInfoSpace(int id, SqlSharkKB sharkKB){

        this.id = id;
        this.sharkKB = sharkKB;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_information_space")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            int id = rs.getInt("asip_space");
            return new SqlAsipSpace(id, sharkKB);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int numberOfInformations() {
        return 0;
    }

    @Override
    public Iterator<ASIPInformation> informations() throws SharkKBException {
        return null;
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


}
