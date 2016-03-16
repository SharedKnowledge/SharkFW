/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.servlethub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.http.HTTPClientMessageStub;
import net.sharkfw.system.L;

/**
 * The HTTPServerMessageStub can extract data from a HttpServlertRequest Object
 * and pass it to the <code>RequestHandler</code>.
 *
 * @author Jacob Zschunke
 */
public class HTTPServerMessageStub implements MessageStub {



    private String replyAddress;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestHandler handler;

    public HTTPServerMessageStub(HttpServletRequest request, HttpServletResponse response, RequestHandler handler) {
        this.request = request;
        this.response = response;
        this.handler = handler;

        String method = request.getMethod();
        if(method.equalsIgnoreCase("GET")) {
            try {
                doGET();
            } catch (IOException ex) {
                Logger.getLogger(HTTPServerMessageStub.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(method.equalsIgnoreCase("POST")) {
            doPOST();
        }
    }

    public void doGET() throws IOException {
        String payload = request.getParameter(HTTPClientMessageStub.KEPPAYLOAD);
        //payload = URLDecoder.decode(payload, "ISO8859_1");
        handler.handleMessage(payload, this);
    }

    public void doPOST() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String s = "";
            String payload = "";
            while ((s = reader.readLine()) != null) {
                payload += s;
            }
            payload = URLDecoder.decode(payload, "UTF-8");
            handler.handleMessage(payload, this);
        } catch (IOException ex) {
            L.d("failed to read from HTTP Stream", this);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                L.d("failed to close reader", this);
            }
        }
    }

    public void setReplyAddressString(String addr) {
        this.replyAddress = addr;
    }

    public void sendMessage(String msg, String recAddress) throws IOException {
        writeHeader(response);
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(msg);
        writer.flush();
        writer.close();
    }

    public void stop() {
        
    }

    public String getReplyAddressString() {
        return replyAddress;
    }

    private void writeHeader(HttpServletResponse response) {
        response.setHeader("Server", "shark");        
        response.setHeader("HTTP/Version", "HTTP/1.0");
        response.setHeader("Status", "200");
        response.setHeader("ResponseMessage", "OK");
        response.setHeader("Content-type", "KEP");
    }
}
