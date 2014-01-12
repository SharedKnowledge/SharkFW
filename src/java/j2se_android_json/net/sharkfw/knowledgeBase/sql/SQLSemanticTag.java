/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.SemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBSemanticTagModel;
import net.sharkfw.knowledgeBase.models.DBSubjectIdentifierModel;
import net.sharkfw.system.Util;

/**
 * Implementation of a Database persisted SemanticTag
 * 
 * @author mfi
 */
public class SQLSemanticTag implements SemanticTag {

  protected DBSemanticTagModel model = null;
  protected DataBaseHandler handler = null;
  
  public SQLSemanticTag(DBSemanticTagModel model, DataBaseHandler handler) {
    this.model = model;
    this.handler = handler;
  }
  
  @Override
  public void removeSI(String si) {
    /*
     * Remove the SI from the Subject_Identifier table
     */
    List<DBSubjectIdentifierModel> sis = this.model.getSubjectIdentifierList();
    for(DBSubjectIdentifierModel dsim : sis) {
      String modelSi = dsim.getUri();
      if(modelSi.equals(sis)) {
        this.handler.deleteSubjectIdentifier(dsim);
      }
    }
  }

  @Override
  public void addSI(String si) {
    /*
     * Add another SI to the Subject_Identifier table and let st_id point
     * to this SQLSemanticTag.
     */

    // Update DB
    DBSubjectIdentifierModel[] dsim = this.handler.createSubjectIdentifier(this.model, si);

    // Update model
    for(DBSubjectIdentifierModel dsimTemp : dsim) {
      this.model.addSubjectIdentifier(dsimTemp);
    }
  }

  @Override
  public void setName(String newName) {
    try {
      this.setProperty(NAME, newName);
    } catch (SharkKBException ex) {
      Logger.getLogger(SQLSemanticTag.class.getName()).log(Level.SEVERE, null, ex);
    }
    // Update the DB
    this.handler.updateDBSemanticTag(model);
  }

  @Override
  public void setProperty(String name, String value) throws SharkKBException {
    /*
     * Add the given property to the JSON properties stored for this
     * SQLSemanticTag
     */
    // TODO add K:V pair to JSON object
    this.model.addProperty(name, value);
    this.handler.updateDBSemanticTag(model);
  }

  @Override
  public void merge(SemanticTag oc) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getID() {
    return String.valueOf(this.model.getId());
  }

  @Override
  public String getName() {
    return this.getProperty(NAME);
  }

  @Override
  public String[] getSI() {
    /*
     * Find all entries (uris) from Subject_Identifier table which point to
     * this SQLSemanticTag
     */
    String[] siArray = null;
    Vector<String> sis = new Vector<String>();
    
    List<DBSubjectIdentifierModel> siList = this.model.getSubjectIdentifierList();
    for(DBSubjectIdentifierModel sim : siList) {
      String si = sim.getUri();
      sis.add(si);
    }
    
    siArray = sis.toArray(new String[]{});
    return siArray;
  }

  @Override
  public String getProperty(String name) {
    /*
     * Return the value for the given key from this SQLSemanticTag's properties
     */
    // TODO: Return property value from JSON object
    String jsonProps = this.model.getProperties();
    try {
      JSONObject props = new JSONObject(jsonProps);
      try {
        return (String) props.get(name);
      } catch (JSONException jex) {
        return null;
      }
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

}
