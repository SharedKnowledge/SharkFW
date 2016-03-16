/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.Interest;
import net.sharkfw.kp.ChatKP;
import net.sharkfw.peer.AbstractKP;
import net.sharkfw.peer.KnowledgePort;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "InterestAction", urlPatterns = {"/InterestAction"})
public class InterestAction extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String type = request.getParameter("type");

        Interest interest = SNPeer.getInterest(Integer.parseInt(id));

        if(action != null && action.equalsIgnoreCase("deleteInterest")) {
            SNPeer.getAllInterests().remove(Integer.parseInt(id));
        } else if (action != null && action.equalsIgnoreCase("activate") && (type == null || type.equalsIgnoreCase(CreateKP.KNOWLEDGE_PORT))) {
            KnowledgePort kp = new KnowledgePort(SNPeer.getSharkPeer(), SNPeer.getSharkKB(), interest);
            kp.activate();
            kp.keepInterestInSyncWithKB(true);
        } else if(action != null && action.equalsIgnoreCase("activate") && type.equalsIgnoreCase(CreateKP.CHAT_KP)) {
            SNPeer peer = new SNPeer();
            peer.createChatKP(interest);
        } else {
            AbstractKP kp = SNPeer.getKP(interest);
            SNPeer.getSharkPeer().deleteKP(kp);
        }

        response.setContentType("text/html;charset=UTF-8");
        response.sendRedirect("manageinterests.jsp");
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
