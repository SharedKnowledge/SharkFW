package net.sharkfw.knowledgeBase.sync;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by thsc42 on 28.07.16.
 */
class SyncPeerSemanticTag extends SyncSemanticTag implements 
        PeerSemanticTag {
    
    private final PeerSemanticTag target;

    SyncPeerSemanticTag(PeerSemanticTag target) {
        super(target);
        
        this.target = target;
    }
    
    @Override
    public String[] getAddresses() {
        return this.target.getAddresses();
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.target.setAddresses(addresses);
        this.changed();
    }

    @Override
    public void removeAddress(String address) {
        this.target.removeAddress(address);
        this.changed();
    }

    @Override
    public void addAddress(String address) {
        this.target.addAddress(address);
        this.changed();
    }
}
