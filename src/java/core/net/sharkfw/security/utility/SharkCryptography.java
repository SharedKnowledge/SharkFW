package net.sharkfw.security.utility;

import net.sharkfw.security.key.SharkKeyAlgorithm;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.system.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;

/**
 * @author ac
 */

//TODO: the process of encoding and decoding should run in a separate thread with an event notifier

public class SharkCryptography {

    private static Cipher cipher;

    /**
     * Encode byte-array.
     * @param data
     * @param privateKey
     * @return Base64 encoded string
     */
    public static String encodeSessionKey(byte[] data, PrivateKey privateKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.name());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBytes(cipher.doFinal(data));
        } catch (InvalidKeyException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        }
    }

    /**
     * Decode Base64 string.
     * @param base64String
     * @param publicKey
     * @param sharkKeyPairAlgorithm
     * @return byte-array
     */
    public static byte[] decodeSessionKey(String base64String, PublicKey publicKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            byte[] buffer = Base64.decode(base64String);
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.name());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(buffer);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Encode data stored in a byte-array.
     * @param data
     * @param sessionKey
     * @param sharkKeyAlgorithm
     * @return Base64 encoded string
     */
    public static String encodeData(byte[] data, byte[] sessionKey, SharkKeyAlgorithm sharkKeyAlgorithm) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKey, sharkKeyAlgorithm.name());
            cipher = Cipher.getInstance(sharkKeyAlgorithm.name());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.encodeBytes(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        }
    }

    /**
     * Encode data stored in a byte-array.
     * @param data
     * @param publicKey
     * @param sharkKeyPairAlgorithm
     * @return
     */
    public static String encodeData(byte[] data, PublicKey publicKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.name());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBytes(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        }
    }

    /**
     * Decode Base64 data.
     * @param base64String
     * @param sessionKey
     * @param sharkKeyAlgorithm
     * @return byte-array
     */
    public static byte[] decodeData(String base64String, byte[] sessionKey, SharkKeyAlgorithm sharkKeyAlgorithm) {
        try {
            byte[] data = Base64.decode(base64String);
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKey, sharkKeyAlgorithm.name());
            cipher = Cipher.getInstance(sharkKeyAlgorithm.name());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Decode Base64 data.
     * @param base64String
     * @param privateKey
     * @param sharkKeyPairAlgorithm
     * @return byte-array
     */
    public static byte[] decodeData(String base64String, PrivateKey privateKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            byte[] data = Base64.decode(base64String);
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.name());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (IOException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (NoSuchPaddingException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        }
    }
}
