package net.sharkfw.ports;

import net.sharkfw.knowledgeBase.Knowledge;

/**
 * Listening for events happening on a KnowledgePort implementation.
 * 
 * Added methods from the programmer's handbook -mfi, 21.06.11
 * 
 * @author thsc
 * @author mfi
 */
public interface KPListener {

  /**
   * Notify the listener about a sent insert.
   *
   * @param kp The KP sending the insert.
   * @param sentKnowledge The knowledge object that is sent.
   */
  public void insertSent(KnowledgePort kp, Knowledge sentKnowledge);
}
