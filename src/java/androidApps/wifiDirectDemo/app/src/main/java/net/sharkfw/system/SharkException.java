package net.sharkfw.system;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * The mother of all Shark exceptions
 * @author thsc
 */
public class SharkException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6062568210964849703L;

    public SharkException() {
        super();
    }

    public SharkException(String s) {
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
    public SharkException(String msg, Throwable cause) {
        super(msg + cause.getMessage());
    }
}
