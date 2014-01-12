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
    public void stop();

    /**
     * Start listening for incoming messages.
     */
    public void start() throws IOException;
    
    public boolean started();
}
