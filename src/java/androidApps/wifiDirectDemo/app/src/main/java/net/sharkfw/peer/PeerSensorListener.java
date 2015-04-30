/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.PeerSemanticTag;

/**
 * A PeerSensorListener which is being notified about new peers.
 * 
 * @author mfi
 */
public interface PeerSensorListener {

  /**
   * Notify the PeerSensor about a newly found peer.
   *
   * @param newPeer The PeerSemanticTag that has been found.
   */
  public void peerReached(PeerSemanticTag newPeer);
  
}
