package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;

/**
 * <p>An interface defining the interest.</p>
 *
 * <p>An interest in shark is used to describe the context in which a peer is
 * willing to exchange information. The interest thus is a special {@link ContextSpace}.
 * The {@link SemanicTag}s on the different dimensions of the interest define this
 * context.</p>
 *
 * ContextSpace is a general concept whereas interest makes more specific 
 * definitions about the communication context. An interest defines seven aspects
 * (constraints) in which a peer is willing to exchange information.
 * 
 * Each aspect can be set. If set, communication takes place if interest
 * have mutual interest. A mutual interest can be calculated by contextualising
 * a interest with another on (see @link CSAlgebra). 
 * 
 * Unset aspects are interpreted as no constraints. Anything is allowed.
 * Thus, the most general interest has no aspect set, meaning: any get-methode return
 * nulls. A peer is interested in virtually everything.
 * 
 * Arbitrary combinations are possible. Application can decide just to define 
 * topics but no locations etc. That's quite similiar to Wikipedia. Others could
 * decide to define topics and peer which looks like a social network application.
 * 
 * Using location aspect leads directly to a location based system. Using all
 * aspects leads to a system that combines social network, wikipedia and 
 * location based systems. SharkNet is such a system which is a demonstration
 * of those Shark features.
 * 
 * Interest inherits from AnchorSet. The major difference is in usage of dimensions.
 * AnchorSets only use semantic tags but not their relations. AnchorSets are
 * usually contextualization parameter. An interest is transmitted to other peers.
 * The set in each dimension are not only an enumeration of tags but also
 * their relations - in a taxonomy or a sementic network.
 * 
 * @author mfi, thsc
 */
public interface ASIPInterest extends ASIPSpace {

  /**
   * Topics are stored in semantic tag set. Most implementations will choose
   * to offer a taxonomy and semantic network implementation beside the plain
   * set.
   * 
   * Derived classes of STset can be used of course. It can be check with
   * instanceof if the STSet is actually a Taxonomy or a SemanticNet.
     * @param topics
   */
  public void setTopics(STSet topics);
  
  /**
   * Set types [add more comments]
   * @param types 
   */
  public void setTypes(STSet types);

  /**
   * Return the direction of this interest.
   * 
   * ContextSpace defines each dimension to be a set of semantic tags.
   * Yes, there is also an implementation thats wraps the direction into
   * a semantic tag. Nevertheless, there are just 4 combinations which are
   * defined by constants in this interface
   * 
   * DIRECTION_IN - peer want's to send information
   * DIRECTION_OUT - peer want's to receive information
   * DIRECTION_INOUT - peer want's to send and retrieve information
   * DIRECTION_NOTHING - peer doesn't want to exchange these information at all
   *
     * @param direction
   * @see net.sharkfw.knowledgeBase.ContextSpace
   */
  public void setDirection(int direction);

  /**
   * Return the originator dimension of this interest.
   * 
   * This peer is the one who can also sign the message.
   * It isn't necessarily part of the peer tag. 
   * 
     * @param originator
   */
  public void setSender(PeerSemanticTag originator);

  /**
   * Return the originator dimension of this interest.
   * 
   * This peer is the one who can also sign the message.
   * It isn't necessarily part of the peer tag. 
   * 
     * @param senders
   */
  public void setSenders(PeerSTSet senders);
  
  public PeerSTSet getSenders();

  /**
   * Return the remotepeer dimension of this interest
   *
     * @param remotePeers
   */
  public void setReceivers(PeerSTSet remotePeers);

  /**
     * @param peers 
   */
  public void setApprovers(PeerSTSet peers);

  /**
   * Return the time dimension of this interest
   *
     * @param times
   */
  public void setTimes(TimeSTSet times);

  /**
   * Return the location dimension of this interest
   *
     * @param location
   */
  public void setLocations(SpatialSTSet location);
  
}

