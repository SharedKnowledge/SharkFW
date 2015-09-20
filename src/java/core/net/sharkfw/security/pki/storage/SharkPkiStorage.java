package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.system.L;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static net.sharkfw.security.utility.SharkCertificateHelper.*;

/**
 * @author ac
 */
public class SharkPkiStorage implements PkiStorage {

    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME = "certificate";
    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_SI = "cc:certificate";
    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_OWNER_PRIVATE_KEY_NAME = "private_key";
    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_OWNER_PRIVATE_KEY_SI = "cc:private_key";
    public final static String PKI_INFORMATION_OWNER_PRIVATE_KEY_NAME = "private_key";
    public final static String PKI_INFORMATION_PUBLIC_KEY_NAME = "public_key";
    public final static String PKI_INFORMATION_TRANSMITTER_LIST_NAME = "transmitter_list";
    public final static String PKI_INFORMATION_TRUST_LEVEL = "trust_level";
    public final static SemanticTag PKI_CONTEXT_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME, new String[]{PKI_CONTEXT_POINT_SEMANTIC_TAG_SI});
    public final static SemanticTag PKI_OWNER_PRIVATE_KEY_CONTEXT_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(PKI_CONTEXT_POINT_SEMANTIC_TAG_OWNER_PRIVATE_KEY_NAME, new String[]{PKI_CONTEXT_POINT_SEMANTIC_TAG_OWNER_PRIVATE_KEY_SI});

    private ContextCoordinates contextCoordinatesFilter;
    private ContextCoordinates ownerPrivateKeyContextCoordinatesFilter;
    private KeyFactory keyFactory;
    private SharkKB sharkPkiStorageKB;
    private PeerSemanticTag sharkPkiStorageOwner;

    public SharkPkiStorage(SharkKB sharkKB, PeerSemanticTag owner, PrivateKey privateKey) throws SharkKBException, NoSuchAlgorithmException {
        initialize(sharkKB, owner);
        storePrivateKey(ownerPrivateKeyContextCoordinatesFilter, privateKey);
    }

    public SharkPkiStorage(SharkKB sharkKBWithStoredPrivateKey, PeerSemanticTag owner) throws SharkKBException, NoSuchAlgorithmException {
        initialize(sharkKBWithStoredPrivateKey, owner);
    }

    private void initialize(SharkKB sharkKB, PeerSemanticTag owner) throws NoSuchAlgorithmException {
        sharkPkiStorageKB = sharkKB;
        sharkPkiStorageOwner = owner;

        contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                PKI_CONTEXT_COORDINATE,
                sharkPkiStorageOwner,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);

        ownerPrivateKeyContextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                PKI_OWNER_PRIVATE_KEY_CONTEXT_COORDINATE,
                sharkPkiStorageOwner,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_NOTHING
        );
        keyFactory = KeyFactory.getInstance(SharkKeyPairAlgorithm.RSA.name());
    }

    @Override
    public PrivateKey getOwnerPrivateKey() throws SharkKBException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, ownerPrivateKeyContextCoordinatesFilter);
        if(knowledge != null) {
            ContextPoint cp = knowledge.contextPoints().nextElement();
            if(cp != null) {
                try {
                    Information information = cp.getInformation(PKI_INFORMATION_OWNER_PRIVATE_KEY_NAME).next();
                    return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(information.getContentAsByte()));
                } catch (InvalidKeySpecException e) {
                    new SharkKBException("No private key stored in the knowledge base. Wrong KB?");
                }
            }
        }
        new SharkKBException("No private key stored in the knowledge base. Wrong KB?");
        return null;
    }

    @Override
    public void replaceOwnerPrivateKey(PrivateKey newPrivateKey) throws SharkKBException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, ownerPrivateKeyContextCoordinatesFilter);
        if(knowledge != null) {
            ContextPoint cp = knowledge.contextPoints().nextElement();
            if(cp != null) {
                //Remove old private key and replace it whit the new one
                cp.removeInformation(cp.getInformation(PKI_INFORMATION_OWNER_PRIVATE_KEY_NAME).next());
                Information private_key = cp.addInformation();
                private_key.setName(PKI_INFORMATION_OWNER_PRIVATE_KEY_NAME);
                private_key.setContent(newPrivateKey.getEncoded());
            }
        }
        new SharkKBException("No private key stored in the knowledge base. Wrong KB?");
    }

    @Override
    public boolean addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException, InvalidKeySpecException {
        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getValidity().getTime());

        if(isCertificateInKb(sharkCertificate)) {
            return false;
        }

        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE,             //Topic
                sharkPkiStorageOwner,               //Originator
                sharkCertificate.getSubject(),      //Peer
                sharkCertificate.getIssuer(),       //Remote peer -> if null any
                time,                               //Time -> if null any
                null,                               //Location -> if null any
                SharkCS.DIRECTION_INOUT);           //Direction
        ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);

        Information publicKey = contextPoint.addInformation();
        publicKey.setName(PKI_INFORMATION_PUBLIC_KEY_NAME);
        publicKey.setContent(sharkCertificate.getSubjectPublicKey().getEncoded());

        Information transmitterList = contextPoint.addInformation();
        transmitterList.setName(PKI_INFORMATION_TRANSMITTER_LIST_NAME);
        transmitterList.setContent(getByteArrayFromLinkedList(sharkCertificate.getTransmitterList()));

        Information trustLevel = contextPoint.addInformation();
        trustLevel.setName(PKI_INFORMATION_TRUST_LEVEL);
        trustLevel.setContent(sharkCertificate.getTrustLevel().name());

        return true;
    }

    @Override
    public boolean addSharkCertificate(ContextPoint sharkCertificate) throws SharkKBException {
        //SharkCS (2nd parameter) can't be null -> exception
        //SharkCSAlgebra.merge(sharkPkiStorageKB, null, sharkCertificate, false);

        //CC still empty no entry was made
        //SharkCSAlgebra.merge(sharkPkiStorageKB, sharkPkiStorageKB.asSharkCS(), sharkCertificate, false);

        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getContextCoordinates().getTime().getDuration());

        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE,                                   //Topic
                sharkPkiStorageOwner,                                     //Originator
                sharkCertificate.getContextCoordinates().getPeer(),       //Peer
                sharkCertificate.getContextCoordinates().getRemotePeer(), //Remote peer -> if null any
                time,                                                     //Time -> if null any
                null,                                                     //Location -> if null any
                SharkCS.DIRECTION_INOUT);                                 //Direction
        ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);

        try {
            Information publicKey = contextPoint.addInformation();
            publicKey.setName(PKI_INFORMATION_PUBLIC_KEY_NAME);
            publicKey.setContent(extractInformation(sharkCertificate, PKI_INFORMATION_PUBLIC_KEY_NAME).getContentAsByte());

            Information transmitterList = contextPoint.addInformation();
            transmitterList.setName(PKI_INFORMATION_TRANSMITTER_LIST_NAME);
            transmitterList.setContent(extractInformation(sharkCertificate, PKI_INFORMATION_TRANSMITTER_LIST_NAME).getContentAsByte());

            Information trustLevel = contextPoint.addInformation();
            trustLevel.setName(PKI_INFORMATION_TRUST_LEVEL);
            trustLevel.setContent(extractInformation(sharkCertificate, PKI_INFORMATION_TRUST_LEVEL).getContentAsString());
        } catch (Exception ex) {
            new SharkKBException(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean addSharkCertificate(HashSet<SharkCertificate> sharkCertificateHashSet) throws SharkKBException, InvalidKeySpecException {
        for (SharkCertificate sharkCertificate : sharkCertificateHashSet) {
            addSharkCertificate(sharkCertificate);
        }
        return true;
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag subject, PublicKey publicKey) throws SharkKBException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (cp.getContextCoordinates().getPeer().getName().equals(subject.getName())
                    && Arrays.equals(cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).next().getContentAsByte(), publicKey.getEncoded())) {

                Information transmitterList = extractInformation(cp, PKI_INFORMATION_TRANSMITTER_LIST_NAME);
                Information trustLevel = extractInformation(cp, PKI_INFORMATION_TRUST_LEVEL);

                return new SharkCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        getLinkedListFromByteArray(transmitterList.getContentAsByte()),
                        Certificate.TrustLevel.valueOf(trustLevel.getContentAsString()),
                        publicKey,
                        new Date(cp.getContextCoordinates().getTime().getDuration()));
            }
        }

        L.d("Certificate with the subject " + subject.getName() + "and public key: " + publicKey.toString() + " not found.");

        return null;
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag issuer, PeerSemanticTag subject) throws SharkKBException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        SharkCertificate sharkCertificate = null;
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (cp.getContextCoordinates().getRemotePeer().getName().equals(issuer.getName()) && cp.getContextCoordinates().getPeer().getName().equals(subject.getName())) {
                if(sharkCertificate == null) {
                    Information transmitterList = extractInformation(cp, PKI_INFORMATION_TRANSMITTER_LIST_NAME);
                    Information trustLevel = extractInformation(cp, PKI_INFORMATION_TRUST_LEVEL);
                    Information publicKey = extractInformation(cp, PKI_INFORMATION_PUBLIC_KEY_NAME);
                    sharkCertificate = new SharkCertificate(
                            cp.getContextCoordinates().getPeer(),
                            cp.getContextCoordinates().getRemotePeer(),
                            getLinkedListFromByteArray(transmitterList.getContentAsByte()),
                            Certificate.TrustLevel.valueOf(trustLevel.getContentAsString()),
                            keyFactory.generatePublic(new X509EncodedKeySpec(publicKey.getContentAsByte())),
                            new Date(cp.getContextCoordinates().getTime().getDuration()));
                } else {
                    throw new SharkKBException("More than one certificate found, aborting. (KB valid?)");
                }
            }
        }

        if(sharkCertificate != null) {
            return sharkCertificate;
        } else {
            L.d("Certificate with the subject: " + subject.getName() + " and issuer : " + issuer.getName() + " not found.");
            return null;
        }
    }

    @Override
    public HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        HashSet<SharkCertificate> sharkCertificateList = new HashSet<>();
        if(knowledge != null) {
            for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {

                Information publicKey = extractInformation(cp, PKI_INFORMATION_PUBLIC_KEY_NAME);
                Information trustLevel = extractInformation(cp, PKI_INFORMATION_TRUST_LEVEL);
                Information transmitterList = extractInformation(cp, PKI_INFORMATION_TRANSMITTER_LIST_NAME);

                sharkCertificateList.add(new SharkCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        getLinkedListFromByteArray(transmitterList.getContentAsByte()),
                        Certificate.TrustLevel.valueOf(trustLevel.getContentAsString()),
                        keyFactory.generatePublic(new X509EncodedKeySpec(publicKey.getContentAsByte())),
                        new Date(cp.getContextCoordinates().getTime().getDuration())
                ));
            }
        }

        if(sharkCertificateList.size() > 0) {
            return sharkCertificateList;
        } else {
            L.d("SharkPkiStorage is empty.");
            return null;
        }
    }

    @Override
    public boolean updateSharkCertificateTrustLevel(SharkCertificate sharkCertificate, Certificate.TrustLevel trustLevel) throws SharkKBException{
        if(getSharkCertificate(sharkCertificate.getSubject(), sharkCertificate.getSubjectPublicKey()) != null) {
            deleteSharkCertificate(sharkCertificate);
            sharkCertificate.setTrustLevel(trustLevel);
            try {
                addSharkCertificate(sharkCertificate);
            } catch (InvalidKeySpecException e) {
                new SharkKBException(e.getMessage());
            }
        }
        return false;
    }

    @Override public boolean deleteSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException {
        if(getSharkCertificate(sharkCertificate.getSubject(), sharkCertificate.getSubjectPublicKey()) != null) {
            this.sharkPkiStorageKB.removeContextPoint(
                    new InMemoSharkKB().createContextCoordinates(
                        PKI_CONTEXT_COORDINATE,                                   //Topic
                        sharkPkiStorageOwner,                                     //Originator
                        sharkCertificate.getSubject(),                            //Peer
                        sharkCertificate.getIssuer(),                             //Remote peer -> if null any
                        InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getValidity().getTime()), //Time -> if null any
                        null,                                                     //Location -> if null any
                        SharkCS.DIRECTION_INOUT)                                  //Direction
            );
            return true;
        }
        return false;
    }

    @Override
    public SharkKB getSharkPkiStorageKB() {
        return this.sharkPkiStorageKB;
    }

    private boolean isCertificateInKb(SharkCertificate sharkCertificate) throws SharkKBException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        if(knowledge != null) {
            for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
                Information publicKey = extractInformation(cp, PKI_INFORMATION_PUBLIC_KEY_NAME);
                if(sharkCertificate.getSubjectPublicKey().equals(keyFactory.generatePublic(new X509EncodedKeySpec(publicKey.getContentAsByte())))) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private Information extractInformation(ContextPoint cp, String name) {
        Information information;
        while (cp.getInformation(name).hasNext()) {
            return cp.getInformation(name).next();
        }
        return null;
    }

    private void storePrivateKey(ContextCoordinates contextCoordinates, PrivateKey privateKey) throws SharkKBException {
        try {
            ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);
            Information private_key = contextPoint.addInformation();
            private_key.setName(PKI_INFORMATION_OWNER_PRIVATE_KEY_NAME);
            private_key.setContent(privateKey.getEncoded());
        } catch (SharkKBException e) {
            new SharkKBException(e.getMessage());
        }
    }
}