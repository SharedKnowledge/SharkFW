package net.sharkfw.knowledgeBase.persistent.sql;

import net.sharkfw.asip.ASIPInformation;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.SharkKBException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

import static net.sharkfw.knowledgeBase.persistent.sql.SqlSharkHelper.*;

public class SqlAsipInformation extends SqlSharkPropertyHolder implements ASIPInformation {

    private ASIPSpace asipSpace;
    private int id;
    private SqlSharkKB sharkKB;
    private byte[] content;
    private String contentType;
    private String name;
    private long contentLength;

    public SqlAsipInformation(ASIPInformation information, ASIPSpace space, SqlSharkKB sharkKB) throws SharkKBException, SQLException {
        super(TABLE_INFORMATION);
        this.sharkKB = sharkKB;
        this.asipSpace = space;
        this.content = information.getContentAsByte();
        this.name = information.getName();
        this.contentLength = information.getContentLength();
        this.contentType = information.getContentType();

        String sql = INSERTINTO + TABLE_INFORMATION + BO + FIELD_CONTENT_TYPE + "," + FIELD_CONTENT_LENGTH + "," + FIELD_CONTENT_STREAM + "," + FIELD_NAME + BC + VALUES + BO + "\"" + contentType + "\"" + "," + "\"" + contentLength + "\"" + "," + "?" + "," + "\"" + name + "\"" + BC;

        SqlHelper.executeSQLCommand(this.getConnection(), sql, content);
        this.id = SqlHelper.getLastCreatedEntry(this.getConnection(), "information");
        this.setProperties(information);
    }

    public SqlAsipInformation(int id, ASIPSpace space, SqlSharkKB sharkKB) throws SharkKBException {
        super(TABLE_INFORMATION);
        this.asipSpace = space;
        this.id = id;
        this.sharkKB = sharkKB;
        getDataFromDB();
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

    }

    @Override
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

        String sql = SELECT + ALL + FROM + TABLE_INFORMATION + WHERE + FIELD_ID + EQ + id;
        try (ResultSet rs = SqlHelper.executeSQLCommandWithResult(this.getConnection(), sql)) {
            if (rs.next()) {
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
    public void setContentType(String mimetype) {

    }

    @Override
    public void streamContent(OutputStream os) {

    }

    @Override
    public long getContentLength() {
        return this.contentLength;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) throws SharkKBException {

    }

    @Override
    public String getContentAsString() throws SharkKBException {
        return new String(content, StandardCharsets.UTF_8);
    }

    @Override
    public Connection getConnection() {
        return this.sharkKB.getConnection();
    }
}
