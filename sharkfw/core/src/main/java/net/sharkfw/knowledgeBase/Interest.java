package net.sharkfw.knowledgeBase;

import net.sharkfw.asip.ASIPInterest;
import net.sharkfw.asip.ASIPSpace;

/**
 * <p>An interface defining the kepInterest.</p>
 *
 * <p>An kepInterest in shark is used to describe the context in which a peer is
 * willing to exchange information. The kepInterest thus is a special {@link ContextSpace}.
 * The {@link SemanicTag}s on the different dimensions of the kepInterest define this
 * context.</p>
 *
 * ContextSpace is a general concept whereas kepInterest makes more specific
 * definitions about the communication context. An kepInterest defines seven aspects
 * (constraints) in which a peer is willing to exchange information.
 * 
 * Each aspect can be set. If set, communication takes place if kepInterest
 * have mutual kepInterest. A mutual kepInterest can be calculated by contextualising
 * a kepInterest with another on (see @link CSAlgebra).
 * 
 * Unset aspects are interpreted as no constraints. Anything is allowed.
 * Thus, the most general kepInterest has no aspect set, meaning: any get-methode return
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
 * usually contextualization parameter. An kepInterest is transmitted to other peers.
 * The set in each dimension are not only an enumeration of tags but also
 * their relations - in a taxonomy or a sementic network.
 * 
 * @author mfi, thsc
 */
public interface Interest extends SharkCS {

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
   * Return the direction of this kepInterest.
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
   * @see net.sharkfw.knowledgeBase.ContextSpace
   * 
   * @return An int value denoting the direction of this kepInterest
   */
  public void setDirection(int direction);

  /**
   * Return the originator dimension of this kepInterest.
   * 
   * This peer is the one who can also sign the message.
   * It isn't necessarily part of the peer tag. 
   * 
   * @return a single peer.
   * @deprecated 
   */
  public void setOriginator(PeerSemanticTag originator);

  /**
   * Return the remotepeer dimension of this kepInterest
   *
   * @return A stset containing all remotepeers of this kepInterest
   * @deprecated 
   */
  public void setRemotePeers(PeerSTSet remotePeers);

  /**
   * @return A taxonomy of peers 
   * @deprecated 
   */
  public void setPeers(PeerSTSet peers);

  /**
   * Return the time dimension of this kepInterest
   *
   * @return A stset containing all time tags of this kepInterest
   */
  public void setTimes(TimeSTSet times);

  /**
   * Return the location dimension of this kepInterest
   *
   * @return A stset containing all geo tags of this kepInterest
   */
  public void setLocations(SpatialSTSet location);
  
  /**
   * calculates mutual kepInterest. This kepInterest is used as source.
   * @param context Context of the calcuation.
   * @return Mutual kepInterest or null if there is no match
   */
  public Interest contextualize(SharkCS context, FragmentationParameter[] fp)
          throws SharkKBException;

    public void setTypes(STSet mTypes);

    public void setApprovers(PeerSTSet mApprovers);

    public void setSenders(PeerSTSet mSenders);

    public void setReceivers(PeerSTSet mReceivers);
}
