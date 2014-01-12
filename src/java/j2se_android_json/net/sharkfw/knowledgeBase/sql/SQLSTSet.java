/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.FragmentationParameter;
import net.sharkfw.knowledgeBase.ROSTSet;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.STSetListener;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.AbstractSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoAssociatedSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoAssociatedSemanticTag;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.L;
import net.sharkfw.system.SharkNotSupportedException;

/**
 * DB impl of STSet.
 * Each STSet inside the KB is designated by a dimension number (int).
 * 
 * @see DataBaseHandler
 * @author mfi
 */
public class SQLSTSet extends AbstractSTSet implements STSet {

  protected DataBaseHandler handler = null;
  protected int dim = -1;
  
  private Vector<STSetListener> listener = new Vector<STSetListener>();
  
  public SQLSTSet(DataBaseHandler handler, int dimension) {
    this.handler = handler;
    this.dim = dimension;
  }
  
  @Override
  public SemanticTag createSemanticTag(String name, String[] si) throws SharkKBException {
    /*
     * Create a new entry to the Semantic_Tag table, containing this stset's
     * dimension, create all SIs inside the Subject_Identifier table and link
     * both entries.
     */
    
    // Save the name as a property
    JSONObject props = new JSONObject();
    try {
      props.put(ROSemanticTag.NAME, name);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), dim, si);
    SQLSemanticTag tag = new SQLSemanticTag(stm, this.handler);
    return tag;
  }

  @Override
  public void removeSemanticTag(String name, String[] si) {
    /*
     * Delete a SemanticTag from this STSet. STSets inside the KB are always defined
     * by the dimension's number.
     */
    // Find the model for the tag
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    
    if(stm == null) {
      // Model could not be found. Print errormessage and be done.
      L.e("Could not find model " + name + " in db. Ignoring remove command", this);
      return;
    }
    
    if(!this.handler.deleteSemanticTag(stm)) {
      L.e("Unable to remove SemanticTag: " + name, this);
    }
  }

  @Override
  public void merge(ROSTSet remote) {
    /*
     * Merge the remote STSet into this. See InMemo impl as foundation.
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addListener(STSetListener listen) {
    this.listener.add(listen);
  }

  @Override
  public void removeListener(STSetListener listen) {
    this.listener.remove(listen);
  }

  @Override
  public ROSTSet findWay(ROSemanticTag to, ROSemanticTag from, FragmentationParameter otp) {
    // TODO: Add dummy impl
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Enumeration getAllSI() {
    /*
     * Return an Enumeration of all SIs from this STSet. The Enumeration will
     * contain Strings.
     */
    List<String> sis = this.handler.getSubjectIdentifier(this.dim);
    Vector<String> v = new Vector<String>();
    
    for(String s : sis) {
      v.add(s);
    }
    return v.elements();
  }

  @Override
  public SemanticTag getSemanticTag(String si) throws SharkKBException {
    /*
     * Find the SI inside the SI-Table and then find the SQLSemanticTag inside
     * the Semantic_Tag table to which the SI refers.
     *
     * Problem with SI's that are used on more than one dimension?!
     */
    
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    SQLSemanticTag tag = new SQLSemanticTag(stm, this.handler);
    return tag;
  }

  @Override
  public SemanticTag getSemanticTag(String[] si) throws SharkKBException {
    /*
     * See above. It's enough if one SI from the array matches
     */
    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    SQLSemanticTag tag = new SQLSemanticTag(stm, this.handler);
    return tag;
  }

  @Override
  public SemanticTag getSemanticTagByID(String id) throws SharkKBException {
    /*
     * The id should be the primary key, so simply return the SQLSemanticTag
     * with the given ID.
     */
    // TODO: this.handler.get .. Get a method for this
    List<DBSemanticTagModel> stmList = this.handler.getSemanticTag(this.dim);
    for(DBSemanticTagModel stm : stmList) {
      String stmId = String.valueOf(stm.getId());
      if(stmId.equals(id)) {
        SemanticTag tag = new SQLSemanticTag(stm, this.handler);
        return tag;
      }
    }
    throw new SharkKBException("Unable to find SemanticTag with id " + id);
  }

  @Override
  public ROSTSet fragment(Enumeration anchor, int depth) throws SharkKBException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public ROSTSet fragment(Enumeration anchor, int depth, Hashtable allowedProps, Hashtable forbiddenProps) throws SharkKBException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String serialize(int type) throws SharkNotSupportedException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Enumeration tags() {
    /*
     * Return an Enumeration of all Tags from this STSet.
     * The Enumeration will hold instances of SQLSemanticTags
     */
    List<DBSemanticTagModel> tags = this.handler.getSemanticTag(this.dim);
    Vector<SemanticTag> v = new Vector<SemanticTag>();
    
    for(DBSemanticTagModel stm : tags) {
      SQLSemanticTag tag = new SQLSemanticTag(stm, this.handler);
      v.add(tag);
    }
    return v.elements();
  }

  @Override
  public ROSTSet copy() {
    /*
     * Return a copy of this STSet (probably inMemory)
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected STSet createNewInstance() {
    return new InMemoAssociatedSTSet();
  }

}
