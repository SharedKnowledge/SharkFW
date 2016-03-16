/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

/**
 *
 * @author Jacob Zschunke
 */
public class HTTPStreamStub implements StreamStub, Runnable {

    private boolean isActive = false;
    private String address;
    private RequestHandler handler;

    public HTTPStreamStub(RequestHandler handler, String address) {
        this.handler = handler;
        this.address = address;
        this.isActive = true;
        ServletHelper.getInstance().setStub(this);
    }

    public StreamConnection createStreamConnection(String addressString) throws IOException {
        return new HTTPStreamConnection(addressString, this.getLocalAddress());
    }

    public String getLocalAddress() {
        return address;
    }

    public void stop() {
        this.isActive = false;
    }

    public void startOrResume() {
        this.isActive = true;
    }

    boolean isActive() {
        return isActive;
    }

    protected RequestHandler getHandler() {
        return this.handler;
    }

    public void run() {
        while(isActive) {
            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException ex) {
                Logger.getLogger(HTTPStreamStub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
