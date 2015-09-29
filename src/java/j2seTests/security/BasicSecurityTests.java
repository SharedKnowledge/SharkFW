package security;

import junit.framework.*;
import net.sharkfw.knowledgeBase.*;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.KPListener;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.security.key.SharkKeyGenerator;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.pki.Certificate;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;
import net.sharkfw.system.L;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author thsc
 * @author ac
 */
public class BasicSecurityTests implements KPListener {

    private boolean knowledgeReceived;

    private static SharkKeyGenerator aliceKeys;
    private static SharkKeyGenerator bobKeys;

    private final String DATE_TIME = "01.01.2100";
    private Date date;

    private LinkedList<PeerSemanticTag> peerList;

    private J2SEAndroidSharkEngine aliceSe;
    private SharkPkiStorage alicePkiStorage;
    private PeerSemanticTag alice;
    private BasicSecurityTestKP aliceSimpleKP;

    private J2SEAndroidSharkEngine bobSe;
    private SharkPkiStorage bobPkiStorage;
    private PeerSemanticTag bob;
    private BasicSecurityTestKP bobSimpleKP;

    @BeforeClass
    public static void setUpClass() {
        aliceKeys = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);
        bobKeys = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);
    }

    @Before
    public void setUp() throws Exception {
        date = new Date();
        date.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(DATE_TIME).getTime());

        alice = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "http://www.alice.de", "tcp://localhost:7080");
        aliceSe = new J2SEAndroidSharkEngine();
        aliceSe.setConnectionTimeOut(1000);
        SharkKB aliceKB = new InMemoSharkKB();
        aliceKB.setOwner(alice);
        alicePkiStorage = new SharkPkiStorage(aliceKB, alice, aliceKeys.getPrivateKey());

        bob = InMemoSharkKB.createInMemoPeerSemanticTag("bob", "http://www.bob.de", "tcp://localhost:7081");
        bobSe = new J2SEAndroidSharkEngine();
        bobSe.setConnectionTimeOut(1000);
        SharkKB bobKB = new InMemoSharkKB();
        bobKB.setOwner(bob);
        bobPkiStorage = new SharkPkiStorage(bobKB, bob, bobKeys.getPrivateKey());

        peerList = new LinkedList<>();
        peerList.addFirst(alice);

        alicePkiStorage.addSharkCertificate(new SharkCertificate(bob, alice, peerList, Certificate.TrustLevel.FULL, bobKeys.getPublicKey(), date));
        bobPkiStorage.addSharkCertificate(new SharkCertificate(alice, bob, peerList, Certificate.TrustLevel.FULL, aliceKeys.getPublicKey(), date));

        aliceSimpleKP = new BasicSecurityTestKP(aliceSe);
        aliceSimpleKP.addListener(this);
        bobSimpleKP = new BasicSecurityTestKP(bobSe);
        bobSimpleKP.addListener(this);
    }

    @Test
    public void testEncryptedTransmission() throws Exception {

//        L.setLogLevel(L.LOGLEVEL_ALL);
//        L.setLogfile("basicsecuritytest.txt");

        Knowledge knowledge = InMemoSharkKB.createInMemoKnowledge();
        ContextCoordinates cc = InMemoSharkKB.createInMemoContextCoordinates(
                InMemoSharkKB.createInMemoSemanticTag("Security_Test", "Security_Test"),
                alice,
                alice,
                bob,
                null,
                null,
                SharkCS.DIRECTION_OUT
        );

        ContextPoint cp = InMemoSharkKB.createInMemoContextPoint(cc);
        knowledge.addContextPoint(cp);

        aliceSe.initSecurity(alice, alicePkiStorage, SharkEngine.SecurityLevel.MUST, SharkEngine.SecurityLevel.NO, SharkEngine.SecurityReplyPolicy.SAME, false);
        bobSe.initSecurity(bob, bobPkiStorage, SharkEngine.SecurityLevel.MUST, SharkEngine.SecurityLevel.NO, SharkEngine.SecurityReplyPolicy.SAME, false);

        aliceSe.startTCP(7080);
        bobSe.startTCP(7081);

        knowledgeReceived = false;

        aliceSe.sendKnowledge(knowledge, bob, aliceSimpleKP);

        Thread.sleep(1000);

        assertTrue(knowledgeReceived);
    }

    @Override
    public void exposeSent(KnowledgePort kp, SharkCS sentMutualInterest) {
    }

    @Override
    public void insertSent(KnowledgePort kp, Knowledge sentKnowledge) {
    }

    @Override
    public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP) {
        System.out.print("Successful Knowledge Received: ");
        System.out.println(newCP.getContextCoordinates().getTopic().getName());
        knowledgeReceived = true;
    }
}

class BasicSecurityTestKP extends KnowledgePort {

    public BasicSecurityTestKP(SharkEngine se) {
        super(se);
    }

    @Override
    protected void doInsert(Knowledge knowledge, KEPConnection kepConnection) {
        System.out.println("Security Test Knowledge received.");
        this.notifyKnowledgeAssimilated(this, knowledge.contextPoints().nextElement());
    }

    @Override
    protected void doExpose(SharkCS interest, KEPConnection kepConnection) {

    }
}
