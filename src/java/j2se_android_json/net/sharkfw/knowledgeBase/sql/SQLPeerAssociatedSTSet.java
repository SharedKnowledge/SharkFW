/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;

import net.sharkfw.knowledgeBase.PeerAssociatedSTSet;
import net.sharkfw.knowledgeBase.PeerAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.Util;

/**
 * DB impl of PeerAssociatedSTSet. 
 * Each STSet inside the KB is designated by a dimension number (int).
 * 
 * @see DataBaseHandler
 * @author mfi
 */
public class SQLPeerAssociatedSTSet extends SQLAssociatedSTSet implements PeerAssociatedSTSet {

  public SQLPeerAssociatedSTSet(DataBaseHandler handler, int dim) {
    super(handler, dim);
  }
  
  @Override
  public PeerAssociatedSemanticTag createPeerAssociatedSemanticTag(String name, String[] si, String[] replyAddresses) {
    // Prepare addresses
    String serializedAddresses = Util.array2string(replyAddresses);
    
    // Prepare properties
    JSONObject props = new JSONObject();
    try {
      props.put(ROSemanticTag.NAME, name);
      props.put(PeerSemanticTag.ADDRESSES, serializedAddresses);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // Persist to DB
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), this.dim, si);
    return new SQLPeerAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public PeerAssociatedSemanticTag getPeerAssociatedSemanticTag(String si) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLPeerAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public PeerAssociatedSemanticTag getPeerAssociatedSemanticTag(String[] sis) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, sis);
    return new SQLPeerAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public PeerAssociatedSemanticTag getPeerAssociatedSemanticTagById(String id) throws SharkKBException {
    int idInt = Integer.parseInt(id);
    DBSemanticTagModel stm = this.handler.getSemanticTagById(idInt);
    return new SQLPeerAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public PeerSemanticTag getPeerSemanticTag(String si) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLPeerSemanticTag(stm, this.handler);
  }

  @Override
  public PeerSemanticTag getPeerSemanticTag(String[] sis) throws SharkKBException {
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, sis);
    return new SQLPeerSemanticTag(stm, this.handler);
  }

  @Override
  public PeerSemanticTag getPeerSemanticTagById(String id) throws SharkKBException {
    return this.getPeerAssociatedSemanticTagById(id);
  }

  @Override
  public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) {
    return this.createPeerAssociatedSemanticTag(name, sis, addresses);
    }


}
