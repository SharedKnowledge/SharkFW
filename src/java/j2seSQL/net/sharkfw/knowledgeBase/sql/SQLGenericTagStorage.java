package net.sharkfw.knowledgeBase.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 * @param <ST>
 */
public class SQLGenericTagStorage<ST extends SemanticTag> extends 
        InMemoGenericTagStorage<ST> {
    
    private final SQLSharkKB sqlSharkKB;
    
    public SQLGenericTagStorage(SQLSharkKB sqlSharkKB) {
        this.sqlSharkKB = sqlSharkKB;
    }
    
    /**
     *
     * @param t
     * @throws SharkKBException
     */
    @Override
    protected void add(ST t) throws SharkKBException {
        if(t == null) return;
        
        super.add(t); // shouldn't be done - only use db!
        
        try {
            SemanticTag tag = t;
            String name = tag.getName();
            
            Statement statement  = this.sqlSharkKB.getConnection().createStatement();
            
            if(name != null) {
                statement.execute("INSERT INTO " + SQLSharkKB.ST_TABLE + "(name) VALUES ('" + name + "')");
            } else {
                statement.execute("INSERT INTO " + SQLSharkKB.ST_TABLE + "(name) VALUES ('')");
            }
            
            statement.execute("select currval('stid');");
            // HIER WEITERMACHEN
            ResultSet resultSet = statement.getResultSet();
            // now: get new index and add sis
        }
        catch(SQLException e) {
            throw new SharkKBException(e.getLocalizedMessage());
        }
    }   
    
    // TODO
    @Override
    public void siAdded(String addSI, ST tag) {
        super.siAdded(addSI, tag);
    }

    // TODO
    @Override
    public void siRemoved(String deleteSI, ST tag) {
        super.siRemoved(deleteSI, tag);
    }
    
    
    /**
     * TODO must be overwritten
     * @param si
     * @return
     * @throws SharkKBException
     */
//    @Override
//    public ST getSemanticTag(String si) throws SharkKBException {    
//        // TODO
//        return null;
//    }
    
    /**
     *
     * @param tag
     */
    @Override
    public void removeSemanticTag(ST tag) {
        super.removeSemanticTag(tag);
        
        // TODO: remove in db
    }
    
    /**
     *
     * @return
     */
//    @Override
//    public Enumeration<ST> tags() { 
//        // TODO
//        return null;
//    }
}
