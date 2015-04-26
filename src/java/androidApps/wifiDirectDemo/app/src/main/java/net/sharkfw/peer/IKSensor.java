/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.peer;

/**
 *
 * @author Jacob Zschunke
 */
public interface IKSensor {
    public void start();
    public void stop();
    public void addListener(IKSensorListener listener);
    public void removeListener(IKSensorListener listener);
}
