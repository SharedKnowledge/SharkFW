package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.system.L;

/**
 *
 * @author thsc
 */
@MultipartConfig
public class AddCPAction extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
//        
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<body>");
        

        Part filePart = request.getPart("file");
        Part topicPart = request.getPart("topic");
        
        String fNameHeader = filePart.getHeader("content-disposition");
        
        String fileName;
        int start, end;
        start = fNameHeader.indexOf("filename=\"");
        start+="filename=\"".length();
        end = fNameHeader.indexOf("\"", start);
        
        fileName = fNameHeader.substring(start, end);
//        out.print("fileName: ");
//        out.print(fileName);
//        
//        out.print("<p/><hr/>");
        InputStream topicSIis = topicPart.getInputStream();
        int len = (int) topicPart.getSize();
        byte[] topicSIBuffer = new byte[len];
        
        topicSIis.read(topicSIBuffer);
        
        String topicSI = new String(topicSIBuffer).trim();
        
        SemanticTag topicST = SNPeer.getSharkKB().getSemanticTag(
                new String[]{topicSI});
        
        // create CP 
        ContextCoordinates coco = SNPeer.getSharkKB().createContextCoordinates(topicST, SNPeer.getOwner(), null, SNPeer.getOwner(), null, null, ContextSpace.INOUT);

        ContextPoint cp = SNPeer.getSharkKB().createContextPoint(coco);
        
        InputStream fileIS = filePart.getInputStream();
        int fileLen = (int) filePart.getSize();
        
        Information info = cp.addInformation(fileIS, fileLen);
        info.setProperty(SNPeer.SN_FILENAME_PROPERTY, fileName); // Save filename

        // test
        coco = SNPeer.getSharkKB().createContextCoordinates(topicST,SNPeer.getOwner(), null, SNPeer.getOwner(), null, null, ContextSpace.INOUT);

        Enumeration cpEnum = SNPeer.getSharkKB().getAllCPs(coco);

        if(cpEnum != null && cpEnum.hasMoreElements()) {
            out.print("ok");
        } else {
            out.print("not ok");
        }
        
        
//        out.print("topicSI: ");
//        out.print(topicSI);
        
//        out.print("<p/><hr/>");
//        InputStream fileis = filePart.getInputStream();
//        len = fileis.available();
//        byte[] fileBuffer = new byte[len];
//        
//        fileis.read(fileBuffer);
        
//        String fileContent = new String(fileBuffer);
//        out.print("fileContent: ");
//        out.print(fileContent);
        
//        out.print("<p>Content-Type: ");
//        out.print(filePart.getContentType());
        
//        out.println("<p>fine");
        out.println("</body>");
        out.println("</html>");
        
        response.sendRedirect(response.encodeRedirectURL("index.jsp"));
        
    }
    
}
