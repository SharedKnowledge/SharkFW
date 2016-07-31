package net.sharkfw.knowledgeBase.sync;

import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticNet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc42
 */
class SyncPeerSemanticNet extends SyncSemanticNet implements PeerSemanticNet {
    private final PeerSemanticNet target;
    
    SyncPeerSemanticNet(PeerSemanticNet target) {
        super(target);
        this.target = target;
    }
    
    SyncPeer_SN_TX_SemanticTag wrapSyncObject(PeerSNSemanticTag target) {
        if(target != null) {
            return new SyncPeer_SN_TX_SemanticTag(target);
        }
        return null;
    }

    @Override
    public PeerSTSet asPeerSTSet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, sis, addresses));
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, si, addresses));
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, si, address));
    }

    @Override
    public PeerSNSemanticTag createSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createSemanticTag(name, sis, address));
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(sis));
    }

    @Override
    public PeerSNSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    @Override
    public PeerSemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.fragment(anchor, fp);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() throws SharkKBException {
        Enumeration<PeerSemanticTag> peerTags = this.target.peerTags();
        return new Iterator2Enumeration(this.wrapSTEnum(this, peerTags).iterator());
    }
}
