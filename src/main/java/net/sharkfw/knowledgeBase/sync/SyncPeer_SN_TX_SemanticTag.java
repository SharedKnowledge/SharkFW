package net.sharkfw.knowledgeBase.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.PeerSNSemanticTag;
import net.sharkfw.knowledgeBase.PeerTXSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.TXSemanticTag;
import net.sharkfw.system.Iterator2Enumeration;

/**
 *
 * @author thsc
 */
class SyncPeer_SN_TX_SemanticTag extends SyncSNSemanticTag 
    implements PeerSNSemanticTag {
    
//    private PeerSNSemanticTag snTarget = null;
    private PeerTXSemanticTag txTarget = null;

    public SyncPeer_SN_TX_SemanticTag(PeerSNSemanticTag target) {
        super(target);
        
        // all semantic net methods are called in super class
        // this.snTarget = target;
    }

    public SyncPeer_SN_TX_SemanticTag(PeerTXSemanticTag target) {
        super(target);
        
        this.txTarget = target;
    }

    SyncPeer_SN_TX_SemanticTag wrapSyncObject(PeerSNSemanticTag target) {
        if(target != null) {
            return new SyncPeer_SN_TX_SemanticTag(target);
        }
        return null;
    }

    SyncPeer_SN_TX_SemanticTag wrapSyncObject(PeerTXSemanticTag target) {
        if(target != null) {
            return new SyncPeer_SN_TX_SemanticTag(target);
        }
        return null;
    }

    @Override
    public String[] getAddresses() {
        return this.txTarget.getAddresses();
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.txTarget.setAddresses(addresses);
        this.changed();
    }

    @Override
    public void removeAddress(String address) {
        this.txTarget.removeAddress(address);
        this.changed();
    }

    @Override
    public void addAddress(String address) {
        this.txTarget.addAddress(address);
        this.changed();
    }

    @Override
    public Enumeration<SemanticTag> subTags() {
        Enumeration<TXSemanticTag> subTags = this.txTarget.getSubTags();
        
        // wrap them
        ArrayList wrapSTIter = this.wrapSTEnum(this, subTags);
        return new Iterator2Enumeration(wrapSTIter.iterator());
    }

    @Override
    public TXSemanticTag getSuperTag() {
        PeerTXSemanticTag superTag = (PeerTXSemanticTag) this.txTarget.getSuperTag();
        return this.wrapSyncObject(superTag);
    }

    @Override
    public Enumeration<TXSemanticTag> getSubTags() {
        Enumeration<TXSemanticTag> subTags = this.txTarget.getSubTags();
        
        // wrap them
        ArrayList wrapSTIter = this.wrapSTEnum(this, subTags);
        return new Iterator2Enumeration(wrapSTIter.iterator());
    }

    @Override
    public void move(TXSemanticTag supertag) {
        this.txTarget.move(supertag);
    }

    @Override
    public void merge(TXSemanticTag toMerge) {
        this.txTarget.merge(toMerge);
    }
}
