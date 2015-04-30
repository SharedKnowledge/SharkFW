/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * <p>An input stream which is able to read UTF-8 encoded Strings from 
 * a stream and return them.</p>
 * 
 * <p>For reading binary data, the underlying InputStream can be accessed.</p>
 * 
 * @author mfi
 */
public interface SharkInputStream {
  
  /**
   * <p>Read the next UTF-8 encoded string token from the stream.</p>
   * 
   * @return A UTF-8 encoded string
   */
  public String readUTF8() throws IOException;
  
  /**
   * <p>Return the number of available bytes on the stream</p>
   * 
   * @return number of available bytes on the stream.
   * @throws IOException 
   */
  public int available() throws IOException;
  /**
   * <p>Return the underlying InputStream for reading binary data.</p>
   * 
   * @return The underlying InputStream.
   */
  public InputStream getInputStream();

  public void set(InputStream encodingIS);
}
