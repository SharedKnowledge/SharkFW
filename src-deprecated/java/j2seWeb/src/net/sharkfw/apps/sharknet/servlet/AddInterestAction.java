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
import net.sharkfw.knowledgeBase.AnchorSet;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoAnchorSet;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "AddInterestAction", urlPatterns = {"/AddInterestAction"})
public class AddInterestAction extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] topicSIs = request.getParameterValues("topics");
        String[] peerSIs = request.getParameterValues("peers");
        String in = request.getParameter("IN");
        String out = request.getParameter("OUT");

        int direction = ContextSpace.NO_DIRECTION;
        if(in != null) {
            direction = ContextSpace.IN;
        }
        if(out != null) {
            direction = ContextSpace.OUT;
        }
        if(in != null && out != null) {
            direction = ContextSpace.INOUT;
        }
        
        SharkKB kb = SNPeer.getSharkKB();
        STSet topics = null;
        if(topicSIs != null) {
            topics = kb.createSTSet();
            for(String si : topicSIs) {
                SemanticTag topic = kb.getSemanticTag(new String[]{si});
                topics.addSemanticTag(topic);
            }
        }
        
        PeerSTSet remotePeers = null;
        if(peerSIs != null) {
            remotePeers = kb.createPeerSTSet();
            for(String si : peerSIs) {
                PeerSemanticTag peer = kb.getPeerSemanticTag(new String[]{si});
                remotePeers.addPeerSemanticTag(peer);
            }
        }
        
        PeerSemanticTag owner = SNPeer.getOwner();
        
        PeerSTSet ownerSet = kb.createPeerSTSet();
        ownerSet.addPeerSemanticTag(owner);
        
        
        AnchorSet as = new InMemoAnchorSet(topics, owner, remotePeers, null, null, null, direction);
        SNPeer.createInterest(as);
        
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
