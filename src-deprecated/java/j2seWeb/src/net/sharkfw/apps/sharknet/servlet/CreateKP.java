/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoSTSet;
import net.sharkfw.kp.HubKP;
import net.sharkfw.kp.MailBoxKP;
import net.sharkfw.kp.SlaveKP;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "CreateKP", urlPatterns = {"/CreateKP"})
public class CreateKP extends HttpServlet {

    public static final String KNOWLEDGE_PORT = "kp";
    public static final String CHAT_KP = "ckp";
    public static final String SLAVE_KP = "skp";
    public static final String MAILBOX_KP = "mkp";
    public static final String HUB_KP = "hkp";

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
            String submit = request.getParameter("submit");
            String kpString = request.getParameter("kpID");
            String action = request.getParameter("action");
            String kpIDString = request.getParameter("kp_ID");

            if(!submit.equalsIgnoreCase("submit")) {
                kpString = submit;
            }

            if (action != null && action.equalsIgnoreCase("delete")) {
                SNPeer.getSharkPeer().deleteKP(SNPeer.getKP(kpIDString));
            } else if (action != null && action.equalsIgnoreCase("create")) {
                if(kpString == null) {
                    return;                                   
                } else if (kpString.equalsIgnoreCase(HUB_KP)) {
                    createHub(request, response);
                } else if (kpString.equalsIgnoreCase(MAILBOX_KP)) {
                    createMailBox(request, response);
                } else if (kpString.equalsIgnoreCase(SLAVE_KP)) {
                    createSlave(request, response);
                }
            } else {
                if (kpString == null) {
                    return;
                } else if (kpString.equalsIgnoreCase(KNOWLEDGE_PORT)) {
                    startKP(request, response);
                } else if (kpString.equalsIgnoreCase(CHAT_KP)) {
                    startChat(request, response);
                } else if (kpString.equalsIgnoreCase(HUB_KP)) {
                    startHub(request, response);
                } else if (kpString.equalsIgnoreCase(MAILBOX_KP)) {
                    startMailBox(request, response);
                } else if (kpString.equalsIgnoreCase(SLAVE_KP)) {
                    startSlave(request, response);
                }
            }

            out.print("<h1>Oooops Sorry! The action you are calling for is currently not available<br /></h1><a href=\"index.jsp\">zurück</a>");
        } finally {
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

    private void startKP(HttpServletRequest request, HttpServletResponse response) {
        try {
            String interestID = request.getParameter("interestRB");
            if(interestID == null) {
                response.sendRedirect("manageinterests.jsp");
                return;
            }
            response.sendRedirect("InterestAction?action=activate&id=" + interestID);
        } catch (IOException ex) {
            Logger.getLogger(CreateKP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getHTML(String title) {
        return "<html><head><title>" + title + "</title></head><body>";
    }

    private void startSlave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("kps/slavekp.jsp");
    }

    private void startMailBox(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MailBoxKP mailbox = new MailBoxKP(SNPeer.getSharkPeer(), SNPeer.getSharkKB());
        response.sendRedirect("index.jsp");
    }

    private void startChat(HttpServletRequest request, HttpServletResponse response) {
        try {
            String interestID = request.getParameter("interestRB");
            if(interestID == null) {
                response.sendRedirect("manageinterests.jsp");
                return;
            }
            response.sendRedirect("InterestAction?action=activate&id=" + interestID + "&type=" + CHAT_KP);
        } catch (IOException ex) {
            Logger.getLogger(CreateKP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startHub(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("kps/hubkp.jsp");
    }

    private void createHub(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String valid = request.getParameter("validTime");
        String check = request.getParameter("checkTime");
        if(valid == null || check == null) {
            return;
        }
        
        HubKP hub = new HubKP(SNPeer.getSharkPeer(), Long.parseLong(valid), Integer.parseInt(check), "hubStatus.html", 10000);
        response.sendRedirect("index.jsp");
    }

    private void createMailBox(HttpServletRequest request, HttpServletResponse response) {
    }

    private void createSlave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] masterSIs = request.getParameterValues("peers");
        String[] topicSIs = request.getParameterValues("topics");
        
        if(masterSIs == null || masterSIs.length == 0 || topicSIs == null || topicSIs.length == 0) {
            return;
        }
        
        SharkKB kb = SNPeer.getSharkKB();
        STSet topics = kb.createSTSet();
        PeerSTSet masterPeers = kb.createPeerSTSet();
        for(String si : masterSIs) masterPeers.addPeerSemanticTag(kb.getPeerSemanticTag(new String[] {si}));
        for(String si : topicSIs) topics.addSemanticTag(kb.getSemanticTag(new String[] {si}));
        
        SlaveKP slave = new SlaveKP(SNPeer.getSharkPeer(), kb, SNPeer.getOwner(), masterPeers, topics);
        response.sendRedirect("index.jsp");
    } 
}
