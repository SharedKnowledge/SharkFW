package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.ALL;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.BC;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.BO;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.EQ;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_CONTENT_LENGTH;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_CONTENT_STREAM;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_CONTENT_TYPE;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_ID;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FIELD_NAME;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.FROM;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.INSERTINTO;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.SELECT;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.TABLE_INFORMATION;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.VALUES;
import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.WHERE;

public class SqlAsipInformation implements ASIPInformation {

    private ASIPSpace asipSpace;
    private int id;
    private SqlSharkKB sharkKB;
    private Connection connection;
    private byte[] content;
    private String contentType;
    private String name;
    private long contentLength;

    public SqlAsipInformation(ASIPInformation information, ASIPSpace space, SqlSharkKB sharkKB) throws SharkKBException, SQLException {
        this.sharkKB = sharkKB;
        this.asipSpace = space;
        this.content = information.getContentAsByte();
        this.name = information.getName();
        this.contentLength = information.getContentLength();
        this.contentType = information.getContentType();
        connection = getConnection(this.sharkKB);

        String sql = INSERTINTO + TABLE_INFORMATION + BO + FIELD_CONTENT_TYPE + "," + FIELD_CONTENT_LENGTH + "," + FIELD_CONTENT_STREAM + "," + FIELD_NAME + BC + VALUES + BO + "\""+contentType + "\"" + "," + "\"" + contentLength + "\"" + "," + "?" + "," +  "\"" +name + "\"" + BC;

        SqlHelper.executeSQLCommand(connection, sql, content);
        this.id = SqlHelper.getLastCreatedEntry(connection, "information");
    }

    public SqlAsipInformation(int id, ASIPSpace space, SqlSharkKB sharkKB) throws SharkKBException {
        this.asipSpace = space;
        this.id = id;
        this.sharkKB = sharkKB;
        getDataFromDB();
    }

    public SqlAsipInformation(int id, byte[] content, String contentType, String name, long contentLength) {
        this.id = id;
        this.content = content;
        this.contentType = contentType;
        this.name = name;
        this.contentLength = contentLength;
    }

    public int getId() {
        return id;
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        return this.asipSpace;
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

    }    @Override
    public byte[] getContentAsByte() {
        if (content != null) {
            return content;
        } else {
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
    public void setContent(String content) {

    }

    @Override
    public void removeContent() {

    }

    private void getDataFromDB() throws SharkKBException {

        try {
            connection = getConnection(sharkKB);
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
//        DSLContext getSetId = DSL.using(connection, SQLDialect.SQLITE);
//        String sql = getSetId.selectFrom(table("information")).where(field("id").eq(inline(id))).getSQL();
        String sql = SELECT + ALL + FROM + TABLE_INFORMATION + WHERE + FIELD_ID + EQ + id;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(connection, sql)) {
            if(rs.next()){
                this.id = rs.getInt("id");
                this.content = rs.getBytes("content_stream");
                this.contentType = rs.getString("content_type");
                this.name = rs.getString("name");
                this.contentLength = rs.getInt("content_length");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    }    @Override
    public void setContentType(String mimetype) {

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



    @Override
    public String getContentType() {
        return this.contentType;
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
        return this.name;
    }


    @Override
    public String getContentAsString() throws SharkKBException {
        return null;
    }

    @Override
    public void setName(String name) throws SharkKBException {

    }

}
