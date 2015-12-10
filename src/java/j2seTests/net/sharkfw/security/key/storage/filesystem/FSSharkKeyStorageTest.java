package net.sharkfw.security.key.storage.filesystem;

import junit.framework.TestCase;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

/**
 * @author ac
 */
public class FSSharkKeyStorageTest extends TestCase {

    private static String filePath;
    private static FSSharkKeyStorage fsSharkKeyStorage;
    private static SharkKeyStorage sharkKeyStorage;

    @BeforeClass
    public void setUp() throws Exception {
        filePath = "KeyStorage.shark";
        fsSharkKeyStorage = new FSSharkKeyStorage(filePath);
        sharkKeyStorage = new SharkKeyStorage();
        sharkKeyStorage.setPrivateKey(null);
        sharkKeyStorage.setPublicKey(null);
        sharkKeyStorage.setSharkKeyPairAlgorithm(SharkKeyPairAlgorithm.RSA);
    }

    @AfterClass
    public void tearDown() throws Exception {
        new File(filePath).delete();
    }

    @Test
    public void testSave() throws Exception {
        assertTrue(fsSharkKeyStorage.save(sharkKeyStorage));
    }

    @Test
    public void testLoad() throws Exception {

        boolean isEqual = false;
        SharkKeyStorage sks = fsSharkKeyStorage.load();

        if(sks == null) {
            testSave();
            sks = fsSharkKeyStorage.load();
        }

        if (    sks.getPublicKey() == sharkKeyStorage.getPublicKey() &&
                sks.getPrivateKey() == sharkKeyStorage.getPrivateKey() &&
                sks.getSharkKeyPairAlgorithm() == sharkKeyStorage.getSharkKeyPairAlgorithm())
        {
            isEqual = true;
        }

        assertTrue(isEqual);
    }
}