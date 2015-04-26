package net.sharkfw.knowledgeBase;

/**
 *
 * @author thsc
 */
public interface KnowledgeListener {
    
  /**
   * Is called whenever a contextpoint has been added to the KB
   *
   * @see net.sharkfw.knowledgeBase.ContextPoint
   * 
   * @param cp The <code>ContextPoint</code> that has been added
   */
  public void contextPointAdded(ContextPoint cp);

  /**
   * Is called whenever a ContextPoint is changed inside the KB
   *
   * @see net.sharkfw.knowledgeBase.ContextPoint
   * 
   * @param cp The ContextPoint which has been changed
   */
  public void cpChanged(ContextPoint cp);

  /**
   * Is called whenever a ContextPoint is removed from the KB
   *
   * @see net.sharkfw.knowledgeBase.ContextPoint
   * 
   * @param cp The ContextPoint which has been removed.
   */
  public void contextPointRemoved(ContextPoint cp);
  
}
