package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author thsc
 */
public interface SharkVocabulary {
    
    public PeerSemanticTag getOwner();
    
    /**
     * Each SharkKB holds Semantic Tag Sets. Thus, it can act as
     * a Shark Context Space. This methode returns this SharkKB 
     * providing the SharkCS interface.
     * 
     * Note:<br/>
     * <ul>
     * <li> Each KB holds only one Peer Semantic Tag set. The Shark CS
     * comprises two, to denote peers and remote peers. Both tags set
     * will be the same.
     * <li> The originator will be the owner which can be set with
     * the SharkKB interface
     * <li> A direction cannot be set on a SharKB. A SharkKB stores 
     * Context Points which can have each direction in their coordinates.
     * The SharkKB will claim to have an IN and OUT interest in general.
     * 
     * <br/>Note: This isn't a security hazard. It just offers semantic tags
     * but no context points, no knowledge, no information. They can only be
     * retrieved by means of extraction. Real direction parameters are checked
     * during extraction.
     * 
     * <li> Semantic Tags can be set hidden. Those tags will not be visible
     * in the interest.
     * </ul>
     * 
     * @return this knowledge base as Shark context space
     */
    public SharkCS asSharkCS();
    
    public Interest asInterest();
    
  public SemanticTag getSemanticTag(String[] sis) throws SharkKBException;
  public SemanticTag getSemanticTag(String si) throws SharkKBException;
  public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException;
  public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException;

  // STSet management
  /**
   * Return the STSets containing all topics.
   * Locations, Times and Peers are considered to be potential topics as well,
   * and thus will be part of this STSet.
   *
   * Changes on this STSet also change the knowledgebase itself!
   * 
   * @return An STSet containing all topics
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public STSet getTopicSTSet() throws SharkKBException;

  /**
   * Return the topics as a SemanticNet.
   *
   * Changes on this STSet also change the knowledgebase itself!
   * FIXME: Time and Geotags are no AssociatedSemanticTags!
   *
   * @return All topics in a SemanticNet.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public SemanticNet getTopicsAsSemanticNet()throws SharkKBException;

  /**
   * Return all topics as a taxonomy.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   * FIXME: Time and Geotags are no AssociatedSemanticTags!
   *
   * @return All topics in a Taxonomy.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Taxonomy getTopicsAsTaxonomy() throws SharkKBException;

  /**
   * Return all Peers as an STSet.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   *
   * @return All peers in a PeerSTSet
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public PeerSTSet getPeerSTSet() throws SharkKBException;

  /**
   * Return all peers as a PeerSemanticNet.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   *
   * @return All peers in a PeerSemanticNet.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public PeerSemanticNet getPeersAsSemanticNet() throws SharkKBException;

  /**
   * Return all peers as a PeerTaxonomy.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   *
   * @return All peers in a PeerTaxonomy.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public PeerTaxonomy getPeersAsTaxonomy() throws SharkKBException;

  /**
   * Return all time tags in a TimeSTSet.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   *
   * @return A TimeSTSet containing all times.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public TimeSTSet getTimeSTSet() throws SharkKBException;

  /**
   * Return all locations in GeoSTSet.
   * 
   * Changes on this STSet also change the knowledgebase itself!
   *
   * @return A GeoSTSet containing all locations.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public SpatialSTSet getSpatialSTSet() throws SharkKBException;

  /**
   * <p>Create a new interest from the given AnchorSet, using the kb's default FP.
   * This method will use the standard fp set.</p>
   *
   * <p>During interest creation each dimension of the interest is filled
   * with values from the kb. The values are STSets. The STSets are determined
   * by running a fragmentation on every STSet of the kb, using the anchor points
   * from <code>as</code> and the standard fp set.</p>
   *
   * @param as The anchorpoints for the new interest.
   * @return An Interest created from the anchorpoints.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Interest contextualize(SharkCS as) throws SharkKBException;

  public Interest contextualize(SharkCS as, FragmentationParameter[] fp) 
          throws SharkKBException;
  
    /**
     * Produces an enumeration of all semantic tags in kb no matter at
     * what dimension they are used.
     * 
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
     */
    public Enumeration<SemanticTag> tags() throws SharkKBException;
    
    /**
     *
     * @return
     * @throws SharkKBException
     */
    public Iterator<SemanticTag> getTags() throws SharkKBException;
}
