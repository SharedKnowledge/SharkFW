package net.sharkfw.knowledgeBase.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;
import net.sharkfw.system.Streamer;

/**
 *
 * @author thsc
 */
public class SQLInformation extends SQLPropertyHolderDelegate implements Information, PropertyOwner {
    private int id;
    private String name;
    private long creationTime;
    private SQLSharkKB kb;
    private String mimetype;
    private long contentlength;

    SQLInformation(SQLSharkKB kb, int id) {
        this.initPropertyHolderDelegate(kb, this);
        this.kb = kb;
        this.id = id;
        
        this.refreshBasics();
    }
    
    SQLInformation(SQLSharkKB kb, int cpid, String name) {
        this.initPropertyHolderDelegate(kb, this);
        this.name = name;
        this.kb = kb;
        
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
            
            long now = System.currentTimeMillis();
            this.creationTime = now;
            
            StringBuilder sqlString = new StringBuilder("INSERT INTO ");
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" (id, cpid, creationtime, lastmodifiedtime");
            if(name != null) {
                sqlString.append(" , name");
            }
            sqlString.append(") VALUES ( ");
            sqlString.append(this.id);
            sqlString.append(", ");
            sqlString.append(cpid);
            sqlString.append(", ");
            sqlString.append(now);
            sqlString.append(", ");
            sqlString.append(now);
            if(name != null) {
                sqlString.append(" , '");
                sqlString.append(name);
                sqlString.append("' ");
            }
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
        Statement statement = null;
        try {
            statement  = this.kb.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("SELECT (lastmodifiedtime) from "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" WHERE id = ");
            sqlString.append(this.id);
            ResultSet result = statement.executeQuery(sqlString.toString());
            if(result.next()) {
                return result.getLong(1);
            }

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
        
        return 0;
    }

    @Override
    public long creationTime() {
        return this.creationTime;
    }

    @Override
    public void setContent(InputStream is, long len) {
        Statement statement = null;
        try {
            StringBuilder sqlString = new StringBuilder("UPDATE "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" SET content = (?) WHERE id = ");
            sqlString.append(this.id);
            
            PreparedStatement ps = this.kb.getConnection().prepareStatement(sqlString.toString());
            ps.setBinaryStream(1, is, len);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            L.e("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
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
        
        this.contentlength = len;
    }

    @Override
    public void setContent(byte[] content) {
        ByteArrayInputStream bin = new ByteArrayInputStream(content);
        this.setContent(bin, content.length);
    }

    @Override
    public void setContent(String content) {
        try {
                this.setContent(content.getBytes(KEPMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            //FIXME: Catch unknown encoding exception?!
            e.printStackTrace();
        }    
    }

    @Override
    public void removeContent() {
    }

    @Override
    public void setContentType(String mimetype) {
        Statement statement = null;
        this.mimetype = mimetype;
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("UPDATE "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append("SET contenttype = '");
            sqlString.append(mimetype);
            
            long now = System.currentTimeMillis();
            sqlString.append("', lastmodifiedtime = ");
            sqlString.append(now);
            sqlString.append(" WHERE id = ");
            sqlString.append(this.id);
            
            statement.execute(sqlString.toString());
            
        } catch (SQLException e) {
            L.e("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
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
    public String getContentType() {
        return this.mimetype;
    }

    @Override
    public byte[] getContentAsByte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) this.getContentLength());
        
        this.streamContent(baos);
        
        return baos.toByteArray();
    }

    @Override
    public void streamContent(OutputStream os) {
        
        Statement statement = null;
        try {
            StringBuilder sqlString = new StringBuilder("SELECT content FROM "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" WHERE id = ");
            sqlString.append(this.id);
            
            Connection conn = this.kb.getConnection();
            statement  = this.kb.getConnection().createStatement();
            
            ResultSet result = statement.executeQuery(sqlString.toString());
            
            while (result.next()) {
                InputStream is = result.getBinaryStream(1);
                if(is != null) {
                    Streamer.stream(is, os, SQLSharkKB.MAX_BUFFER_SIZE);
                }
            }
            result.close();

        } catch (SQLException e) {
            L.e("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
        } catch (IOException ex) {
            L.e("error while streaming content: " + ex.getLocalizedMessage(), this);
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
    public long getContentLength() {
        return this.contentlength;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        byte[] contentAsByte = this.getContentAsByte();
        
        try {
            return new String(contentAsByte, KEPMessage.ENCODING);
        } catch (UnsupportedEncodingException ex) {
            throw new SharkKBException("cannot convert content to string: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void setName(String name) throws SharkKBException {
        Statement statement = null;
        this.name = name;
        
        try {
            statement  = this.kb.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("UPDATE "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" SET name = '");
            sqlString.append(name);
            long now = System.currentTimeMillis();
            sqlString.append("', lastmodifiedtime = ");
            sqlString.append(now);
            sqlString.append(" WHERE id = ");
            sqlString.append(this.id);
            
            statement.execute(sqlString.toString());
            
        } catch (SQLException e) {
            L.e("error while creating SQL-statement: " + e.getLocalizedMessage(), this);
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

    private void refreshBasics() {
        Statement statement = null;
        try {
            statement  = this.kb.getConnection().createStatement();
            
            StringBuilder sqlString = new StringBuilder("SELECT (name, contenttype, creationtime, contentlength) FROM "); 
            sqlString.append(SQLSharkKB.INFORMATION_TABLE);
            sqlString.append(" WHERE id = ");
            sqlString.append(this.id);
            ResultSet result = statement.executeQuery(sqlString.toString());
            if(result.next()) {
                this.name = result.getString("name");
                this.mimetype = result.getString("contenttype");
                this.creationTime = result.getLong("creationtime");
                this.contentlength = result.getLong("contentlength");
            }
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
    
    ///////////////////////////////////////////////////////////////////////
    //      deprecated and wont be supported in SQL implementation       //
    ///////////////////////////////////////////////////////////////////////
    
    @Override
    public OutputStream getOutputStream() throws SharkKBException {
        throw new SharkKBException("Not supported yet in SQL implementation."); 
    }

    @Override
    public InputStream getInputStream() throws SharkKBException {
        throw new SharkKBException("Not supported yet in SQL implementation."); 
    }

    @Override
    public ASIPSpace getASIPSpace() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
