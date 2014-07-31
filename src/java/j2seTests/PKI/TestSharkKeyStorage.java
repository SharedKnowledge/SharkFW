/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PKI;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.pki.SharkCertificate;
import net.sharkfw.pki.SharkKeyStorage;
import net.sharkfw.system.SharkPKVerifiyException;
import org.junit.*;
import sun.security.provider.DSAPublicKey;

/**
 *
 * @author s0539748
 */
public class TestSharkKeyStorage {
    
    SharkKeyStorage _keyStore;
    PeerSemanticTag alice;
    
    @Before
    void setUp(){    
           
        //_keyStore = new SharkKeyStorage();
        alice = InMemoSharkKB.createInMemoPeerSemanticTag("Alice","alice","alice");
        _keyStore = new SharkKeyStorage() {

            @Override
            public void createKeyPair() {

            }

            @Override
            public void createKeyPair(String format) {

            }

            @Override
            public PrivateKey getPrivateKey() {
                return null;
            }

            @Override
            public PrivateKey getPublicKey() {
                return null;
            }

            @Override
            public void addKey(PublicKey key, PeerSemanticTag peer) {

            }

            @Override
            public void addKey(PublicKey key, PeerSemanticTag peer, long validity) {

            }

            @Override
            public SharkCertificate getCertificate(PeerSemanticTag peer) {
                return null;
            }

            @Override
            public void addCertificate(SharkCertificate certificate) {

            }

            @Override
            public PublicKey getPublicKey(PeerSemanticTag certifiedPeer) {
                return null;
            }

            @Override
            public boolean hasCertificate(PeerSemanticTag peer) {
                return false;
            }

            @Override
            public void removeCertificate(PeerSemanticTag peer) {

            }

            @Override
            public void signCertificate(PeerSemanticTag certifiedPeer, PeerSemanticTag signingPeer) {

            }

            @Override
            public void removeSignature(PeerSemanticTag certifiedPeer, PeerSemanticTag signingPeer) {

            }

            @Override
            public int getTrustLevel(PeerSemanticTag certifiedPeer) {
                return 0;
            }
        };
    }
    
    @Test
    void testCreateKeyPair(){
        _keyStore.createKeyPair();
        
        Assert.assertNotNull(_keyStore.getPrivateKey());
        Assert.assertNotNull(_keyStore.getPublicKey());

    }

    @Test
    void testCreateKeyPairWithFormat(){

        _keyStore.createKeyPair("RSA");

        Assert.assertNotNull(_keyStore.getPrivateKey());
        Assert.assertNotNull(_keyStore.getPublicKey());

    }
    
    @Test
    void testAddKey() throws SharkKBException {
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
        
        PublicKey key1 = new DSAPublicKey();
        _keyStore.addKey(key1,bob);
        
        Assert.assertEquals(key1,_keyStore.getPublicKey(bob));
        
        PublicKey key2 = new DSAPublicKey();
        _keyStore.addKey(key2,bob);
        
        Assert.assertEquals(key2,_keyStore.getPublicKey(bob));
        Assert.assertTrue(_keyStore.getCertificate(bob).isSignedBy(alice));
        Assert.assertNotSame(key1, key2);
    }
    

    @Test
    void testAddAndSignCertificate() throws SharkKBException {



        SharkCertificate bobsCertificate = null;

        _keyStore.addCertificate(bobsCertificate);
        _keyStore.signCertificate(bobsCertificate.getCertifiedPeer(),alice);

        Assert.assertEquals(bobsCertificate.getPublicKey(), _keyStore.getPublicKey(bobsCertificate.getCertifiedPeer()));
        Assert.assertTrue(_keyStore.getCertificate(bobsCertificate.getCertifiedPeer()).isSignedBy(alice));
    }

    @Test
    void testRemoveSignature(){
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");

        PublicKey key1 = new DSAPublicKey();
        _keyStore.addKey(key1, bob);
        _keyStore.signCertificate(bob,alice);

        Assert.assertTrue(_keyStore.getCertificate(bob).isSignedBy(alice));

        _keyStore.removeSignature(bob, alice);

        Assert.assertFalse(_keyStore.getCertificate(bob).isSignedBy(alice));


    }

    @Test
    void testRemoveCertificate(){
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");

        PublicKey key1 = new DSAPublicKey();
        _keyStore.addKey(key1, bob);

        Assert.assertTrue(_keyStore.hasCertificate(bob));

        _keyStore.removeCertificate(bob);

        Assert.assertFalse(_keyStore.hasCertificate(bob));
    }

    /*
    private byte[] getSignature(PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        Signature signature =Signature.getInstance("SHA1withDSA");
        signature.initSign(key);
        return signature.sign();
    }*/
    

    

    
}
