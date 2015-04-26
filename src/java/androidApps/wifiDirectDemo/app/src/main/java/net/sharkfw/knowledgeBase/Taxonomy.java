package net.sharkfw.knowledgeBase;

import java.util.Enumeration;

/**
 * STSet managings tags, that can be arranged in a hierarchical way.
 *
 * @author mfi
 */
public interface Taxonomy extends STSet {
    
    /**
    * Add a tag to this Taxonomy - a copy is made
    * @see net.sharkfw.knowledgeBase.STSet#addSemanticTag(net.sharkfw.knowledgeBase.SemanticTag)
    * 
    * @param tag The tag to be added.
    */
    public void merge(TXSemanticTag tag) throws SharkKBException;
    /**
     * Creates a hierarchical order between two tags. First (superTag) becomes
     * a super concept of the second (subTag).
     * @param superTag
     * @param subTag
     * @throws SharkKBException 
     */
    public void move(TXSemanticTag superTag, TXSemanticTag subTag) throws SharkKBException;

    public TXSemanticTag createSemanticTag(TXSemanticTag superTag, String name, String[] sis) throws SharkKBException;

    /**
    * Remove a given tag from this Taxonomy.
    * @see net.sharkfw.knowledgeBase.STSet#removeSemanticTag(net.sharkfw.knowledgeBase.SemanticTag)
    * 
    * @param tag The tag to be removed.
    */
    public void removeSemanticTag(TXSemanticTag tag) throws SharkKBException;
    
    /**
     * Removes a given tag and all subtags
     * @param tag
     * @throws SharkKBException 
     */
    public void removeSubTree(TXSemanticTag tag) throws SharkKBException;

    /**
     * This taxonomy is actually a 'wood' - means: Any tag without a super tag 
     * is assumed to be a root. Thus, there can be an arbitrary number of root tags.
     * 
     * @return Enumeration of root tags or null if taxonomy is empty
     * @throws SharkKBException 
     */
    public Enumeration<TXSemanticTag> rootTags() throws SharkKBException;
    
    /**
     * Checks whether tag is (transitiv) sub tag of root.
     * @param root presumed root tag
     * @param tag semantic tag to investigate
     * @return 
     */
    public boolean isSubTag(TXSemanticTag root, TXSemanticTag tag);
    
    public TXSemanticTag createTXSemanticTag(String name, String[] sis) throws SharkKBException;
    public TXSemanticTag createTXSemanticTag(String name, String si) throws SharkKBException;
    
    @Override
    public TXSemanticTag getSemanticTag(String[] sis) throws SharkKBException;
    @Override
    public TXSemanticTag getSemanticTag(String si) throws SharkKBException;
    
    public Taxonomy contextualizeTaxonomy(STSet context, FragmentationParameter fp) throws SharkKBException;

    public Taxonomy fragmentTaxonomy(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException;
    
}
