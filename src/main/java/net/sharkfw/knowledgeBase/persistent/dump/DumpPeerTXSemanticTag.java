package net.sharkfw.knowledgeBase.persistent.dump;

import net.sharkfw.knowledgeBase.PeerTXSemanticTag;

/**
 * Created by j4rvis on 2/28/17.
 */
public class DumpPeerTXSemanticTag extends DumpTXSemanticTag implements PeerTXSemanticTag {

    private final PeerTXSemanticTag peerTXSemanticTag;

    public DumpPeerTXSemanticTag(DumpSharkKB dumpSharkKB, PeerTXSemanticTag tag) {
        super(dumpSharkKB, tag);
        peerTXSemanticTag = tag;
    }

    @Override
    public String[] getAddresses() {
        return this.peerTXSemanticTag.getAddresses();
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.peerTXSemanticTag.setAddresses(addresses);
        this.kb.persist();
    }

    @Override
    public void removeAddress(String address) {
        this.peerTXSemanticTag.removeAddress(address);
        this.kb.persist();
    }

    @Override
    public void addAddress(String address) {
        this.peerTXSemanticTag.addAddress(address);
        this.kb.persist();
    }
}
