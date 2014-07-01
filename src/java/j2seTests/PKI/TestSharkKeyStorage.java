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
    }
    
    @Test
    void testCreateKeyPair(){
        _keyStore.createKeyPair();
        
        Assert.assertNotNull(_keyStore.getPrivateKey());
        Assert.assertNotNull(_keyStore.getPublicKey());
    }
    
    @Test
    void testAddKey(){
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
        
        PublicKey key = new DSAPublicKey();        
        _keyStore.addKey(key,bob);
        
        Assert.assertEquals(key,_keyStore.getPublicKey(bob));
        
        key = new DSAPublicKey();
        _keyStore.addKey(key,bob);
        
        Assert.assertEquals(key,_keyStore.getPublicKey(bob));
    }
    
    @Test
    void testCreateCertificate() throws SharkKBException{
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
        PublicKey key = new DSAPublicKey();
        
        _keyStore.createCertificate(key, bob, 100);
        
        Assert.assertEquals(_keyStore.getCertificate(bob).getPublicKey(),key);        
    }
    
    @Test
    void testSignCertificate() throws SharkKBException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, SharkPKVerifiyException{
        PeerSemanticTag bob = InMemoSharkKB.createInMemoPeerSemanticTag("Bob","bob","bob");
        PublicKey pubKey = new DSAPublicKey();
        _keyStore.createKeyPair();
        PrivateKey privKey = _keyStore.getPrivateKey();  
        byte[] sig = getSignature(privKey);        
        
        _keyStore.createCertificate(pubKey, bob, 100);
        _keyStore.getCertificate(bob).addSignatureToPublicKey(alice, sig);
        Assert.assertTrue(_keyStore.getCertificate(bob).isSignedBy(alice));
    }
    
    @Test
    void testAddCertificate(){
    }
    
    @Test
    void signCertificate(){
        
    }
    
    private byte[] getSignature(PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        Signature signature =Signature.getInstance("SHA1withDSA");
        signature.initSign(key);
        return signature.sign();
    }
    

    

    
}
