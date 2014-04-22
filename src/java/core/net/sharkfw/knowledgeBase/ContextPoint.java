package net.sharkfw.knowledgeBase;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>ContextPoint objects represent - as the name implies - a context of {@link Information}.
 * Each context is represented by a single context point object.</p>
 *
 * <p>ContextPoint are points in the n-dimensional space that is defined as
 * the {@link ContextSpace}. A point can be in- or outside to a given context space.</p>
 *
 * <p>If a context can not be described completely, the coordinate for the appropriate
 * dimension will stay empty containing <code>null</code> to denote the fact
 * that it is undefined.</p>
 *
 * <p>ContextPoints bring together {@link ContextCoordinates} and {@link Information}
 * thus producing knowledge.</p>
 *
 * @see net.sharkfw.knowledgeBase.ContextCoordinates
 * @see net.sharkfw.knowledgeBase.Information
 * 
 * @author thsc
 * @author mfi
 */
public interface ContextPoint extends SystemPropertyHolder {
    /**
   * Creates a new empty information.
   *
   * @return An instance of Information which is empty
   */
  public Information addInformation();
  
  /**
   * Adds an existing information reference to that context point.
   * Handle with care: It is not clear where this information belongs,
   * if it is persistent etc.
   * 
   * @param source 
   */
  public void addInformation(Information source);

  /**
   * Creates a new information with content read from the stream.
   * Reads <code>len</code> bytes from <code>is</code> after initial creation
   * 
   * @param is {@link java.io.InputStream} to read from.
   * @param len The number of bytes to read.
   */
  public Information addInformation(InputStream is, long len);

  /**
   * Creates a new information. Uses the <code>content</code> as content
   * for the Information object.
   *
   * @param content An array of bytes containing arbitrary data
   * @return An instance of Information containing <code>content</code> as content.
   */
  public Information addInformation(byte[] content);

  /**
   * Creates a new information. Saves the string as content.
   * This is convenience method, as often the user/programmer just wants
   * to store Strings inside an information.
   *
   * @param content A String to be used as content.
   * @return An instance of Information containing <code>content</code> as content.
   */
  public Information addInformation(String content);

  /**
   * Generate an {@link Enumeration} of all {@link Information} on this <code>ContextPoint</code>.
   * @return An {@link Enumeration} containing all {@link Information} attached to this <code>ContextPoint</code>
   */
  public Enumeration<Information> enumInformation();
  
  /**
   * Returns a information with a given name - Note: names are not unique.
   * This methode can return an empty iterator, a single information or an arbitrary
   * number of information.
   * 
   * @param name
   * @return 
   */
  public Iterator<Information> getInformation(String name);
  
  /**
   * Return information with no name
   * @return 
   */
  public Iterator<Information> getInformation();

  /**
   * Remove <code>toDelete</code> {@link Information} from this contextpoint.
   * @param toDelete The information that shall be deleted
   */
  public void removeInformation(Information toDelete);

  /**
   * Return the the {@link ContextCoordinates} of this ContextPoint.
   * @return ContextCoordinates defining the position of this context point inside the context space.
   */
  public ContextCoordinates getContextCoordinates();

  public void setContextCoordinates(ContextCoordinates cc);

  /**
   * Return the number of {@link Information} objects attached to this ContextPoint.
   * 
   * @return The number of attached Information objects.
   */
  public int getNumberInformation();

    /**
     * Set the listener for this ContextPoint.
     * Every ContextPoint usually has one listener only - the SharkKB.
     *
     * @param cpl The ContextPointListener to be notified of changes.
     */
    public void setListener(ContextPointListener cpl);

    /**
     * Remove the listener for this ContextPoint.
     * Calling this method resets the reference to the ContextPointListener to <code>null</code>.
     */
    public void removeListener();

    // End of left-over methods
    // ===========================================================================
}
