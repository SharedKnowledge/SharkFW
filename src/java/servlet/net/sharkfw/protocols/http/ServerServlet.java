/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sharkfw.protocols.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Desty Nova
 */
public class ServerServlet extends HttpServlet {

    public static String AUTH = "42_Arrr";
    
    private HTTPStreamStub stub;

    public ServerServlet() {
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(stub.getLocalAddress());
        if(stub == null) {
            stub = ServletHelper.getInstance().getStub();
            return;
        }
        
        if (!stub.isActive()) {
            return;
        }

//        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String auth = reader.readLine();
        if (auth.equalsIgnoreCase(AUTH)) {
            if (stub.getHandler() == null) {
                return;
            }
            HTTPStreamConnection con = new HTTPStreamConnection(AUTH, request.getInputStream(), response.getOutputStream());
            stub.getHandler().handleStream(con);
        }

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Arrrrrrrrrrrrr...</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>You should not see this!!</h1>");
            out.println("</body>");
            out.println("</html>");

        } finally {
            out.close();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        stub = ServletHelper.getInstance().getStub();
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
