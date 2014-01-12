/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import net.sharkfw.knowledgeBase.AssociatedSemanticTag;
import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.knowledgeBase.models.DBSemanticTagRelationModel;

/**
 *
 * @author mfi
 */
public class SQLAssociatedSemanticTag extends SQLSemanticTag implements AssociatedSemanticTag {

	
  public SQLAssociatedSemanticTag(DBSemanticTagModel stm, DataBaseHandler handler) {
    super(stm, handler);
  }


  @Override
  public Enumeration getAssocTypes() {
    List<String> predicates = this.handler.getSemanticTagRelationPredicates(model);
    // Can't get an Enumeration from a list. Re-pack and return Enumeration from Vector
    Vector<String> predVector = new Vector<String>();
    
    for(String s : predicates) {
      predVector.add(s);
    }
    
    return predVector.elements();
  }


  @Override
  public Enumeration getAssociatedTags(String type) {
    List<DBSemanticTagModel> objects = this.handler.getSemanticTagRelationObjects(this.model, type);
    // Can't get an Enumeration from a list. Re-pack and return Enumeration from Vector
    Vector<AssociatedSemanticTag> retval = new Vector<AssociatedSemanticTag>();
    
    for(DBSemanticTagModel stm : objects) {
      AssociatedSemanticTag tag = new SQLAssociatedSemanticTag(stm, this.handler);
      retval.add(tag);
    }
    
    return retval.elements();
  }
 

  @Override
  public void setPredicate(String type, AssociatedSemanticTag concept) {
    // Get the model for the "object" of this association   
    // Possible to use concept ID as it unique throughout the whole database
    int idInt = Integer.parseInt(concept.getID());
    DBSemanticTagModel object = this.handler.getSemanticTagById(idInt);
    
    // No use for the return value
    this.handler.createSemanticTagRelation(this.model, object, type);
  }

 
  @Override
  public void removePredicate(String type, AssociatedSemanticTag concept) {
    // Get the model for the "object" of this association
    // Possible to use concept ID as it unique throughout the whole database
    int idInt = Integer.parseInt(concept.getID());
    DBSemanticTagModel stm = this.handler.getSemanticTagById(idInt);
    
    // parametrize the Relation
    DBSemanticTagRelationModel statement = new DBSemanticTagRelationModel();
    statement.setSubject(this.model);
    statement.setObject(stm);
    statement.setPredicate(type);
    
    this.handler.deleteSemanticTagRelation(statement);
  }
  


}
