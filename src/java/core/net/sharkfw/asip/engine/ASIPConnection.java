package net.sharkfw.asip.engine;

import net.sharkfw.peer.KEPConnection;

/**
 *
 * @author thsc
 */
public interface ASIPConnection extends KEPConnection {
    public KEPConnection asKepConnection();
}
