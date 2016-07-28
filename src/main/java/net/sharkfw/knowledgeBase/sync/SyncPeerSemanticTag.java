package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by local on 28.07.16.
 */
class SyncPeerSemanticTag extends SyncSemanticTag implements PeerSemanticTag {

    SyncPeerSemanticTag(PeerSemanticTag target) {
        super(target);
    }

    protected SyncPeerSemanticTag getTarget() {
        return (SyncPeerSemanticTag) super.getTarget();
    }

    @Override
    public String[] getAddresses() {
        return new String[0];
    }

    @Override
    public void setAddresses(String[] addresses) {

    }

    @Override
    public void removeAddress(String address) {

    }

    @Override
    public void addAddress(String address) {

    }
}
