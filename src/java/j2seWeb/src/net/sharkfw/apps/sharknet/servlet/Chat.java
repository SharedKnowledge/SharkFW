/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jacob Zschunke
 */
@WebServlet(name = "Chat", urlPatterns = {"/Chat"})
public class Chat extends HttpServlet {

    private int tabCount = 0;

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
            openTag(out, "html", null);
            openTag(out, "head", null);
            closeTag(out, "head");
            openTag(out, "body", null);
            
            closeTag(out, "body");
            closeTag(out, "html");
        } finally {
            out.close();
        }
    }

    private void openTag(PrintWriter out, String tagName, Hashtable<String, String> params) {
        String html = "<" + tagName;
        if (params != null) {
            Enumeration<String> keys = params.keys();
            while (keys != null && keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = params.get(key);
                if (value != null) {
                    html += " " + key + "=\"" + value + "\" ";
                } else {
                    html += " " + key;
                }
            }
        }
        html += ">";
        out.print(this.tabString() + html);
        tabCount++;
    }

    private String tabString() {
        String tabs = "";
        for (int i = 0; i < tabCount; i++) {
            tabs += "\t";
        }
        return tabs;
    }

    private void closeTag(PrintWriter out, String tagName) {
        out.print(this.tabString() + "</" + tagName + ">");
        tabCount--;
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
