package net.sharkfw.ports;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.SharkEngine;

/**
 * Created by j4rvis on 22.06.16.
 */
public class RouterPort extends ASIPPort {

    public RouterPort(SharkEngine se) {
        super(se);
    }

    @Override
    public final boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
        return true;
    }
}
