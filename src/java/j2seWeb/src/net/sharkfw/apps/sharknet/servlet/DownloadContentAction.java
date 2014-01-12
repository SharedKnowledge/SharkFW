/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.Information;

/**
 *
 * @author mfi
 */
public class DownloadContentAction extends HttpServlet {

  /** 
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * 
   * Prepare download and stream content of information object through a
   * ServletOutputStream.
   * 
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    // Trigger removal of information from ContextPoint
    String doString = request.getParameter("do");
    if(doString != null) {
      doString = doString.trim();
      if(doString.equals("remove")) {
        
        String infoId = request.getParameter("info");
        String cpId = request.getParameter("cp");
        
        // Assume both are not null
        Information info = SNPeer.getInfoWithId(Integer.valueOf(infoId));
        ContextPoint cp = SNPeer.getContextPointWithId(Integer.valueOf(cpId));
        
        // Delete info from CP
        cp.removeInformation(info);
        info = null;
        response.sendRedirect(response.encodeRedirectURL("index.jsp"));
      } else if(doString.equals("download")){
        /* 
       * This is the default behavior to prepare content for download.
       * If no "do" parameter is passed it is assumed that an information
       * shall be downloaded to the browser.
       */

        // Get the handle (key) for the information object to send
        String infostring = request.getParameter("info");
        infostring = infostring.trim();

        // Grab the actual object from the public Map on SNPeer
        Information info = (Information) SNPeer.getInfoWithId(Integer.parseInt(infostring));
        String filename = info.getProperty(SNPeer.SN_FILENAME_PROPERTY);
        String contentType = info.getContentType();

        response.setHeader( "Content-disposition", "attachment; filename="+filename);

        if(contentType == null) {
          // If unset, default to 'application/unknown' type.
          response.setContentType("application/unknown");
        } else {
          response.setContentType(contentType); 
        }

        ServletOutputStream sos = response.getOutputStream();
        info.streamContent(sos);
        sos.close();
      }
    } else {
      // No do-argument given. Print errormessage.
      PrintWriter out = response.getWriter();
      out.write("<html><head><title>No parameter specified</title></head><body>No 'do' parameter specified!<br /> <a href=\"index.jsp\">Return to index</a></body></html>");
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
