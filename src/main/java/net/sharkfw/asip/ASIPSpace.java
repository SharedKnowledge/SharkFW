package net.sharkfw.asip;

import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SpatialSTSet;
import net.sharkfw.knowledgeBase.TimeSTSet;

/**
 * 
 * @author thsc
 */
public interface ASIPSpace {
    
    public static final String TOPICS = "TOPICS";
    public static final String TYPES = "TYPES";
    public static final String APPROVERS = "APPROVERS";
    public static final String RECEIVERS = "RECEIVERS";
    public static final String SENDER = "SENDER";
    public static final String LOCATIONS = "LOCATIONS";
    public static final String TIMES = "TIMES";
    public static final String DIRECTION = "DIRECTION";
    
  /**
   * The maximum number of allowed dimensions in the context space
   * In for-loops this value can be used as the upper boundary, as long
   * as the condition is '< MAXDIMENSIONS'. Using '<=' or '==' won't work.
   *<br/>
   * Example:<br />
   * <code>
   * for(int dim = 0; dim < ContextSpace.MAXDIMENSIONS; dim++) {
   *  ...
   * }
   * </code>
   */
    public static final int MAXDIMENSIONS = 8;


    /**
     * This dimention denotes for which topic this context is valid
     */
    public static final int DIM_TOPIC = 0;

    /**
     * This dimention denotes type of information, e.g. news
     */
    public static final int DIM_TYPE = 1;
    
    /**
     * This dimensions describes what peers agrees that topics describe
     * attaced information correctly. (is/was orignator in KEP)
     */
    public static final int DIM_APPROVERS = 2;

    /**
     * This dimension denotes the actual peer that (originally) issued 
     * commands. It is the logical sender.
     */
    public static final int DIM_SENDER = 3;

    /**
     * This dimension denotes to whom a peer likes to exchange information
     * with.
     */
    public static final int DIM_RECEIVER = 4;

    /**
     * This dimension denote in which timespans the context is valid
     */
    public static final int DIM_TIME = 5;

    /**
     * This dimension denotes where this context is valid
     */
    public static final int DIM_LOCATION = 6;
    
    /**
     * In interests: Is this a sending or a receiving kepInterest (or both?)
     * (identical to direction in KEP)
     */
    public static final int DIM_DIRECTION = 7;

    /**
     * The single URL to use as SI when describing the ANY tag
     */
    public static final String ANYURL = "http://www.sharksystem.net/psi/anything";

    /**
     * A ready made string array to use as SI for the ANY tag
     */
    public static final String[] ANYSI = new String[] {ANYURL};

    /**
     * This single URL to be used as SI for the IN tag to denote incoming interests etc.
     */
    public static final String INURL = "http://www.sharksystem.net/psi/in";

    /**
     * A ready made string array to use as SI for the IN tag
     */
    public static final String[] INSI = new String[] {INURL};

    /**
     * This single URL to be used as SI for the OUT tag
     */
    public static final String OUTURL = "http://www.sharksystem.net/psi/out";

    /**
     * A ready made string array to use as SI for the OUT tag
     */
    public static final String[] OUTSI = new String[] {OUTURL};
    
    /**
     * This single URL to be used as SI for the OUT tag
     */
    public static final String INOUTURL = "http://www.sharksystem.net/psi/inout";

    /**
     * A ready made string array to use as SI for the OUT tag
     */
    public static final String[] INOUTSI = new String[] {INOUTURL};
    
    /**
     * This single URL to be used as SI for the NO_DIRECTION tag
     */
    public static final String NO_DIRECTION_URL = "http://www.sharksystem.net/psi/no_direction";
    
    /**
     * A ready made string array to use as SI for the OUT tag
     */
    public static final String[] NO_DIRECTION_SI = new String[] {NO_DIRECTION_URL};

    /**
     * Direction IN constant
     */
    public static final int DIRECTION_IN = 0;

    /**
     * Direction OUT constant
     */
    public static final int DIRECTION_OUT = 1;

    /**
     * Direction IN and OUT constant
     */
    public static final int DIRECTION_INOUT = 2;
    
    /**
     * No Direction constant
     */
    public static final int DIRECTION_NOTHING = 3;
    
  /**
   * Topics are stored in semantic tag set. Most implementations will choose
   * to offer a taxonomy and semantic network implementation beside the plain
   * set.
   * 
   * Derived classes of STset can be used of course. It can be check with
   * instanceof if the STSet is actually a Taxonomy or a SemanticNet.
     * @return 
   */
  public STSet getTopics();
  
  /**
   * Topics are stored in semantic tag set. Most implementations will choose
   * to offer a taxonomy and semantic network implementation beside the plain
   * set.
   * 
   * Derived classes of STset can be used of course. It can be check with
   * instanceof if the STSet is actually a Taxonomy or a SemanticNet.
     * @return 
   */
  public STSet getTypes();
  
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
   *
   * @return An int value denoting the direction of this kepInterest
   */
  public int getDirection();

  /**
   * Return the originator dimension of this kepInterest.
   * 
   * This peer is the one who can also sign the message.
   * It isn't necessarily part of the peer tag. 
   * 
   * @return a single peer.
     * @throws net.sharkfw.knowledgeBase.SharkKBException
   */
  public PeerSemanticTag getSender();

  /**
   * Return the remotepeer dimension of this kepInterest
   *
   * @return A stset containing all remotepeers of this kepInterest
   */
  public PeerSTSet getReceivers();

  /**
   * @return A taxonomy of peers 
   */
  public PeerSTSet getApprovers();

  /**
   * Return the time dimension of this kepInterest
   *
   * @return A stset containing all time tags of this kepInterest
   */
  public TimeSTSet getTimes();

  /**
   * Return the location dimension of this kepInterest
   *
   * @return A stset containing all geo tags of this kepInterest
   */
  public SpatialSTSet getLocations();
}
