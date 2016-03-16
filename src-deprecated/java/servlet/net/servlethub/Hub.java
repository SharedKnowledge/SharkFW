/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.servlethub;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.kp.impl.MailBoxKP;

/**
 * The Hub represented by a Servlet, is a special Shark Peer. Its designed to
 * wait for incoming Data. It will save these Date like a Mailbox using the
 * <code>MailboxKP</code>.
 * To ensure that only Shark clients can connect, the user-agent Attribute from
 * the HTTP Header must set to "shark".
 * A communication can only occur through the HTTP Method POST.
 *
 * @author Jacob Zschunke
 */
public class Hub extends HttpServlet {

    private HubEngine engine;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("\n\n\n\n\n\n\n\nincoming GET connection!\n\n\n\n\n\n\n\n");
        String isShark = request.getParameter("isShark");

        if(isShark != null && isShark.equalsIgnoreCase("true")) {
            engine.createHTTPServerMessageStub(request, response);
        } else {
            this.writeDefaultResponse(response);
        }
    }

    private void writeDefaultResponse(HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Test</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Hub is running</h1>");
            out.println("</body>");
            out.println("</html>");
        } catch (IOException ex) {
        } finally {
            out.close();
        }
    }

    /**
     * Checks if the user-agent attribute of the HTTP Header is set to shark.
     * if not nothing happens. Else there will be a new
     * <code>HTTPServerMessageStub</code> created.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n\n\n\n\n\n\n\nincoming POST connection!\n\n\n\n\n\n\n\n");

        String userAgent = request.getHeader("user-agent");
        if (userAgent.equalsIgnoreCase("shark")) {
            engine.createHTTPServerMessageStub(request, response);
        } else {
            this.writeDefaultResponse(response);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        InMemoSharkKB kb = new InMemoSharkKB("hub");
        engine = new HubEngine(kb);
        MailBoxKP kp = new MailBoxKP(engine);
        //engine.publishKP(kp);
    }

    @Override
    public String getServletInfo() {
        return "Here is running a Shark Hub.";
    }
}
