/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;

/**
 * Be notified about reaching a new location.
 * 
 * @author mfi
 */
public interface GeoSensorListener {

  /**
   * Notify the listener about a ready location.
   *
   * @param gst A GeoSemanticTag representing the location reached.
   */
  public void locationReached(SpatialSemanticTag gst);
}
