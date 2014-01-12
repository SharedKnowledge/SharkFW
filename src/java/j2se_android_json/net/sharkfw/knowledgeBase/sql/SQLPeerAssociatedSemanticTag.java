/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.Util;

/**
 * DB impl of PeerAssociatedSemanticTag. Stores addresses and certificate as properties in
 * JSON format.
 * 
 * @see DataBaseHandler
 * @see DBSemanticTagModel
 * 
 * @author mfi
 */
public class SQLPeerAssociatedSemanticTag extends SQLAssociatedSemanticTag implements PeerAssociatedSemanticTag{

  public SQLPeerAssociatedSemanticTag(DBSemanticTagModel model, DataBaseHandler handler) {
    super(model, handler);
  }
  
  @Override
  public void setAddresses(String[] addresses) {
    String serializedAddresses = Util.array2string(addresses);
    
    try {
      this.setProperty(ADDRESSES, serializedAddresses);
    } catch (SharkKBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void setCertificate(byte[] certificate) {
    // TODO: Certificate should be String
    String cert = new String(certificate);
    
    try {
      this.setProperty(CERTIFICATE, cert);
    } catch (SharkKBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public String[] getAddresses() {
    String serializedAddresses = this.getProperty(ADDRESSES);
    String[] addresses = Util.string2array(serializedAddresses);
    return addresses;
  }

  @Override
  public byte[] getCertificate() {
    // TODO: certificate should be a String
    String cert = this.getProperty(CERTIFICATE);
    return cert.getBytes();
  }


}
