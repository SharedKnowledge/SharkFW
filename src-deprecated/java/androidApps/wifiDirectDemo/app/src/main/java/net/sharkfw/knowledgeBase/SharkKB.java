package net.sharkfw.knowledgeBase;

import java.util.Enumeration;
import java.util.Iterator;
import net.sharkfw.knowledgeBase.geom.SharkGeometry;

/**
 *
 * <p>The Shark knowledgebase.</p>
 *
 * <p>The primary functions of the SharkKB are: <br />
 * <ul>
 * <li>Management of {@link SemanticTag}s</li>
 * <li>Management of {@link ContextPoint}s</li>
 * <li>Creation of standard {@link Interest}s</li>
 * </ul>
 * </p>
 * 
 * <p><strong>Management of semantic tags</strong> <br />
 * The shark kb manages all semantic tags known to the local peer.
 * Semantic tags can be divided into different types (peers, locations, timespans, topics).
 * The SharkKB offers methods for creating instances of all of these types, sometimes,
 * in more than one way to offer a convienient interface. <br />
 * Peers, locations, and timespans can be used as topics as well (a place i.e. can be something to talk about),
 * while topics (i.e. 'Gardenwork'), are not fit to be used as anything but topics. Thus, whenever a new peer, location
 * or timespan is created, the created tag will automatically become part of the topics.
 * </p>
 * 
 * <p><strong>Management of context points</strong> <br />
 * ContextPoints tie {@link Information} and {@link ContextCoordinates} together.
 * They can be created by providing a set of coordinates, and offer ways to
 * manage information.
 * </p>
 * 
 * <p><strong>Creation of standard interests</strong> <br />
 * Standard interests are used in {@link net.sharkfw.peer.KnowledgePort} to define the details
 * of a communication with other peers. They define about what, when and where an
 * exchange may take place. The creation of these standard interests is handled here.
 * By providing a number of anchor points (in an {@link AnchorSet}) and a number of {@link FragmentationParameter}s,
 * the interest creation extracts parts of the local stsets and puts them into
 * a new interest.
 * </p>
 * 
 *
 * @see net.sharkfw.knowledgeBase.ContextPoint
 * @see net.sharkfw.knowledgeBase.STSet
 * 
 * @author mfi, thsc
 */
public interface SharkKB extends SharkVocabulary, SystemPropertyHolder, STSetListener, InterestStorage {
    
    public void setOwner(PeerSemanticTag owner);
    
    @Override
    public PeerSemanticTag getOwner();
  /**
   * Return the ContextPoint at the given coordinates. Exact match.
   *
   * @param coordinates ContextCoordinates describing a certain point in the ContextSpace.
   * @return The ContextPoint that can be found at these coordinates, or null if no ContextPoint could be found.
   * @throws SharkKBException Needed?!
   */
  public ContextPoint getContextPoint(ContextCoordinates coordinates) throws SharkKBException;
  
  /**
   * Each dimension can be null. It means unknown and will match to each other
   * concept in this dimension. 
   * 
   * <br/> Bob want's to talk with anybody. He can use null as parameter for
   * remote peer. Each other interest will match in this dimension and will
   * try to communicate with Bob. 
   * 
   * It is the same for any other dimension.
   * 
   * create fresh coordinates
   * @param topic
   * @param peer
   * @param remotepeer
   * @param originator
   * @param time
   * @param location
   * @param direction
   * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException 
   */
    public ContextCoordinates createContextCoordinates(SemanticTag topic,
            PeerSemanticTag originator,
            PeerSemanticTag peer,
            PeerSemanticTag remotepeer,
            TimeSemanticTag time,
            SpatialSemanticTag location,
            int direction) throws SharkKBException;
  
  /**
   * Create a new ContextPoint at the given coordinates.
   *
   * @param coordinates ContextCoordinates describing a certain point in the ContextSpace.
   * @return If a ContextPoint was already present at the given coordinates it will be returned, if not a new ContextPoint will be created and returned
   * @throws SharkKBException
   */
  public ContextPoint createContextPoint(ContextCoordinates coordinates) 
          throws SharkKBException;
  
    /**
     * Create a new (empty) knowledge object. The actual knowledge base will
     * be the context of this knowledge.
     * 
     * <b>Note: This knowledge object does NOT contain KBs context points.
     * It is just an empty knowledge object but with the kb as background.</b>
     * 
     * Context points of this knowledge base can be enumerated with getContextPoints()
     * and similiar methods.
     * @return 
     */
    public Knowledge createKnowledge();

  /**
   * Remove the ContextPoint at the given coordinates.
   *
   * @param coordinates ContextCoordinates describing a certain point in the ContextSpace.
   * @throws SharkKBException
   */
  public void removeContextPoint(ContextCoordinates coordinates) throws SharkKBException;

