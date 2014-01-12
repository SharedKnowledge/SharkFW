/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.SpatialSemanticTag;

/**
 * A Sensor that reports changes in the spatial dimension.
 * 
 * @author mfi
 */
public interface GeoSensor {

  /**
   * Start the GeoSensor.
   */
  public void start();

  /**
   * Stop the GeoSensor.
   */
  public void stop();

  /**
   * Add a new GeoSensorListener to this GeoSensor, that shall be notified if
   * the position of the device changes.
   *
   * @param gsl A listener to call back on, if a change above the threshold occurs.
   */
  public void addListener(GeoSensorListener gsl);

  /**
   * Remove a GeoSensorListener from this GeoSensor, that shall no longer be notified if
   * the position of the device changes.
   *
   * @param gsl A listener that shall no longer be called, if a change above the threshold occurs.
   */
  public void removeListener(GeoSensorListener gsl);

  /**
   * Define the radius the tag shall have set, when it is returned through the callback interface.
   * @param radius A number of meters
   */
  public void setRadius(double radius);

  /**
   * Define a threshold which has to be met, before a callback will take place.
   * This shall ensure that not every tiny bit of inaccuracy and movement will trigger a callback.
   * @param meters
   */
  public void setThreshold(int meters);

  /**
   * Return the current location of the device as a GeoSemanticTag using the set radius.
   * @return A GeoSemanticTag representing the current location of the device.
   */
  public SpatialSemanticTag getCurrentPlace();


}
