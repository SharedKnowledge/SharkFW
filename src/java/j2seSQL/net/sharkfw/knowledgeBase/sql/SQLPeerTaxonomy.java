package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.SemanticNet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.knowledgeBase.TaxonomyWrapper;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
public class SQLPeerTaxonomy extends TaxonomyWrapper implements PeerTaxonomy {
    private final SQLPeerSemanticNet psn;
    private final SQLSharkKB kb;
    
    SQLPeerTaxonomy(SQLSharkKB kb, SQLPeerSemanticNet psn) {
        this.kb = kb;
        this.psn = psn;
    }

    @Override
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSemanticTag(SemanticTag tag) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerTaxonomy resolveSuperPeers(PeerTXSemanticTag pstGroup) throws SharkKBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSTSet asPeerSTSet() throws SharkKBException {
        return this.kb.getPeerSTSet();
    }

    @Override
    public PeerTXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.psn.getSemanticTag(sis);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        return this.psn.createSemanticTag(name, sis, addresses);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, new String[] {si}, addresses);
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, sis, new String[] {address});
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.createPeerTXSemanticTag(name, new String[] {si}, new String[] {address});
    }

    @Override
    public void move(PeerTXSemanticTag superPST, PeerTXSemanticTag subPST) throws SharkKBException {
        subPST.move(superPST);
    }

    @Override
    public PeerTaxonomy contextualize(PeerSTSet context, FragmentationParameter fp) throws SharkKBException {
        PeerSemanticNet peerSN = (PeerSemanticNet) this.psn.contextualize(context, fp);
        return new InMemoPeerTaxonomy(peerSN);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Iterator tags = this.psn.tags(SQLSharkKB.PEER_SEMANTIC_TAG_TYPE);
        return new Iterator2Enumeration(tags);
    }
}
