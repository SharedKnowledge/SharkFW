package net.sharkfw.peer;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.asip.engine.ASIPMessage;

import java.io.InputStream;

/**
 * Created by j4rvis on 22.06.16.
 */
public class SimpleContentPort extends ContentPort {

    public SimpleContentPort(SharkEngine se) {
        super(se);
    }

    @Override
    protected boolean handleRaw(ASIPInMessage message, ASIPConnection connection, InputStream inputStream) {
        return false;
    }
}
