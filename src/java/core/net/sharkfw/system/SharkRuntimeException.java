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

    public SharkRuntimeException(){
        super();
    }

    public SharkRuntimeException(String m){
        super(m);
    }

}
