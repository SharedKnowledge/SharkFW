package net.sharkfw.knowledgeBase.persistent.fileDump;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * Created by j4rvis on 2/27/17.
 */
public class FileDumpPeerSemanticTag  extends FileDumpSemanticTag implements PeerSemanticTag{
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
