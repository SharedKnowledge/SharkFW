/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.peer;

import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.knowledgeBase.Knowledge;

/**
 *
 * @author Jacob Zschunke
 */
public interface IKSensorListener {
    public void handle(Interest interest);
    public void handle(Knowledge k);
}
