package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * <p>A plain STSet simply holds a number of SemanticTags. </p>
 *
 * <p>In plain STSets, the {@ SemanticTag}s have no relation to each other. The set can be
 * changed by adding or removing tags from it. Every STSet also defines
 * a fragmentation method. The fragmentation returns a subset of tags, given
 * certain parameters.</p>
 *
 * <p>Every STSet also offers methods to return a tag by a given SI, or in an {@link Enumeration}.</p>
 * 
 * @author mfi, thsc
 */
public interface STSet {

    /**
     * Makes copies of each source parameter and stores it in the set.
     * A copy of source in this set is created in other words.
     * 
     * @param tag
     * @return copy of source in this set
     * @throws SharkKBException 
     */
  public SemanticTag merge(SemanticTag tag) throws SharkKBException;
  
  /**
   * Create an semantic tag in that set if no identical tag already exists.
   * An execption is thrown in this case.
   * @param name
   * @param sis 
     * @return  
   * @throws SharkKBException if identical tag already exists
   */
  public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException;

  /**
   * Create an semantic tag in that set if no identical tag already exists.
   * An execption is thrown in this case.
   * @param name 
     * @param si 
     * @return  
   * @throws SharkKBException if identical tag already exists
   */
  public SemanticTag createSemanticTag(String name, String si) throws SharkKBException;
  
  /**
   * Remove a given SemanticTag
   *
   * @param tag The tag to be removed.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public void removeSemanticTag(SemanticTag tag) throws SharkKBException;
  
  /**
   * Semantic tags can be set hidden. This feature can be evaluated during
   * enumerating tags with {@link  tags()}. 
   * 
   * @param hide True: Hidden tags won't be enumerated. False: Hidden property
   * is ignored (default).
   */
  public void setEnumerateHiddenTags(boolean hide);

  /**
   * Return an Enumeration of all SemanticTags in this STSet
   *
   * @return An Enumeration containing all SemanticTags in this STSet.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Enumeration<SemanticTag> tags() throws SharkKBException;
  
  public Iterator<SemanticTag> stTags() throws SharkKBException;

  /**
   * Retrieve a SemanticTag by its SIs. If the <code>si</code> array contains a number
   * of sis of which some are used in different tags in this STSet, the method
   * will return the first hit.
   * 
   * @param si A number of Sis to identify one tag
   * @return The tag, or null if the tag could not be found.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public SemanticTag getSemanticTag(String[] si) throws SharkKBException;

  /**
   * Retrieve a SemanticTag by its SIs. If the <code>si</code> array contains a number
   * of sis of which some are used in different tags in this STSet, the method
   * will return the first hit.
   * 
   * @param si a single Sis to identify one tag
   * @return The tag, or null if the tag could not be found.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public SemanticTag getSemanticTag(String si) throws SharkKBException;
  
  /**
   * Produces set of semantic tags which names fit to the given pattern. 
   * An interator of that set is returned. Tags are not copied! The iterator
   * returns references to the objects in the actual ST set.
   * 
   * @param pattern A pattern to which the tag names are compared
   * @return Itertor of fitting tags
   * @throws SharkKBException 
   */
  public Iterator<SemanticTag> getSemanticTagByName(String pattern) throws SharkKBException;

  /**
   * The most simple fragmentation. Check if the given tag exists and return
   * a STSet with that tag in it.
   *
   * @param anchor The tag to be retrieved.
   * @return A STSet containing the tag, if it could be found, or null if an error occurred.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public STSet fragment(SemanticTag anchor) throws SharkKBException;
  
  /**
   * Each set has build fragmentation parameter. They can be retrieved.
   * @return default fragmentation parameter
   */
  public FragmentationParameter getDefaultFP();
  
  /**
   * Each set has build fragmentation parameter. They can be set with this 
   * methode.
     * @param fp
   */
  public void setDefaultFP(FragmentationParameter fp);
  
  /**
   * A fragment is made up of copies of semantic tags that a found in
   * this set and which match to the given interest and/or can be found
   * with the given fragmentation parameter.
   * @param anchor
   * @param fp
   * @return
   * @throws SharkKBException 
   */
  public STSet fragment(SemanticTag anchor, FragmentationParameter fp) throws SharkKBException;

  /**
   * Contextualization is the most generic method of semantic tag sets.
   * Read the manual for detailed descriptions and mathematical background.
   * 
   * For application programmers, it is a convenient methode to extract 
   * 'fitting' and related tags
   *
   * @param anchorSet an enumeration of semantic tags which shall be searched
   * @param fp parameter describing how the search shall be performed. It has
   * different results whether ii is used on a plain set, taxonomy or semantic net.
   * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
   */
  public STSet contextualize(Enumeration<SemanticTag> anchorSet, FragmentationParameter fp) throws SharkKBException;
  
  /**
   * Convient version of the other contextualization method. Default fragmentation
   * parameter a used. Those defaults can be choose arbitrarely by implementations.
   * Refer to their manuals.
   * 
   * @param anchorSet context
   * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
   */
  public STSet contextualize(Enumeration<SemanticTag> anchorSet) throws SharkKBException;

  public STSet contextualize(STSet context, FragmentationParameter fp) throws SharkKBException;

  public STSet contextualize(STSet context) throws SharkKBException;
  
    /**
    * Merging a STSet into this STSet. Copy all tags which are yet unknown to
    * this STSet, and copy their properties as well.
    *
    * @param stSet The STSet to be merged into this.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
    */
    public void merge(STSet stSet) throws SharkKBException;
  
    /**
     * Set a listener to listen for changes in this stset.
     * Each STSet can have exactly one listener (usually the SharkKB).
     *
     * @param listen The listener to be notified of changes.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void addListener(STSetListener listen);

    /**
     * Remove a listener.
     * Will set the reference to the listener to <code>null</code>.
     * @param listener
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public void removeListener(STSetListener listener) throws SharkKBException;
    
    /**
     * Tag set is empty or not.
     * @return 
     */
    public boolean isEmpty();
    
    /**
     * 
     * @return number of tags in this set
     */
    public int size();
}
