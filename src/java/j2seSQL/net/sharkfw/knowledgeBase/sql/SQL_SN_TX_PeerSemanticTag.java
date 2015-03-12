package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQL_SN_TX_PeerSemanticTag extends SQL_SN_TX_SemanticTag {
    SQL_SN_TX_PeerSemanticTag(SQLSharkKB kb, SQLSemanticNet sn, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sn, sqlST);
    }
}
