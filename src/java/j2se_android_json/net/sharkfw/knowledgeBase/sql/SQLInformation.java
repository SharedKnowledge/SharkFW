/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sharkfw.knowledgeBase.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.knowledgeBase.models.DBInformationModel;
import net.sharkfw.system.Streamer;

/**
 * DB impl of Information
 * @author mfi
 */
public class SQLInformation implements Information{
  private DBInformationModel model = null;
  private DataBaseHandler handler = null;
  private ByteArrayOutputStream baos = null;

  public SQLInformation(DBInformationModel ifm, DataBaseHandler handler) {
    this.model = ifm;
    this.handler = handler;
  }

  @Override
  public void streamContent(OutputStream os) {
    /*
     * Stream the contents of the data field to the given OutputStream
     */
    
    byte[] data = this.handler.getInformationContent(model.getId());
    ByteArrayInputStream bais = new ByteArrayInputStream(data);
    try {
      Streamer.stream(bais, os, 8000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void fillContent(InputStream is, long len) {
    /*
     * Read len bytes from InputStream and write them to the data field.
     */
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      Streamer.stream(is, baos, 8000);
      this.model.setData(baos.toByteArray());
      this.handler.updateInformation(model, true);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public OutputStream getWriteAccess() {
    /*
     * Grant access to the data field by using a OutputStream.
     * Add listener to Information interface to signal end of writing
     */
    // When calling this method the contents of the information (if existing) are 
    // overwritten
    this.baos = new ByteArrayOutputStream();
    return baos;
  }

  @Override
  public byte[] getContentAsByte() {
    /*
     * Return the contents of the data field as a byte[]
     */
    return this.handler.getInformationContent(model.getId());
  }

  /**
   * ! To determine the content's lengththe whole content is loaded into memory !
   */
  @Override
  public long getContentLength() {
    /*
     * Return the size of the data field as long
     */
    byte[] content = this.handler.getInformationContent(model.getId());
    return content.length;
  }

  @Override
  public void setProperty(String name, String value) throws SharkKBException {
    String jsonProps = this.model.getProperty();
    try {
      JSONObject props = new JSONObject(jsonProps);
      props.put(name, value);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getProperty(String name) throws SharkKBException {
    String jsonProps = this.model.getProperty();
    
    try {
      JSONObject props = new JSONObject(jsonProps);
      return (String) props.get(name);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Enumeration getPropertyNames() throws SharkKBException {
    String jsonProps = this.model.getProperty();
    Vector<String> retval = new Vector<String>();
    
    try {
      JSONObject props = new JSONObject(jsonProps);
      Iterator keys = props.keys();
      while(keys.hasNext()) {
        String key = (String) keys.next();
        retval.add(key);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return retval.elements();
  }

  @Override
  public void deleteProperty(String name) throws SharkKBException {
    this.setProperty(name, null);
  }

  @Override
  public String getContentType() {
    /*
     * Read the "ContentType" property and return the value
     */
    return "application/x-shark";
  }
  
  public void finishedWriting() {
    // write the changed model (including the data) into the DB
    if(this.baos != null) {
      this.model.setData(baos.toByteArray());
    }
    this.handler.updateInformation(this.model, true);
    this.baos = null;
  }

}
