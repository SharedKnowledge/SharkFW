package net.sharkfw.knowledgeBase;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Information can be annotated with semantic tags. It plural, e.g.
 * information can have multiple topics to which they apply.
 * 
 * They can have multiple approvers who agree those topics describe
 * or classify information correctly. 
 * 
 * An information space has only two parts:
 * <ul>
 * <li>A context space describing the <i>geometry</i> of the space</li>
 * <li>A list of information</li>
 * </ul>
 * @author thsc
 */
public interface InformationSpace {
    LASP_CS getContextSpace() throws SharkKBException;
    void setContextSpace(LASP_CS space) throws SharkKBException;
    
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
   * Return the number of {@link Information} objects attached to this ContextPoint.
   * 
   * @return The number of attached Information objects.
   */
  public int getNumberInformation();
}
