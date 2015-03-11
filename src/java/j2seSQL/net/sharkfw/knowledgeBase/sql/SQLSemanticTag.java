package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
class SQLSemanticTag implements PropertyOwner {
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

    SQLSemanticTag(SQLSharkKB kb, int id) {
        this.kb = kb;
        this.id = id;
        
        this.propertyHolder = new SQLPropertyHolder(kb, this);
    }

    /**
     * Force to re-read line from st table
     */
    private void refreshBasics() throws SharkKBException {
        Statement statement = null;
        try {
            statement = this.kb.getConnection().createStatement();
            
            String sqlStatement = "SELECT * FROM " + SQLSharkKB.ST_TABLE + 
                    "where id = " 
                    + this.id                 
                    + ")";
            
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
            this.type = result.getInt("durationTime");
            
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
        return null;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getType() {
        return this.type;
    }
    
}
