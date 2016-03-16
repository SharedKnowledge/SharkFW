/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sharkfw.system.Streamer;
import net.sharkfw.system.Util;

/**
 *
 * @author mfi
 */
public class StandardSharkInputStream implements SharkInputStream {

  private InputStream is = null;
  
  public StandardSharkInputStream(InputStream stream) {
    this.is = stream;
  }
  
  public String readUTF8() throws IOException {
    
    // Read 4 bytes as length info first
    byte[] lenBytes = new byte[4];
    int readLen = this.is.read(lenBytes);
    if(readLen < 4) {
      // No bytes read - stream at an end?!
      throw new IOException("Stream ended.");
    }
    
    // Translate into long value
    int len = Util.byteArrayToInt(lenBytes);
    
    // Stream bytes
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Streamer.stream(this.is, baos, UTF8SharkOutputStream.STREAM_BUFFER_SIZE, len);
    
    // Create String with UTF-8 encoding
//    String utfstring = new String(baos.toByteArray(), KEPMessage.ENCODING);
    String utfstring = new String(baos.toByteArray(), "UTF-8");
    
    return utfstring;
  }
  
    @Override
  public int available() throws IOException {
    return this.is.available();
  }

    @Override
  public InputStream getInputStream() {
    return this.is;
  }

    @Override
    public void set(InputStream is) {
        this.is = is;
    }

}
