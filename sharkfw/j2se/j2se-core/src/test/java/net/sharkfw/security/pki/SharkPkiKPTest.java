package net.sharkfw.security.pki;

import junit.framework.TestCase;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.SharkException;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author ac
 */
public class SharkPkiKPTest extends TestCase implements KPListener {

    private final String DATE_TIME = "01.01.2100";
    private Date date;
    private final byte[] privateKeyArray = {48, -126, 4, -66, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 4, -88, 48, -126, 4, -92, 2, 1, 0, 2, -126, 1, 1, 0, -74, 14, 32, 73, 41, 127, -19, -18, 115, 121, -97, -82, 26, -87, 63, 26, 94, -27, 59, -10, 31, -66, 107, -92, -19, 84, -37, -111, -60, -87, 45, -87, 111, -41, -63, -7, -45, -47, 15, 18, -111, -30, 37, 14, 125, 87, 93, -106, -68, 23, 122, -60, 102, -17, 52, 60, 91, -115, 68, -9, 85, -20, -26, 50, 1, -65, 13, 4, 93, 47, -43, -31, -6, 75, 47, 43, -93, 78, 67, 35, 73, 62, -103, 9, -69, -78, -19, -107, -15, 20, 115, 88, 4, -36, 13, 115, 71, 91, -94, 25, -38, 74, -57, 22, -86, -27, 12, 40, -25, 73, -95, -115, -59, -106, 56, -62, 48, -40, 89, -63, -22, -65, -128, -71, -91, 47, -5, 46, -14, -107, -8, -46, 72, 55, 92, -27, 24, -117, 95, -98, 49, 16, 102, 97, -23, 82, 71, 98, 58, 52, -60, 110, -26, -21, -52, -11, -3, 114, -40, -108, -66, -110, 122, 31, 6, 50, 120, 114, -121, -74, -72, 100, -25, -32, -40, -93, -67, -49, 30, 91, 56, 65, -86, -7, 21, -21, 113, -51, 26, 85, 127, -71, 30, 47, 70, -115, -125, 0, -76, -45, 123, 76, 95, -128, -43, -22, 58, 115, 45, 71, 73, 81, -88, -75, -86, 31, 0, 58, -28, 56, -101, -58, 58, -95, -63, -68, 61, 105, -3, 93, -5, -67, 123, 78, 60, -14, 110, -69, 65, -78, 126, 82, -28, 27, 5, -77, 49, 55, -100, -56, -126, 19, -24, 26, 59, -19, 2, 3, 1, 0, 1, 2, -126, 1, 1, 0, -91, 69, 103, -85, -29, 95, -66, -71, 20, -35, -103, -57, 109, 110, -83, -39, -101, -62, 80, -49, -37, -5, 123, 47, 75, 65, 24, -33, 89, 0, 24, 114, -44, 20, -77, -124, -65, -12, -18, -48, -11, -36, -83, -75, 92, -74, 62, 65, 3, -39, -16, -38, 113, 115, -64, 32, -9, 31, -27, 55, 0, 43, 66, -62, -84, -15, 42, -71, 6, 123, -47, -104, 7, 39, -49, -41, -74, -75, -22, -97, 18, -12, 31, -20, 69, 24, 103, 53, 18, -103, 117, 40, -73, -88, -87, 17, 56, -5, -121, -54, 30, 112, 122, 18, 42, -20, 42, 64, -53, -50, -3, 57, 125, -31, 36, -1, -83, 102, 10, -3, -50, -59, 48, -17, -41, 117, 26, 121, -11, 116, -15, 59, -100, -125, 60, 53, -79, 28, 72, -126, 101, -102, -91, 56, 34, 91, -96, 40, 80, 63, -10, -93, 53, -12, -52, 104, -125, 65, 18, -81, 81, -16, 76, 98, 11, -40, -37, 34, -78, -13, -108, -33, -73, 82, -97, 1, 70, -105, -48, -32, 28, -58, 82, 61, -64, -119, 64, 105, 125, 114, -86, -112, -98, -21, -52, -11, 52, 121, -33, 59, -122, 60, 98, -86, -18, -78, 115, -108, 19, -46, 73, -19, -41, -30, -76, 42, -43, 21, -32, 69, 78, -42, -121, 69, 43, -46, 59, 46, 78, 64, 113, -9, -46, -104, -76, -66, -90, -73, 93, -116, -27, -51, 66, 1, -58, -117, -79, -103, -35, 24, 67, -48, -65, -53, 4, -91, 2, -127, -127, 0, -8, 63, 52, 63, 0, 25, -44, 11, -103, 41, -62, 33, 26, -30, -123, -59, 44, 53, 114, 37, -66, 63, 41, 55, -123, -56, 55, 103, -52, -52, 34, 42, -41, 21, -108, -63, -71, 21, -64, -110, -23, -97, 35, 77, -111, 82, -1, -12, 114, 95, 77, 3, 122, -118, -80, 116, -40, 103, -56, 60, 86, -46, 92, -107, -3, -91, -40, 69, -110, -43, -40, -127, -54, 2, 114, -33, 80, -56, 96, 73, 112, 125, -79, -36, 30, 24, 67, -86, 95, -59, 58, 80, -109, -21, -105, 67, 69, 90, 109, 90, -30, -62, -76, -90, 31, 110, 38, 101, -42, -57, 96, -99, -105, -39, -46, -69, -112, 47, -105, 112, 65, 114, -77, 29, 33, -66, 46, 119, 2, -127, -127, 0, -69, -67, -77, -24, 106, 82, -18, 35, -57, -6, -54, 116, -126, -80, 106, 41, -40, 79, -21, 58, -125, 1, -98, -43, -108, -69, 3, -108, 44, 41, 41, -50, -124, 16, 20, 55, -111, 120, -11, -113, -63, 115, -74, 79, -4, 21, -93, -62, 125, -41, 33, -38, -78, 78, 82, -28, -91, -73, -102, 73, 124, -80, -64, -122, -35, -90, -87, 41, 96, -111, 38, 102, 111, 73, -120, 127, 71, -53, 1, -124, -20, 35, -99, 2, 97, 65, -22, -103, 94, 54, -76, -23, -113, -31, -68, -55, -103, -9, -117, 95, -61, -113, 112, -124, -85, -45, 46, -87, 55, -6, 14, -104, -83, -55, 60, 86, 71, 83, 44, -24, -69, 50, -44, -87, 50, 15, -51, -69, 2, -127, -128, 94, -13, 117, -35, 95, 1, -126, 12, -119, -95, -30, 65, -35, 81, -91, 78, -36, 112, -8, -33, 28, -36, 117, -68, 105, -20, 65, -36, -17, 5, 105, 80, 47, -126, 110, -86, -122, 75, -98, -93, -46, -67, -18, -73, -35, -116, -4, -97, -104, 27, -127, -114, -126, 17, -120, 26, -71, 39, 64, -4, 107, 76, -4, -30, -43, -24, -40, 71, 12, 57, -59, 29, 39, -110, 54, 51, -38, 103, 58, 4, -3, 4, -26, -63, -59, -75, 12, 52, -97, 123, 5, -86, 10, 90, 3, 58, 84, -51, -104, -92, 57, -75, -75, 121, 16, -18, -123, -69, 80, 34, 100, 127, 2, -76, -21, 24, -104, -74, -53, 90, -63, 125, -56, -86, -84, -36, 92, 83, 2, -127, -127, 0, -127, 9, -39, 127, 52, -99, 61, -117, 32, 103, -77, 1, -77, -43, -9, -110, 61, 88, 104, -16, -75, -100, -58, -124, 60, -114, -15, -35, -59, 32, -95, -40, -115, 18, -44, 34, -127, 125, 103, 34, -32, -81, 92, -82, -53, 48, 64, 119, 108, -79, 18, 86, -45, -39, 91, -58, 70, 84, -44, -38, 15, -73, 25, 13, 89, -119, -76, -103, 121, 3, 43, 38, -102, -11, 42, -71, 121, 42, 2, 40, 79, 122, 6, -17, -36, -45, 70, -95, 21, -115, 122, 121, 82, 104, -80, -82, -52, -127, -59, -83, 19, -88, 34, -119, 61, -28, -112, 28, -59, 25, -67, 48, -103, -70, 72, 27, 113, 56, -83, 20, 101, -98, -127, 102, -83, -65, 9, 3, 2, -127, -128, 10, 59, 58, -24, -6, 105, 18, -54, 8, -107, 93, 65, 76, -97, -41, -79, 16, 62, 0, -99, -99, 79, -114, 81, 115, 125, -20, -59, -55, -25, -76, -102, -11, 78, -87, -94, 10, -7, -80, 39, -64, 104, -22, 1, -35, 71, -40, -100, 79, -41, -27, 103, 117, -73, -31, 59, -58, 2, -108, 5, 95, 72, -49, 13, 102, 49, -35, 6, 66, 59, 95, 17, 8, -113, -2, 127, -31, 96, -51, 126, -127, -108, -69, 79, 60, 29, -45, -6, 20, 22, -5, 79, 96, -66, -77, -47, -63, -9, -105, -98, 66, -11, -91, 117, 48, -52, -123, -87, 50, 62, 35, 100, 27, 97, -121, 51, 111, -13, -36, -86, 55, 79, -93, -101, -4, -52, -34, 2};
    private final byte[] publicKeyArray = {48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -74, 14, 32, 73, 41, 127, -19, -18, 115, 121, -97, -82, 26, -87, 63, 26, 94, -27, 59, -10, 31, -66, 107, -92, -19, 84, -37, -111, -60, -87, 45, -87, 111, -41, -63, -7, -45, -47, 15, 18, -111, -30, 37, 14, 125, 87, 93, -106, -68, 23, 122, -60, 102, -17, 52, 60, 91, -115, 68, -9, 85, -20, -26, 50, 1, -65, 13, 4, 93, 47, -43, -31, -6, 75, 47, 43, -93, 78, 67, 35, 73, 62, -103, 9, -69, -78, -19, -107, -15, 20, 115, 88, 4, -36, 13, 115, 71, 91, -94, 25, -38, 74, -57, 22, -86, -27, 12, 40, -25, 73, -95, -115, -59, -106, 56, -62, 48, -40, 89, -63, -22, -65, -128, -71, -91, 47, -5, 46, -14, -107, -8, -46, 72, 55, 92, -27, 24, -117, 95, -98, 49, 16, 102, 97, -23, 82, 71, 98, 58, 52, -60, 110, -26, -21, -52, -11, -3, 114, -40, -108, -66, -110, 122, 31, 6, 50, 120, 114, -121, -74, -72, 100, -25, -32, -40, -93, -67, -49, 30, 91, 56, 65, -86, -7, 21, -21, 113, -51, 26, 85, 127, -71, 30, 47, 70, -115, -125, 0, -76, -45, 123, 76, 95, -128, -43, -22, 58, 115, 45, 71, 73, 81, -88, -75, -86, 31, 0, 58, -28, 56, -101, -58, 58, -95, -63, -68, 61, 105, -3, 93, -5, -67, 123, 78, 60, -14, 110, -69, 65, -78, 126, 82, -28, 27, 5, -77, 49, 55, -100, -56, -126, 19, -24, 26, 59, -19, 2, 3, 1, 0, 1};
    private J2SEAndroidSharkEngine aliceSe;
    private J2SEAndroidSharkEngine bobSe;
    private J2SEAndroidSharkEngine malorySe;
    private ContextCoordinates contextCoordinatesFilter;
    private SharkCertificate sharkCertificate;
    private SharkKB aliceKb;
    private PeerSTSet peerSTSet;
    private SharkPkiStorage alicePkiStorage;
    private SharkKB bobKb;
    private SharkPkiStorage bobPkiStorage;
    private SharkKB maloryKb;
    private SharkPkiStorage maloryPkiStorage;
    private SharkPkiKP alicePkiKP;
    private SharkPkiKP bobPkiKP;
    private SharkPkiKP maloryPkiKP;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PeerSemanticTag alice;
    private PeerSemanticTag bob;
    private PeerSemanticTag malory;
    private LinkedList<PeerSemanticTag> peerList;
    private boolean fingerprintIsValid;

