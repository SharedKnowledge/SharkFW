package net.sharkfw.system;

/**
 *
 * @author thsc
 */
public class SharkSecurityException extends SharkException {
    public SharkSecurityException() {
        super();
    }

    public SharkSecurityException(String s) {
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
    public SharkSecurityException(String msg, Throwable cause) {
        super(msg + cause.getMessage());
    }
    
    
}
