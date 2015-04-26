package net.sharkfw.kep;

import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;

/**
 * When two peers meet both send all of their interests to each other. While this is useful to start a conversation,
 * it tends to produce duplicate messages if both peers share certain interests. Message accounting shall prevent the peer from
 * sending the same message twice in a certain amount of time.
 *
 * @author mfi
 */
public interface KEPMessageAccounting {

  /**
   * Set the time in milliseconds in which a message must not be sent twice
   *
   * @param millis The time in milliseconds to wait between two duplicate messages
   */
  public void setSilentPeriod(int millis);

  /**
   * Notify the accounting system of a sent interest.
   *
   * @param interest The <code>ExposedInterest</code> that was sent.
   */
  public void sentInterest(SharkCS interest);

  /**
   * Notify the accounting system of a sent knowledge object.
   *
   * @param knowledge The <code>Knowledge</code> that was sent.
   */
  public void sentKnowledge(Knowledge knowledge);

  /**
   * Check if accounting allows to send the interest
   * @param interest The interest to be sent
   * @return <code>True</code> if this interest has not been sent before in the silence period. <code>False</code> otherwise.
   */
  public boolean interestAllowed(SharkCS interest);

  /**
   * Check if accounting allows to send the knowledge
   * @param knowledge The knowledge to be sent
   * @return <code>True</code> if this knowledge has not been sent before in the silence period. <code>False</code> otherwise.
   */
  public boolean knowledgeAllowed(Knowledge knowledge);
  
}
