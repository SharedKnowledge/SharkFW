package net.sharkfw.asip;

import net.sharkfw.asip.engine.ASIPInMessage;

/**
 *
 * @author thsc
 */
public interface ASIPStub extends SharkStub {
    public boolean callListener(ASIPInMessage msg);
}
