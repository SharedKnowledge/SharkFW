/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.isphere.peer;

import de.isphere.knowledgeBase.ISphereKB;
import de.isphere.peer.impl.SimpleISpherePeer;
import java.io.IOException;
import java.util.Enumeration;
import net.sharkfw.kep.KEPMessage;
import net.sharkfw.kep.KEPResponse;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.ExposedInterest;
import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.ROPeerSTSet;
import net.sharkfw.knowledgeBase.ROPeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.InMemoPeerAssociatedSTSet;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.protocols.MessageStub;
import net.sharkfw.protocols.Protocols;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
//import net.sharkfw.protocols.http.HTTPClientMessageStub;
import net.sharkfw.protocols.http.HTTPClientMessageStub;
import net.sharkfw.protocols.tcp.TCPStreamStub;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;
import net.sharkfw.system.Util;

/**
 *
 * @author Romy Gerlach
 */
public class J2MEISpherePeer extends SimpleISpherePeer{

    public J2MEISpherePeer(ISphereKB imKb){
        super(imKb);
    }

    /*
   * Code that handles platform specific creation of communication stubs.
   */

  /**
   * Implement the locally supported protocols
   * @param handler
   * @param port
   * @return
   * @throws SharkProtocolNotSupportedException
   */
  protected StreamStub createTCPStreamStub(RequestHandler handler, int port) throws SharkProtocolNotSupportedException {
    try {
      return new TCPStreamStub(handler, port);
    }
    catch(IOException ioe) {
      ioe.printStackTrace();
      throw new SharkProtocolNotSupportedException(ioe.getMessage());
    }
  }

  

  /*
   * Code that handles platform specific publication of knowledge ports
   */

  public void publishKP(KnowledgePort kp) {
    L.d("publishKP() started", this);

    /**
     * If no relay address is set
     * send the interest to every peer
     * on the REMOTEPEER dimension.
     */
    if(this.getRelaisAddress() == null) {
      L.d("No relayaddress set", this);
      ExposedInterest interest = kp.getInterest();
      try {
        ROPeerSTSet recipients = (ROPeerSTSet) interest.getSTSet(ContextSpace.DIM_REMOTEPEER);
        try {
          Enumeration recipientTags = recipients.tags();
          while(recipientTags != null && recipientTags.hasMoreElements()) {
            ROPeerSemanticTag ropst = (ROPeerSemanticTag) recipientTags.nextElement();
            this.publishKP(kp, ropst);
          }
        } catch (SharkNotSupportedException ex) {
          L.e(ex.getMessage(), this);
          ex.printStackTrace();
        }

      } catch (SharkKBException ex) {
        L.e(ex.getMessage(), this);
        ex.printStackTrace();
      }

    } else {
      L.d("Relayaddress set", this);
      /**
       * If a relay address is set,
       * send the interest to the relay only
       */
      PeerSTSet pst = new InMemoPeerAssociatedSTSet();
      /*
       * create a dummy concept containing the relay's address information
       */
      ROPeerSemanticTag relay = pst.createPeerSemanticTag("relay", new String[]{""}, new String[]{this.getRelaisAddress()});
      this.publishKP(kp, relay);

    }
    L.d("publishKP() ended", this);
  }


  public void publishKP(KnowledgePort kp, ROPeerSemanticTag recipient) {
    L.d("Publishing KP to recipient", this);
    /*
     * This code looks a lot like the code in KEPRequest.
     * Maybe we make use of that?
     */

    if(!isAllowedForPublish(kp)){
        return;
    }
    KEPResponse response = null;
    MessageStub mStub;
    StreamStub sStub;
    StreamConnection sConn = null;

    // See if a response has been sent yet
    boolean sent = false;

    /*
     * Read receipient's addresses
     */
    String[] addresses = recipient.getAddresses();

    if(addresses == null) {
      L.e("This peer tag has null as addresses!", this);
      return;
    }

    Enumeration addrEnum = Util.array2Enum(addresses);

    while(addrEnum.hasMoreElements() && !sent) {
        String address = (String) addrEnum.nextElement();
        try {
          /*
           * Check if stub is available
           */
            int type = Protocols.getValueByAddress(address);

            if(this.openStubs[type] == null) {
                // there is no stub of this type - start it
                this.startProtocol(type);
            }

            mStub = null;
            sStub = null;
            sConn = null;

            /*
             * Find out which protocol to use
             */
            if(Protocols.isStreamProtocol(type)) {
              sStub = (StreamStub) this.openStubs[type];
              sConn = sStub.createStreamConnection(address);
              response = new KEPResponse(this, sConn, KEPMessage.getKnowledgeSerializer(this.kFormat));
            } else {
              mStub = (MessageStub) this.openStubs[type];
              response = new KEPResponse(this, mStub, KEPMessage.getKnowledgeSerializer(this.kFormat), address);
            }
        } catch (net.sharkfw.system.SharkNotSupportedException ex) {
          L.e(ex.getMessage(), this);
          ex.printStackTrace();
        } catch (IOException ex) {
          L.e(ex.getMessage(), this);
          ex.printStackTrace();
        } catch (SharkProtocolNotSupportedException spn) {
          L.e(spn.getMessage(), this);
          spn.printStackTrace();
        }

        if(response != null) {
          // Response could be created
          response.expose(kp.getInterest(), kp);
          sent = response.responseSent();
        }

        if(sConn != null) {
          this.kepStub.handleStream(sConn);
        }

        // If the response has been sent we are finished.
       
    }
    L.d("End publishing KP to recipient", this);
  }


  public void publishAllKp() {
    L.d("Publishing all KPs", this);
    // Find all KPs
    Enumeration kpEnum = this.kps.elements();

    // publish one by one to the environment
    while(kpEnum.hasMoreElements()) {
      KnowledgePort kp = (KnowledgePort) kpEnum.nextElement();
      if(!isAllowedForPublish(kp)) {
        continue;
      }
      this.publishKP(kp);
    }

  }


  public void publishAllKp(ROPeerSemanticTag recipient){
    L.d("Publishing all KPs", this);

    // Find all KPs
    Enumeration kpEnum = this.kps.elements();

    // Publish them one by one to the recipient
    while(kpEnum.hasMoreElements()) {
      KnowledgePort kp = (KnowledgePort) kpEnum.nextElement();
      if(!isAllowedForPublish(kp)) {
        continue;
      }
      this.publishKP(kp, recipient);
    }

  }

    protected MessageStub createHttpClientMessageStub(String localAdr, String method) {
        return new HTTPClientMessageStub(this.kepStub, localAdr, method);
        
    }


}
