package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInformationSpace;
import net.sharkfw.asip.ASIPKnowledge;

/**
 * <p>Knowledge is a set of {@link ContextPoint}s including attached information.
 * Knowledge also holds a contextmap to further describe the coordinates
 * of the contextpoints it holds.</p>
 *
 * <p>The contextmap's job is twofold:<br />
 * <ul>
 * <li>Define the {@link SemanticTag}s (of various types) that make up the {@link ContextCoordinates} of the contained {@link ContextPoint}s. </li>
 * <li>Deliver additional information about these tags, i.e. relations to other tags etc. </li>
 * </ul>
 * </p>
 *
 * @see net.sharkfw.knowledgeBase.ContextPoint
 * @see net.sharkfw.knowledgeBase.ContextSpace
 * 
 * @author thsc
 * @author mfi
 */
public interface Knowledge extends ASIPKnowledge {

  /**
   * Add a ContextPoint to the knowledge object
   *
   * @see net.sharkfw.knowledgeBase.ContextPoint
   *
   * @param cp The ContextPoint to be added
   * @deprecated 
   */
  public void addContextPoint(ContextPoint cp);
  
  public void addInformationPoint(InformationPoint iPoint) throws SharkKBException;
  
    public Iterator<ASIPInformationSpace> informationSpaces() throws SharkKBException;  

  /**
   * Remove a given Context Point from the knowledge.
   *
   * @see net.sharkfw.knowledgeBase.ContextPoint
   *
   * @param cp The context point to be removed
   * @deprecated 
   */
  public void removeContextPoint(ContextPoint cp);
  
  public void removeInformationPoint(InformationPoint cp) throws SharkKBException;

  /**
   * An enumeration of all contained context points.
   *
   * @return An Enumeration containing all contextpoints from this knowledge object
   * @deprecated 
   */
  public Enumeration <ContextPoint> contextPoints();
  
//  @Override
//  Iterator<InformationPoint> informationPoints() throws SharkKBException;

  /**
   * Return a ContextSapce containing Tag for the different coordinates of the
   * contained ContextPoints plus (optional) background information for these tags.
   * 
   * Note: Method can return null. Context map can also have empty dimensions.
   * 
   *
   * @see net.sharkfw.knowledgeBase.ContextSpace
   *
   * @return A ContextSpace with the above characteristics
   */

    /**
     * Return a ContextSapce containing Tag for the different coordinates of the
 contained ContextPoints plus (optional) background information for these tags.Note: Method can return null. Context map can also have empty dimensions.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     * @see net.sharkfw.knowledgeBase.ContextSpace
     * @return A ContextSpace with the above characteristics
     */
    @Override
    public SharkVocabulary getVocabulary();

  /**
   * The number of context points contained in this knowledge object
   *
   * @return An int value representing the number of contextpoints in this knowledge object
   * @deprecated 
   */
  public int getNumberOfContextPoints();
  
  public int getNumberOfInformationPoints() throws SharkKBException;

  // ===========================================================================
  // These methods are a left-over from the internal Knowledge interface

    /**
     * Returns the contextpoint at the index <code>i</code>
     *
     * @see net.sharkfw.knowledgeBase.ContextPoint
     *
     * @param i An integer value to describe which contextpoint shall be returned
     * @return A contextpoint
     * @deprecated 
     */
    public ContextPoint getCP(int i);
    
    public InformationPoint getInformationPoint(int i) throws SharkKBException;
    
    public void addListener(KnowledgeListener kListener);
    
    public void removeListener(KnowledgeListener kListener);
}
