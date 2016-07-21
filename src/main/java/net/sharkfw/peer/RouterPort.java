package net.sharkfw.peer;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;

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
