package net.sharkfw.peer;

import net.sharkfw.asip.SharkStub;
import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;

import java.io.InputStream;

/**
 * Created by msc on 22.06.16.
 */
public abstract class ContentPort extends ASIPPort {

    private SharkStub sharkStub;

    public ContentPort(SharkEngine se) {

    }

    @Override
    public final boolean handleMessage(ASIPInMessage message, ASIPConnection connection) {
        return handleRaw(message, connection, message.getInputStream());
    }

    protected abstract boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream);
}
