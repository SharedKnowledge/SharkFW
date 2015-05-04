package net.sharkfw.security.key;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ac
 */
public class SharkKeyGeneratorTest {

    private SharkKeyGenerator sharkKeyGenerator = null;

    @Before
    public void setUp() throws Exception {
        //Keep in mind that the session key cannot exceed 128bit (AES) expect you use the Unlimited Strength library
        //see http://examples.javacodegeeks.com/core-java/security/invalidkeyexception/java-security-invalidkeyexception-how-to-solve-invalidkeyexception/
        sharkKeyGenerator = new SharkKeyGenerator(SharkKeyPairAlgorithm.RSA, 1024);
    }

    @Test
    public void testGetPublicKey() throws Exception {
        assertNotNull(sharkKeyGenerator.getPublicKey());
    }

    @Test
    public void testGetPrivateKey() throws Exception {
        assertNotNull(sharkKeyGenerator.getPrivateKey());
    }

    @Test
    public void testGetRandomSessionKey() throws Exception {
        //Expected size of the returned byte array
        assertEquals(16, sharkKeyGenerator.getRandomSessionKey(SharkKeyAlgorithm.AES).length);
    }
}