package net.sharkfw.asip;

import net.sharkfw.protocols.*;
import net.sharkfw.system.L;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by msc on 21.03.16.
 */
public class TestConnection implements StreamConnection {

    private ByteArrayOutputStream os;
    private ByteArrayInputStream is;

    public TestConnection() {
        this.os = new ByteArrayOutputStream();
    }

    public void createInputStream(){
        this.is = new ByteArrayInputStream(this.os.toByteArray());
    }

    @Override
    public SharkInputStream getSharkInputStream() {
        return new StandardSharkInputStream(this.is);
    }

    @Override
    public InputStream getInputStream() {
        return this.is;
    }

    @Override
    public OutputStream getOutputStreamOutputStream() {
        return this.os;
    }

    @Override
    public SharkOutputStream getSharkOutputStream() {
        return new UTF8SharkOutputStream(this.os);
    }

    @Override
    public void sendMessage(byte[] msg) throws IOException {
        this.os.write(msg);
    }

    @Override
    public String getReplyAddressString() {
        return null;
    }

    @Override
    public String getReceiverAddressString() {
        return null;
    }

    @Override
    public String getLocalAddressString() {
        return null;
    }

    @Override
    public void setLocalAddressString(String localAddress) {

    }

    @Override
    public void close() {
        try {
            this.os.close();
            this.is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addConnectionListener(ConnectionStatusListener newListener) {

    }

    @Override
    public void removeConnectionListener(ConnectionStatusListener listener) {

    }
}
