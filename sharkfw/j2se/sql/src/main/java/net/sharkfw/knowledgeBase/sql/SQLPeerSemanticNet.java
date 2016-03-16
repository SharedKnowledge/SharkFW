package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkCSAlgebra;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerSemanticNet;
import net.sharkfw.system.Iterator2Enumeration;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class SQLPeerSemanticNet extends SQLSemanticNet implements PeerSemanticNet {

    public SQLPeerSemanticNet(SQLSharkKB kb) {
        super(kb, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
    }    

    @Override
    public PeerSTSet asPeerSTSet() {
        try {
            return this.kb.getPeerSTSet();
        } catch (SharkKBException ex) {
            L.e("SQLSharkKB doesn't have a peer st set - fatal", this);
        }
        
        return null;
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        SQLSemanticTagStorage sqlST = this.createSQLSemanticTag(kb, name,
                null, 0, 0, false, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE, 
                sis, addresses);
        
        return (PeerSNSemanticTag) SQLSharkKB.wrapSQLTagStorage(
                kb, sqlST, SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si} , addresses);
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createSemanticTag(name, new String[] {si} , new String[] {address});
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createSemanticTag(name, sis, new String[] {address});
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        SQLSemanticTagStorage sqlST = this.getSQLSemanticTagStorage(sis);
        
        if(sqlST == null) {
            return null;
        }
        
        return new SQL_SN_TX_PeerSemanticTag(this.getSSQLSharkKB(), sqlST);
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return (PeerSNSemanticTag) super.getSemanticTag(si);
    }

    @Override
    public PeerSemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        PeerSemanticNet fragment = new InMemoPeerSemanticNet();
        SharkCSAlgebra.fragment(fragment, anchor, this, 
                fp.getAllowedPredicates(), 
                fp.getForbiddenPredicates(), fp.getDepth());
        
        return fragment;
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        return new Iterator2Enumeration(this.stTags());
    }
}
