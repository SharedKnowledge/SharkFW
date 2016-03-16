package net.sharkfw.kp;

import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;
import net.sharkfw.peer.KnowledgePort;

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
   * Notify the listener about a sent expose.
   * 
   * @param kp The KP sending the expose.
   * @param sentMutualInterest The interest that is exposed.
   */
  public void exposeSent (KnowledgePort kp, SharkCS sentMutualInterest);

  /**
   * Notify the listener about a sent insert.
   *
   * @param kp The KP sending the insert.
   * @param sentKnowledge The knowledge object that is sent.
   */
  public void insertSent(KnowledgePort kp, Knowledge sentKnowledge);

  /**
   * Notify the listener of assimilated knowledge.
   * <em>Note:</em> This method may be called subsequently, if more than on <code>ContextPoint</code> is assimilated.
   *
   * @param kp The KP assimilating the knowledge.
   * @param newCP The ContextPoint assimilated.
   */
  public void knowledgeAssimilated(KnowledgePort kp, ContextPoint newCP);
}
