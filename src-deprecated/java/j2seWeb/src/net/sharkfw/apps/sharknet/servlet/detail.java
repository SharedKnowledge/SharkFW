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
import net.sharkfw.knowledgeBase.GeoSemanticTag;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.TimeSemanticTag;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "detail", urlPatterns = {"/detail"})
public class detail extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        SharkKB kb = SNPeer.getSharkKB();
        String[] si = new String[]{request.getParameter("tag")};

        SemanticTag topic = kb.getSemanticTag(si);
        PeerSemanticTag peer = kb.getPeerSemanticTag(si);
        TimeSemanticTag time = kb.getTimeSemanticTag(si);
        GeoSemanticTag geo = kb.getGeoSemanticTag(si);

        if (topic != null) {
            
            response.sendRedirect("detail_topic.jsp?si=" + si[0]);
        } else if (peer != null) {
            response.sendRedirect("detail_peer.jsp?si=" + si[0]);
        } else if (time != null) {
            response.sendRedirect("detail_time.jsp?si=" + si[0]);
        } else if (geo != null) {
            response.sendRedirect("detail_geo.jsp?si=" + si[0]);
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
