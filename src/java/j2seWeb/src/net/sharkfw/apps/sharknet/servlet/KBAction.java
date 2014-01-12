/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "KBAction", urlPatterns = {"/KBAction"})
public class KBAction extends HttpServlet {

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
        String clear = request.getParameter("clear");
        boolean gotError = false;
        if (clear.equalsIgnoreCase("true")) {
            try {
                SNPeer.clearKB();
            } catch (SharkKBException ex) {
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<head>");
                out.println("<title>KB Error</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>");
                out.println("For some reason the File which represents your KnowledgeBase"
                        + "could not be deleted. Check your Filesystem permissions or"
                        + " delete it manually.");
                out.println("<br />");
                out.println("Path: [path to your tomcat]/bin/.shark.skb");
                out.println("</h1>");
                out.println("<a href=\"index.jsp\">back to Mainpage</a>");
                out.println("</body>");
                out.println("</html>");
                out.flush();
                out.close();
            }
        }

        if(!gotError) {
            response.sendRedirect("index.jsp");
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
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
