/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.system;

/**
 * Eine Exception, die erst zur Laufzeit geworfen wird und andere Exception enthalten kann.
 *
 * @author Matthias
 */
public class SharkRuntimeException extends RuntimeException{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7980033147439148633L;

    public SharkRuntimeException(){
        super();
    }

    public SharkRuntimeException(String m){
        super(m);
    }

}
