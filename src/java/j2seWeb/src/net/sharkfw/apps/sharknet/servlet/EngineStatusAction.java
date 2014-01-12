/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.peer.J2SESharkEngine;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.system.L;

/**
 *
 * @author mfi
 */
public class EngineStatusAction extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EngineStatusAction</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EngineStatusAction at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
        } finally {
            response.sendRedirect("index.jsp");
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        J2SESharkEngine se = SNPeer.getSharkPeer();

        String engine = request.getParameter("engine");

        if (engine != null) {
            engine = engine.trim();

            if (engine.equals("start")) {
                if(SNPeer.isProtocolConfigured(Protocols.TCP)) {
                    se.startTCP(SNPeer.getTCPPort());
                }
                
                if(SNPeer.isProtocolConfigured(Protocols.MAIL)) {
                    se.startMail();
                }
            }

            if (engine.equals("stop")) {
                se.stop();
            }

        }
        // All well, return to index page.
        response.sendRedirect(response.encodeRedirectURL("index.jsp"));
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int protocol = Integer.parseInt(request.getParameter("protocol"));
        
        switch(protocol) {
            case Protocols.TCP: this.doTCP(request, response);
                break;
            case Protocols.MAIL: this.doMail(request, response);
                break;
            default:
                break;
        }
        

    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void doTCP(HttpServletRequest request, HttpServletResponse response) {
        String sPort = request.getParameter("port");
        String sTimeout = request.getParameter("timeout");
        
        if(sPort == null || sPort.equalsIgnoreCase("")) {
            return;
        }
        
        if(sTimeout == null || sTimeout.equalsIgnoreCase("")) {
            return;
        }
        
        try {
            int port = Integer.parseInt(sPort);
            long timeout = Long.parseLong(sTimeout);
            
            SNPeer.getSharkPeer().setConnectionTimeOut(timeout);
            SNPeer.setTCPport(port);
            
            SNPeer.setProtocolConfigured(Protocols.TCP, true);
            
            PrintWriter out = response.getWriter();
            out.write("<html><head><title>Success!</title></head><body><p>TCP data has been set!:" +
                    "Port: " + sPort + "<br/>Connection Timeout: " + sTimeout
                + "</p><p><a href=\"index.jsp\">Return to index</a></p></body></html>");
        } catch(Exception ex) {
            L.e(ex.getMessage(), this);
        }
    }

    private void doMail(HttpServletRequest request, HttpServletResponse response) throws IOException {
                // Let's assume all params are passed correctly
        String pop3User = request.getParameter("pop3user").trim();
        String smtpUser = request.getParameter("smtpuser").trim();
        String pop3Host = request.getParameter("pop3host").trim();
        String smtpHost = request.getParameter("smtphost").trim();
        String pop3Password = request.getParameter("pop3pw").trim();
        String smtpPassword = request.getParameter("smtppw").trim();
        String pop3Address = request.getParameter("pop3address").trim();
        String interval = request.getParameter("interval").trim();

        //Store mail config to SNPeer
        SNPeer.setMailConfig("smtphost", smtpHost);
        SNPeer.setMailConfig("smtpuser", smtpUser);
        SNPeer.setMailConfig("smtppw", smtpPassword);
        SNPeer.setMailConfig("pop3host", pop3Host);
        SNPeer.setMailConfig("pop3user", pop3User);
        SNPeer.setMailConfig("pop3pw", pop3Password);
        SNPeer.setMailConfig("pop3address", pop3Address);
        SNPeer.setMailConfig("interval", interval);


        // Set the config on the engine
        SNPeer.getSharkPeer().setMailConfiguration(smtpHost, smtpUser, smtpPassword, pop3Host, pop3User, pop3Address, pop3Password, Integer.parseInt(interval));
        SNPeer.setProtocolConfigured(Protocols.MAIL, true);
        PrintWriter out = response.getWriter();
        out.write("<html><head><title>Success!</title></head><body><p>Mail account data has been set!:"
                + pop3User + ", " + smtpUser + ", " + pop3Host + ", " + smtpHost + ", " + pop3Password + ", " + smtpPassword + ", " + pop3Address + ", " + interval
                + "</p><p><a href=\"index.jsp\">Return to index</a></p></body></html>");
    }
}
