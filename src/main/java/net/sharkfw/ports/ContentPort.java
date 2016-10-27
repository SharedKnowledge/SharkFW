package net.sharkfw.ports;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;
import net.sharkfw.peer.ASIPPort;
import net.sharkfw.peer.SharkEngine;

import java.io.InputStream;

/**
 * Created by j4rvis on 22.06.16.
 */
public abstract class ContentPort extends ASIPPort {

    public ContentPort(SharkEngine se) {
        super(se);
    }

    @Override
    public final boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
        if(message.getCommand() == ASIPMessage.ASIP_EXPOSE ||
                message.getCommand() == ASIPMessage.ASIP_INSERT ||
                message.getCommand() == ASIPMessage.ASIP_RAW){
            return handleRaw(message, connection, message.getRaw());
        }
        return false;
    }

    protected abstract boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream);
}
