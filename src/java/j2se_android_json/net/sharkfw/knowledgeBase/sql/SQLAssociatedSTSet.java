/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.AssociatedSTSet;
import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ROAssociatedSTSet;
import net.sharkfw.knowledgeBase.ROSemanticTag;
import net.sharkfw.knowledgeBase.STSet;
import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.inmemory.AbstractAssociatedSTSet;
import net.sharkfw.knowledgeBase.inmemory.InMemoAssociatedSTSet;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.system.L;

/**
 * DB handling of AssociatedSTSet. Stores names as properties to the tags in
 * JSON format.
 * 
 * @see DataBaseHandler
 * 
 * @author mfi
 */
public class SQLAssociatedSTSet extends AbstractAssociatedSTSet implements AssociatedSTSet {
  protected DataBaseHandler handler = null;
  protected int dim = -1;
  
  public SQLAssociatedSTSet(DataBaseHandler handler, int dimension) {
    this.handler = handler;
    this.dim = dimension;
  }
  
  @Override
  public AssociatedSemanticTag createAssociatedSemanticTag(String name, String[] si) throws SharkKBException {
    /*
     * Add a new entry to the Semantic_Tag table using the given data.
     * Add SIs as provided and make them point to the newly created SemanticTag
     */
    
    JSONObject props = new JSONObject();
    
    try {
      props.put(ROSemanticTag.NAME, name);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    DBSemanticTagModel stm = this.handler.createSemanticTag(props.toString(), this.dim, si);
    AssociatedSemanticTag tag = new SQLAssociatedSemanticTag(stm, this.handler);
    return tag;
  }

  @Override
  public void setPredicate(AssociatedSemanticTag c1, AssociatedSemanticTag c2, String assocType) {
    /*
     * c1 is the subject, c2 is the subject. Add an entry to the relation table
     * using the given assocType
     */
    DBSemanticTagModel subject = this.handler.getSemanticTag(this.dim, c1.getSI());
    DBSemanticTagModel object = this.handler.getSemanticTag(this.dim, c2.getSI());
    
    // try to create the relation in the DB
    if(!this.handler.createSemanticTagRelation(subject, object, assocType)) {
     L.e("Can't create relation between:" + c1.getName() + " and " + c2.getName(), this); 
    }
  }

  @Override
  public AssociatedSemanticTag getAssociatedSemanticTag(String si) throws SharkKBException {

    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public AssociatedSemanticTag getAssociatedSemanticTag(String[] si) throws SharkKBException {

    DBSemanticTagModel stm = this.handler.getSemanticTag(this.dim, si);
    return new SQLAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  public AssociatedSemanticTag getAssociatedSemanticTagByID(String id) throws SharkKBException {
    
    int idInt = Integer.parseInt(id);
    DBSemanticTagModel stm = this.handler.getSemanticTagById(idInt);
    return new SQLAssociatedSemanticTag(stm, this.handler);
  }

  @Override
  protected AssociatedSTSet copyAssociatedSTSet() {
    AssociatedSTSet retval = new InMemoAssociatedSTSet();
    retval.merge(this);
    return retval;
  }

  @Override
  protected AssociatedSemanticTag createAssociatedTagInFragment(
    // TODO: Move this code to an abstract superclass
    AssociatedSTSet fragment, AssociatedSemanticTag tag) {
    try {
      AssociatedSemanticTag created = fragment.createAssociatedSemanticTag(tag.getName(), tag.getSI());
      this.preserveProperties(tag, created);
      return created;
    } catch (SharkKBException ex) {
      L.e(ex.getMessage(), this);
      return null;
    }
  }

  @Override
  protected STSet createNewInstance() {
    // TODO: Move this code to an abstract superclass
    return new InMemoAssociatedSTSet();
  }

  @Override
  public SemanticTag createSemanticTag(String name, String[] si)
      throws SharkKBException {
    return this.createAssociatedSemanticTag(name, si);
  }


}
