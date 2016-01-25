package net.sharkfw.protocols;

import java.io.IOException;

/**
 * A Stub is the general super interface for all protocol abstractions.
 * 
 * @author thsc
 * @author mfi
 */
public interface Stub {
    public void setHandler(RequestHandler handler);

    /**
     * Stop listening for incoming messages.
     */
    void stop();

    /**
     * Start listening for incoming messages.
     */
    void start() throws IOException;
    
    boolean started();
}
