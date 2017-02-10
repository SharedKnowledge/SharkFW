/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.system;

/**
 *
 * @author df
 */
public class SharkPKVerifyException extends SharkException{
    public SharkPKVerifyException() {
        super();
    }

    public SharkPKVerifyException(String s) {
        super(s);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param msg the detail message. The detail message is saved for later
     *      retrieval by the Throwable.getMessage() method.
     * @param cause cause - the cause (which is saved for later retrieval by
     *      the Throwable.getCause() method). (A null value is permitted, and
     *      indicates that the cause is nonexistent or unknown.)
     */
    public SharkPKVerifyException(String msg, Throwable cause) {
        super(msg + cause.getMessage());
    }
}
