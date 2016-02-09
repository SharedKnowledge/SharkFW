package net.sharkfw.protocols;

import java.io.IOException;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.ASIPSpace;
import net.sharkfw.system.SharkNotSupportedException;

/**
 * A Stub is the general super interface for all protocol abstractions.
 * 
 * @author thsc
 * @author mfi
 */
public interface Stub {
    void setHandler(RequestHandler handler);

    /**
     * Stop listening for incoming messages.
     */
    void stop();

    /**
     * Start listening for incoming messages.
     */
    void start() throws IOException;
    
    boolean started();
    
    
    /**
     * 
     * @param interest 
     */
    void offer(ASIPSpace interest) throws SharkNotSupportedException;
    
    void offer(Knowledge knowledge) throws SharkNotSupportedException;
}
