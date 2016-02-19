package net.sharkfw.asip;

import java.io.InputStream;
import java.io.OutputStream;
import net.sharkfw.knowledgeBase.PropertyHolder;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author thsc
 */
public interface ASIPInformation extends PropertyHolder {
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
}
