package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.table;

public class SqlAsipInformation implements ASIPInformation {

    public int getId() {
        return id;
    }

    private int id;
    private SqlSharkKB sharkKB;
    private Connection connection;
    private byte[] content;
    private String contentType;

    public SqlAsipInformation(String contentType, int contentLength, byte[] content, String name, SqlAsipInfoSpace infoSpace, SqlSharkKB sharkKB) throws SharkKBException, SQLException {

        this.sharkKB = sharkKB;
        connection = getConnection(this.sharkKB);
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
        String sql = create.insertInto(table("asip_information"),
                field("content_type"),field("content_length"), field("content_stream"), field("name"), field("asip_information_space_id"))
                .values(inline(contentType),inline(contentLength),
                        inline(content), inline(name),inline(infoSpace.getId())).getSQL();

        SqlHelper.executeSQLCommand(connection, sql);
        id = SqlHelper.getLastCreatedEntry(connection, "asip_information");
    }

    public SqlAsipInformation(int id, SqlSharkKB sharkKB) {

        this.id = id;
        this.sharkKB = sharkKB;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return getDataFromDB();
    }

    private ASIPSpace getDataFromDB() throws SharkKBException {

        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
            return null;
        }
        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
        String sql = getSetId.selectFrom(table("asip_information")).where(field("id")
                .eq(inline(id))).getSQL();
        ResultSet rs;
        try {
            rs = SqlHelper.executeSQLCommandWithResult(connection, sql);
            int id = rs.getInt("asip_information_space_id");
            content = rs.getBytes("content_stream");

            return new SqlAsipSpace(id, sharkKB);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getContentAsByte() {
        if (content != null) {
            return content;
        }
        else {
            try {
                getDataFromDB();
            } catch (SharkKBException e) {
                e.printStackTrace();
                return null;
            }
            return content;
        }

    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public long creationTime() {
        return 0;
    }

    @Override
    public void setContent(InputStream is, long len) {

    }

    @Override
    public void setContent(byte[] content) {

    }

    @Override
    public void setContent(String content) {

    }

    @Override
    public void removeContent() {

    }

    @Override
    public void setContentType(String mimetype) {

    }

    @Override
    public String getContentType() {
        return null;
    }


    @Override
    public void streamContent(OutputStream os) {

    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        return null;
    }

    @Override
    public void setName(String name) throws SharkKBException {

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

    private Connection getConnection(SqlSharkKB sharkKB) throws SharkKBException {
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
