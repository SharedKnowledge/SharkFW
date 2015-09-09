package net.sharkfw.security.pki;

import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */
public class SharkPkiKPTest implements KPListener {

    private final String DATE_TIME = "01.01.2100";
    private final byte[] publicKeyArray = {48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -74, 14, 32, 73, 41, 127, -19, -18, 115, 121, -97, -82, 26, -87, 63, 26, 94, -27, 59, -10, 31, -66, 107, -92, -19, 84, -37, -111, -60, -87, 45, -87, 111, -41, -63, -7, -45, -47, 15, 18, -111, -30, 37, 14, 125, 87, 93, -106, -68, 23, 122, -60, 102, -17, 52, 60, 91, -115, 68, -9, 85, -20, -26, 50, 1, -65, 13, 4, 93, 47, -43, -31, -6, 75, 47, 43, -93, 78, 67, 35, 73, 62, -103, 9, -69, -78, -19, -107, -15, 20, 115, 88, 4, -36, 13, 115, 71, 91, -94, 25, -38, 74, -57, 22, -86, -27, 12, 40, -25, 73, -95, -115, -59, -106, 56, -62, 48, -40, 89, -63, -22, -65, -128, -71, -91, 47, -5, 46, -14, -107, -8, -46, 72, 55, 92, -27, 24, -117, 95, -98, 49, 16, 102, 97, -23, 82, 71, 98, 58, 52, -60, 110, -26, -21, -52, -11, -3, 114, -40, -108, -66, -110, 122, 31, 6, 50, 120, 114, -121, -74, -72, 100, -25, -32, -40, -93, -67, -49, 30, 91, 56, 65, -86, -7, 21, -21, 113, -51, 26, 85, 127, -71, 30, 47, 70, -115, -125, 0, -76, -45, 123, 76, 95, -128, -43, -22, 58, 115, 45, 71, 73, 81, -88, -75, -86, 31, 0, 58, -28, 56, -101, -58, 58, -95, -63, -68, 61, 105, -3, 93, -5, -67, 123, 78, 60, -14, 110, -69, 65, -78, 126, 82, -28, 27, 5, -77, 49, 55, -100, -56, -126, 19, -24, 26, 59, -19, 2, 3, 1, 0, 1};
    J2SEAndroidSharkEngine aliceSe;
    J2SEAndroidSharkEngine bobSe;
    ContextCoordinates contextCoordinatesFilter;
    SharkCertificate sharkCertificate;
    SharkKB aliceKb;
    SharkPkiStorage alicePkiStorage;
    SharkKB bobKb;
    SharkPkiStorage bobPkiStorage;
    SharkPkiKP alicePkiKP;
    SharkPkiKP bobPkiKP;
    private PublicKey publicKey;
    private PeerSemanticTag alice;
    private PeerSemanticTag bob;

    @Before
    public void setUp() throws Exception {

        alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "http://www.alice.de", "tcp://localhost:7080");
        bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "http://www.bob.de", "tcp://localhost:7081");

        contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                null,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);

        Date date = new Date();
        date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(DATE_TIME).getTime());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyArray));

        LinkedList<PeerSemanticTag> peerList = new LinkedList<>();
        peerList.addFirst(bob);

        sharkCertificate = new SharkCertificate(alice, bob, peerList, Certificate.TrustLevel.UNKNOWN, publicKey, date);

        aliceSe = new J2SEAndroidSharkEngine();
        aliceSe.setConnectionTimeOut(1000);

        bobSe = new J2SEAndroidSharkEngine();
        bobSe.setConnectionTimeOut(1000);

        aliceKb = new InMemoSharkKB();
        aliceKb.setOwner(alice);
        alicePkiStorage = new SharkPkiStorage(aliceKb, alice);
        alicePkiKP = new SharkPkiKP(bobSe, bobPkiStorage, Certificate.TrustLevel.UNKNOWN, SharkPkiKP.TrustedIssuer.ALL);

        bobKb = new InMemoSharkKB();
        bobKb.setOwner(bob);
        bobPkiStorage = new SharkPkiStorage(bobKb, bob);
        bobPkiKP = new SharkPkiKP(bobSe, bobPkiStorage, Certificate.TrustLevel.UNKNOWN, SharkPkiKP.TrustedIssuer.ALL);

        /********************/
        //L.setLogLevel(L.LOGLEVEL_ALL);
        //L.setLogfile("log.txt");

        bobPkiKP.addListener(this);
    }

    @Test
    public void testDoInsert() throws Exception {
        alicePkiStorage.addSharkCertificate(sharkCertificate);
        Knowledge knowledge = SharkCSAlgebra.extract(alicePkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);
        bobSe.startTCP(7081);
        aliceSe.startTCP(7080);
        aliceSe.sendKnowledge(knowledge, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();
    }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
        System.out.println("SharkPkiKPTest: exposeSent");
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
        System.out.println("SharkPkiKPTest: insertSent");
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        System.out.println("SharkPkiKPTest: knowledgeAssimilated");
        System.out.println("Peer: " + newCP.getContextCoordinates().getPeer().getName());
        System.out.println("Remote Peer: " + newCP.getContextCoordinates().getRemotePeer().getName());
    }
}