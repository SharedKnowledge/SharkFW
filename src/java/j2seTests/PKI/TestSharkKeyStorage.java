/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PKI;

import net.sharkfw.pki.SharkKeyStorage;
import org.junit.*;

/**
 *
 * @author s0539748
 */
public class TestSharkKeyStorage {
    
    SharkKeyStorage _keyStore;
    
    /*@Before
    void setUp(){
        _keyStore = new SharkKeyStorage();
    }*/
    
    @Test
    void testCreateKeyPair(){
        _keyStore.createKeyPair();
        
        Assert.assertNotNull(_keyStore.getPrivateKey());
        
    }
    
    
}
