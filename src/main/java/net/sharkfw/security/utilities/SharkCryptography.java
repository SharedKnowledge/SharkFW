package net.sharkfw.security.utilities;

import net.sharkfw.system.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author ac
 */

public class SharkCryptography {

    private static Cipher cipher;

    /**
     * Encode byte-array.
     *
     * @param data       byte-array
     * @param privateKey {@link PrivateKey}
     * @return Base64 encoded string
     */
    public static String encodeSessionKey(byte[] data, PrivateKey privateKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.getSpec());
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
     *
     * @param base64String          {@link String}
     * @param publicKey             {@link PublicKey}
     * @param sharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
     * @return byte-array
     */
    public static byte[] decodeSessionKey(String base64String, PublicKey publicKey, SharkKeyPairAlgorithm sharkKeyPairAlgorithm) {
        try {
            byte[] buffer = Base64.decode(base64String);
            cipher = Cipher.getInstance(sharkKeyPairAlgorithm.getSpec());
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
     *
     * @param data              byte-array
     * @param sessionKey        byte-array
     * @param sharkKeyAlgorithm {@link SharkKeyAlgorithm}
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
     *
     * @param data                  byte-array
     * @param publicKey             {@link PublicKey}
     * @param sharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
     * @return Encoded data as a {@link String}
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
     *
     * @param base64String      {@link String}
     * @param sessionKey        byte-array
     * @param sharkKeyAlgorithm {@link SharkKeyAlgorithm}
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
     *
     * @param base64String          {@link String}
     * @param privateKey            {@link PrivateKey}
     * @param sharkKeyPairAlgorithm {@link SharkKeyPairAlgorithm}
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