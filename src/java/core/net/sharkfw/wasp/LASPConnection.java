package net.sharkfw.wasp;

import net.sharkfw.peer.KEPConnection;

/**
 *
 * @author thsc
 */
public interface LASPConnection extends KEPConnection {
    public KEPConnection asKepConnection();
}
