
package net.sharkfw.kep;

import java.io.IOException;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.SharkCS;

/**
 *
 * This interface describes general protocol primitives KEP supports.
 * 
 * @author thsc
 * @author mfi
 */
public interface KEPEngine {

  /**
   * Send the <code>Knowledge</code> k via an insert command
   *
   * @see net.sharkfw.knowledgeBase.Knowledge
   *
   * @param k The <code>Knowledge</code> to send
   * @param kp The <code>KP</code> from which this command is sent
   */
    public void insert(Knowledge k) throws IOException;

    /**
     * Send the <code>ExposedInterest</code> via an expose command
     *
     * @see net.sharkfw.knowledgeBase.ExposedInterest
     *
     * @param exposedInterest The <code>ExposedInterest</code> to send
     * @param kp The <code>KP</code> from which this command is sent
     */
    public void expose(SharkCS exposedInterest) throws IOException;
}
