package net.sharkfw.knowledgeBase;

/**
 * A listener interface to listen for changes on a given ContextPoint.
 * Events usually cover the adding and removing of Information on a ContextPoint.
 * 
 * @author mfi
 */
public interface ContextPointListener {

  /**
   * The Information info has been added to the ContextPoint
   * @param info The information that was added
   * @param cp The ContextPoint to which this Information is attached
   */
  public void addedInformation(Information info, ContextPoint cp);

  /**
   * An Information has been removed from a ContextPoint.
   *
   * @param info The Information that has been removed
   * @param cp The ContextPoint to which this Information was attached
   */
  public void removedInformation(Information info, ContextPoint cp);

}
