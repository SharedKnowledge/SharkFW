/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 * A Sensor that tracks time. After a certain amount of time defined by the
 * threshold a callback to all registered listeners must occure, notifying them
 * of the changed time.
 *
 * The current state of the time can always actively be procured by calling
 * <code>getCurrentTime</code> which will produce a TimeSemanticTag representing
 * the current time, with a span of 5 minuten between from (current time) and to (current time + 5 minutes)
 *
 * If a different timespan has been assigned using <code>setTimespan()</code> it will replace
 * the 5 minute default value.
 * 
 * @author mfi
 */
public interface TimeSensor {


  /**
   * Start the TimeSensor.
   */
  public void start();

  /**
   * Stop the TimeSensor.
   */
  public void stop();

  /**
   * Register a new listener to be notified when a certain amount of time, as defined
   * in threshold, has passed.
   *
   * @param tsl A listener to be notified when threshold many seconds have passed.
   */
  public void addListener(TimeSensorListener tsl);

  /**
   * Remove a listener, that shall no longer be notified if the threshold value of
   * passed time has been reached.
   *
   * @param tsl The listener to be removed
   */
  public void removeListener(TimeSensorListener tsl);

  /**
   * Define the threshold after which a notification shall occur to prevent ongoing
   * events, just because another millisecond has passed.
   *
   * @param threshold seconds, after which a new notification must be sent.
   */
  public void setThreshold(int threshold);

  /**
   * Define the timespan on the returnvalue of <code>getCurrentTime()</code> and
   * the callback method. The default is set to 5 minutes (see class decription).
   * 
   * @param timespan A number of seconds like:  <code>to - from = timespan</code>
   */
  public void setTimespan(int timespan);

  /**
   * The currently set timespan (5 minuten = 300 seconds default).
   * @return An integer value of seconds
   */
  public int getTimespan();

  /**
   * Return the current time as TimeSemanticTag. The timespan between from and to
   * will be taken from this object. If no changes were made the default is 5 minutes.
   *
   * @return A TimeSemanticTag with from = current time, and to = current time + timespan, repeat = NONE
   */
  public TimeSemanticTag getCurrentTime();

}
