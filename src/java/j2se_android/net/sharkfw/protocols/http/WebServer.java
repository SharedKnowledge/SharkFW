/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.protocols.tcp.SharkServer;
import net.sharkfw.system.L;

/**
 *
 * @author Jacob Zschunke
 */
public class WebServer implements SharkServer {

    private int port;
    private boolean isRunning = true;
    private StreamStub stub;
    private RequestHandler handler;
    private String redirectURL = "http://www.sharksystem.net";
    private String filePath = null;
    private ServerSocket server;

    public WebServer(int port, RequestHandler handler, StreamStub stub) throws IOException {
        this.port = port;
        this.handler = handler;
        this.stub = stub;
        if (port == Protocols.ARBITRARY_PORT) {
            port = 8080;
            server = new ServerSocket(port);
        } else {
            server = new ServerSocket(port);
        }

        L.d("HTTP Server is bound to port " + this.port, this);
    }

    /**
     * The Webserver will listen to any HTTP-Requests. If a Shark Client
     * (identified through the <b>User-Agent: Shark</b> parameter in the Request)
     * connects to the Webserver, the Request Header will be discarded and the 
     * Stream will be handled as usual <code>TCPConnection</code>.
     * If a Browser tries to connect to the Webserver it will either redirect the
     * User to a specified Website or show a stored HTML file.
     */
    @SuppressWarnings("unused")
    @Override
    public void run() {
        try {
            while (isRunning) {
                L.d("TCP Server accepts connection requests", this);
                Socket sock = server.accept();
                InputStream is = sock.getInputStream();

                List<Byte> bytes = new ArrayList<Byte>();

                // Read the HTTP Request from the stream                                
                for (int i = 0; i < 2048; i++) {
                    bytes.add((byte) is.read());
                    int count = 0;
                    // check when we hit the double 'newline' cause its the end
                    // of the request
                    if (bytes.size() > 3) {
                        byte[] endingBytes = new byte[4];
                        endingBytes[0] = bytes.get(bytes.size() - 1);
                        endingBytes[1] = bytes.get(bytes.size() - 2);
                        endingBytes[2] = bytes.get(bytes.size() - 3);
                        endingBytes[3] = bytes.get(bytes.size() - 4);

                        if (endingBytes[0] == (byte) '\n'
                                && endingBytes[1] == (byte) '\r'
                                && endingBytes[2] == (byte) '\n'
                                && endingBytes[3] == (byte) '\r') {
                            break;
                        }
                    }
                }

                byte[] buf = new byte[bytes.size()];
                for (int i = 0; i < bytes.size(); i++) {
                    buf[i] = bytes.get(i).byteValue();
                }
                String request = new String(buf);

                // is it a Shark-Client or Browser...
                if (request.contains("User-Agent: Shark")) {
                    // ... its a Shark-Client. Handle that stream as TCPConnection
                    HTTPConnection con = new HTTPConnection(sock, this.stub.getLocalAddress(), false);
                    
                    L.d("Calling handler for stream", this);
                    handler.handleStream(con);
                } else {
                    // ... its a Browser or anything else so respond to it.
                    this.respondToBrowser(sock);
                    sock.close();
                }
            }
        } catch (IOException e) {
            // nothing todo
        }
    }

    public void hold() {
        this.isRunning = false;
    }

    private void respondToBrowser(Socket sock) throws IOException {
        PrintStream ps = new PrintStream(sock.getOutputStream());

        if (this.filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                this.sendFile(file, ps);
            }
        }
        this.redirectTo(ps, this.redirectURL);
    }

    private void redirectTo(PrintStream out, String url) {
        out.print("HTTP/1.1 302 FOUND\r\n");
        Date now = new Date();
        out.print("Date: " + now + "\r\n");
        out.print("Server: Redirector 1.0\r\n");
        out.print("Location: " + url + "\r\n");
        out.print("Content-type: text/html\r\n\r\n");
        out.flush();
    }
    static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    private void sendFile(File file, PrintStream ps) throws IOException {
        // write Response-Header
        ps.print("HTTP/1.0 200 OK");
        ps.write(EOL);
        ps.print("Server: Shark-Webserver");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);
        ps.print("Content-length: " + file.length());
        ps.write(EOL);
        ps.print("Last Modified: " + (new Date()));
        ps.write(EOL);
        ps.print("Content-type: text/html");
        ps.write(EOL);
        ps.write(EOL);

        FileInputStream fis = new FileInputStream(file);
        int b = 0;
        while (b != -1) {
            b = fis.read();
            ps.write(b);
        }

        ps.flush();
        fis.close();
    }

    @Override
    public int getPortNumber() {
        return this.port;
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }
}
