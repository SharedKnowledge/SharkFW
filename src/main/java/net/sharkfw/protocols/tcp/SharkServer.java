package net.sharkfw.protocols.tcp;

import net.sharkfw.protocols.RequestHandler;

/**
 *
 * @author Jacob Zschunke
 */
public interface SharkServer extends Runnable {
    public void hold();
    public int getPortNumber();    
    public void setHandler(RequestHandler handler);    
}
