/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>An output stream wrapping a java native outputstream which allows writing
 * of strings as a primitive (like {@link DataOutputStream}). </p>
 * 
 * <p>The encapsulated {@link OutputStream} can be accessed by calling <code>getOutputStream()</code>,
 * when binary data needs to be written to the stream directly.</p>
 * @author mfi
 */
public interface SharkOutputStream {
  
  /**
   * <p>Write a string to stream using UTF-8 encoding.</p>
   * <p>The string itself will be prefixed with an int value denoting its length</p>
   */
  public void write(String utfString) throws IOException;
  
  /**
   * <p>Return the underlying OutoutStream</p>
   * 
   * <p>Call this method if binary data is to be sent</p>
   * @return The underlying OutputStream.
   */
  public OutputStream getOutputStream();

    public void set(OutputStream os);
}
