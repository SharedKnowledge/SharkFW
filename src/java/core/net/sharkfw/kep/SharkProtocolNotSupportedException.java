/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.kep;

import net.sharkfw.system.SharkException;
import net.sharkfw.system.*;

/**
 *
 * @author thsc
 */
public class SharkProtocolNotSupportedException extends SharkException {
    public SharkProtocolNotSupportedException() {
        super();
    }

    public SharkProtocolNotSupportedException(String s) {
        super(s);
    }
}
