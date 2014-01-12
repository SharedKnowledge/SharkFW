/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

/**
 * A PeerSensor must offer methods to register for changes.
 * All other methods are up to the implementation.
 * 
 * @author mfi
 */
public interface PeerSensor {

  /**
   * Start the PeerSensor.
   */
  public void start();

  /**
   * Stop the PeerSensor.
   */
  public void stop();

  /**
   * Add a PeerSensorListener.
   *
   * @param psl The PeerSensorListener to be added, receiving events from this sensor.
   */
  public void addListener(PeerSensorListener psl);

  /**
   * Remove a PeerSensorListener.
   *
   * @param psl The PeerSensorListener to be removed. By removing the listener no more events are sent to the listener.
   */
  public void removeListener(PeerSensorListener psl);

}
