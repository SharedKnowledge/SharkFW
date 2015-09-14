package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* TODO addCertificate
    - Estimate the trustlevel of the certificate issuer
    - Estimate if the issuer is in the known peers list
 */


/**
 * @author ac
 */
public class SharkPkiStorage implements PkiStorage {

    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME = "certificate";
    public final static String PKI_CONTEXT_POINT_SEMANTIC_TAG_SI = "cc:certificate";
    public final static String PKI_INFORMATION_PUBLIC_KEY_NAME = "public_key";
    public final static String PKI_INFORMATION_TRANSMITTER_LIST_NAME = "transmitter_list";
    public final static String PKI_INFORMATION_TRUST_LEVEL = "trust_level";
    public final static SemanticTag PKI_CONTEXT_COORDINATE = InMemoSharkKB.createInMemoSemanticTag(PKI_CONTEXT_POINT_SEMANTIC_TAG_NAME, new String[]{PKI_CONTEXT_POINT_SEMANTIC_TAG_SI});
    public final static Interest PKI_INTEREST = InMemoSharkKB.createInMemoInterest(
            InMemoSharkKB.createInMemoSTSet(),
            null,
            null,
            null,
            null,
            null,
            SharkCS.DIRECTION_INOUT
    );
    private final String LINKED_LIST_SEPARATOR_NAME = "<name>";
    private final String LINKED_LIST_SEPARATOR_SIS = "<sis>";
    private final String LINKED_LIST_SEPARATOR_ADR = "<adr>";
    private final String LINKED_LIST_SEPARATOR_END = "<end>";
    ContextCoordinates contextCoordinatesFilter;
    KeyFactory keyFactory;
    private HashSet<SharkCertificate> sharkCertificateList;
    private SharkKB sharkPkiStorageKB;
    private PeerSemanticTag sharkPkiStorageOwner;
    public SharkPkiStorage(SharkKB sharkKB, PeerSemanticTag owner) throws SharkKBException, NoSuchAlgorithmException {
        sharkPkiStorageKB = sharkKB;
        sharkPkiStorageOwner = owner;
        sharkCertificateList = new HashSet<>();
        contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                PKI_CONTEXT_COORDINATE,
                sharkPkiStorageOwner,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);
        keyFactory = KeyFactory.getInstance(SharkKeyPairAlgorithm.RSA.name()); //TODO: determine dynamically
    }

    @Override
    public boolean addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException, InvalidKeySpecException {
        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getValidity().getTime());

        if(isCertificateInKb(sharkCertificate)) {
            //TODO: what if a certificate is already in the KB (e.g. update if expired)
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

        //contextPoint.addInformation(publicKey);
        //contextPoint.addInformation(transmitterList);
        return true;
    }

    @Override
    public boolean addSharkCertificate(ContextPoint sharkCertificate) throws SharkKBException {
        //TODO add ContextPoint

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
            if (cp.getContextCoordinates().getRemotePeer().getName().equals(subject.getName())
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
        throw new SharkKBException("Certificate with the subject " + subject.getName() + "and public key: " + publicKey.toString() + " not found.");
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag subject) throws SharkKBException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        SharkCertificate sharkCertificate = null;
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (cp.getContextCoordinates().getRemotePeer().getName().equals(subject.getName())) {
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
                    throw new SharkKBException("More than one certificate with the same subject: " + subject.getName() + " aborting.");
                }
            }
        }

        if(sharkCertificate != null) {
            return sharkCertificate;
        } else {
            throw new SharkKBException("Certificate with the subject: " + subject.getName() + " not found.");
        }
    }

    @Override
    public HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
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
        return sharkCertificateList; //TODO: Evaluate iterator
    }

    @Override
    public SharkKB getSharkPkiStorageKB() {
        return this.sharkPkiStorageKB;
    }

    private byte[] getByteArrayFromLinkedList(LinkedList<PeerSemanticTag> transmitterList) {

        StringBuilder s = new StringBuilder();

        for (PeerSemanticTag p : transmitterList) {
            s.append(LINKED_LIST_SEPARATOR_NAME);
            s.append(p.getName());
            s.append(LINKED_LIST_SEPARATOR_SIS);
            for (int i = 0; i < p.getSI().length; i++) {
                s.append(p.getSI()[i]);
                if (i < p.getSI().length - 1) {
                    s.append(",");
                }
            }

            s.append(LINKED_LIST_SEPARATOR_ADR);
            for (int i = 0; i < p.getAddresses().length; i++) {
                s.append(p.getAddresses()[i]);
                if (i < p.getAddresses().length - 1) {
                    s.append(",");
                }
            }

            s.append(LINKED_LIST_SEPARATOR_END);
        }

        return String.valueOf(s).getBytes();
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

    private LinkedList<PeerSemanticTag> getLinkedListFromByteArray(byte[] transmitterList) {

        LinkedList<PeerSemanticTag> linkedList = new LinkedList<>();
        String listAsString = new String(transmitterList);

        List<String> listOfNames = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_NAME + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_SIS + ")");
        List<String> listOfSis = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_SIS + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_ADR + ")");
        List<String> listOfAdr = extractStringByRegEx(listAsString, "(?<=" + LINKED_LIST_SEPARATOR_ADR + ")(.*?)(?=" + LINKED_LIST_SEPARATOR_END + ")");

        for (int i = 0; i < listOfNames.size(); i++) {
            PeerSemanticTag peerSemanticTag = InMemoSharkKB.createInMemoPeerSemanticTag(listOfNames.get(i), listOfSis.get(i).split(","), listOfAdr.get(i).split(","));
            linkedList.add(peerSemanticTag);
        }

        return linkedList;
    }

    private Information extractInformation(ContextPoint cp, String name) {
        Information information;
        while (cp.getInformation(name).hasNext()) {
            return cp.getInformation(name).next();
        }
        return null;
    }

    private List<String> extractStringByRegEx(String text, String expression) {
        List<String> matchList = new ArrayList<>();
        Matcher matcher = Pattern.compile(expression).matcher(text);

        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                matchList.add(matcher.group(i));
            }
        }

        return matchList;
    }
}