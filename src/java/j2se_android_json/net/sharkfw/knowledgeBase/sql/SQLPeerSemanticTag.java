/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.Util;

/**
 * Db impl of PeerSemanticTag. Storing addresses and certificate as properties
 * in JSON format.
 * 
 * @see DBSemanticTagModel
 * @see DataBaseHandler
 * 
 * @author mfi
 */
public class SQLPeerSemanticTag extends SQLSemanticTag implements PeerSemanticTag {

  public SQLPeerSemanticTag(DBSemanticTagModel model, DataBaseHandler handler) {
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
