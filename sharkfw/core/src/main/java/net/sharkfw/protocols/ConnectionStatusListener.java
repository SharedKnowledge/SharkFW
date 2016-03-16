package net.sharkfw.protocols;

/**
 *
 * @author thsc
 */
public interface ConnectionStatusListener {
    /**
     * Called when connection has been closed
     * Note: return quickly from that call back method
     * No thread is started in kernel to call that method.
     * Thus, lengthy application logic could have nasty side effects.
     */
    public void connectionClosed();
}
