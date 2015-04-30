package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * <p>This class represents a tag that is able of having associations to other tags.</p>
 *
 * <p>AssociatedSemanticTags are able to have relations of freely definable types
 * to other AssociatedSemanticTags. The types of the associations are <code>String</code>.
 * Thus application can create their very own vocabulary of association types</p>
 *
 * <p>An association is called a statement, just like in rdf. A statement consists
 * of a <em>subject</em> (the tag making the statement), a <em>predicate type</em>
 * (the string defining the association) and an <em>object</em> (the tag that
 * is referred to).</p>
 *
 *
 * <p><strong>Direction of statements</strong><br />
 * All statements are one-way associations. Meaning that <em>A-predicate->B</em> does not
 * imply <em>B-predicate->A</em>. The only <strong>exception</strong> to this rule is the Sub/Super-relation
 * which is sensibly bidirectional.</p>
 *
 * <p><strong>Example:</strong>
 * <p><code>assocTagA.setPredicate("belongs-to", assocTagB);</code> <br />
 * is a one-way-statement, as A may belong B does not imply, that B also belongs to A
 * or that any other sensible association type can automatically be guessed.</p>
 * <p><code>assocTagA.setPredicate(ROAssociatedSemanticTag.SUPER, assocTagB);</code> <br />
 * However also executes: <br />
 * <code>assocTagB.setPredicate(ROAssociatedSemanticTag.SUB, assocTagA); </code><br />
 * because the nature of the sub/super relation is bidirectional. Thus the
 * proper "backwards"-association type can be guessed.</p></p>
 *
 * <p>All AssociatedSemanticTags are implicitly HierarchicalSemanticTags as they
 * feature all functions of HierarchicalSemanticTags, but offer even more functions
 * as custom predicate types can be applied.</p>
 *
 * @author thsc
 * @author mfi
 */
public interface SNSemanticTag extends SemanticTag {
    
    /**
     * Return an <code>Enumeration</code> of strings. Each string representing
     * a predicate type in which this tag is involved as source
     * 
     * @return An <code>Enumeration</code> of strings containing predicate types
     */
    public Enumeration<String> predicateNames();

    /**
     * Return an <code>Enumeration</code> of strings. Each string represents
     * a predicate type in which this tag is involved as target.
     * 
     * Note: This is a kind of reverse reference. Predicates are seen as
     * directed arcs. A tags points to another. It is the source in this case. 
     * The other is the target.
     * 
     * This methode return predicate names in which this tags plays the role of
     * the target.
     * 
     * @return An <code>Enumeration</code> of strings containing predicate types
     */
    public Enumeration<String> targetPredicateNames();

    /**
     * Return an <code>Enumeration</code> of <code>SNSemanticTag</code>s
     * which are connected to this tag by the given <code>type</code>
     * 
     * @param type The predicate type as string
     * @return An <code>Enumeration</code> of <code>AssociatedSemanticTag</code>s.
     */
    public Enumeration <SNSemanticTag> targetTags(String predicateName);

    /**
     * 
     * @param predicateName
     * @return Enumeration of tags which are source of the predicate described by
     * <code>predicateName</code> and in which this is target.
     */
    public Enumeration <SNSemanticTag> sourceTags(String predicateName);

    /**
     * Create an association between this tag and <code>tag</code> using <code>type</code>
     * as the predicate's type.
     * 
     * @param type The type of the predicate as string.
     * @param target The tag on the other side of this predicate.
     */
    void setPredicate(String type, SNSemanticTag target);

    /**
     * Remove the predicate as identified by <code>tag</code> and <code>type</code>.
     * 
     * @param type String representing the type of the predicate to remove.
     * @param target <code>AssociatedSemanticTag</code> the other end of the statement.
     */
    void removePredicate(String type, SNSemanticTag target);
    
    /**
     * Merges (copy of a ) tag into this. It uses merging of Semantic Tag and also
     * copies predicates in which toMerge is source or target.
     * @param toMerge 
     */
    void merge(SNSemanticTag toMerge);
}
