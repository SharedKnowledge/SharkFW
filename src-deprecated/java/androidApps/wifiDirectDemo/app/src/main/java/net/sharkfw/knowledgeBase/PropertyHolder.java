package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 *
 * @author thsc
 */
public interface PropertyHolder {
  /**
   * Set a property with the given name to the given value.
   * 
   * @param name property name - must not be start with _shark_
   * Properties namesd _shark* are used internally by the framework
   * @param value
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public void setProperty(String name, String value) throws SharkKBException;

  /**
   * Return a value for the property name <code>name</code>.
   * 
   * @param name The property's name.
   * @return A String value or null, if the property has not been set.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public String getProperty(String name) throws SharkKBException;

  /**
   * Set a name/value pair and determine if the property can be transfered to
   * other peers, if the entity itself is transfered.
   *
   * @param name A String value to determine the name of the property
   * @param value A String value containing the value of the property
   * @param transfer A boolean value to determine if the property is transferable or not.
   * <code>true</code> = transferable, <code>false</code> = not transferable
   */
  public void setProperty(String name, String value, boolean transfer) throws SharkKBException;
  
  /**
   * A property is permanently removed. Nothing happens a no
   * property with that name exists.
   * @param name 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
   */
  public void removeProperty(String name) throws SharkKBException;

  /**
   * Return an <code>Enumeration</code> of all property-names (transferable or not)
   * @return An Enumeration of Strings containing all property-names of this property holder.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Enumeration<String> propertyNames() throws SharkKBException;
  
  /**
   * 
   * @param all if false: property names that are not to be transfered are not part of enumeration
   * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
   */
  public Enumeration<String> propertyNames(boolean all) throws SharkKBException;
}
