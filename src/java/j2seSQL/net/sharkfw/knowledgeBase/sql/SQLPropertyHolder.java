package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.inmemory.InMemoPropertyHolder;

/**
 *
 * @author thsc
 */
public class SQLPropertyHolder extends InMemoPropertyHolder {
    private final int id;
    private final int entityType;
    
    SQLPropertyHolder(int id, int entityType) {
        this.id = id;
        this.entityType = entityType;
    }
}
