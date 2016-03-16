/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.net.sharkfw.apps.sharknet.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sharkfw.apps.sharknet.SNPeer;
import net.sharkfw.knowledgeBase.AnchorSet;
import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.HierarchicalSemanticTag;
import net.sharkfw.knowledgeBase.PeerHierarchicalSemanticTag;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKB;
import net.sharkfw.knowledgeBase.inmemory.InMemoAnchorSet;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.system.Util;
import net.sharkfw.wrapper.Vector;

/**
 * Show a UI to create an interest
 * 
 * @author mfi
 */
@WebServlet(name = "AddKP", urlPatterns = {"/AddKP"})
public class AddKP extends HttpServlet {


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
    //processRequest(request, response);
    
    SharkKB kb = SNPeer.getSharkKB();
    
    String page = request.getParameter("page");
    
    PrintWriter out = response.getWriter();
    // Print all selectable topics with a link to add them to the anchorset
    if(page.equals("topic")) {
      
      StringBuilder sb = new StringBuilder();
      HierarchicalSemanticTag tag = (HierarchicalSemanticTag) kb.getSemanticTag(new String[]{SNPeer.SN_TOPICS_ROOT_ENTRY});
      
      out.write(this.printTopicPage(this.printTopicTree(tag, sb, false, request), request));
      out.write("<section>\n<kpNav>\n");
      out.write("<a href=\"addKP?page=direction"+ this.urlEncodedParametersFromThisRequest(request) + "\"> <- Back to direction </a>  ");
      out.write("<a href=\"addKP?page=remotepeer"+ this.urlEncodedParametersFromThisRequest(request) +"\"> Pick remotepeers -> </a>"); // Next step: Remotepeer
      out.write("</kpNav>\n</section>");
      out.write("</body>");
      out.write("</html>");
    
      
    } else if(page.equals("remotepeer")) {
      // Print the page to select the remotepeer
      
      StringBuilder sb = new StringBuilder();
      PeerHierarchicalSemanticTag tag = (PeerHierarchicalSemanticTag) kb.getPeerSemanticTag(new String[]{SNPeer.SN_PEERS_ROOT_ENTRY});
      
      out.write(this.printPeerPage(this.getPeerTree(request), request)); // true (last) = remotepeers
      out.write("<section>\n<kpNav>\n");
      out.write("<a href=\"addKP?page=topic"+ this.urlEncodedParametersFromThisRequest(request) + "\"> <- Back to topics </a>  ");
      out.write("<a href=\"addKP?page=finish"+ this.urlEncodedParametersFromThisRequest(request) +"\"> Create -> </a>"); // Next step: Creation
      out.write("</kpNav>\n</section>");
      out.write("</body>");
      out.write("</html>");
      
    } else if(page.equals("finish")) {
      // Create a KP and return to index.jsp
      
      // Resolve SIs to Tags
      String[] topics = request.getParameterValues("topic");
      String[] remotepeers = request.getParameterValues("remotepeer");
      String peerSi = request.getParameter("peer");
      String direction = request.getParameter("direction");
      
      int dir = Integer.parseInt(direction);
      
      List<SemanticTag> topicList = new ArrayList<SemanticTag>();
      
      if(topics != null) {
        for(String topicSi : topics) {
          SemanticTag tag = kb.getSemanticTag(new String[]{topicSi});
          if(tag != null) {
            // Found
            topicList.add(tag);
          }
        }
      }
      
      List<PeerSemanticTag> remotepeerList = new ArrayList<PeerSemanticTag>();
      if(remotepeers != null) {
        for(String remotepeerSi : remotepeers) {
         PeerSemanticTag tag = kb.getPeerSemanticTag(new String[]{remotepeerSi});
         if(tag != null) {
           // Found
           remotepeerList.add(tag);
         }
        }  
      }
      
      // Determine (local)peer
      PeerSemanticTag localPeer = null;
      if(peerSi != null) {
        peerSi = peerSi.trim();
         localPeer = kb.getPeerSemanticTag(new String[]{peerSi});
      }
      
    
      
      // Create STSets for AnchorSet
      
      STSet topicSTSet = kb.createSTSet();
      // If no topics have been selected, leave topicSTSet set to null to indicate ANY
      if(topicList.isEmpty()) {
        topicSTSet = null;
      }
      Iterator<SemanticTag> topicIt = topicList.iterator();
      while(topicIt.hasNext()) {
        SemanticTag tag = topicIt.next();
        topicSTSet.addSemanticTag(tag);
      }
      
      PeerSTSet peerSTSet = kb.createPeerSTSet();
      // If no peers have been selected, leave remotepeerList set to null to indicate ANY
      if(remotepeerList.isEmpty()) {
        peerSTSet = null;
      }
      Iterator<PeerSemanticTag> remotepeerIt = remotepeerList.iterator();
      while(remotepeerIt.hasNext()) {
        PeerSemanticTag tag = remotepeerIt.next();
        peerSTSet.addPeerSemanticTag(tag);
      }
      
      AnchorSet as = new InMemoAnchorSet(topicSTSet, localPeer, peerSTSet, null, null, null, dir);
      KnowledgePort kp = SNPeer.getSharkPeer().createKP(as, kb);
      kp.setOtp(kb.getStandardFPSet());
      
      // Update on changes from the KB
      kp.keepInterestInSyncWithKB(true);
      
      // Return to index-page
      response.sendRedirect(response.encodeRedirectURL("index.jsp"));
      
    // End of finish page
    } else if (page.equals("direction")) {
      
      // Check if peer is set.
      // Preserve peer value when changing directions.
      
      String peer = request.getParameter("peer");
      if(peer != null) {
        peer = peer.trim();
      }
      
      String[] topics = request.getParameterValues("topic");
      
      String[] remotepeers = request.getParameterValues("remotepeer");
      
      StringBuilder sb = new StringBuilder();
      sb.append("<html><head><title>Direction</title>");
      sb.append(SNPeer.STYLESHEET_HTML);
      sb.append("</head>");
      sb.append("<body>");
      sb.append("<p>");
      sb.append("<h1>Pick direction:</h1>");
      sb.append("<section>");
      sb.append("<direction>");
      sb.append("<ul>");
      sb.append("<li><a href=\"addKP?page=direction&direction=");
      sb.append(ContextSpace.IN);
      // Preserve peer, topic and remotepeer value
      if(peer != null){
        sb.append("&peer=");
        sb.append(peer);
      }
      if(topics != null) {
        for(String si : topics) {
          si = si.trim();
          sb.append("&topic=");
          sb.append(si);
        }
      }
      if(remotepeers != null) {
        for(String si : remotepeers) {
          si = si.trim();
          sb.append("&remotepeer=");
          sb.append(si);
        }
      }
      sb.append("\">IN</a></li>");
      sb.append("<li><a href=\"addKP?page=direction&direction=");
      sb.append(ContextSpace.OUT);
      // Preserve peer, topic and remotepeer value
      if(peer != null){
        sb.append("&peer=");
        sb.append(peer);
      }
      if(topics != null) {
        for(String si : topics) {
          si = si.trim();
          sb.append("&topic=");
          sb.append(si);
        }
      }
      if(remotepeers != null) {
        for(String si : remotepeers) {
          si = si.trim();
          sb.append("&remotepeer=");
          sb.append(si);
        }
      }
      sb.append("\">OUT</a></li>");
      sb.append("<li><a href=\"addKP?page=direction&direction=");
      sb.append(ContextSpace.INOUT);
      // Preserve peer, topic and remotepeer value
      if(peer != null){
        sb.append("&peer=");
        sb.append(peer);
      }
      if(topics != null) {
        for(String si : topics) {
          si = si.trim();
          sb.append("&topic=");
          sb.append(si);
        }
      }
      if(remotepeers != null) {
        for(String si : remotepeers) {
          si = si.trim();
          sb.append("&remotepeer=");
          sb.append(si);
        }
      }
      sb.append("\">IN/OUT</a></li>");
      sb.append("</ul>");
      sb.append("</direction>");
      sb.append("</section>");
      sb.append("</p>");
      sb.append(this.getAnchorSection(request));
      sb.append("<br />");
      
      sb.append("<section>\n<kpNav>\n");
      sb.append("<a href=\"addKP?page=topic");
      sb.append(this.urlEncodedParametersFromThisRequest(request));
      sb.append("\">");
      sb.append("Pick topics ->");
      sb.append("</a>");
      sb.append("</kpNav>\n</section>");
      sb.append("<body>");
      
      out.append(sb.toString());
    }
    
    
  }
  
  /**
   * Print the complete anchor section for a page.
   * 
   * @param request To read passed parameters from
   * @return An HTML string containing the anchors already selected
   */
  private String getAnchorSection(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    sb.append("<section>");
    sb.append("<anchors>");
    sb.append(this.printAnchorPoints(request));
    sb.append("</anchors>");
    sb.append("</section>");
    return sb.toString();
  }
  
  /**
   * Encode all paramters from the request into a URL compliant string.
   * @param request
   * @return 
   */
  private String urlEncodedParametersFromThisRequest(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    
    Enumeration names = request.getParameterNames();
    while(names != null && names.hasMoreElements()) {
      String name = (String) names.nextElement();
      if(name.equals("page")) {
        continue; // Don't preserve page parameter.
      }
      String[] values = request.getParameterValues(name);
      boolean first = true;
      for(String si : values) {
        sb.append("&" + name + "=" + si);
      }
    }
    return sb.toString();
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
    //processRequest(request, response);
  }

  /** 
   * Returns a short description of the servlet.
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>
  
  
  /**
   * Print the topic tree with a link to add a topic to the interest.
   * 
   * @param tag The starting tag of the tree to be printed.
   * @param buf The buffer to which the tree is written.
   * @param print 
   */
  private String printTopicTree(HierarchicalSemanticTag tag, StringBuilder buf, boolean print, HttpServletRequest request) {
    
     if(print) {
            buf.append("<topic>");
            buf.append(tag.getName());
            
            // add the newly selected topic
            buf.append("\n<addTopic><a href=\"AddKP?page=topic&topic=" + tag.getSI()[0]);
            buf.append(this.urlEncodedParametersFromThisRequest(request));
            buf.append("\">");
            buf.append("add to interest</a></addTopic>");
            
        }
        
        
        // If tag == root tag, check different type
        Enumeration subEnum = null;
        if(Util.sameEntity(tag.getSI(), new String[]{SNPeer.SN_TOPICS_ROOT_ENTRY})) {
          AssociatedSemanticTag assocTag = (AssociatedSemanticTag) tag;
          subEnum = assocTag.getAssociatedTags(SNPeer.SN_FIRST_LEVEL_PREDICATE);
        } else {
          // Not the root tag: Follow sub/super predicate
          subEnum = tag.getSubTags();
        
        }
        // print out sub tags - if any
        if(subEnum != null) {
            buf.append("<ul>");

            while(subEnum.hasMoreElements()) {
                buf.append("<li>");
                HierarchicalSemanticTag subTag = 
                        (HierarchicalSemanticTag)subEnum.nextElement();
                this.printTopicTree(subTag, buf, true, request);

                buf.append("</li>\n");

            }
            buf.append("</ul>\n");
        }
        
        if(print) buf.append("</topic>");
        
        return buf.toString();
    }
  
  
  /**
   * Print the whole topic page skeleton filled with values from
   * other methods.
   * 
   * @param topicTree The tree to present on the topic's page
   * @param request The request containing previously selected tags
   * @return A string containing an unfinished HTML file with all topic relevant information.
   */
  private String printTopicPage(String topicTree, HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("<html>");
    sb.append("<head>");
    sb.append("<title>");
    sb.append("Create KP");
    sb.append("</title>");
    sb.append(SNPeer.STYLESHEET_HTML);
    sb.append("<head>");
    sb.append("<body>");
    sb.append("<h2>Pick topics:</h2>");
    sb.append("<section>");
    sb.append("<topics>");
    sb.append(topicTree);
    sb.append("</topics>");
    sb.append("</section>");
    sb.append("<br />");
    sb.append(this.getAnchorSection(request));
    
    return sb.toString();
  }
  
  /**
   * Print the whole peer tree and all sub-peers.
   * 
   * @param request Containing parameters for kp-creation
   * @return A String containing an unordered list in HTML
   */
  public String getPeerTree(HttpServletRequest request) {
    SNPeer peer = new SNPeer();
    
    StringBuilder buf = new StringBuilder();
    
    List<PeerHierarchicalSemanticTag> rootTags = peer.getAllRootPeerTags();
//    Enumeration rootTagEnum = rootTags.elements();
    buf.append("<ul>");
    for(PeerHierarchicalSemanticTag rootTag : rootTags) {
//    while(rootTagEnum != null && rootTagEnum.hasMoreElements()) {
//      PeerHierarchicalSemanticTag rootTag = (PeerHierarchicalSemanticTag) rootTagEnum.nextElement();
      buf.append("<li>");
      this.printPeerTree(rootTag, buf, true, true, request);
      buf.append("</li>");
    }
    buf.append("</ul>");
    
    return buf.toString();
  }
  
  
  /**
   * Print the topic tree with a link to add a topic to the interest.
   * 
   * @param tag The starting tag of the tree to be printed.
   * @param buf The buffer to which the tree is written.
   * @param print 
   */
  private void printPeerTree(PeerHierarchicalSemanticTag tag, StringBuilder buf, 
            boolean print, boolean remotepeer, HttpServletRequest request) {
    
    // Determine if the peer is a group
    boolean isGroup = false;
    String group = tag.getProperty(KnowledgePort.HIDDEN);
    if(group != null && group.equals(Boolean.toString(Boolean.TRUE))) {
      isGroup = true;
    }
    
    if(print) {
            buf.append("<peer>");
            buf.append(tag.getName());
            
            if(isGroup) {
              buf.append(" ");
              buf.append("(Group)");
              buf.append(" ");
            }
            
            
            // add button
            if(remotepeer) {
              buf.append("\n<addPeer><a href=\"AddKP?page=remotepeer&remotepeer=" + tag.getSI()[0]);
              // Add all previously selected remotepeers
              buf.append(this.urlEncodedParametersFromThisRequest(request));
              buf.append("\">");
              buf.append("add to interest</a></addPeer>");
            } else {
              buf.append("\n<addPeer><a href=\"AddKP?page=peer&peer=" + tag.getSI()[0] + "\">");
              buf.append("add to interest</a></addPeer>");
            }
            
            
        }
        
        // If tag == root tag, check different type
        Enumeration subEnum = tag.getSubTags();
        
        // print out sub tags - if any
        if(subEnum != null) {
            buf.append("<ul>");

            while(subEnum.hasMoreElements()) {
                buf.append("<li>");
                PeerHierarchicalSemanticTag subTag = 
                        (PeerHierarchicalSemanticTag)subEnum.nextElement();
                this.printPeerTree(subTag, buf, true, remotepeer, request);

                buf.append("</li>\n");

            }
            buf.append("</ul>\n");
        }
        
        if(print) buf.append("</peer>");
        
    }
  
 
  /**
   * Print the whole peer page skeleton filled with values from
   * other methods.
   * 
   * @param peerTree The tree to present on the peer's page
   * @param request The request containing previously selected tags
   * @return A string containing an unfinished HTML file with all peer relevant information.
   */
  public String printPeerPage(String peerTree, HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    
    sb.append("<html>");
    sb.append("<head>");
    sb.append("<title>");
    sb.append("Create KP");
    sb.append("</title>");
    sb.append(SNPeer.STYLESHEET_HTML);
    sb.append("<head>");
    sb.append("<body>");
    sb.append("<h2>Pick peers:</h2>");
    sb.append("<section>");
    sb.append("<peers>");
    sb.append(peerTree);
    sb.append("</peers>");
    sb.append("</section>");
    sb.append("<br />");
    sb.append(this.getAnchorSection(request));
    
    
    return sb.toString();
  }
  
  /**
   * Print all currently selected anchor tags into a table.
   * @param request The request to read the previously selected tags from.
   * @return HTML code representing a table.
   */
  private String printAnchorPoints(HttpServletRequest request) {
    
    SharkKB kb = SNPeer.getSharkKB();
    
    // Get the SIs for each aspect of the anchors
    String[] topicSis = request.getParameterValues("topic");
    String[] peerSis = request.getParameterValues("peer");
    String[] remotepeerSis = request.getParameterValues("remotepeer");
    
    List<HierarchicalSemanticTag> topics = new ArrayList<HierarchicalSemanticTag>();
    List<PeerHierarchicalSemanticTag> peers = new ArrayList<PeerHierarchicalSemanticTag>();
    List<PeerHierarchicalSemanticTag> remotepeers = new ArrayList<PeerHierarchicalSemanticTag>();
    
    // Resolve the Sis to names to display
    if(topicSis != null) {
      for(String si : topicSis) {
        HierarchicalSemanticTag topic = (HierarchicalSemanticTag) kb.getSemanticTag(new String[]{si});
        topics.add(topic);
      }  
    }
    
    if(peerSis != null) {
      for(String si : peerSis) {
        PeerHierarchicalSemanticTag peer = (PeerHierarchicalSemanticTag) kb.getPeerSemanticTag(new String[]{si});
        peers.add(peer);
      }
    }
    
    if(remotepeerSis != null) {
      for(String si : remotepeerSis) {
        PeerHierarchicalSemanticTag peer = (PeerHierarchicalSemanticTag) kb.getPeerSemanticTag(new String[]{si});
        remotepeers.add(peer);
      }  
    }
    
    
    // Print the values
    StringBuilder sb = new StringBuilder();
    sb.append("<h2>");
    sb.append("Interest:");
    sb.append("</h2>");
    sb.append("<table>");
    sb.append("<tr>");
    sb.append("<td>");
    sb.append("Topics:");
    sb.append("</td>");
    sb.append(this.printAllTagFromIteratorToTable(topics.iterator()));
    sb.append("<tr>");
    sb.append("<td>");
    sb.append("Peer:");
    sb.append("</td>");
    sb.append(this.printAllTagFromIteratorToTable(peers.iterator()));
    sb.append("<tr>");
    sb.append("<td>");
    sb.append("Remotepeer:");
    sb.append("</td>");
    sb.append(this.printAllTagFromIteratorToTable(remotepeers.iterator()));
    sb.append("</tr>");
    sb.append("<tr>");
    sb.append("<td>");
    sb.append("Direction: ");
    sb.append("</td>");
    sb.append("<td>");
    sb.append(this.printDirectionFromRequest(request));
    sb.append("</td>");
    sb.append("</tr>");
    sb.append("</table>");
    
    return sb.toString();
            
  }
  
  private String printDirectionFromRequest(HttpServletRequest request) {
    String dir = request.getParameter("direction");
    if(dir != null) {
      int direction = Integer.parseInt(dir);
      if(direction == ContextSpace.IN) {
        return "IN";
      } else if(direction == ContextSpace.OUT) {
        return "OUT";
      } else {
        return "INOUT";
      }
    } else {
      return "UNSET";
    }
  }
  
  /**
   * Print tds with the topic's names from the iterator passed.
   * 
   * @param it An Iterator containing HierarchicalSemanticTags
   * @return A part of a table structure with the tag's names in tds
   */
  private String printAllTagFromIteratorToTable(Iterator it) {
    StringBuilder sb = new StringBuilder();
    
    while(it.hasNext()) {
      HierarchicalSemanticTag tag = (HierarchicalSemanticTag) it.next();
      sb.append("<td>");
      sb.append(tag.getName());
      sb.append("</td>");
    }
    return sb.toString();
  }
}
