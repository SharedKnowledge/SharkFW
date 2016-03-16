/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.system;

/**
 *
 * @author thsc
 */
public class SharkNotSupportedException extends SharkException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5593151700965690611L;

    public SharkNotSupportedException() {
        super();
    }
    
    public SharkNotSupportedException(String m) {
        super(m);
    }
}
