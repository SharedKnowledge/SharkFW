package net.sharkfw.security.utility;

import net.sharkfw.security.SharkSignatureAlgorithm;

import java.io.BufferedInputStream;
import java.security.*;

/**
 * @author ac
 */
public class SharkSign {

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

    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey, SharkSignatureAlgorithm sharkSignatureAlgorithm) {
        try {
            Signature sig = Signature.getInstance(sharkSignatureAlgorithm.name());
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature) ? true : false;
        } catch (NoSuchAlgorithmException e) {
            return false;
        } catch (InvalidKeyException e) {
            return false;
        } catch (SignatureException e) {
            return false;
        }
    }
}
