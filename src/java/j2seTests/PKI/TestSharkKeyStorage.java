///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package PKI;
//
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.util.Iterator;
//
//import net.sharkfw.knowledgeBase.PeerSemanticTag;
//import net.sharkfw.knowledgeBase.SharkKBException;
//import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
//import net.sharkfw.pki.SharkCertificate;
//import net.sharkfw.pki.SharkKeyStorage;
//import org.junit.*;
//import sun.security.provider.DSAPublicKey;
//
///**
// *
// * @author Sascha Saunus (Matr.Nr.: 540070), Daniel Rockenstein (Matr.Nr.: 539748)
// *
// */
//public class TestSharkKeyStorage {
//
//    SharkKeyStorage _keyStore;
//    PeerSemanticTag alice;
//
//    @Before
//    void setUp(){
//
//        //_keyStore = new SharkKeyStorage();
//        alice = InMemoSharkKB.createInMemoPeerSemanticTag("Alice","alice","alice");
//        _keyStore = new SharkKeyStorage() {
//
//
//            @Override
//            public void createKeyPair() {
//
//            }
//
//            @Override
//            public void createKeyPair(String format) {
//
//            }
//
//            @Override
//            public PrivateKey getPrivateKey() {
//                return null;
//            }
//
//            @Override
//            public PrivateKey getPublicKey() {
//                return null;
//            }
//
//            @Override
//            public void addPublicKey(PublicKey key, PeerSemanticTag peer) {
//
//            }
//
//            @Override
//            public void deletePublicKey(PeerSemanticTag peer) {
//
//            }
//
//            @Override
//            public PublicKey getPublicKey(PeerSemanticTag peer) {
//                return null;
//            }
//
//            @Override
//            public void signPublicKey(PeerSemanticTag certifiedPeer, PublicKey key) {
//
//            }
//
//            @Override
//            public void signPublicKey(PeerSemanticTag certifiedPeer, long validity) {
//
//            }
//
//            @Override
//            public void addCertificate(SharkCertificate certificate) {
//
//            }
//
//            @Override
//            public Iterator<SharkCertificate> getCertificates() {
//                return null;
//            }
//
//            @Override
//            public Iterator<SharkCertificate> getCertificatesBy(PeerSemanticTag certifyingPeer) {
//                return null;
//            }
//
//            @Override
//            public Iterator<SharkCertificate> getCertificates(PeerSemanticTag certifiedPeer) {
//                return null;
//            }
//
//            @Override
//            public SharkCertificate getCertificate(PeerSemanticTag certifiedPeer, PeerSemanticTag certifyingPeer) {
//                return null;
//            }
//
//            @Override
//            public SharkCertificate getBestVerifiedCertificate(PeerSemanticTag certifiedPeer) {
//                return null;
//            }
//
//            @Override
//            public SharkCertificate getBestVerifiedCertificate(PeerSemanticTag certifiedPeer, int maxTrustLevel) {
//                return null;
//            }
//
//            @Override
//            public Iterator<SharkCertificate> getVerifiedCertificates(PeerSemanticTag certifiedPeer) {
//                return null;
//            }
//
//            @Override
//            public Iterator<SharkCertificate> getVerifiedCertificates(PeerSemanticTag certifiedPeer, int maxTrustLevel) {
//                return null;
//            }
//
//            @Override
//            public boolean hasCertificate(PeerSemanticTag certifiedPeer) {
//                return false;
//            }
//
//            @Override
//            public void removeCertificate(PeerSemanticTag certifiedPeer) {
//
//            }
//
//            @Override
//            public int getTrustLevel(SharkCertificate certificate) {
//                return 0;
//            }
//
//            @Override
//            public boolean verify(SharkCertificate certificate) {
//                return false;
//            }
//
//            @Override
//            public boolean verify(SharkCertificate certificate, int maxTrustLevel) {
//                return false;
//            }
//
//            @Override
//            public int getDefaultTrustLevel() {
//                return 0;
//            }
//
//            @Override
//            public void setDefaultTrustLevel(int level) {
//
//            }
//
//            @Override
//            public void removeInvalidCertificates() {
//
//            }
//        };
//    }
//
//    @Test
//    void testCreateKeyPair(){
//        _keyStore.createKeyPair();
//
//        Assert.assertNotNull(_keyStore.getPrivateKey());
//        Assert.assertNotNull(_keyStore.getPublicKey());
//
//    }
//
//    @Test
//    void testCreateKeyPairWithFormat(){
//
//        _keyStore.createKeyPair("RSA");
//
//        Assert.assertNotNull(_keyStore.getPrivateKey());
//        Assert.assertNotNull(_keyStore.getPublicKey());
//
//    }
//
//    @Test
//    void testAddKey() throws SharkKBException {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        Assert.assertEquals(key1,_keyStore.getPublicKey(bob));
//
//        PublicKey key2 = new DSAPublicKey();
//        _keyStore.addPublicKey(key2,bob);
//
//        Assert.assertEquals(key2,_keyStore.getPublicKey(bob));
//        Assert.assertNotSame(key1, key2);
//    }
//
//    @Test
//    void testDeleteKey() {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1, bob);
//
//        _keyStore.deletePublicKey(bob);
//        Assert.assertNull(_keyStore.getPublicKey(bob));
//
//    }
//
//    @Test
//    void testAddCertificate() throws SharkKBException {
//
//        SharkCertificate bobsCertificate = null;
//
//        _keyStore.addCertificate(bobsCertificate);
//
//        Iterator<SharkCertificate> certs = _keyStore.getCertificates();
//
//        Assert.assertTrue(certs.hasNext());
//    }
//
//    @Test
//    void testSignKeyAndGetCertificates() {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Iterator<SharkCertificate> certs = _keyStore.getCertificates();
//
//        Assert.assertTrue(certs.hasNext());
//    }
//
//    @Test
//    void testGetCertificate() {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Assert.assertNotNull(_keyStore.getCertificate(bob, alice));
//    }
//
//    @Test
//    void testGetCertificates(){
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Iterator<SharkCertificate> certs = _keyStore.getCertificates(bob);
//
//        Assert.assertTrue(certs.hasNext());
//    }
//
//    @Test
//    void testGetCertificatesBy(){
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Iterator<SharkCertificate> certs = _keyStore.getCertificatesBy(alice);
//
//        Assert.assertTrue(certs.hasNext());
//    }
//
//    @Test
//    void testGetBestVerifiedCertificate() throws SharkKBException {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob,_keyStore.getPublicKey(bob));
//
//        Assert.assertEquals(key1, _keyStore.getBestVerifiedCertificate(bob, 0).getPublicKey());
//    }
//
//    @Test
//    void testGetVerifiedCertificates() {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1,bob);
//
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Iterator<SharkCertificate> certs = _keyStore.getVerifiedCertificates(bob, 0);
//        Assert.assertTrue(certs.hasNext());
//    }
//
//    @Test
//    void testRemoveCertificate(){
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1, bob);
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Assert.assertTrue(_keyStore.hasCertificate(bob));
//
//        _keyStore.removeCertificate(bob);
//
//        Assert.assertFalse(_keyStore.hasCertificate(bob));
//    }
//
//    @Test
//    void testDefaultTrustLevel() {
//        _keyStore.setDefaultTrustLevel(1);
//
//        Assert.assertEquals(_keyStore.getDefaultTrustLevel(),1);
//
//    }
//
//    @Test
//    void testGetTrustLevel() {
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1, bob);
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Assert.assertEquals(_keyStore.getTrustLevel(_keyStore.getCertificate(bob,alice)),0);
//    }
//
//    @Test
//    void testVerify(){
//        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
//
//        PublicKey key1 = new DSAPublicKey();
//        _keyStore.addPublicKey(key1, bob);
//        _keyStore.signPublicKey(bob, _keyStore.getPublicKey(bob));
//
//        Assert.assertTrue(_keyStore.verify(_keyStore.getCertificate(bob,alice),0));
//    }
//
//}
