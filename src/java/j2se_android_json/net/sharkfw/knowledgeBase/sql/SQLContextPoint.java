/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.ContextCoordinates;
import net.sharkfw.knowledgeBase.ContextPoint;
import net.sharkfw.knowledgeBase.ContextPointListener;
import net.sharkfw.knowledgeBase.ContextSpace;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkDuplicateException;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBContextPointModel;
import net.sharkfw.knowledgeBase.models.DBInformationModel;
import net.sharkfw.knowledgeBase.models.DBSubjectIdentifierModel;
import net.sharkfw.system.Streamer;
import net.sharkfw.system.Util;

/**
 * The DB implementation of Shark's ContextPoint.
 * TODO: Doesn't notify any listener yet.
 * TODO: Add duplicate suppression support.
 * 
 * @author mfi
 */
public class SQLContextPoint implements ContextPoint{

  private DataBaseHandler handler = null;
  private DBContextPointModel model = null;
  
  private ContextPointListener listener = null;
  
  public SQLContextPoint(DataBaseHandler handler, DBContextPointModel model) {
    this.handler = handler;
    this.model = model;
  }
  
  @Override
  public Enumeration getInformation() {
    /*
     * Return an enumeration of all SQLInformation which are held at this
     * ContextPoint.
     */
    DBInformationModel[] infos = this.handler.getInformation(this.model.getId());

    // Prepare Vector for Enumeration return value
    Vector<SQLInformation> retval = new Vector<SQLInformation>();
    
    for(DBInformationModel ifm : infos) {
      SQLInformation info = new SQLInformation(ifm, this.handler);
      retval.add(info);
    }
    
    return retval.elements();
  }

  @Override
  public int getNumberInformation() {
    DBInformationModel[] infos = this.handler.getInformation(this.model.getId());
    return infos.length;
  }

  @Override
  public ContextCoordinates getCoordinates() {
    /*
     * Read the coordinates from the db and wrap them up inside a
     * ContextCoordinates object.
     */
    ContextCoordinates co = new ContextCoordinates();
    // collect all topics that play a role in this context point
    List<DBSubjectIdentifierModel> io = this.model.getIo().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> originator = this.model.getOriginator().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> peer = this.model.getPeer().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> remotepeer = this.model.getRemote().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> location = this.model.getLocation().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> time = this.model.getTime().getSubjectIdentifierList();
    List<DBSubjectIdentifierModel> topic = this.model.getTopic().getSubjectIdentifierList();
    
    // Translate models to Strings
    String[] ioSis = this.list2Array(io);
    String[] originatorSis = this.list2Array(originator);
    String[] peerSis = this.list2Array(peer);
    String[] remotepeerSis = this.list2Array(remotepeer);
    String[] locationSis = this.list2Array(location);
    String[] timeSis = this.list2Array(time);
    String[] topicSis = this.list2Array(topic);
    
    // Set the dimension inside the ContextCoordinates object
    co.setSI(ContextSpace.DIM_DIRECTION, ioSis);
    co.setSI(ContextSpace.DIM_ORIGINATOR, originatorSis);
    co.setSI(ContextSpace.DIM_PEER, peerSis);
    co.setSI(ContextSpace.DIM_REMOTEPEER, remotepeerSis);
    co.setSI(ContextSpace.DIM_LOCATION, locationSis);
    co.setSI(ContextSpace.DIM_TIME, timeSis);
    co.setSI(ContextSpace.DIM_TOPIC, topicSis);
    
    return co;
  }

  @Override
  public void associateContextPoint(ContextPoint ctx, String type) {
    // deprecated
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void deleteAssociation(ContextPoint ctx, String type) {
    //deprecated
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setProperty(String name, String value) {
    String jsonProps = this.model.getProperties();
    try {
      JSONObject props = new JSONObject(jsonProps);
      props.put(name, value);
      // Set props to the model
      this.model.setProperties(props.toString());
      // Update the database
      this.handler.updateContextPoint(this.model);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public String getProperty(String name) {
    String jsonprops = this.model.getProperties();
    try {
      JSONObject props = new JSONObject(jsonprops);
      return props.getString(name);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void deleteProperty(String name) {
    // Setting a prop to null
    this.setProperty(name, null);
  }

  /**
   * Doesn't care for duplicates yet.
   */
  @Override
  public Information addInformation(Hashtable props, boolean duplicatesAllowed) throws SharkKBException, SharkDuplicateException {
    /*
     * Create a new entry into the information table and save its properties.
     * No data is available yet.
     */
    JSONObject properties = new JSONObject();
    
    Enumeration keys = props.keys();
    while(keys != null && keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = (String) props.get(key);
      try {
        properties.put(key, value);
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    this.handler.createInformation(this.model, new byte[1], properties.toString());
    return null;
  }

  @Override
  public Information addInformation(String string, InputStream bais, long len) throws SharkKBException {

    // Write all content from the input stream into a byte array
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      Streamer.stream(bais, baos, 8000);
      // Get the byte array to store it in the DB
      byte[] data = baos.toByteArray();
      DBInformationModel infoModel = this.handler.createInformation(model, data, "{}");
      // Wrap up the model inside the SQLInformation class
      return new SQLInformation(infoModel, this.handler);
    } catch (IOException e) {
      e.printStackTrace();
      throw new SharkKBException(e.getMessage());
    }
  }

  @Override
  public void removeInformation(Information info) {
    /*
     * Delete the entry 'info' from the information table
     */
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setListener(ContextPointListener cpl) {
    /*
     * Register a new listener
     */
    this.listener = cpl;
  }

  @Override
  public void removeListener() {
    /*
     * Remove the listener
     */
    this.listener = null;
  }

  /**
   * Helper method to transform a list of models into strings
   * 
   *
   * @param list
   * @return A String[] containing the URIs of the models
   */
  private String[] list2Array(List<DBSubjectIdentifierModel> list) {
    String[] sis = null;
    for(DBSubjectIdentifierModel sim : list) {
      String s = sim.getUri();
      Util.addString(sis, s);
    }
    return sis;
  }
}
