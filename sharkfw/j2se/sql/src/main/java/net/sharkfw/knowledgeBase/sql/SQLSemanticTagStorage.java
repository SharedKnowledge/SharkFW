package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
class SQLSemanticTagStorage implements PropertyOwner {
    private final SQLSharkKB kb;
    private final int id;
    private String name;
    private String ewkt;
    private long startTime;
    private long durationTime;
    private boolean hidden;
    private int type;
    private String[] sis;
    private String[] addresses;
    
    SQLSemanticTagStorage(SQLSharkKB kb, int id) throws SharkKBException {
        this.kb = kb;
        this.id = id;
        
        this.refreshBasics();
    }
    
    SQLSemanticTagStorage(
            SQLSharkKB kb,
            String name, 
            String ewkt, // if spatial semantic tag
            long startTime, // if time semantic tag
            long durationTime, // if time semantic tag
            boolean hidden, 
            int type,
            String[] sis,
            String[] addresses) throws SharkKBException {
        
        if(type != SQLSharkKB.SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.PEER_SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.SPATIAL_SEMANTIC_TAG_TYPE && 
                type != SQLSharkKB.TIME_SEMANTIC_TAG_TYPE) {
            
            throw new SharkKBException("unknown semantic tag type: " + type);
        }
        
        this.kb = kb;
        this.name = name;
        this.ewkt = ewkt;
        this.startTime = startTime;
        this.durationTime = durationTime;
        this.hidden = hidden;
        this.type = type;
        this.sis = sis;
        
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            ResultSet result = statement.executeQuery("select nextval('stid');");
            if(result.next()) {
                // there must be a result
                this.id = result.getInt(1);
            } else {
                // TODO: remove semantic tag entry!!
                throw new SharkKBException("cannot get next semantic tag primary key");
            }
            
            String sqlHead = "INSERT INTO " + SQLSharkKB.ST_TABLE + " (id, ";
            String sqlValues = "VALUES (" + this.id + ", "; 
            
            if(name != null) { 
                sqlHead += "name, ";
                sqlValues += "'" + name + "', "; 
            }
            
            if(ewkt != null) { 
                sqlHead += "ewkt, ";
                sqlValues += "'" + ewkt + "', "; 
            }
            
            // rest
            sqlHead += "starttime, durationtime, hidden, st_type) ";
            sqlValues += startTime + ", "
                    + durationTime + ", "
                    + "'" + String.valueOf(hidden) + "', "
                    + type
                    + ")";
            
            String hiddenString;
            hiddenString = hidden ? "'true'" : "'false'";
            
            String sqlStatement = sqlHead + sqlValues;
            
            statement.execute(sqlStatement);
            
            // insert subject identifier
            if(sis != null && sis.length > 0) {
                for (String si : sis) {
                    // insert each si into si table - duplicates are not allowed
                    String sqlString = "INSERT INTO " + SQLSharkKB.SI_TABLE + "(si, stid) VALUES ('" + si + "', '" + this.id + "')";
                    try {
                        statement.execute(sqlString);
                    }
                    catch(SQLException e) {
                        // tried to insert si twice
                        // TODO: can that happen here or do we check on merge etc. in callee?
                    }
                }
            }
            
            // insert addresses if any
            if(addresses != null && addresses.length > 0) {
                for (String addr : addresses) {
                    // insert each si into si table - duplicates are not allowed
                    String sqlString = "INSERT INTO " + SQLSharkKB.ADDRESS_TABLE + "(addr, stid) VALUES ('" + addr + "', '" + this.id + "')";
                    try {
                        statement.execute(sqlString);
                    }
                    catch(SQLException e) {
                        // tried to insert si twice
                        // TODO: can that happen here or do we check on merge etc. in callee?
                    }
                }
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot create semantic tag in SQL DB: " + ex.getLocalizedMessage());
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

    /**
     * Force to re-read line from st table
     */
    private void refreshBasics() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "SELECT * FROM " + SQLSharkKB.ST_TABLE + 
                    " where id = " 
                    + this.id                 
                    + ";";
            
            ResultSet result = statement.executeQuery(sqlStatement);
            
            if(!result.next()) {
                throw new SharkKBException("semantic tag removed in database");
            } 
            
            // else
            this.name = result.getString("name");
            this.ewkt = result.getString("ewkt");
            this.startTime = result.getLong("startTime");
            this.durationTime = result.getLong("durationTime");
            this.hidden = result.getBoolean("hidden");
            this.type = result.getInt("st_type");
            
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    /**
     * Force to re-read from sis table
     */
    private void refreshSIS() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "SELECT si FROM " + SQLSharkKB.SI_TABLE + 
                    " WHERE stid = " 
                    + this.id;
            
            ResultSet result = statement.executeQuery(sqlStatement);
            
            if(!result.next()) {
                // no si - it's an any tag
                return;
            } 
            
            // else
            List<String> siList = new ArrayList<>();
            
            do {
                String si = result.getString(1);
                siList.add(si);
            } while(result.next());
            
            String[] sisTmp = new String[siList.size()];
            Iterator<String> siIter = siList.iterator();
            
            for(int i = 0; i < sisTmp.length; i++) {
                sisTmp[i] = siIter.next();
            }
            
            this.sis = sisTmp;
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    String getName() {
        return this.name;
    }

    void setName(String name) throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "UPDATE " + SQLSharkKB.ST_TABLE + 
                    " SET name = '"
                    + name + "' WHERE id = "
                    + this.id;
            
            statement.execute(sqlStatement);
            
            this.name = name;
        } catch (SQLException ex) {
            L.d("cannot access SQL DB properly: " + ex.getLocalizedMessage(), this);
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    public int getID() {
        return this.id;
    }

    @Override
    public int getType() {
        return this.type;
    }
    
    String getEWKT() {
        return this.ewkt;
    }
    
    long getStartTime() {
        return this.startTime;
    }
    
    long getDurationTime() {
        return this.durationTime;
    }
    
    boolean isHidden() {
        return this.hidden;
    }
    
    void setHidden(boolean hidden) throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "UPDATE " + SQLSharkKB.ST_TABLE + 
                    " SET hidden = "
                    + String.valueOf(hidden) + " WHERE id = "
                    + this.id;
            
            statement.execute(sqlStatement);
            
            this.hidden = hidden;
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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

    String[] getSIS() throws SharkKBException {
        this.refreshSIS();
        return this.sis;
    }

    void removeSI(String si) throws SharkKBException {
        
        if(sis.length == 1) {
            throw new SharkKBException("cannot remove final subject identifier");
        }
        
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "DELETE FROM " + SQLSharkKB.SI_TABLE + 
                    " WHERE si = '" 
                    + si + "'";
            
            statement.execute(sqlStatement);
            
            String[] oldSIS = this.sis;
            this.sis = new String[oldSIS.length-1];
            int j = 0;
            for(int i = 0; i < this.sis.length; i++) {
                if(oldSIS[j].equalsIgnoreCase(si)) {
                    j++; // happens exactly once!
                } else {
                    this.sis[i] = oldSIS[j++];
                }
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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

    void addSI(String si) throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "INSERT INTO " + SQLSharkKB.SI_TABLE + 
                    " (si, stid) VALUES ('"
                    + si + "', "
                    + this.id
                    + ")";
            
            statement.execute(sqlStatement);
            
            String[] oldSIS = this.sis;
            this.sis = new String[oldSIS.length+1];
            
            // add new sis
            this.sis[0] = si;
            
            for(int i = 1; i < this.sis.length; i++) {
                this.sis[i] = oldSIS[i-1];
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    String[] getAddresses() throws SharkKBException {
        this.refreshAddresses();
        return this.addresses;
    }

    void removeAddress(String addr) throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "DELETE FROM " + SQLSharkKB.ADDRESS_TABLE + 
                    " WHERE addr = '" 
                    + addr + "'";
            
            statement.execute(sqlStatement);
            
            if(this.addresses.length > 1) {
                String[] oldAddr = this.addresses;
                this.addresses = new String[oldAddr.length-1];
                int j = 0;
                for(int i = 0; i < this.sis.length; i++) {
                    if(oldAddr[j].equalsIgnoreCase(addr)) {
                        j++; // happens exactly once!
                    } else {
                        this.addresses[i] = oldAddr[j++];
                    }
                }
            } else {
                this.addresses = new String[0];
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    void setAddresses(String[] addresses) throws SharkKBException {
        this.removeAllAddresses();
        
        if(addresses == null || addresses.length == 0) return;
        
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            for(String addr : addresses) {
                String sqlStatement = "INSERT INTO " + SQLSharkKB.ADDRESS_TABLE + 
                        " (addr, stid) VALUES ('"
                        + addr + "', "
                        + this.id
                        + ")";

                statement.execute(sqlStatement);
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
        
        this.addresses = new String[addresses.length];
        
        net.sharkfw.system.Util.copyStringArray(this.addresses, addresses, addresses.length);
    }
    
    /**
     * Force to re-read from sis table
     */
    private void refreshAddresses() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "SELECT addr FROM " + SQLSharkKB.ADDRESS_TABLE + 
                    " WHERE stid = " 
                    + this.id;
            
            ResultSet result = statement.executeQuery(sqlStatement);
            
            if(!result.next()) {
                // no addr
                return;
            } 
            
            // else
            List<String> addrList = new ArrayList<>();
            
            do {
                String si = result.getString(1);
                addrList.add(si);
            } while(result.next());
            
            String[] addrTmp = new String[addrList.size()];
            Iterator<String> addrIter = addrList.iterator();
            
            for(int i = 0; i < addrTmp.length; i++) {
                addrTmp[i] = addrIter.next();
            }
            
            this.addresses = addrTmp;
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    void removeAllAddresses() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            // remove Addresses
            String sqlStatement = "DELETE FROM " + SQLSharkKB.ADDRESS_TABLE
                    + " WHERE stid = " + this.id;
            
            statement.execute(sqlStatement);
            
            
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    void removeAllPredicates() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            // remove Addresses
            String sqlStatement = "DELETE FROM " + SQLSharkKB.PREDICATE_TABLE
                    + " WHERE sourceid = " + this.id
                    + " OR targetid = " + this.id;
            
            statement.execute(sqlStatement);
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    
    void addAddress(String addr) throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "INSERT INTO " + SQLSharkKB.ADDRESS_TABLE + 
                    " (addr, stid) VALUES ('"
                    + addr + "', "
                    + this.id
                    + ")";
            
            statement.execute(sqlStatement);
            
            String[] oldAddr = this.addresses;
            this.addresses = new String[oldAddr.length+1];
            
            // add new sis
            this.addresses[0] = addr;
            
            for(int i = 1; i < this.addresses.length; i++) {
                this.addresses[i] = oldAddr[i-1];
            }
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
    /**
     * Removes entries in kb
     */
    void remove() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "DELETE FROM " + SQLSharkKB.ST_TABLE
                    + " WHERE id = " + this.id;
            
            statement.execute(sqlStatement);
            
            // remove SI
            sqlStatement = "DELETE FROM " + SQLSharkKB.SI_TABLE
                    + " WHERE stid = " + this.id;
            
            statement.execute(sqlStatement);
            
            // remove Addresses
            sqlStatement = "DELETE FROM " + SQLSharkKB.ADDRESS_TABLE
                    + " WHERE stid = " + this.id;
            
            statement.execute(sqlStatement);
            
            
        } catch (SQLException ex) {
            throw new SharkKBException("cannot access SQL DB properly: " + ex.getLocalizedMessage());
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
}
