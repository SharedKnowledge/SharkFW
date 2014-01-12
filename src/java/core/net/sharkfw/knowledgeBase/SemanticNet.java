package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * <p>Organizes tags in a net-like way. Tags can be associated with other tags
 * to represent relations between them.</p>
 *
 * <p>Each relation can be understood as a statement in which a tag (the <em>object</em>)
 * makes a statement about another tag (the <em>subject</em>). The type of the statement
 * is called a <em>predicate</em> and is represented by a String. The tags represent
 * nodes in a graph, while the predicates are the typed edges of a graph.</p>
 *
 * <p>SemanticNet contains {@link SNSemanticTag}s.</p>
 *
 * <p>The fragmentation of SemanticNets returns a subset of the original SemanticNet
 * containing AssociatedSemanticTags the relations between them. When merging
 * SemanticNets the relations of the merged SemanticNet are preservered.</p>
 *
 * <p><strong>Fragmentation</strong> <br />
 * Fragmentation on SemanticNets starts from a given <em>anchor</em> point. The
 * fragmentation requires some configuration information which are stored inside
 * a <em>FragmentationParameter</em> (fp). The fp contains information about
 * the allowed and forbidden predicate types to follow, and a maximum number
 * of hops to make starting from the anchor.<br />
 * The fragmentation starts at the anchor, then follows all allowed predicate types
 * , increments its depth counter by 1 and adds all tags found, along with the
 * associations that it followed. It repeats this until the maximum depth is reached.
 * </p>
 * 
 * @author mfi,thsc
 */
public interface SemanticNet extends STSet {

  /**
   * The constant for defining a subclass relation to another tag.
   * Usage A -sub-> B = B is a subclass of A
   */
   public static final String SUBTAG = "sub";

  /**
   * The constant fpr defining a superclass relation to another tag
   * Usage A -super-> B = B is a superclass of A
   */
    public static final String SUPERTAG = "super";

    public STSet asSTSet();
    
    @Override
    public SNSemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException;
    
    @Override
    public SNSemanticTag createSemanticTag(String name, String si) throws SharkKBException;

    /**
    * Remove an AssociatedSemanticTag from this SemanticNet.
    * If tag does not exist inside this SemanticNet, nothing will happen.
    * 
    * All precicates that uses this tag are removed.
    *
    * @param tag The tag to be removed.
    */
    public void removeSemanticTag(SNSemanticTag tag) throws SharkKBException;
    
    /**
    * Return an AssociatedSemanticTag by its SI. If the String array contains
    * SI which would belong to different tags in this SemanticNet the first
    * match is returned.
    *
    * @param si A number of String to be searched for
    * @return An AssociatedSemanticTag with at least one matching SI. If none can be found, null is returned.
    */
    @Override
    public SNSemanticTag getSemanticTag(String[] sis) throws SharkKBException;
    
    @Override
    public SNSemanticTag getSemanticTag(String si) throws SharkKBException;

    /**
     * Sets a predicate between two tags. A semantic net is actually and not
     * suprisingly a network of tags and connections between them. These 
     * connections are called predicates. Thus, setting a predicate creates
     * an connection from source tag to target tag. The connection can and should
     * also be named by a string denoting the type. Two standard types are already
     * defined in this class - super and sub relation between tags. The rest is up
     * to developers imagination.
     * @param source Source of the predicate
     * @param target Predicate target
     * @param type Predicate type
     */
    public void setPredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException;

    public void removePredicate(SNSemanticTag source, SNSemanticTag target, String type) throws SharkKBException;

    /**
    * Return a fragment of this SemanticNet. Fragmentation starts from <code>anchor</code>
    * and follows allowed association types (as described in <code>fp</code>) to
    * a maximum depth of <code>fp.getDepth()</code>. The resulting fragment of this
    * SemanticNet is returned.
    *
    * @param anchor The starting point
    * @param fp Configuration information for the fragmentation process
    * @return A SemanticNet containing the result, or null if an error occurred.
    */
    @Override
    public SemanticNet fragment(SemanticTag anchor, FragmentationParameter fp) 
                                                    throws SharkKBException;

    @Override
    public SemanticNet fragment(SemanticTag anchor) throws SharkKBException;

    /** Contextualization is the most generic method of semantic tag sets.
    * Read the manual for detailed descriptions and mathematical background.
    * 
    * For application programmers, it is a convenient methode to extract 
    * 'fitting' and related tags
    *
    * @param anchorSet an enumeration of semantic tags which shall be searched
    * @param fp parameter describing how the search shall be performed. It has
    * different results whether ii is used on a plain set, taxonomy or semantic net.
    * @return 
    */
    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet, 
            FragmentationParameter fp) throws SharkKBException;

    @Override
    public SemanticNet contextualize(STSet context, FragmentationParameter fp) 
            throws SharkKBException;

    @Override
    public SemanticNet contextualize(Enumeration<SemanticTag> anchorSet) 
            throws SharkKBException;

    @Override
    public SemanticNet contextualize(STSet context) throws SharkKBException;

    /**
    * Merge a remote SemanticNet into this one. When merging two SemanticNets
    * the local SemanticNet will create all tags from remoteSemanticNet which
    * are yet unknown to it. It will also copy all associations (if applicable).
    *
    * @param remoteSemanticNet The remote SemanticNet that will be merged into this.
    */
    public void merge(SemanticNet remoteSemanticNet) throws SharkKBException;

    /**
     * Makes a copy of the source concept and stores it in the network.
     * It is just a copy of the tag - no predicates are copied. 
     * 
     * Use fragmentation / merging for this task.
     * 
     * @param source
     * @return
     * @throws SharkKBException 
     */
    @Override
    public SNSemanticTag merge(SemanticTag source) throws SharkKBException;
    
    /**
     * just adds this tag. No copies are made etc. Handle with care - better
     * use merge instead.
     * 
     * @param tag 
     */
    public void add(SemanticTag tag) throws SharkKBException;
    
}
