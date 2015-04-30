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
public interface IKEnviroment {
    public boolean setInterest(Interest interest);
    public boolean setKnowledge(Knowledge k);
}
