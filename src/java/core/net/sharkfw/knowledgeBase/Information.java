package net.sharkfw.knowledgeBase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Information contains content. Content type should
 * be known, but are optional. The content type is stored as a property.</p>
 *
 * <p>The content-type is best provided as MIME-type.</p>
 *
 * <p>Information can be anything that can be streamed using java's
 * Input and OutputStreams.</p>
 *
 * <p>Information offer some convienience methods, like: <br />
 * <code>info.setContent("This is string content!");</code> All string set here, are treated as UTF-8 strings! <br />
 * If content shall be streamed the methods: <br />
 * <code>
 * [...]
 * FileInputStream fis = new FileInputStream(somefile);</code> <br />
 * <code>
 * info.setContent(fis, somefile.length());
 * [...]
 * </code> <br /> or <br />
 * <code>
 * FileOutputStream fos = new FileOutputStream(someOtherFile); </code> <br />
 * <code>
 * info.streamContent(fos);
 * </code>
 * </p>
 *
 * @see java.io.InputStream
 * @see java.io.OutputStream
 * 
 * @author thsc
 * @author mfi
 */
public interface Information extends SystemPropertyHolder {
    /**
     * Time in millis since 1.1.1970 - UNIX area.
     * @return 
     */
    public long lastModified();

    /**
     * Time in millis since 1.1.1970 - UNIX area.
     * @return 
     */
    public long creationTime();

  /**
   * Read <code>len</code> bytes from <code>is</code> to use them as the information's content.
   *
   * @param is An <code>InputStream</code> to read from
   * @param len The number of bytes to read from <code>is</code>
   */
  public void setContent(InputStream is, long len);

  /**
   * Use the passed byte array as content for this information.
   *
   * @param content An array of bytes containing the content for this information.
   */
  public void setContent(byte[] content);

  /**
   * Set the passed UTF-8 encoded String as content on this information.
   *
   * @param content A String that is saved to this information.
   */
  public void setContent(String content);

  /**
   * Purge the content from this information leaving it empty.
   */
  public void removeContent();

  /**
   * Set the type of the content stored using MIME-types.
   * This property is always tranfered.
   *
   * @param mimetype A String value denoting a valid MIME-type
   */
  public void setContentType(String mimetype);

  /**
   * Return the content type of this Information object
   *
   * @return A string containing a MIME-Type
   */
  public String getContentType();

  /**
   * Return the content of this Information as an array of bytes
   *
   * @return byte representation of this Information's content.
   */
  public byte[] getContentAsByte();

  /**
   * Asks the object to streams its content into this
   * output stream
   *
   * @param os
   */
  public void streamContent(OutputStream os);

  /**
   * Returns the length of the content
   *
   * @return An integer value denoting the length of content
   */
  public long getContentLength();

    /**
     * Return an OutputStream containing the content of this Information.
     * @return An OutputStream containing the content of this Information.
     *     public OutputStream getWriteAccess();
     * @throws SharkKBException 
     * @deprecated use streamContent instead
     */
    public OutputStream getOutputStream() throws SharkKBException;
    
    /**
     * Return an InputStream that allows streaming data into information object.
     * @return InputStream to information object
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     * @deprecated use setContent(InputStream instead)
     */
    public InputStream getInputStream() throws SharkKBException;
    
    /**
     * Returns String that was previously set with setName();
     * @return a string or null if setName was never used before
     */
    public String getName();
    
    /**
     * Converts content into a string.
     * @return
     * @throws SharkKBException if conversion wasn't possible
     */
    public String getContentAsString() throws SharkKBException;
    
    /**
     * Gives information a name - shark never uses that name. It just for 
     * applications.
     * WARNING: The name must not contain slash (/) or backslash (\).
     * @param name 
     * @throws SharkKBException if the name contains not allowed characters.
     */
    public void setName(String name) throws SharkKBException;
    
    // size() was duplicate to getContentLength() 
    
    /**
     * Returns the unique ID of that information object
     * @return unique ID as String, "" if there is no unique ID set
     */
    public String getUniqueID();
       
}
