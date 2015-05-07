package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.security.pki.SharkCertificate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ac
 */
public class SharkPkiStorage implements PkiStorage, Serializable {

    private List<SharkCertificate> sharkCertificateList;

    public SharkPkiStorage() {
        sharkCertificateList = new ArrayList<>();
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag) {
        for(SharkCertificate sharkCertificate : sharkCertificateList) {
            if(sharkCertificate.getIssuer().identical(peerSemanticTag)){
                return sharkCertificate;
            }
        }
        return null;
    }

    @Override
    public void addSharkCertificate(SharkCertificate sharkCertificate) {

    }

    @Override
    public List<SharkCertificate> getSharkCertificateList() {
        return sharkCertificateList;
    }
}
