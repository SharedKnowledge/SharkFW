package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @author ac
 */
public class SharkPkiStorage implements PkiStorage {

    private final static String PKI_STORAGE_FOLDER = "/pkistorage";
    private final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME = "certificate";
    private final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_SI = "cc:certificate";
    private final static String PKI_INFORMATION_PUBLIC_KEY_NAME = "public_key";

    private final SemanticTag PKI_CONTEXT_COORDINATE_TOPIC = InMemoSharkKB.createInMemoSemanticTag(PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME, new String[]{PKI_CONTEXT_POINT_SEMANTIC_TAG_SI});

    ContextCoordinates contextCoordinatesFilter;

    private List<SharkCertificate> sharkCertificateList;
    private FSSharkKB sharkPkiStorageKB;
    private PeerSemanticTag sharkPkiStorageOwner;
    KeyFactory keyFactory;

    public SharkPkiStorage(FSSharkKB fsSharkKB, PeerSemanticTag owner, SharkEngine sharkEngine) throws SharkKBException, NoSuchAlgorithmException {
        this(fsSharkKB.getFoldername() + PKI_STORAGE_FOLDER, owner, sharkEngine);
    }

    public SharkPkiStorage(String sharkPkiFolder, PeerSemanticTag owner, SharkEngine sharkEngine) throws SharkKBException, NoSuchAlgorithmException {
        sharkPkiStorageKB = new FSSharkKB(sharkPkiFolder);
        sharkPkiStorageOwner = owner;
        sharkCertificateList = new ArrayList<>();
        contextCoordinatesFilter = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE_TOPIC,
                sharkPkiStorageOwner,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);
        keyFactory = KeyFactory.getInstance(SharkKeyPairAlgorithm.RSA.name()); //TODO: determine dynamically
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag, PublicKey publicKey) throws SharkKBException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        for(ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if(cp.getContextCoordinates().getRemotePeer().getName().equals(peerSemanticTag.getName())
               && cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).next().getContentAsByte().equals(publicKey.getEncoded())) {
                return new SharkCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        publicKey,
                        new Date(cp.getContextCoordinates().getTime().getDuration()));
            }
        }

        return null;
    }

    @Override
    public void addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException {
        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getValidity().getTime());
        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE_TOPIC,       //Topic
                sharkPkiStorageOwner,               //Originator
                sharkCertificate.getSubject(),   //Peer
                sharkCertificate.getIssuer(),    //Remote peer -> if null any
                time,                               //Time -> if null any
                null,                               //Location -> if null any
                SharkCS.DIRECTION_INOUT);           //Direction
        ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);
        Information information = InMemoSharkKB.createInMemoInformation();
        information.setName(PKI_INFORMATION_PUBLIC_KEY_NAME);
        information.setContent(sharkCertificate.getSubjectPublicKey().getEncoded());
        contextPoint.addInformation(information);
    }

    @Override
    public List<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        for(ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            Information information = null;
            while(cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).hasNext()) {
                information = cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).next();
                if(information.getName().equals(PKI_INFORMATION_PUBLIC_KEY_NAME)) {
                    break;
                }
                sharkCertificateList.add(new SharkCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        keyFactory.generatePublic(new X509EncodedKeySpec(information.getContentAsByte())),
                        new Date(cp.getContextCoordinates().getTime().getDuration())
                ));
            }
        }
        return sharkCertificateList;
    }

    @Override
    public FSSharkKB getPkiStorage() {
        return sharkPkiStorageKB;
    }
}