package net.sharkfw.system;

/**
 * Created by j4rvis on 9/26/16.
 */
public class SharkTaskException extends Exception {

    public SharkTaskException() {
    }

    public SharkTaskException(String message) {
        super(message);
    }

    public SharkTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public SharkTaskException(Throwable cause) {
        super(cause);
    }

    public SharkTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
