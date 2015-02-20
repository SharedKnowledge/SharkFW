package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoGenericTagStorage;

/**
 *
 * @author thsc
 * @param <ST>
 */
public class SQLGenericTagStorage<ST extends SemanticTag> extends 
        InMemoGenericTagStorage<ST> {
    
    private final String tableName;
    
    public SQLGenericTagStorage(String tableName) {
        this.tableName = tableName;
    }
    
    /**
     * TODO
     */
    protected void persist() {
        // TODO
    }
    
    /**
     *
     * @param tag
     * @throws SharkKBException
     */
    @Override
    protected void add(ST tag) throws SharkKBException {
        // TODO
    }   
    
    /**
     *
     * @param si
     * @return
     * @throws SharkKBException
     */
    @Override
    public ST getSemanticTag(String si) throws SharkKBException {    
        // TODO
        return null;
    }
    
    /**
     *
     * @param tag
     */
    @Override
    public void removeSemanticTag(ST tag) {
        // TODO
    }
    
    /**
     *
     * @return
     */
    @Override
    public Enumeration<ST> tags() {    
        // TODO
        return null;
    }
}
