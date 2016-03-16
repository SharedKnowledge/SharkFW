package net.sharkfw.knowledgeBase;

/**
 * <p>This class unites the characteristics of a PeerSemanticTag and a HierarchicalSemanticTag,
 * to allow peer tags to be ordered in a taxonomical way.</p>
 *
 * <p>It follows the same priciples, regarding the ordering of tags, as described
 * in {@link Taxonomy} and {@link HierarchicalSemanticTag}. Thus all PeerHierarchicalTags
 * have 0..1 super-tag, and 0..* sub-peers. A way of using this ordering to define
 * groups of peers is described in {@link PeerAssociatedSemanticTag}.</p>
 *
 * <p>This class does not add methods itself.</p>
 * 
 * @author mfi
 */
public interface PeerTXSemanticTag extends TXSemanticTag, PeerSemanticTag {

}
