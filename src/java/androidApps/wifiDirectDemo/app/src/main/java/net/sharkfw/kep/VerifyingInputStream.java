package net.sharkfw.kep;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class VerifyingInputStream extends InputStream {
    private InputStream is;
    private Signature signature;
    
    public VerifyingInputStream(InputStream is, PublicKey publicKey) {
        try {
            this.is = is;
            this.signature = Signature.getInstance("MD5withRSA");
            this.signature.initVerify(publicKey);

            byte[] buffer = new byte[1024];
            int len;
            while (is.available() > 0) {
                len = is.read(buffer);
                signature.update(buffer, 0, len);
            };
            
//            System.out.println(">>>>>>>>>>>>> init verifying with public key:\n" + publicKey.toString());
        } catch (InvalidKeyException ex) {
            L.d(ex.getMessage(), this);
        } catch (NoSuchAlgorithmException ex) {
            L.d(ex.getMessage(), this);
        } catch (IOException ex) {
            Logger.getLogger(VerifyingInputStream.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(VerifyingInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

//    private int counter = 0;
    @Override
    public int read() throws IOException {
        try {
            int i = this.is.read();
//            System.out.print("v[" + this.counter + "]" + i + " ");
//            this.counter++;
            
            this.signature.update((byte)i);
            
            return i;
        } catch (SignatureException ex) {
            L.d(ex.getMessage(), this);
        }
        
        // will be reached in case of an exception
        return -1;
        
    }
    

    public boolean verify(byte[] signature) {
//        System.out.println(">>>>>>>>>>>>>>>> counter, verify: " + this.counter);
        try {
            boolean verifyResult = this.signature.verify(signature);
//            if(verifyResult) {
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> verification OK <<<<<<<<<<<<<<< ");
//            } else {
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> verification NOT OK <<<<<<<<<<<<<<< ");
//            }
            return verifyResult;
        } catch (SignatureException ex) {
            L.d(ex.getMessage(), this);
        }
        
        return false;
    }
}
