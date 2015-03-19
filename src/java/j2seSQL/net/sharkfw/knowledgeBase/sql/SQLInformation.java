package net.sharkfw.knowledgeBase.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class SQLInformation extends SQLPropertyHolderDelegate implements Information, PropertyOwner {
    private int id;

    SQLInformation(SQLSharkKB kb, int id) {
        this.initPropertyHolderDelegate(kb, this);
        this.id = id;
    }
    
    SQLInformation(SQLSharkKB kb, int cpid, String name) {
        this.initPropertyHolderDelegate(kb, this);
        
        Statement statement = null;
        try {
            statement  = kb.getConnection().createStatement();
            
            ResultSet result = statement.executeQuery("select nextval('infoid');");
            if(result.next()) {
                // there must be a result
                this.id = result.getInt(1);
            } else {
                // TODO
            }
            
            StringBuilder sqlString = new StringBuilder("INSERT INTO ");
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" (cpid) VALUES ( ");
            sqlString.append(cpid);
            sqlString.append(" )");
            
            statement.execute(sqlString.toString());

        } catch (SQLException e) {
            L.w("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
        }
        finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    // ignore
                }
            }
        }
    }
    
    @Override
    public long lastModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long creationTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContent(InputStream is, long len) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContent(byte[] content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContent(String content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeContent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContentType(String mimetype) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] getContentAsByte() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void streamContent(OutputStream os) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getContentLength() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutputStream getOutputStream() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getInputStream() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setName(String name) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUniqueID() {
        return String.valueOf(this.id);
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getType() {
        return SQLSharkKB.INFORMATION;
    }
}
