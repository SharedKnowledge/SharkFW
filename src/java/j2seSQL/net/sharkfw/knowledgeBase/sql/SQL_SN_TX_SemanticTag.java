package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.SystemPropertyHolder;
import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_SemanticTag;

/**
 *
 * @author thsc
 */
public class SQL_SN_TX_SemanticTag extends InMemo_SN_TX_SemanticTag {
    private final int stID;

    SQL_SN_TX_SemanticTag(String name, String[] si, int stID, SQLPropertyHolder sqlPropertyHolder) {
        super(name, si);
        
        this.stID = stID;
        this.setPropertyHolder((SystemPropertyHolder) sqlPropertyHolder);
    }
    
    @Override
    public void persist() {
        // save name and/or sis to db
    }
}
