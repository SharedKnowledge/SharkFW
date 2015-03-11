package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoPropertyHolder;

/**
 *
 * @author thsc
 */
public class SQLPropertyHolder implements PropertyHolder {
    private final PropertyOwner pOwner;
    private final SQLSharkKB kb;
    private InMemoPropertyHolder pHolder;
    private boolean inSync;
    
    SQLPropertyHolder(SQLSharkKB kb, PropertyOwner pOwner) {
        this.kb = kb;
        this.pOwner = pOwner;
        
        this.inSync = false;
    }

    void refresh() throws SharkKBException {
        if(inSync) { return; }
        
        // drop in memo holder
        this.pHolder = new InMemoPropertyHolder();
        
        // re-read from database
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "SELECT name, value, hidden FROM " + SQLSharkKB.PROERTIES_TABLE + 
                    " where ownerid = " 
                    + this.pOwner.getID() + " AND "
                    + "entity_type = " + this.pOwner.getType()
                    + ";";
            
            ResultSet result = statement.executeQuery(sqlStatement);
            
            if(!result.next()) {
                // no properties in DB - that's ok.
                return;
            } 
            
            // else - there are properties - fill in propertyHolder
            do {
                this.pHolder.setProperty(
                    result.getString("name"),
                    result.getString("value"),
                    result.getBoolean("hidden"));
            } while (result.next());
            
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
    
    private void save() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            // remove all properties first
            String sqlremove = "DELETE FROM " + SQLSharkKB.PROERTIES_TABLE +
                    " WHERE ownerid = '" 
                    + this.pOwner.getID() + "' and entity_type = '"
                    + this.pOwner.getType() + "';";
            
            statement.execute(sqlremove);
            
            // not hidden properties
            HashMap<String, String> props = this.pHolder.getUnhiddenProperties();
            Iterator<String> iterNames = null;
            Iterator<String> iterValues = null;
            if(props != null) {
                 iterNames = props.keySet().iterator();
                 while(iterNames.hasNext()) {
                     String name = iterNames.next();
                     String value = props.get(name);
                     
                     // write unhidden property to db
                    String sqlStatement = "INSERT INTO " + 
                            SQLSharkKB.PROERTIES_TABLE
                            + "(name, value, hidden) VALUES ('"
                            + name + "', '"
                            + value + "', 'false')";
                    
                    statement.execute(sqlStatement);
                 }
            }
            
            // hidden properties
            props = this.pHolder.getHiddenProperties();
            if(props != null) {
                 iterNames = props.keySet().iterator();
                 while(iterNames.hasNext()) {
                     String name = iterNames.next();
                     String value = props.get(name);
                     
                     // write unhidden property to db
                    String sqlStatement = "INSERT INTO " + 
                            SQLSharkKB.PROERTIES_TABLE
                            + "(name, value, hidden) VALUES ('"
                            + name + "', '"
                            + value + "', 'true')";
                    
                    statement.execute(sqlStatement);
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
        
        this.inSync = true;
    }
    
    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        this.refresh();
        this.pHolder.setProperty(name, value);
        this.save();
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        this.refresh();
        return this.pHolder.getProperty(name);
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        this.refresh();
        this.pHolder.setProperty(name, value, transfer);
        this.save();
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        this.refresh();
        this.pHolder.removeProperty(name);
        this.save();
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        this.refresh();
        return this.pHolder.propertyNames();
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        this.refresh();
        return this.pHolder.propertyNames(all);
    }
}
