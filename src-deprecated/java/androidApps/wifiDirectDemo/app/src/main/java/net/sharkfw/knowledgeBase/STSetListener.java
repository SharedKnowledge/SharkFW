package net.sharkfw.knowledgeBase;

/**
 * Listen for changes on a stand-alone STSet.
 * 
 * @author mfi
 */
public interface STSetListener {

  /**
   * A SemanticTag has been created.
   * @param tag The created tag.
   * @param stset The stset in which the tag was created.
   */
  public void semanticTagCreated(SemanticTag tag, STSet stset);

  /**
   * A SemanticTag has been removed from a stset.
   * @param tag The removed tag.
   * @param stset The stset from which it has been removed.
   */
  public void semanticTagRemoved(SemanticTag tag, STSet stset);
}
