/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.PeerSTSet;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.Util;

/**
 * DB interface for PeerSTSets. Stores addresses and the name of the tag as JSONObject 
 * containing the appropriate properties.
 * 
 * @author mfi
 */
public class SQLPeerSTSet extends SQLSTSet implements PeerSTSet {

  public SQLPeerSTSet(DataBaseHandler handler, int dim) {
    super(handler, dim);
  }
  
  @Override
  public PeerSemanticTag createPeerSemanticTag(String name, String[] sis, String[] addresses) {
    /*
     * Create a new entry to the Semantic_Tag table. Add SIs as provided in
     * the array. Set the addresses inside the properties.
     */
    JSONObject props = new JSONObject();
    try {
      props.put(SemanticTag.NAME, name);
      
      String serializedAddresses = Util.array2string(addresses);
      props.put(PeerSemanticTag.ADDRESSES, serializedAddresses);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), this.dim, sis);
    return new SQLPeerSemanticTag(stm, this.handler);
    
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
    int idInt = Integer.parseInt(id);
    DBSemanticTagModel stm = this.handler.getSemanticTagById(idInt);
    return new SQLPeerSemanticTag(stm, this.handler);
  }


}
