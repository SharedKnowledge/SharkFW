package net.sharkfw.peer;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;

/**
 * Created by msc on 22.06.16.
 */
public abstract class ASIPPort {

    public abstract boolean handleMessage(ASIPInMessage message, ASIPConnection connection);
}
