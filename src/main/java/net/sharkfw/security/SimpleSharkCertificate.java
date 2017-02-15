package net.sharkfw.security;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

import java.security.PublicKey;

/**
 * Created by j4rvis on 2/13/17.
 */
public class SimpleSharkCertificate implements SharkCertificate {

    private PublicKey ownerPublickKey;
    private PeerSemanticTag owner;
    private long validity;
    private byte[] fingerprint;
    private long receiveDate;
    private PeerSemanticTag signer;
    private byte[] signature;
    private long signingDate;

    public SimpleSharkCertificate(PublicKey ownerPublickKey,
                                  PeerSemanticTag owner,
                                  long validity,
                                  byte[] fingerprint,
                                  long receiveDate,
                                  PeerSemanticTag signer,
                                  byte[] signature,
                                  long signingDate) {

        this.ownerPublickKey = ownerPublickKey;
        this.owner = owner;
        this.validity = validity;
        this.fingerprint = fingerprint;
        this.receiveDate = receiveDate;
        this.signer = signer;
        this.signature = signature;
        this.signingDate = signingDate;
    }

    @Override
    public PublicKey getOwnerPublicKey() {
        return this.ownerPublickKey;
    }

    @Override
    public PeerSemanticTag getOwner() {
        return this.owner;
    }

    @Override
    public long getValidity() {
        return this.validity;
    }

    @Override
    public byte[] getFingerprint() {
        return this.fingerprint;
    }

    @Override
    public long receiveDate() {
        return this.receiveDate;
    }

    @Override
    public void delete() {

    }

    @Override
    public PeerSemanticTag getSigner() {
        return this.signer;
    }

    @Override
    public byte[] getSignature() {
        return this.signature;
    }

    @Override
    public long signingDate() {
        return this.signingDate;
    }
}
