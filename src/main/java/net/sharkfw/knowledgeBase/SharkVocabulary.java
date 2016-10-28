package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.ASIPSpace;
import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.asip.ASIPInterest;

/**
 *
 * @author thsc
 */
public interface SharkVocabulary {
    
    public final static String PEERS = "PEERS";
    public final static String TIMES = "TIMES";
    public final static String LOCATIONS = "LOCATIONS";
    public final static String TOPICS = "TOPICS";
    public final static String TYPES = "TYPES";
    
    
    public PeerSemanticTag getOwner();

    public ASIPSpace asASIPSpace() throws SharkKBException;
    public ASIPInterest asASIPInterest() throws SharkKBException;
      
  ///////////////////////////////////////////////////////////////////////////
  //                            STSet management                           //
  ///////////////////////////////////////////////////////////////////////////
  
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
   * @return An STSet containing all types
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public STSet getTypeSTSet() throws SharkKBException;

  /**
   * @return All types in a SemanticNet.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public SemanticNet getTypesAsSemanticNet()throws SharkKBException;

  /**
   * @return All types in a Taxonomy.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Taxonomy getTypesAsTaxonomy() throws SharkKBException;
  
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

  public ASIPInterest contextualize(ASIPSpace as) throws SharkKBException;

  public ASIPInterest contextualize(ASIPSpace as, FPSet fps) 
          throws SharkKBException;
  
}