    public void setUp(int alicePort, int bobPort, int maloryPort) throws Exception {

        alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "http://www.alice.de", "tcp://localhost:"+alicePort);
        bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "http://www.bob.de", "tcp://localhost:"+bobPort);
        malory = InMemoSharkKB.createInMemoPeerSemanticTag("malory", "http://www.malory.de", "tcp://localhost:"+maloryPort);

        peerSTSet = InMemoSharkKB.createInMemoPeerSTSet();
        peerSTSet.merge(alice);
        peerSTSet.merge(bob);
        fingerprintIsValid = true;

        contextCoordinatesFilter = InMemoSharkKB.createInMemoContextCoordinates(
                SharkPkiStorage.PKI_CONTEXT_COORDINATE,
                alice,
                null,
                null,
                null,
                null,
                SharkCS.DIRECTION_INOUT);

        date = new Date();
        date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(DATE_TIME).getTime());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyArray));
        publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyArray));

        peerList = new LinkedList<>();
        peerList.addFirst(alice);

        sharkCertificate = new SharkCertificate(bob, alice, peerList, Certificate.TrustLevel.UNKNOWN, publicKey, date);

        aliceSe = new J2SEAndroidSharkEngine();
        aliceSe.setConnectionTimeOut(1000);

        bobSe = new J2SEAndroidSharkEngine();
        bobSe.setConnectionTimeOut(1000);

        malorySe = new J2SEAndroidSharkEngine();
        malorySe.setConnectionTimeOut(1000);

        aliceKb = new InMemoSharkKB();
        aliceKb.setOwner(alice);
        alicePkiStorage = new SharkPkiStorage(aliceKb, alice, privateKey);
        alicePkiKP = new SharkPkiKP(aliceSe, alicePkiStorage, Certificate.TrustLevel.UNKNOWN, peerSTSet);

        bobKb = new InMemoSharkKB();
        bobKb.setOwner(bob);
        bobPkiStorage = new SharkPkiStorage(bobKb, bob, privateKey);
        bobPkiKP = new SharkPkiKP(bobSe, bobPkiStorage, Certificate.TrustLevel.UNKNOWN, peerSTSet);

        maloryKb = new InMemoSharkKB();
        maloryKb.setOwner(malory);
        maloryPkiStorage = new SharkPkiStorage(maloryKb, malory, privateKey);
        maloryPkiKP = new SharkPkiKP(malorySe, maloryPkiStorage, Certificate.TrustLevel.UNKNOWN, null);

        alicePkiKP.addListener(this);
        bobPkiKP.addListener(this);
        maloryPkiKP.addListener(this);
    }

    @Test
    public void testDoInsert() throws Exception {
        int alicePort = 7010;
        int bobPort = 7011;
        setUp(alicePort, bobPort, 7012);

        alicePkiStorage.addSharkCertificate(sharkCertificate);
        Knowledge knowledge = SharkCSAlgebra.extract(alicePkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);
        bobSe.startTCP(bobPort);
        aliceSe.startTCP(alicePort);
        aliceSe.sendKnowledge(knowledge, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();

        //Add alice again to match bob certificate
        sharkCertificate.getTransmitterList().add(alice);

        assertEquals(sharkCertificate, bobPkiStorage.getSharkCertificate(alice, bob));
    }

    @Test
    public void testDoInsertWithPeerSTSetNull() throws Exception {
        int alicePort = 7090;
        int maloryPort = 7092;
        setUp(alicePort, 7091, maloryPort);

        sharkCertificate = new SharkCertificate(malory, alice, peerList, Certificate.TrustLevel.UNKNOWN, publicKey, date);
        alicePkiStorage.addSharkCertificate(sharkCertificate);
        Knowledge knowledge = SharkCSAlgebra.extract(alicePkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);
        aliceSe.startTCP(alicePort);
        malorySe.startTCP(maloryPort);
        aliceSe.sendKnowledge(knowledge, malory, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        malorySe.stopTCP();

        //Add alice again to match bob certificate
        sharkCertificate.getTransmitterList().add(alice);

        assertEquals(sharkCertificate, maloryPkiStorage.getSharkCertificate(alice, malory));
    }

    @Test
    public void testTrustLevelChanged() throws Exception {
        int alicePort = 7070;
        int bobPort = 7071;
        setUp(alicePort, bobPort, 7072);

        sharkCertificate = new SharkCertificate(bob, alice, peerList, Certificate.TrustLevel.FULL, publicKey, date);
        alicePkiStorage.addSharkCertificate(sharkCertificate);
        Knowledge knowledge = SharkCSAlgebra.extract(alicePkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);
        bobSe.startTCP(bobPort);
        aliceSe.startTCP(alicePort);
        aliceSe.sendKnowledge(knowledge, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();

        assertEquals(Certificate.TrustLevel.UNKNOWN, bobPkiStorage.getSharkCertificate(alice, bob).getTrustLevel());
    }

    @Test
    public void testTransmitterListChanged() throws Exception {
        int alicePort = 7060;
        int bobPort = 7061;
        setUp(alicePort, bobPort, 7062);

        sharkCertificate = new SharkCertificate(bob, alice, peerList, Certificate.TrustLevel.FULL, publicKey, date);
        alicePkiStorage.addSharkCertificate(sharkCertificate);

        Knowledge knowledge = SharkCSAlgebra.extract(alicePkiStorage.getSharkPkiStorageKB(), contextCoordinatesFilter);

        bobSe.startTCP(bobPort);
        aliceSe.startTCP(alicePort);
        aliceSe.sendKnowledge(knowledge, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();

        assertEquals(2, bobPkiStorage.getSharkCertificate(alice, bob).getTransmitterList().size());
    }

    @Test
    public void testDoExposeCertificate() throws Exception {
        int alicePort = 7050;
        int bobPort = 7051;
        setUp(alicePort, bobPort, 7052);

        SharkCertificate sc = new SharkCertificate(alice, bob, peerList, Certificate.TrustLevel.UNKNOWN, publicKey, date);
        bobPkiStorage.addSharkCertificate(sc);

        bobSe.startTCP(bobPort);
        aliceSe.startTCP(alicePort);

        //Prepare interest
        STSet stSetInterest = InMemoSharkKB.createInMemoSTSet();
        stSetInterest.createSemanticTag(Certificate.CERTIFICATE_SEMANTIC_TAG_NAME, Certificate.CERTIFICATE_SEMANTIC_TAG_SI);

        //Prepare Issuer
        PeerSTSet peerStSetIssuer = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetIssuer.merge(bob);

        //Prepare Subject
        PeerSTSet peerStSetSubject = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetSubject.merge(alice);

        Interest interest = InMemoSharkKB.createInMemoInterest(
            stSetInterest,              //List of Topics        -> Certificate identifier
            alice,                      //Originator
            peerStSetSubject,           //List of Peers         -> Subject
            peerStSetIssuer,            //List of RemotePeers   -> Issuer
            null,                       //List of Times
            null,                       //List of Locations
            SharkCS.DIRECTION_OUT       //Direction
        );

        aliceSe.sendInterest(interest, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();

        //Add alice again to match bob certificate
        sc.getTransmitterList().add(alice);

        assertEquals(sc, alicePkiStorage.getSharkCertificate(bob, alice));
    }

    @Test
    public void testDoExposeCertificateValidation() throws Exception {
        int alicePort = 7040;
        int bobPort = 7041;
        setUp(alicePort, bobPort, 7042);

        alicePkiStorage.addSharkCertificate(sharkCertificate);
        bobPkiStorage.addSharkCertificate(sharkCertificate);

        bobSe.startTCP(bobPort);
        aliceSe.startTCP(alicePort);

        //Prepare interest
        STSet stSet = InMemoSharkKB.createInMemoSTSet();
        stSet.createSemanticTag(SharkPkiKP.KP_CERTIFICATE_VALIDATION_TAG_NAME, SharkPkiKP.KP_CERTIFICATE_VALIDATION_TAG_SI);

        //Prepare Issuer
        PeerSTSet peerStSetIssuer = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetIssuer.merge(alice);

        //Prepare Subject
        PeerSTSet peerStSetSubject = InMemoSharkKB.createInMemoPeerSTSet();
        peerStSetSubject.merge(bob);

        Interest interest = InMemoSharkKB.createInMemoInterest(
                stSet,                      //List of Topics        -> Certificate validation identifier
                alice,                      //Originator
                peerStSetSubject,           //List of Peers         -> Subjects
                peerStSetIssuer,            //List of RemotePeers   -> Issuer
                null,                       //List of Times
                null,                       //List of Locations
                SharkCS.DIRECTION_OUT       //Direction
        );

        aliceSe.sendInterest(interest, bob, alicePkiKP);

        Thread.sleep(1000);

        aliceSe.stopTCP();
        bobSe.stopTCP();

        assertEquals(fingerprintIsValid, true);
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
        if (SharkCSAlgebra.identical(newCP.getContextCoordinates().getTopic(), SharkPkiStorage.PKI_CONTEXT_COORDINATE)) {
            System.out.println("knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP)");
            System.out.println("Subject: " + newCP.getContextCoordinates().getPeer().getName());
            System.out.println("Issuer: " + newCP.getContextCoordinates().getRemotePeer().getName());
        }

        if (SharkCSAlgebra.identical(newCP.getContextCoordinates().getTopic(), Certificate.FINGERPRINT_COORDINATE)) {
            try {
                if(Arrays.equals(newCP.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte(), sharkCertificate.getFingerprint())) {
                    System.out.println("Fingerprints are equal:");
                    System.out.println(Arrays.toString(newCP.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte()));
                    System.out.println(Arrays.toString(sharkCertificate.getFingerprint()));
                    fingerprintIsValid = true;
                }
                else {
                    System.out.println("Fingerprints are different:");
                    System.out.println(Arrays.toString(newCP.getInformation(Certificate.FINGERPRINT_INFORMATION_NAME).next().getContentAsByte()));
                    System.out.println(Arrays.toString(sharkCertificate.getFingerprint()));
                    fingerprintIsValid = false;
                }
            } catch (SharkException e) {
                e.printStackTrace();
            }
        }
    }
}