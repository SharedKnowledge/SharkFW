package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
class SQL_SN_TX_PeerSemanticTag extends SQL_SN_TX_SemanticTag implements PeerSemanticTag, PeerTXSemanticTag, PeerSNSemanticTag {
    SQL_SN_TX_PeerSemanticTag(SQLSharkKB kb, SQLSemanticTagStorage sqlST) throws SharkKBException {
        super(kb, sqlST);
    }

    @Override
    public String[] getAddresses() {
        try {
            return this.sqlST.getAddresses();
        } catch (SharkKBException ex) {
            L.e("cannot access addresses: " + ex.getLocalizedMessage());
        }
        
        return null;
    }

    @Override
    public void setAddresses(String[] addresses) {
        try {
            this.sqlST.setAddresses(addresses);
        } catch (SharkKBException ex) {
            L.e("cannot access addresses: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void removeAddress(String address) {
        try {
            this.sqlST.removeAddress(address);
        } catch (SharkKBException ex) {
            L.e("cannot remove address: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void addAddress(String address) {
        try {
            this.sqlST.addAddress(address);
        } catch (SharkKBException ex) {
            L.e("cannot add address: " + ex.getLocalizedMessage());
        }
    }
}
