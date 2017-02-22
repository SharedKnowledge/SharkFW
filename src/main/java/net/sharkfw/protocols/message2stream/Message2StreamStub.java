package net.sharkfw.protocols.message2stream;

import net.sharkfw.asip.ASIPKnowledge;
import net.sharkfw.asip.ASIPSpace;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.system.SharkNotSupportedException;

import java.io.IOException;

/**
 * Created by local on 17.01.17.
 */
public class Message2StreamStub implements StreamStub, RequestHandler {
    @Override
    public void setHandler(RequestHandler handler) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public boolean started() {
        return false;
    }

    @Override
    public void offer(ASIPSpace interest) throws SharkNotSupportedException {

    }

    @Override
    public void offer(ASIPKnowledge knowledge) throws SharkNotSupportedException {

    }

    @Override
    public void handleMessage(byte[] msg, MessageStub stub) {

    }

    @Override
    public void handleStream(StreamConnection con) {

    }

    @Override
    public void handleNewConnectionStream(StreamConnection con) {

    }

    @Override
    public StreamConnection createStreamConnection(String addressString) throws IOException {
        return null;
    }

    @Override
    public String getLocalAddress() {
        return null;
    }
}
