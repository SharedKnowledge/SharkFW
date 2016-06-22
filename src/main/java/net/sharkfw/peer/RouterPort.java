package net.sharkfw.peer;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;

/**
 * Created by msc on 22.06.16.
 */
public class RouterPort extends ASIPPort {

    @Override
    public final boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
        return true;
    }
}
