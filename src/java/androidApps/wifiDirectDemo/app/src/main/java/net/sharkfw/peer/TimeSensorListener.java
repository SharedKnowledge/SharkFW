/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 * Be notified when a time has been reached.
 * The TimeSemanticTag usually covers a timespan of a minute. (?)
 * 
 * @author mfi
 */
public interface TimeSensorListener {

  /**
   * Notify the listener about a certain time tag, that has been reached.
   *
   * @param tst The TimeSemanticTag representing the currently reached timespan.
   */
  public void timeReached(TimeSemanticTag tst);
  
}
