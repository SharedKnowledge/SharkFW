package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.filesystem.FSSharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.key.SharkKeyAlgorithm;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.SharkPkiCertificate;
import net.sharkfw.security.utility.SharkCryptography;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Time;
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

    private List<SharkPkiCertificate> sharkPkiCertificateList;
    private FSSharkKB sharkPkiStorageKB;
    private PeerSemanticTag sharkPkiStorageOwner;

    public SharkPkiStorage(FSSharkKB fsSharkKB, PeerSemanticTag owner, SharkEngine sharkEngine) throws SharkKBException {
        this(fsSharkKB.getFoldername() + PKI_STORAGE_FOLDER, owner, sharkEngine);
    }

    public SharkPkiStorage(String sharkPkiFolder, PeerSemanticTag owner, SharkEngine sharkEngine) throws SharkKBException {
        sharkPkiStorageKB = new FSSharkKB(sharkPkiFolder);
        sharkPkiStorageOwner = owner;
        sharkPkiCertificateList = new ArrayList<>();
    }

    @Override
    public SharkPkiCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag) {
        for (SharkPkiCertificate sharkPkiCertificate : sharkPkiCertificateList) {
            if (sharkPkiCertificate.getIssuer().identical(peerSemanticTag)) {
                return sharkPkiCertificate;
            }
        }
        return null;
    }

    @Override
    public void addSharkCertificate(SharkPkiCertificate sharkPkiCertificate) throws SharkKBException {
        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkPkiCertificate.getValidity().getTime());
        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE_TOPIC,       //Topic
                sharkPkiStorageOwner,               //Originator
                sharkPkiCertificate.getSubject(),   //Peer
                sharkPkiCertificate.getIssuer(),    //Remote peer -> if null any
                time,                               //Time -> if null any
                null,                               //Location -> if null any
                SharkCS.DIRECTION_INOUT);           //Direction
        ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);
        Information information = InMemoSharkKB.createInMemoInformation();
        information.setName(PKI_INFORMATION_PUBLIC_KEY_NAME);
        information.setContent(sharkPkiCertificate.getSubjectPublicKey().getEncoded());
        contextPoint.addInformation(information);
    }

    @Override
    public List<SharkPkiCertificate> getSharkPkiCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE_TOPIC,
                sharkPkiStorageOwner,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinates);
        KeyFactory keyFactory = KeyFactory.getInstance(SharkKeyPairAlgorithm.RSA.name()); //TODO: dynamically determinable
        for(ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            Information information = null;
            while(cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).hasNext()) {
                information = cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).next();
                if(information.getName().equals(PKI_INFORMATION_PUBLIC_KEY_NAME)) {
                    break;
                }
                sharkPkiCertificateList.add(new SharkPkiCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        keyFactory.generatePublic(new X509EncodedKeySpec(information.getContentAsByte())),
                        new Date(cp.getContextCoordinates().getTime().getDuration())
                ));
            }
        }
        return sharkPkiCertificateList;
    }

    @Override
    public FSSharkKB getPkiStorage() {
        return sharkPkiStorageKB;
    }
}