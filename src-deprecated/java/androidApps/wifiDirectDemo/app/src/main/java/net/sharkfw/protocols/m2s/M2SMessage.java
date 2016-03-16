package net.sharkfw.protocols.m2s;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.system.Util;

/**
 *
 * @author thsc
 */
class M2SMessage {
    static int FIRST_PACKAGE_NUMBER = 0;
    
    static void writeM2SHeader(ByteArrayOutputStream baos, String id, 
            int packageNumber, boolean finished) throws IOException {

        DataOutputStream dos = new DataOutputStream(baos);
        
        // write ID
        dos.writeUTF(id);
        
        // write command
        dos.writeInt(M2SStub.M2S_INSERT);
        
        // write package number
        dos.writeInt(packageNumber);
        
        // write finished
        dos.writeBoolean(finished);
        
//        // Write len of id as byte[] first
//        int idLen = id.length();
//        byte[] idLenBytes = Util.intToByteArray(idLen);
//        baos.write(idLenBytes);
//
//        // Write id itself
//        byte[] idBytes = id.getBytes(KEPMessage.ENCODING);
//        baos.write(idBytes);
//
//        // tell remote peer to insert following content
//        baos.write(M2SStub.M2S_INSERT);
//
//        // Write package number as int
//        byte[] packageNumberBytes = Util.intToByteArray(packageNumber);
//        baos.write(packageNumberBytes);
//
//        if(finished) {
//          baos.write(M2SStub.INT_TRUE);
//        } else {
//          baos.write(M2SStub.INT_FALSE);
//        }
    }
}
