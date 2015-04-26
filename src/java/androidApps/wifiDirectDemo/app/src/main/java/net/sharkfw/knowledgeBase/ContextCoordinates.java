package net.sharkfw.knowledgeBase;

/**
 * <p>This class is able to define a point in the {@link ContextSpace}.</p>
 *
 * <p>ContextCoordinates define points in the context space, by acting as a
 * 7-value-tuple of {@link SemanticTag}s. Each dimension of the context space
 * is represented through one tag. Thus, if all seven dimensions are set, the
 * coordinate defines a single point in the context space.</p>
 *
 * <p>ContextCoordinates are used in {@link ContextPoint}s to define their place
 * in the context space.</p>
 *
 * <p>Not all coordinates need to be set though. It is possible to create
 * coordinates with i.e. only a topic set and a direction set (dimensions TOPIC and DIRECTION).
 * Such a coordinate would be used for a ContextPoint which holds information
 * on the given topic, which may be exchanged always, everywhere and with everyone
 * (dimensions TIME, LOCATION, REMOTEPEER).</p>
 *
 * <p>The DIRECTION dimension is represented by int values, each denoting either
 * if information for this point shall be received (IN), shall be sent (OUT) or both (INOUT).</p>
 * 
 * @author mfi
 */
public interface ContextCoordinates extends SharkCS {

  /**
   * Return the topic tag for this coordinate. If none is set, return null.
   * @return The topic tag, or null if unset
   */
  public SemanticTag getTopic();

  /**
   * Return the peer tag for this coordinate. If none is set, return null.
   * @return The peer tag, or null if unset
   */
  public PeerSemanticTag getPeer();

  /**
   * Return the remotepeer tag for this coordinate. If none is set, return null.
   * @return The remotepeer tag, or null if unset
   */
  public PeerSemanticTag getRemotePeer();

  /**
   * Return the originator tag for this coordinate. If none is set, return null.
   * @return The originator tag, or null if unset
   */
    @Override
  public PeerSemanticTag getOriginator();

  /**
   * Return the time tag for this coordinate. If none is set, return null.
   * @return The time tag, or null if unset
   */
  public TimeSemanticTag getTime();

  /**
   * Return the location tag for this coordinate. If none is set, return null.
   * @return The location tag, or null if unset
   */
  public SpatialSemanticTag getLocation();

  /**
   * Return the int value representing the direction tag.
   * 
   * @see net.sharkfw.knowledgeBase.ContextSpace
   *
   * @return An int value representing the direction.
   */
  public int getDirection();
}
