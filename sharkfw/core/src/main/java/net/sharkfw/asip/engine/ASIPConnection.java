package net.sharkfw.asip.engine;

import java.io.InputStream;
import net.sharkfw.peer.KEPConnection;
import net.sharkfw.system.SharkException;

/**
 *
 * @author thsc
 */
public interface ASIPConnection extends KEPConnection {
    public KEPConnection asKepConnection();

    public void sendMessage(ASIPOutMessage msg, String[] addresses) throws SharkException;

    public void sendMessage(ASIPOutMessage msg) throws SharkException;

    public InputStream getInputStream();
}
