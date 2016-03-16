package net.sharkfw.protocols;

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author thsc
 */
public class ConnectionListenerManager {
    private HashSet<ConnectionStatusListener> listenerSet;
    
    /**
     * Create set lately - listener are not set in most cases. Thus,
     * don't create set during object creation
     * @return 
     */
    private HashSet<ConnectionStatusListener> getListener() {
        if(this.listenerSet == null) {
            this.listenerSet =  new HashSet();
        }
        
        return this.listenerSet;
    }
    
    /**
     * Add listener that is informed about connection status changes
     */
    public void addConnectionListener(ConnectionStatusListener newListener) {
        this.getListener().add(newListener);
    }
    
    public void removeConnectionListener(ConnectionStatusListener listener) {
        this.getListener().remove(listener);
    }
    
    protected void notifyConnectionClosed() {
        Iterator<ConnectionStatusListener> lIter = this.getListener().iterator();
        while(lIter.hasNext()) {
            lIter.next().connectionClosed();;
        }
    }
}
