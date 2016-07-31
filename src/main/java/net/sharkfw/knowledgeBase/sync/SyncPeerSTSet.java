package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc42
 */
class SyncPeerSTSet extends SyncSTSet implements PeerSTSet {
    private final PeerSTSet target;
    
    SyncPeerSTSet(PeerSTSet peers) {
        super(peers);
        this.target = peers;
    }
    
    SyncPeerSemanticTag wrapSyncObject(PeerSemanticTag newST) {
        if(newST != null) {
            return new SyncPeerSemanticTag(newST);
        }

        return null;
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerSemanticTag(name, sis, addresses));
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerSemanticTag(name, sis, address));
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerSemanticTag(name, si, addresses));
    }

    @Override
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException {
        return this.wrapSyncObject(this.target.createPeerSemanticTag(name, si, address));
    }

    @Override
    public PeerSemanticTag getSemanticTag(String[] sis) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(sis));
    }

    @Override
    public PeerSemanticTag getSemanticTag(String si) throws SharkKBException {
        return this.wrapSyncObject(this.target.getSemanticTag(si));
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor) throws SharkKBException {
        return this.target.fragment(anchor);
    }

    @Override
    public PeerSTSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.fragment(anchor, fp);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(anchor, fp);
    }

    @Override
    public PeerSTSet contextualize(Enumeration<SemanticTag> anchor) throws SharkKBException {
        return this.target.contextualize(anchor);
    }

    @Override
    public PeerSTSet contextualize(STSet context) throws SharkKBException {
        return this.target.contextualize(context);
    }

    @Override
    public PeerSTSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException {
        return this.target.contextualize(context, fp);
    }

    @Override
    public Enumeration<PeerSemanticTag> peerTags() {
        Enumeration<PeerSemanticTag> pTags = this.target.peerTags();
        
        if(pTags == null) return null;

        // wrap it
        return new Iterator2Enumeration(this.wrapSTEnum(this, pTags).iterator());
    }
}
