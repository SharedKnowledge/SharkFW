package net.sharkfw.wasp;

import net.sharkfw.peer.KEPConnection;

/**
 *
 * @author thsc
 */
public interface ASIPConnection extends KEPConnection {
    public KEPConnection asKepConnection();
}
