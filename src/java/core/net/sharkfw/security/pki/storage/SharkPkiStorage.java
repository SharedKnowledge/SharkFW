package net.sharkfw.security.pki.storage;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
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
    public final static String PKI_INFORMATION_PUBLIC_TRANSMITTER_LIST_NAME = "transmitter_list";
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
    private Trustlevel trustlevel;
    private TrustedIssuer trustedIssuer;
    public SharkPkiStorage(SharkKB sharkKB, PeerSemanticTag owner, Trustlevel trustlevel, TrustedIssuer trustedIssuer) throws SharkKBException, NoSuchAlgorithmException {
        sharkPkiStorageKB = sharkKB;
        sharkPkiStorageOwner = owner;
        this.trustlevel = trustlevel;
        this.trustedIssuer = trustedIssuer;
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
    public void addSharkCertificate(SharkCertificate sharkCertificate) throws SharkKBException {
        TimeSemanticTag time = InMemoSharkKB.createInMemoTimeSemanticTag(TimeSemanticTag.FIRST_MILLISECOND_EVER, sharkCertificate.getValidity().getTime());
        ContextCoordinates contextCoordinates = sharkPkiStorageKB.createContextCoordinates(
                PKI_CONTEXT_COORDINATE,             //Topic
                sharkPkiStorageOwner,               //Originator
                sharkCertificate.getSubject(),      //Peer
                sharkCertificate.getIssuer(),       //Remote peer -> if null any
                time,                               //Time -> if null any
                null,                               //Location -> if null any
                SharkCS.DIRECTION_INOUT);           //Direction
        ContextPoint contextPoint = sharkPkiStorageKB.createContextPoint(contextCoordinates);

        Information publicKey = InMemoSharkKB.createInMemoInformation();
        publicKey.setName(PKI_INFORMATION_PUBLIC_KEY_NAME);
        publicKey.setContent(sharkCertificate.getSubjectPublicKey().getEncoded());

        Information transmitterList = InMemoSharkKB.createInMemoInformation();
        transmitterList.setName(PKI_INFORMATION_PUBLIC_TRANSMITTER_LIST_NAME);
        transmitterList.setContent(getByteArrayFromLinkedList(sharkCertificate.getTransmitterList()));

        contextPoint.addInformation(publicKey);
        contextPoint.addInformation(transmitterList);
    }

    @Override
    public void addSharkCertificate(ContextPoint sharkCertificate) {
        //TODO add ContextPoint
    }

    @Override
    public void addSharkCertificate(HashSet<SharkCertificate> sharkCertificateHashSet) throws SharkKBException {
        for (SharkCertificate sharkCertificate : sharkCertificateHashSet) {
            addSharkCertificate(sharkCertificate);
        }
    }

    @Override
    public SharkCertificate getSharkCertificate(PeerSemanticTag peerSemanticTag, PublicKey publicKey) throws SharkKBException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {
            if (cp.getContextCoordinates().getRemotePeer().getName().equals(peerSemanticTag.getName())
                    && Arrays.equals(cp.getInformation(PKI_INFORMATION_PUBLIC_KEY_NAME).next().getContentAsByte(), publicKey.getEncoded())) {

                Information transmitterList = extractInformation(cp, PKI_INFORMATION_PUBLIC_TRANSMITTER_LIST_NAME);

                return new SharkCertificate(
                        cp.getContextCoordinates().getPeer(),
                        cp.getContextCoordinates().getRemotePeer(),
                        getLinkedListFromByteArray(transmitterList.getContentAsByte()),
                        publicKey,
                        new Date(cp.getContextCoordinates().getTime().getDuration()));
            }
        }

        return null;
    }

    @Override
    public HashSet<SharkCertificate> getSharkCertificateList() throws SharkKBException, NoSuchAlgorithmException, InvalidKeySpecException {
        Knowledge knowledge = SharkCSAlgebra.extract(sharkPkiStorageKB, contextCoordinatesFilter);
        for (ContextPoint cp : Collections.list(knowledge.contextPoints())) {

            Information publicKey = extractInformation(cp, PKI_INFORMATION_PUBLIC_KEY_NAME);
            Information transmitterList = extractInformation(cp, PKI_INFORMATION_PUBLIC_TRANSMITTER_LIST_NAME);

            sharkCertificateList.add(new SharkCertificate(
                    cp.getContextCoordinates().getPeer(),
                    cp.getContextCoordinates().getRemotePeer(),
                    getLinkedListFromByteArray(transmitterList.getContentAsByte()),
                    keyFactory.generatePublic(new X509EncodedKeySpec(publicKey.getContentAsByte())),
                    new Date(cp.getContextCoordinates().getTime().getDuration())
            ));
        }
        return sharkCertificateList;
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

    public static enum Trustlevel {
        NONE,
        UNKNOWN,
        MARGINAL,
        FULL
    }

    public static enum TrustedIssuer {
        ALL,
        KNOWN
    }
}