  /**
   * Return (copies) of all ContextPoints, which are covered by the 
   * ContextSpace <code>cs</code>. Note: Just the context points are copied, information
   * not.
   * 
   * @param cs A ContextSpace
   * @return An Enumeration of ContextPoints which are covered by the ContextSpace <code>cs</code>
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public Enumeration<ContextPoint> getContextPoints(SharkCS cs) throws SharkKBException;

  /**
   * Return (copies) of all ContextPoints, which are covered by the 
   * ContextSpace <code>cs</code>. Note: Just the context points are copied, information
   * not.
   * 
   * @param cs
   * @return
   * @throws SharkKBException 
   */
  public Iterator<ContextPoint> contextPoints(SharkCS cs) throws SharkKBException;

  public Enumeration<ContextPoint> getContextPoints(SharkCS cs, boolean matchAny) throws SharkKBException;
  
  /**
   * 
   * @param cs
   * @param matchAny true: any tags in each dimensions are used as joker signs: There
   * are no contraints on the dimension in which the st set in any. If false: 
   * any tag is used as each other tag and it is looked for an exact match
   * @return
   * @throws SharkKBException 
   */
  public Iterator<ContextPoint> contextPoints(SharkCS cs, boolean matchAny) throws SharkKBException;
  
  /**
   * Returns enumeration of all context points. This actually is the same as
   * getContextPoints with an context space covering anything - which is technically 
   * a null reference. 
   * 
   * Use this methode very carefully. It produces a complete knowledge base dump.
   * This can be a lot.
   * 
   * @return
   * @throws SharkKBException 
   */
  public Enumeration<ContextPoint> getAllContextPoints() throws SharkKBException;

  /**
   * Register a new listener for changes on this SharkKB
   *
   * @param kbl The listener to be added.
   */
  public void addListener(KnowledgeBaseListener kbl);

  /**
   * Remove a listener for changes on this SharkKB.
   *
   * @param kbl The listener to be removed.
   */
  public void removeListener(KnowledgeBaseListener kbl);

  /**
   * Set the standard FragmentationParameter for this SharkKB.
   * The standard fp will be used in interest creation if no fp is provided otherwise.
   *
   * @param fps A FragmentationParameter array with ContextSpace.MAXDIMENSION fields
   */
  public void setStandardFPSet(FragmentationParameter fps[]);

  /**
   * Return the standard FragmentationParameter set for this SharkKB.
   *
   * @return A FragmentationParameter array with ContextSpace.MAXDIMENSION fields
   */
  public FragmentationParameter[] getStandardFPSet();

    public Interest createInterest() throws SharkKBException;
    
    /**
     * Create an interest by copying coordinate settings
     * @param cc
     * @return 
     * @throws net.sharkfw.knowledgeBase.SharkKBException
     */
    public Interest createInterest(ContextCoordinates cc) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param sis
     * @return
     * @throws SharkKBException 
     */
    public SemanticTag createSemanticTag(String name, String[] sis) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param si
     * @return
     * @throws SharkKBException 
     */
    public SemanticTag createSemanticTag(String name, String si) throws SharkKBException;

    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param sis
     * @param addresses
     * @return
     * @throws SharkKBException 
     */
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param si
     * @param address
     * @return
     * @throws SharkKBException 
     */
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String address) throws SharkKBException;    
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param sis
     * @param address
     * @return
     * @throws SharkKBException 
     */
    public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String address) throws SharkKBException;  
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param si
     * @param addresses
     * @return
     * @throws SharkKBException 
     */
    public PeerSemanticTag createPeerSemanticTag(String name, String si, String[] addresses) throws SharkKBException;

    /**
     * @deprecated please add a geometry if it is really a spatial (!) semantic tag
     * @param name
     * @param sis
     * @return
     * @throws SharkKBException 
     */
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * @param name
     * @param sis
     * @param geom
     * @return
     * @throws SharkKBException 
     */
    public SpatialSemanticTag createSpatialSemanticTag(String name, String[] sis, SharkGeometry geom) throws SharkKBException;
     
    /**
     * @deprecated use method semantic tag set instead
     * @param from
     * @param duration
     * @return
     * @throws SharkKBException 
     */
    public TimeSemanticTag createTimeSemanticTag(long from, long duration) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * Delete tag in any dimension - if it can be found
     * @param sis
     * @throws SharkKBException 
     */
    public void removeSemanticTag(String[] sis) throws SharkKBException;
    
    /**
     * @deprecated use method semantic tag set instead
     * Delete tag in any dimension - if it can be found
     * @param st
     * @throws SharkKBException 
     */
    public void removeSemanticTag(SemanticTag st) throws SharkKBException;
    
    /**
     * TODO: shouldn't be a public method in interface
     */
    public void persist();
}
