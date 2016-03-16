package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public class SQLPeerSemanticTag extends SQLSemanticTag implements PeerSemanticTag {
    SQLPeerSemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
        
        if(sqlST.getType() != SQLSharkKB.TIME_SEMANTIC_TAG_TYPE) {
            throw new SharkKBException("cannot create time semantic tag with non time semantic tag values");
        }
    }

    @Override
    public String[] getAddresses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAddresses(String[] addresses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAddress(String address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAddress(String address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
