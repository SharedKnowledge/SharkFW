package net.sharkfw.security.utilities;

import java.security.*;

/**
 * @author ac
 */
public class SharkSign {

    /**
     * see https://docs.oracle.com/javase/7/docs/api/java/security/Signature.html
     * for additional algorithms
     */
    public enum SharkSignatureAlgorithm {
        SHA1withRSA
    }

    /**
     * Calculates the signature of a given bytearray.
     * @param data byte-array
     * @param privateKey {@link PrivateKey}
     * @param sharkSignatureAlgorithm {@link net.sharkfw.security.utilities.SharkSign.SharkSignatureAlgorithm}
     * @return Signature
     */
    public static byte[] sign(byte[] data, PrivateKey privateKey, SharkSignatureAlgorithm sharkSignatureAlgorithm) {
        try {
            Signature signature = Signature.getInstance(sharkSignatureAlgorithm.name());
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (InvalidKeyException e) {
            return null;
        } catch (SignatureException e) {
            return null;
        }
    }

    /**
     * Validates signature and data.
     * @param data byte-array
     * @param signature byte-array
     * @param publicKey {@link PublicKey}
     * @param sharkSignatureAlgorithm {@link net.sharkfw.security.utilities.SharkSign.SharkSignatureAlgorithm}
     * @return true or false
     */
    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey, SharkSignatureAlgorithm sharkSignatureAlgorithm) {
        try {
            Signature sig = Signature.getInstance(sharkSignatureAlgorithm.name());
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (InvalidKeyException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        }
    }
}