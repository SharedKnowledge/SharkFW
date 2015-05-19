//package net.sharkfw.security.pki.storage.filesystem;
//
//import net.sharkfw.knowledgeBase.PeerSemanticTag;
//import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
//import net.sharkfw.security.key.SharkKeyGenerator;
//import net.sharkfw.security.key.SharkKeyPairAlgorithm;
//import net.sharkfw.security.pki.SharkCertificate;
//import net.sharkfw.security.pki.storage.SharkPkiStorage;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.io.File;
//import java.security.PublicKey;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static org.junit.Assert.*;
//
///**
// * @author ac
// */
//public class FSSharkPkiStorageTest {
//
//    private static String filePath;
//    private static FSSharkPkiStorage fsSharkPkiStorage;
//    private static SharkPkiStorage sharkPkiStorage;
//    private static PeerSemanticTag alice;
//    private static PeerSemanticTag bob;
//    private static PublicKey publicKey;
//    private static Date valid;
//
//    @BeforeClass
//    public static void setUp() throws Exception {
//
//        filePath = "PkiStorage.shark";
//        fsSharkPkiStorage = new FSSharkPkiStorage(filePath);
//        sharkPkiStorage = new SharkPkiStorage();
//
//        InMemoSharkKB inMemoSharkKB = new InMemoSharkKB();
//        SharkKeyGenerator sharkKeyGenerator = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);
//
//        alice = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "http://www.alice.de", "alice@shark.net");
//        bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob", "http://www.bob.de", "bob@shark.net");
//        publicKey = sharkKeyGenerator.getPublicKey();
//        valid = new SimpleDateFormat("yyyy.MM.dd").parse("2020.01.01");
//
//        SharkCertificate sharkPkiCertificate = new SharkCertificate(alice, bob, publicKey, valid);
//        sharkPkiStorage.addSharkCertificate(sharkPkiCertificate);
//    }
//
//    @AfterClass
//    public static void tearDown() throws Exception {
//        new File(filePath).delete();
//    }
//
//    @Test
//    public void testSave() throws Exception {
//        assertTrue(fsSharkPkiStorage.save(sharkPkiStorage));
//    }
//
//    @Test
//    public void testLoad() throws Exception {
//        boolean isEqual = false;
//
//        SharkPkiStorage sps = fsSharkPkiStorage.load();
//
//        if(sps == null) {
//            testSave();
//            sps = fsSharkPkiStorage.load();
//        }
//
//        if(sps.getSharkCertificate(alice) == sharkPkiStorage.getSharkCertificate(alice)) {
//            isEqual = true;
//        }
//
//        assertTrue(isEqual);
//    }
//}