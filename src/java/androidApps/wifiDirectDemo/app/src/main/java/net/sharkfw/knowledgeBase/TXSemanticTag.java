package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * <p>Hierarchical semantic tags use a simple taxonomical structure to order.</p>
 *
 * <p>Hierarchical tags are ordered in sub/super relations. Hierarchical tags,
 * allow only this one association type. Thus there is only one method to move
 * inside a taxonomy, where <code>move()</code> is used to move a tag from one position
 * in a tree to a different position. Tags with no super-tags, are considered to
 * be <em>root-tags</em>. Tags with no sub-tags are considered <em>leaves</em>.</p>
 *
 * <p>In this type only one super-tag is allowed per tag. The root tag has no super tag.
 * This is represented by the <code>null</code> value.</p>
 *
 * <p>For further information on how to use associations between tags please see {@link AssociatedSemanticTag}.</p>
 *
 * <p> When moving a tag from one position to another, please be aware that all sub-tags
 * of the moved tag are still connected to it, and thus are moved along.</p>
 *
 * <p> When removing a tag from a taxonomy, please be aware that this might
 * break a taxonomical relation if the tag had a super tag and one or more sub-tags.
 * The removed tag will be the <em>missing link</em> in the taxonomical relation.</p>
 * 
 * @author thsc
 * @author mfi
 */
public interface TXSemanticTag extends SemanticTag {

  /**
   * Return all tags connected to this tag by the means of a sub relation.
   * @return An <code>Enumeration</code> of <code>HierarchicalSemanticTag</code>s
   * which are connected to this tag though a subclass relation.
   * 
   * Note: The enumeration type is not a typo. Yes, in principle, a sub tag
   * of a taxonomy tag can be of an arbitrary semantic tag type.
   */
    public Enumeration<SemanticTag> subTags();

    /**
     * Returns the super-tag or null, if this is the root tag
     * 
     * Yes, a super concept can be - in principle - of an sub type of SemanticTag.
     * 
     * @return concept or null if it doesn't exist
     */
    public TXSemanticTag getSuperTag();

    public Enumeration<TXSemanticTag> getSubTags();

    /**
     * Move this tag (and all trailing sub-tags) to the new super-tag <code>supertag</code>.
     * @param supertag The new super-tags
     */
    public void move(TXSemanticTag supertag);
    
    /**
     * Makes a merge as described in SemanticTag. Furthermore, all subRelations will
     * be copied. If this tag is a root tag and toMerge is not - this tag adopts
     * the superTag of toMerge. An already existing superTag of this wont be
     * changed, though.
     * 
     * @param toMerge 
     */
    public void merge(TXSemanticTag toMerge);

}
