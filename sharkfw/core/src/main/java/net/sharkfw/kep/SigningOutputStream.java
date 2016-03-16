package net.sharkfw.kep;

import java.io.IOException;
import java.io.OutputStream;
import java.security.*;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class SigningOutputStream extends OutputStream {
    private OutputStream os = null;
    private Signature signature = null;
    
    public SigningOutputStream(OutputStream os, PrivateKey privateKey) {
        try {
            this.os = os;
            this.signature = Signature.getInstance("MD5withRSA");
            this.signature.initSign(privateKey);
            
//            System.out.println(">>>>>>>>>>>>> init signing with private Key:\n" + privateKey.toString());
            
        } catch (InvalidKeyException ex) {
            L.d(ex.getMessage(), this);
        } catch (NoSuchAlgorithmException ex) {
            L.d(ex.getMessage(), this);
        }
        
    }

//    private int counter = 0;
    @Override
    public void write(int i) throws IOException {
//        System.out.print("s[" + this.counter + "]" + i + " ");
        this.os.write(i);
//        this.counter++;
        try {
            this.signature.update((byte)i);
        } catch (SignatureException ex) {
            L.d(ex.getMessage(), this);
        }
    }

    byte[] getSignature() {
//        System.out.println(">>>>>>>>>>>>>>>> counter, sign: " + this.counter);
        try {
            return this.signature.sign();
        } catch (SignatureException ex) {
            L.d(ex.getMessage(), this);
        }
        
        return null;
    }
    
}
