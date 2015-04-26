/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.system.Streamer;
import net.sharkfw.system.Util;

/**
 *
 * @author mfi
 */
public class UTF8SharkOutputStream implements SharkOutputStream{

    public static final int STREAM_BUFFER_SIZE = 1048576; // = 1 MByte
//    public static final int STREAM_BUFFER_SIZE = 524288; // = 0,5 MByte
    
    
  private OutputStream os = null;
  
  public UTF8SharkOutputStream(OutputStream stream) {
    this.os = stream;
  }
  
  public void write(String utfString) throws IOException {
    try {
      
      // Translate the string to a byte array
      byte[] bytes = utfString.getBytes(KEPMessage.ENCODING);
      
      // Store the size of ths byte array
      int len = bytes.length;
      
      // Translate the long value into a byte[] for writing it on the stream
      byte[] lenBytes = Util.intToByteArray(len);
      
      // Write length info first
      this.os.write(lenBytes);
      
      // Write payload next (byte representation of string)
      Streamer.stream(new ByteArrayInputStream(bytes), this.os, STREAM_BUFFER_SIZE, len);
      
    } catch (UnsupportedEncodingException ex) {
      // Notify by IOException
      throw new IOException(ex.getMessage());
    }
  }

  public OutputStream getOutputStream() {
    return this.os;
  }

    @Override
    public void set(OutputStream os) {
        this.os = os;
    }
  
}
