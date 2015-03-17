package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
class SQLPeerSTSet extends SQLSTSet implements PeerSTSet {
    
    SQLPeerSTSet(SQLSharkKB kb) {
        super(kb);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        SQLSemanticTagStorage sqlTag = this.createSQLSemanticTag(
                this.getSSQLSharkKB(), name, null, 0, 0, false, 
                SQLSharkKB.PEER_SEMANTIC_TAG_TYPE, sis, addresses);
        
        return new SQL_SN_TX_PeerSemanticTag(this.getSSQLSharkKB(), sqlTag);
    }

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.getSemanticTag(new String[] {si});
    }
    
    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, sis, new String[] {address});
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, addresses);
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerSemanticTag(name, new String[] {si}, new String[] {address} );
    }

    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        SQLSemanticTagStorage sqlTag = this.getSQLSemanticTagStorage(sis);
        return (PeerSemanticTag) SQLSharkKB.wrapSQLTagStorage(this.kb, sqlTag, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet contextualize(STSet context) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
