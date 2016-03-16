/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.peer.AbstractKP;
import net.sharkfw.peer.J2SESharkEngine;

/**
 *
 * @author mfi
 */
public class PublishKPAction extends HttpServlet {


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
   /*
     * Expecting get-values for publishing KPs to a certain recipient.
     */
    
    PrintWriter out = response.getWriter();
    
    String[] id = request.getParameterValues("kp");
    String peerSi = request.getParameter("peer");
    
    if(id != null && peerSi != null) {
      peerSi = peerSi.trim();


      SharkKB kb = SNPeer.getSharkKB();
      PeerSemanticTag remotepeer = kb.getPeerSemanticTag(new String[]{peerSi});
      
      if(remotepeer != null) {
        // Remotepeer could be found
              
        J2SESharkEngine se = SNPeer.getSharkPeer();
        Enumeration kps = se.getKPs();
        while(kps != null && kps.hasMoreElements()) {
          AbstractKP kp = (AbstractKP) kps.nextElement();
          // Check every KP againt every ID value
          for(String value : id) {
            if(kp.getID().equals(value)) {
            // Publish this KP
            se.publishKP(kp, remotepeer);
            }
          }
        }
        
        
      } else {
        // Remotepeer could not be found. Print errormessage!
        out.write("<html><head><title>Can't find peer!</title></head><body>Unable to find peer for si: " + peerSi + ". <br /> <a href=\"index.jsp\">Back to index</a></body></html>");
      }

    } else {
      // Missing an argument. Print errormessage!
      out.write("<html><head><title>Missing argument</title></head><body>Missing argument id or peer-si!. <br /> <a href=\"index.jsp\">Back to index</a></body></html>");
    }
    
    // All good, return to index.
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
    // NOOP
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
