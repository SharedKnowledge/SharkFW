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

        try {
            L.d(this.os.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.is = new ByteArrayInputStream(this.os.toByteArray());

        L.d("new IS created " + (this.is.available() > 0));
    }

    @Override
    public SharkInputStream getInputStream() {
        return new StandardSharkInputStream(this.is);
    }

    @Override
    public SharkOutputStream getOutputStream() {
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
