package net.sharkfw.kep;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
public class EncryptingOutputStream extends OutputStream {
    private final OutputStream os;
    private Cipher cipher;
    private final Key key;
    private int blocksize; 
    private byte[] block;
    
    public EncryptingOutputStream(OutputStream os, Key key) {
        this.os = os;
        this.key = key;
        try {
            this.cipher = Cipher.getInstance("AES");
//            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            this.blocksize = cipher.getBlockSize();
            this.block = new byte[this.blocksize];
        } catch (Exception ex) {
            L.d(ex.getMessage(), this);
        }
    }

    private int counter = 0;
    
    @Override
    public void write(int i) throws IOException {
//        System.out.print("w(" + this.counter + ")" + i + ",");
//        if(this.counter == 15) {
//            System.out.print("\n");
//        };
        
//        if(i == -1) {
//            // final
//            this.flush();
//        }
        
        if(counter < this.blocksize) {
            this.block[counter++] = (byte)i;
        } else {
            this.finishAndSendBlock();
            this.write(i);
        }
    }
    
    private void finishAndSendBlock() {
//        System.out.print("send block");

        try {
            // encrypt
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> going to encrypt following block with size: " + block.length);
//                for(int i = 0; i < block.length; i++) {
//                    System.out.print("e[" + i + "]" + block[i] + ",");
//                }
//                System.out.print("\n");
            this.cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBlock = this.cipher.doFinal(this.block);
            
//            System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> going to send encrypted block with size: " + encryptedBlock.length);
            // send block length
            this.os.write(encryptedBlock.length);
            this.os.write(encryptedBlock);
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> sent encrypted block with size: " + encryptedBlock.length);
//                for(int i = 0; i < encryptedBlock.length; i++) {
//                    System.out.print("ee[" + i + "]" + encryptedBlock[i] + ",");
//                }
//                System.out.print("\n");
            
//            L.printByte(encryptedBlock, "encrypted Block");

        } catch (Exception ex) {
            L.d(ex.getMessage(), this);
        }

        this.block = new byte[this.blocksize];
        this.counter = 0;
    }
    
    @Override
    public void flush() throws IOException {
        this.os.flush();
    }

    void doFinal() throws IOException {
        this.finishAndSendBlock();
        this.flush();
    }
}
