package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpPeerSemanticTag extends FileDumpSemanticTag implements PeerSemanticTag {

    private final PeerSemanticTag peerSemanticTag;

    public FileDumpPeerSemanticTag(FileDumpSharkKB fileDumpSharkKB, PeerSemanticTag tag) {
        super(fileDumpSharkKB, tag);
        peerSemanticTag = tag;
    }

    @Override
    public String[] getAddresses() {
        return this.peerSemanticTag.getAddresses();
    }

    @Override
    public void setAddresses(String[] addresses) {
        this.peerSemanticTag.setAddresses(addresses);
        this.kb.persist();
    }

    @Override
    public void removeAddress(String address) {
        this.peerSemanticTag.removeAddress(address);
        this.kb.persist();
    }

    @Override
    public void addAddress(String address) {
        this.peerSemanticTag.addAddress(address);
        this.kb.persist();
    }
}
