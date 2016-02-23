package net.sharkfw.asip.engine;

import java.io.InputStream;
import net.sharkfw.peer.KEPConnection;

/**
 *
 * @author thsc
 */
public interface ASIPConnection extends KEPConnection {
    public KEPConnection asKepConnection();
    
    public InputStream getInputStream();
}
