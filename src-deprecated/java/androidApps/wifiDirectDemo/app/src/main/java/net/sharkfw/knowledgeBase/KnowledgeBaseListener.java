package net.sharkfw.knowledgeBase;

/**
 * Implementations of this interface receive updates once one of the following
 * events occur.
 * 
 * @author thsc
 * @author mfi
 */
public interface KnowledgeBaseListener extends KnowledgeListener {


  /**
   * Called when a topic is created.
   * @param tag The topic tag that has been created.
   */
  public void topicAdded(SemanticTag tag);

  /**
   * Called when a peer is created.
   * @param tag The peer tag that has been created.
   */
  public void peerAdded(PeerSemanticTag tag);

  /**
   * Called when a location is created.
   * @param tag The geo tag that has been created.
   */
  public void locationAdded(SpatialSemanticTag location);

  /**
   * Called when a timespan is created.
   * @param tag The time tag that has been created.
   */
  public void timespanAdded(TimeSemanticTag time);

  /**
   * Called when a topic is removed.
   * @param tag The topic tag that has been removed.
   */
  public void topicRemoved(SemanticTag tag);

  /**
   * Called when a peer is removed.
   * @param tag The peer tag that has been removed.
   */
  public void peerRemoved(PeerSemanticTag tag);

  /**
   * Called when a location is removed.
   * @param tag The geo tag that has been removed.
   */
  public void locationRemoved(SpatialSemanticTag tag);

  /**
   * Called when a timespan is removed.
   * @param tag The time tag that has been removed.
   */
  public void timespanRemoved(TimeSemanticTag tag);
  /**
   * Is called whenever a new predicate has been created between two AssociatedSemanticTags.
   * 
   * @param subject The tag making the statement.
   * @param type A string describing the kind of relationship.
   * @param object The tag a statement is made about.
   */
  public void predicateCreated(SNSemanticTag subject, String type, SNSemanticTag object);
  
  /**
   * Is called whenever a new predicate between two AssociatedSemanticTags has been removed.
   * 
   * @param subject The tag making the statement.
   * @param type A string describing the kind of relationship.
   * @param object The tag a statement is made about.
   */
  public void predicateRemoved(SNSemanticTag subject, String type, SNSemanticTag object);
}
