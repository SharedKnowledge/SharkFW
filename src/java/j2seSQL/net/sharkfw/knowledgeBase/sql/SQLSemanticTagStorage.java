package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
class SQLSemanticTagStorage implements PropertyOwner, PropertyHolder {
    private final SQLSharkKB kb;
    private final int id;
    private String name;
    private String ewkt;
    private long startTime;
    private long durationTime;
    private boolean hidden;
    private int type;
    private String[] sis;
    
    private SQLPropertyHolder propertyHolder;

    SQLSemanticTagStorage(SQLSharkKB kb, int id) throws SharkKBException {
        this.kb = kb;
        this.id = id;
        
        this.propertyHolder = new SQLPropertyHolder(kb, this);
        
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
            String[] sis) throws SharkKBException {
        
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
        this.propertyHolder = new SQLPropertyHolder(kb, this);
        
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            // get next id
            int semanticTagID = 0;
            
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
                    String sqlString = "INSERT INTO " + SQLSharkKB.SI_TABLE + "(si, stid) VALUES ('" + si + "', '" + semanticTagID + "')";
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
            
            String sqlStatement = "SELECT stid FROM " + SQLSharkKB.ST_TABLE + 
                    "where id = " 
                    + this.id                 
                    + ")";
            
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
    
    private void refreshPropertys() throws SharkKBException {
        this.propertyHolder.refresh();
    }
        
    String getName() {
        return this.name;
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
    
    String[] getSIS() throws SharkKBException {
        this.refreshSIS();
        return this.sis;
    }

    void removeSI(String si) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void addSI(String si) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setHidden(boolean hidden) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.propertyHolder.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        return this.propertyHolder.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.propertyHolder.setProperty(name, value, transfer);
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.propertyHolder.removeProperty(name);
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        return this.propertyHolder.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        return this.propertyHolder.propertyNames(all);
    }
}
