package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.inmemory.InMemo_SN_TX_PeerSemanticTag;

/**
 *
 * @author thsc
 */
public class SQL_SN_TX_PeerSemanticTag extends InMemo_SN_TX_PeerSemanticTag {
    private final SQLPropertyHolder sqlPropertyHolder;
    private final int stID;
    
    public SQL_SN_TX_PeerSemanticTag(String name, String[] si, String[] addresses, int stID, SQLPropertyHolder sqlPropertyHolder) {
        super(name, si, addresses);
        
        this.stID = stID;
        this.sqlPropertyHolder = sqlPropertyHolder;
    }
}
