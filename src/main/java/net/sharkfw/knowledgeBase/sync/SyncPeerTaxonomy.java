package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.PeerTaxonomy;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerTaxonomy;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
class SyncPeerTaxonomy extends SyncTaxonomy implements PeerTaxonomy {
    private final PeerTaxonomy target;
    
    public SyncPeerTaxonomy(PeerTaxonomy tx) {
        super(tx);
        
        this.target = tx;
    }
    
    SyncPeer_SN_TX_SemanticTag wrapSyncObject(PeerTXSemanticTag target) {
        if(target != null) {
            return new SyncPeer_SN_TX_SemanticTag(target);
        }
        return null;
    }

    /**
     * not wrapped ? (TODO)
     * @param pstGroup
     * @return
     * @throws SharkKBException 
     */
    @Override
    public PeerTaxonomy resolveSuperPeers(PeerTXSemanticTag pstGroup) throws SharkKBException {
        return this.target.resolveSuperPeers(pstGroup);
    }

    @Override
    public PeerSTSet asPeerSTSet() throws SharkKBException {
        return new SyncPeerSTSet(this.target.asPeerSTSet());
    }

    @Override
    public PeerTXSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(sis));
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerTXSemanticTag(name, sis, addresses));
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerTXSemanticTag(name, si, addresses));
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerTXSemanticTag(name, sis, address));
    }

    @Override
    public PeerTXSemanticTag createPeerTXSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerTXSemanticTag(name, si, address));
    }

    @Override
    public void move(PeerTXSemanticTag superPST, PeerTXSemanticTag subPST) throws SharkKBException {
        this.target.move(superPST, subPST);
    }

    @Override
    public PeerTaxonomy contextualize(PeerSTSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Enumeration<PeerSemanticTag> peerTags = this.target.peerTags();
        return new Iterator2Enumeration(this.wrapSTEnum(this, peerTags).iterator());
    }

    @Override
    PeerTaxonomy getChangesAsTaxonomy(Long since) throws SharkKBException {
        PeerTaxonomy changes = new InMemoPeerTaxonomy();
        
        this.putChanges(since, changes);
        
        return changes;
    }
}
