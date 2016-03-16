/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.kep;

import net.sharkfw.system.SharkException;

/**
 *
 * @author thsc
 */
public class SharkProtocolNotSupportedException extends SharkException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2764391712902085343L;

    public SharkProtocolNotSupportedException() {
        super();
    }

    public SharkProtocolNotSupportedException(String s) {
        super(s);
    }
}
