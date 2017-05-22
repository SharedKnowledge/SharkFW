package net.sharkfw.security;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.peer.J2SESharkEngine;
import net.sharkfw.system.L;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Created by j4rvis on 2/27/17.
 */
public class SharkPkiStorageTest {

    @Test
    public void test(){

        L.setLogLevel(L.LOGLEVEL_ALL);

        J2SESharkEngine engine = new J2SESharkEngine();
        SharkPkiStorage pkiStorage = (SharkPkiStorage) engine.getPKIStorage();

        PeerSemanticTag alice = InMemoSharkKB.createInMemoPeerSemanticTag("Alice", "si:alice", "tcp://alice.de");

        KeyPairGenerator keyPairGenerator = null;
        try {
            pkiStorage.setPkiStorageOwner(alice);
            pkiStorage.generateNewKeyPair(1000*60*60*24*365);
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
        } catch (SharkKBException | NoSuchAlgorithmException | IOException e) {
            L.e(e.getMessage(), this);
            e.printStackTrace();
        }

        L.d("KeyGenerator initiated.", this);

        long hour = 1000 * 60 * 60;
        long day = 24 * hour;
        long week = 7 * day;
        long today = System.currentTimeMillis();
        long tomorrow = today + day;
        long nextWeek = today + week;
        long yesterday = today - day;
        long previousWeek = today - week;

        String kName = "Karl";
        String kSI = "st:k";
        String kAddr = "tcp://shark.net/k";
        PeerSemanticTag karl = InMemoSharkKB.createInMemoPeerSemanticTag(kName, kSI, kAddr);
        KeyPair kKeyPair = keyPairGenerator.generateKeyPair();

        String lName = "Louis";
        String lSI = "st:l";
        String lAddr = "tcp://sharl.net/l";
        PeerSemanticTag louis = InMemoSharkKB.createInMemoPeerSemanticTag(lName, lSI, lAddr);
        KeyPair lKeyPair = keyPairGenerator.generateKeyPair();

        String mName = "Marc";
        String mSI = "st:m";
        String mAddr = "tcp://sharm.net/m";
        PeerSemanticTag marc = InMemoSharkKB.createInMemoPeerSemanticTag(mName, mSI, mAddr);
        KeyPair mKeyPair = keyPairGenerator.generateKeyPair();


        String nName = "Ned";
        String nSI = "st:n";
        String nAddr = "tcp://sharn.net/n";
        PeerSemanticTag ned = InMemoSharkKB.createInMemoPeerSemanticTag(nName, nSI, nAddr);
        KeyPair nKeyPair = keyPairGenerator.generateKeyPair();


        String oName = "Olaf";
        String oSI = "st:o";
        String oAddr = "tcp://sharo.net/o";
        PeerSemanticTag olaf = InMemoSharkKB.createInMemoPeerSemanticTag(oName, oSI, oAddr);
        KeyPair oKeyPair = keyPairGenerator.generateKeyPair();

        L.d("Keys generated", this);

        SharkPublicKey kKey = pkiStorage.addUnsignedKey(karl, kKeyPair.getPublic(), tomorrow);
        SharkPublicKey lKey = pkiStorage.addUnsignedKey(louis, lKeyPair.getPublic(), tomorrow + hour);
        SharkPublicKey mKey = pkiStorage.addUnsignedKey(marc, mKeyPair.getPublic(), yesterday);
        SharkPublicKey nKey = pkiStorage.addUnsignedKey(ned, nKeyPair.getPublic(), nextWeek);
        SharkPublicKey oKey = pkiStorage.addUnsignedKey(olaf, oKeyPair.getPublic(), previousWeek);
        L.d("Keys added", this);
        try {
            // Signed by myself
            pkiStorage.sign(kKey);
            pkiStorage.sign(lKey);
            pkiStorage.sign(nKey);

            // Signed by others
            pkiStorage.sign(lKey, karl, kKeyPair.getPrivate());
            pkiStorage.sign(mKey, karl, kKeyPair.getPrivate());
            pkiStorage.sign(oKey, karl, kKeyPair.getPrivate());

            pkiStorage.sign(mKey, louis, lKeyPair.getPrivate());
            pkiStorage.sign(nKey, louis, lKeyPair.getPrivate());
            pkiStorage.sign(oKey, louis, lKeyPair.getPrivate());

            pkiStorage.sign(kKey, marc, mKeyPair.getPrivate());
            pkiStorage.sign(nKey, marc, mKeyPair.getPrivate());
            pkiStorage.sign(oKey, marc, mKeyPair.getPrivate());
            pkiStorage.sign(lKey, marc, mKeyPair.getPrivate());

            pkiStorage.sign(kKey, ned, nKeyPair.getPrivate());
            pkiStorage.sign(lKey, ned, nKeyPair.getPrivate());
            pkiStorage.sign(oKey, ned, nKeyPair.getPrivate());

            pkiStorage.sign(kKey, olaf, oKeyPair.getPrivate());
            pkiStorage.sign(nKey, olaf, oKeyPair.getPrivate());

            Assert.assertEquals(18, pkiStorage.getAllSharkCertificates().size());
            Assert.assertEquals(3, pkiStorage.getSharkCertificatesBySigner(alice).size());
            Assert.assertEquals(3, pkiStorage.getSharkCertificatesBySigner(karl).size());
            Assert.assertEquals(3, pkiStorage.getSharkCertificatesBySigner(louis).size());
            Assert.assertEquals(4, pkiStorage.getSharkCertificatesBySigner(marc).size());
            Assert.assertEquals(3, pkiStorage.getSharkCertificatesBySigner(ned).size());
            Assert.assertEquals(2, pkiStorage.getSharkCertificatesBySigner(olaf).size());

            Assert.assertEquals(0, pkiStorage.getSharkCertificatesByOwner(alice).size());
            Assert.assertEquals(4, pkiStorage.getSharkCertificatesByOwner(karl).size());
            Assert.assertEquals(4, pkiStorage.getSharkCertificatesByOwner(louis).size());
            Assert.assertEquals(2, pkiStorage.getSharkCertificatesByOwner(marc).size());
            Assert.assertEquals(4, pkiStorage.getSharkCertificatesByOwner(ned).size());
            Assert.assertEquals(4, pkiStorage.getSharkCertificatesByOwner(olaf).size());
        } catch (SharkKBException e) {
            e.printStackTrace();
        }
    }
}
