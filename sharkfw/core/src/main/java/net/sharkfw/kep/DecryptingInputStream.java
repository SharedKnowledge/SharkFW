package net.sharkfw.kep;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import javax.crypto.Cipher;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class DecryptingInputStream extends InputStream {
    private final InputStream is;
    private final Key key;
    private Cipher cipher;
    private int blocksize;
    private byte[] block;
    private int counter = 0;
    
    public DecryptingInputStream(InputStream is, Key key) {
        this.is = is;
        this.key = key;
        
        try {
            this.cipher = Cipher.getInstance("AES");
//            this.cipher.init(Cipher.DECRYPT_MODE, this.key);
            
            this.blocksize = this.cipher.getBlockSize();
        } catch (Exception ex) {
            L.e(ex.getMessage(), this);
        }
    }

    @Override
    public int read() throws IOException {
        
        // refill block?
        if(this.block == null || this.counter == this.blocksize) {
            this.refillBlock();
        }
        
        byte b = this.block[this.counter];
        int retVal = b;
//        System.out.print("r(" + this.counter + ")" + retVal + ",");
//        if(this.counter == 15) {
//            System.out.print("\n");
//        };
        
        this.counter++;

//        System.out.println("\n>>>>>>>>>>>>>>> return from read with " + retVal);
        return retVal;
    }
    
    private void refillBlock() {
            try {
                // first: read block length from stream
                int length = this.is.read();
                byte[] encodedBlock = new byte[length];
                
//                System.err.println(">>>>>>>>>>>>>> going to received encode block with size: " + length);
                this.is.read(encodedBlock);
//                System.out.println(">>>>>>>>>>>>>> received encode block with size: " + encodedBlock.length);
//                for(int i = 0; i < length; i++) {
//                    System.out.print("de[" + i + "]" + encodedBlock[i] + ",");
//                }
//                System.out.print("\n");

                // decode
                this.cipher.init(Cipher.DECRYPT_MODE, this.key);
                this.block = this.cipher.doFinal(encodedBlock);
                this.counter = 0;
            } catch (Exception ex) {
                L.d(ex.getMessage(), this);
                ex.printStackTrace();
                System.err.println(ex.getLocalizedMessage());
            }
            
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> decrypted following block with size: " + block.length);
//            for(int i = 0; i < block.length; i++) {
//                System.out.print("d[" + i + "]" + block[i] + ",");
//            }
//            System.out.print("\n");
    }
}
